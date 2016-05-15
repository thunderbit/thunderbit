package controllers;

import be.objectify.deadbolt.java.actions.SubjectPresent;
import com.avaje.ebean.Query;
import com.google.inject.Inject;
import flexjson.JSONSerializer;
import models.Item;
import play.data.Form;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.Arrays;
import java.util.List;

public class Items extends Controller {
    @Inject
    public modules.storage.Storage storage;

    @SubjectPresent
    public F.Promise<Result> delete(Long id) {
        Item item = Item.find.byId(id);
        if (item == null) {
            // If there is no item with the provided id returns a 404 Result
            return F.Promise.pure(notFound());
        } else {
            // Deletes the item from the database
            item.delete();
            // Returns a promise of deleting the stored file
            return storage.delete(item.storageKey, item.name)
                    .map((F.Function<Void, Result>) aVoid -> ok())
                    // If an error occurs when deleting the item returns a 500 Result
                    .recover(throwable -> internalServerError());
        }
    }

    public Result list() {
        String jointTagNames = Form.form().bindFromRequest().get("tags");

        // Fetch items from the database
        List<Item> items;
        if (jointTagNames != null && !jointTagNames.isEmpty()) {
            List<String> tagNames = Arrays.asList(jointTagNames.split(","));

            String oql = "find item " +
                    "where tags.name in (:tagList) " +
                    "group by id " +
                    "having count(distinct tags.name) = :tagCount";
            Query<Item> query = Item.find.setQuery(oql);
            query.setParameter("tagList", tagNames);
            query.setParameter("tagCount", tagNames.size());
            items = query.orderBy().desc("uploadDate").findList();
        } else {
            items = Item.find.orderBy().desc("uploadDate").findList();
        }

        if (items != null) {
            String serialized = new JSONSerializer()
                    .include("id")
                    .include("name")
                    .include("storageKey")
                    .include("tags.name")
                    .include("uploadDate")
                    .include("fileSize")
                    .exclude("*")
                    .serialize(items);
            return ok(serialized);
        } else return notFound();
    }
}
