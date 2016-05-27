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

package modules.security;

import be.objectify.deadbolt.core.models.Permission;
import be.objectify.deadbolt.core.models.Role;
import be.objectify.deadbolt.core.models.Subject;
import be.objectify.deadbolt.java.AbstractDeadboltHandler;
import be.objectify.deadbolt.java.DynamicResourceHandler;
import controllers.routes;
import play.libs.F.Promise;
import play.mvc.Http;
import play.mvc.Result;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class MyDeadboltHandler extends AbstractDeadboltHandler {

	@Override
	public Promise<Optional<Result>> beforeAuthCheck(final Http.Context context) {
        if (context.session().get("username") != null && !context.session().get("username").isEmpty()) {
			// user is logged in
			return Promise.pure(Optional.empty());
		} else {
			// user is not logged in
            return Promise.promise(() -> Optional.of(redirect(routes.Authentication.login())));
		}
	}

	@Override
	public Promise<Optional<Subject>> getSubject(final Http.Context context) {
        if (context.session().get("username") != null && !context.session().get("username").isEmpty()) {
            // return an anonymous user
            return Promise.pure(Optional.of(new Subject() {
                @Override
                public List<? extends Role> getRoles() {
                    return Collections.emptyList();
                }

                @Override
                public List<? extends Permission> getPermissions() {
                    return Collections.emptyList();
                }

                @Override
                public String getIdentifier() {
                    return context.session().get("username");
                }
            }));
        } else {
            return Promise.pure(Optional.empty());
        }
	}

	@Override
	public Promise<Optional<DynamicResourceHandler>> getDynamicResourceHandler(final Http.Context context) {
		return Promise.pure(Optional.empty());
	}

	@Override
	public Promise<Result> onAuthFailure(final Http.Context context, final String content) {
        if (context.session().get("username") != null && !context.session().get("username").isEmpty()) {
            return Promise.promise(() -> forbidden("Forbidden"));
        } else {
            return Promise.promise(() -> redirect(routes.Authentication.login()));
        }
	}
}
