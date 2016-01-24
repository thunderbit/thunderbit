package controllers;

import akka.dispatch.Futures;
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
import play.mvc.Security;
import scala.concurrent.Promise;
import views.html.uploadForm;
import views.html.uploadResult;

import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;

public class Storage extends Controller {
    @Inject
    public modules.storage.Storage storage;

    @Inject
    public MongoDB mongoDB;

    @Security.Authenticated(Secured.class)
    public Result uploadForm() {
        return ok(uploadForm.render());
    }

    @Security.Authenticated(Secured.class)
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

                Promise<Void> databaseScalaPromise = Futures.promise();

                MongoCollection<Item> items = mongoDB.getDatabase().getCollection("items", Item.class);
                items.insertOne(item, (anotherVoid, throwable) -> databaseScalaPromise.success(anotherVoid));

                return F.Promise.wrap(databaseScalaPromise.future())
                        .map((F.Function<Void, Result>) anotherVoid -> ok(uploadResult.render(fileName)))
                        .recover(throwable -> internalServerError(throwable.getMessage()));
            }).recover(throwable -> internalServerError(throwable.getMessage()));
        } else {
            return F.Promise.pure(badRequest());
        }
    }

    public F.Promise<Result> download(String id) {
        Promise<F.Tuple<Result, String>> promise = Futures.promise();

        FindIterable<Item> itemsIterable = mongoDB.getDatabase().getCollection("items", Item.class)
                .find().filter(eq("_id", new ObjectId(id)));

        itemsIterable.first((item, throwable) ->
                storage.getDownload(item.storageKey, item.name).map(result ->
                        promise.success(F.Tuple(result, item.name))));

        return F.Promise.wrap(promise.future()).map(resultStringTuple -> {
            response().setHeader("Content-Disposition", "attachment; filename="+resultStringTuple._2);
            return resultStringTuple._1;
        }).recover(throwable -> internalServerError(throwable.getMessage()));
    }
}
