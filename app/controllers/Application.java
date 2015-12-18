package controllers;

import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.index;
import views.html.uploadForm;
import views.html.uploadResult;

public class Application extends Controller {

    public Result index() {
        return ok(index.render("Your new application is ready."));
    }

	public Result uploadForm() {
		return ok(uploadForm.render());
	}

	public Result upload() {
		Http.MultipartFormData body = request().body().asMultipartFormData();
		Http.MultipartFormData.FilePart file = body.getFile("file");
		if (file != null) {
			String fileName = file.getFilename();
            return ok(uploadResult.render(fileName));
        } else {
			return badRequest();
		}
	}
}
