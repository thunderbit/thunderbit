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
