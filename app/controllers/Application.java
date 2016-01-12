package controllers;

import akka.dispatch.Futures;
import com.google.inject.Inject;
import com.mongodb.async.client.FindIterable;
import models.Item;
import modules.mongodb.MongoDB;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Result;
import scala.concurrent.Promise;
import views.html.index;

import java.util.ArrayList;
import java.util.List;

public class Application extends Controller {
    @Inject
    public MongoDB mongoDB;

    public F.Promise<Result> index() {
        Promise<List<Item>> promise = Futures.promise();

        FindIterable<Item> itemsIterable = mongoDB.getDatabase().getCollection("items", Item.class).find();

        itemsIterable.into(new ArrayList<>(), (items, throwable) -> promise.success(items));

        return F.Promise.wrap(promise.future())
                .map((F.Function<List<Item>, Result>) items -> ok(index.render(items)))
                .recover(throwable -> internalServerError(throwable.getMessage()));
    }
}
