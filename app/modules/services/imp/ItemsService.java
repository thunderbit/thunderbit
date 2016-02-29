package modules.services.imp;

import akka.dispatch.Futures;
import com.google.inject.Inject;
import models.Item;
import modules.mongodb.MongoDB;
import modules.services.api.IItemsService;
import org.bson.types.ObjectId;
import play.Logger;
import play.Logger.ALogger;
import play.libs.F;
import scala.concurrent.Promise;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class ItemsService implements IItemsService {
    @Inject
    public MongoDB mongoDB;

    private static final ALogger logger = Logger.of(ItemsService.class);

    public F.Promise<Item> create (String name, String storageKey) {
        Promise<Item> promise = Futures.promise();

        Item item = new Item();
        item.name = name;
        item.storageKey = storageKey;

        mongoDB.getDatabase().getCollection("items", Item.class)
                .insertOne(item, (aVoid, throwable) -> {
                    if (throwable == null) {
                        promise.success(item);
                    } else {
                        logger.error("Could not create item in database", throwable);
                        promise.failure(throwable);
                    }
                });

        return F.Promise.wrap(promise.future());
    }

    public F.Promise<Item> read (String id) {
        Promise<Item> promise = Futures.promise();

        mongoDB.getDatabase().getCollection("items", Item.class)
                .find().filter(eq("_id", new ObjectId(id)))
                .first((item, throwable) -> {
                    if (throwable == null) {
                        promise.success(item);
                    } else {
                        logger.error("Could not read item from database", throwable);
                        promise.failure(throwable);
                    }
                });

        return F.Promise.wrap(promise.future());
    }

    public F.Promise<Item> delete (String id) {
        Promise<Item> promise = Futures.promise();

        mongoDB.getDatabase().getCollection("items", Item.class)
                .findOneAndDelete(eq("_id", new ObjectId(id)), (item, throwable) -> {
                    if (throwable == null) {
                        promise.success(item);
                    } else {
                        logger.error("Could not delete item from database", throwable);
                        promise.failure(throwable);
                    }
                });

        return F.Promise.wrap(promise.future());
    }

    public F.Promise<List<Item>> findAll () {
        Promise<List<Item>> promise = Futures.promise();

        mongoDB.getDatabase().getCollection("items", Item.class)
                .find().into(new ArrayList<>(), (items, throwable) -> {
            if (throwable == null) {
                promise.success(items);
            } else {
                logger.error("Could not read items from database", throwable);
                promise.failure(throwable);
            }
        });

        return F.Promise.wrap(promise.future());
    }
}
