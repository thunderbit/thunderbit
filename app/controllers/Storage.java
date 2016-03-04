package controllers;

import be.objectify.deadbolt.java.actions.SubjectPresent;
import com.google.inject.Inject;
import modules.services.api.IItemsService;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.error;

import java.util.UUID;

public class Storage extends Controller {
    @Inject
    public modules.storage.Storage storage;

    @Inject
    public IItemsService itemsService;

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
                            .map(item -> redirect(routes.Application.index()))
                            // If the database entity save is not successful returns a 500 Result
                            .recover(throwable -> internalServerError(error.render()))
                    // If the file storage fails returns a 500 Result
                    .recover(throwable -> internalServerError(error.render())));
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
                                .recover(throwable -> internalServerError(error.render()));
                    }
                })
                // If an error occurs when retrieving the item returns a 500 Result
                .recover(throwable -> internalServerError(error.render()));
    }
}
