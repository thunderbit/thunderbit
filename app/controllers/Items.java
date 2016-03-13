package controllers;

import be.objectify.deadbolt.java.actions.SubjectPresent;
import com.google.inject.Inject;
import flexjson.JSONSerializer;
import models.Item;
import modules.services.api.IItemsService;
import play.data.Form;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.Arrays;
import java.util.List;

public class Items extends Controller {
    @Inject
    public IItemsService itemsService;

    @Inject
    public modules.storage.Storage storage;

    @SubjectPresent
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
        String jointTags = Form.form().bindFromRequest().get("tags");

        // Get a promise of fetching items from the database
        F.Promise<List<Item>> itemsPromise;
        if (jointTags != null && !jointTags.isEmpty()) {
            List<String> tags = Arrays.asList(jointTags.split(","));
            itemsPromise = itemsService.findTagged(tags);
        } else {
            itemsPromise = itemsService.findAll();
        }

        // Returns the items fetching promise
        return itemsPromise
                .map(items -> {
                    if (items != null) {
                        String serialized = new JSONSerializer()
                                .include("id")
                                .include("name")
                                .include("storageKey")
                                .include("tags")
                                .exclude("*")
                                .serialize(items);
                        return ok(serialized);
                    } else return notFound();
                })
                .recover(throwable -> internalServerError());
    }
}
