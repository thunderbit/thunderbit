package controllers;

import be.objectify.deadbolt.java.actions.SubjectPresent;
import com.google.inject.Inject;
import models.Item;
import modules.services.api.IItemsService;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.uploadForm;
import views.html.uploadResult;

import java.util.UUID;

public class Storage extends Controller {
    @Inject
    public modules.storage.Storage storage;

    @Inject
    public IItemsService itemsService;

    @SubjectPresent
    public Result uploadForm() {
        return ok(uploadForm.render());
    }

    @SubjectPresent
    public F.Promise<Result> upload() {
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart file = body.getFile("file");

        if (file != null) {
            String fileName = file.getFilename();
            String uuid = UUID.randomUUID().toString();

            // Returns a promise of storing the the file
            return storage.store(file.getFile().toPath(), uuid, file.getFilename())
                    // If the file storage is successful returns a promise of the database entity save
                    .flatMap(aVoid -> itemsService.create(fileName, uuid)
                            // If the database entity save is successful returns a 200 Result
                            .map((F.Function<Item, Result>) item -> ok(uploadResult.render(item.name)))
                            // If the database entity save is not successful returns a 500 Result
                            .recover(throwable -> internalServerError(throwable.getMessage()))
                    // If the file storage fails returns a 500 Result
                    .recover(throwable -> internalServerError(throwable.getMessage())));
        } else {
            return F.Promise.pure(badRequest());
        }
    }

    public F.Promise<Result> download(String id) {
        // Returns a promise of retrieving an item from the database
        return itemsService.read(id)
                .flatMap(item -> {
                    if (item == null) {
                        // If there is no item with the provided id returns a 404 Result
                        return F.Promise.pure(notFound());
                    } else {
                        // Returns a promise of retrieving a download URL for the stored file
                        return storage.getDownload(item.storageKey, item.name)
                                // If an error occurs when retrieving the download URL returns a 500 Result
                                .recover(throwable -> internalServerError(throwable.getMessage()));
                    }
                })
                // If an error occurs when retrieving the item returns a 500 Result
                .recover(throwable -> internalServerError(throwable.getMessage()));
    }

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
                                .map(aVoid -> redirect(routes.Application.index()))
                                // If an error occurs when retrieving the item returns a 500 Result
                                .recover(throwable -> internalServerError(throwable.getMessage()));
                    }
                })
                // If an error occurs when retrieving the item returns a 500 Result
                .recover(throwable -> internalServerError(throwable.getMessage()));
    }
}
