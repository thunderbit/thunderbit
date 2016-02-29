package controllers;

import com.google.inject.Inject;
import models.Item;
import modules.services.api.IItemsService;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.error;
import views.html.index;

import java.util.List;

public class Application extends Controller {
    @Inject
    public IItemsService itemsService;

    public F.Promise<Result> index() {
        // Returns a promise of retrieving all items from the database
        return itemsService.findAll()
                .map((F.Function<List<Item>, Result>) items -> ok(index.render(items)))
                // If an error occurs when retrieving the items returns a 500 Result
                .recover(throwable -> internalServerError(error.render()));
    }
}
