package controllers;

import com.google.inject.Inject;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import views.html.uploadForm;
import views.html.uploadResult;

import java.nio.file.Path;
import java.util.UUID;

public class Storage extends Controller {
    @Inject
    public modules.storage.Storage storage;

    public Result uploadForm() {
        return ok(uploadForm.render());
    }

    public F.Promise<Result> upload() {
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart file = body.getFile("file");
        if (file != null) {
            String fileName = file.getFilename();
            String uuid = UUID.randomUUID().toString();
            return storage.store(file.getFile().toPath(), uuid).map((F.Function<Void, Result>) aVoid -> ok(uploadResult.render(uuid)));
        } else {
            return F.Promise.promise(Results::badRequest);
        }
    }

    public F.Promise<Result> download(String id) {
        return storage.retrieve(id).map((F.Function<Path, Result>) path -> ok(path.toFile()));
    }
}
