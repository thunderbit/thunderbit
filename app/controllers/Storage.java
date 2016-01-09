package controllers;

import com.google.inject.Inject;
import com.mongodb.async.client.FindIterable;
import com.mongodb.async.client.MongoCollection;
import models.Item;
import modules.mongodb.MongoDB;
import org.bson.types.ObjectId;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import scala.concurrent.Promise;
import scala.concurrent.Promise$;
import views.html.uploadForm;
import views.html.uploadResult;

import java.nio.file.Path;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;

public class Storage extends Controller {
    @Inject
    public modules.storage.Storage storage;

    @Inject
    public MongoDB mongoDB;

    public Result uploadForm() {
        return ok(uploadForm.render());
    }

    public F.Promise<Result> upload() {
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart file = body.getFile("file");

        if (file != null) {
            String fileName = file.getFilename();
            String uuid = UUID.randomUUID().toString();

            F.Promise<Void> storagePromise = storage.store(file.getFile().toPath(), uuid);

            return storagePromise.flatMap(aVoid -> {
                Item item = new Item();
                item.name = fileName;
                item.storageKey = uuid;

                Promise<Void> databaseScalaPromise = Promise$.MODULE$.apply();

                MongoCollection<Item> items = mongoDB.getDatabase().getCollection("items", Item.class);
                items.insertOne(item, (anotherVoid, throwable) -> databaseScalaPromise.success(anotherVoid));

                return F.Promise.wrap(databaseScalaPromise.future()).map((F.Function<Void, Result>) anotherVoid -> ok(uploadResult.render(fileName)));
            });
        } else {
            return F.Promise.pure(badRequest());
        }
    }

    public F.Promise<Result> download(String id) {
        Promise<F.Tuple<Path, String>> promise = Promise$.MODULE$.apply();

        FindIterable<Item> itemsIterable = mongoDB.getDatabase().getCollection("items", Item.class)
                .find().filter(eq("_id", new ObjectId(id)));

        itemsIterable.first((item, throwable) -> storage.retrieve(item.storageKey).map(path -> promise.success(F.Tuple(path, item.name))));

        return F.Promise.wrap(promise.future()).map((F.Function<F.Tuple<Path, String>, Result>) pathStringTuple -> {
            response().setHeader("Content-Disposition", "attachment; filename="+pathStringTuple._2);
            return ok(pathStringTuple._1.toFile());
        });
    }
}
