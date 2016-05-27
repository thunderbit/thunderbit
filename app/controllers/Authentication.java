/*
 *     Thunderbit is a web application for digital assets management with emphasis on tags
 *     Copyright (C) 2016  thunderbit team
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
        flash("success", "youHaveBeenLoggedOut");
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
                return "invalidUsernameOrPassword";
            }
        }
    }
}
