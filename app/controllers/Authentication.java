package controllers;

import play.Play;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.authentication.login;

public class Authentication extends Controller {
    public Result login () {
        return ok(login.render(Form.form(Login.class)));
    }

    public Result doLogin() {
        Form<Login> loginForm = Form.form(Login.class).bindFromRequest();
        if (loginForm.hasErrors()) {
            return badRequest(login.render(loginForm));
        } else {
            session().clear();
            session("username", loginForm.get().name);
            return redirect(routes.Application.index());
        }
    }

    public Result logout () {
        session().clear();
        flash("success", "You've been logged out");
        return redirect(routes.Authentication.login());
    }

    public static class Login {
        public String name;
        public String password;

        public String validate() {
            String authenticationUsername = Play.application().configuration().getString("authentication.username");
            String authenticationPassword = Play.application().configuration().getString("authentication.password");

            if (name.equals(authenticationUsername) && password.equals(authenticationPassword)) {
                return null;
            } else {
                return "Invalid user or password";
            }
        }
    }
}
