package controllers;

import com.google.inject.Inject;
import flexjson.JSONSerializer;
import modules.services.api.IItemsService;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Result;

public class Items extends Controller {
    @Inject
    public IItemsService itemsService;

    @Inject
    public modules.storage.Storage storage;

    public F.Promise<Result> delete(String id) {
        // Returns a promise of deleting an item from the database
        return itemsService.delete(id)
                .flatMap(item -> {
                    if (item == null) {
                        // If there is no item with the provided id returns a 404 Result
                        return F.Promise.pure(notFound());
                    } else {
                        // Returns a promise of deleting the stored file
                        return storage.delete(item.storageKey, item.name)
                                .map((F.Function<Void, Result>) aVoid -> ok())
                                // If an error occurs when deleting the item returns a 500 Result
                                .recover(throwable -> internalServerError());
                    }
                })
                // If an error occurs when deleting the item returns a 500 Result
                .recover(throwable -> internalServerError());
    }

    public F.Promise<Result> list() {
        // Returns a promise of fetching all items from the database
        return itemsService.findAll()
                .map(items -> {
                    if (items != null) {
                        String serialized = new JSONSerializer()
                                .include("id")
                                .include("name")
                                .include("storageKey")
                                .exclude("*")
                                .serialize(items);
                        return ok(serialized);
                    } else return notFound();
                })
                .recover(throwable -> internalServerError());
    }
}
