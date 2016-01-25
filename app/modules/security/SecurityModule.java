package modules.security;

import be.objectify.deadbolt.java.cache.HandlerCache;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class SecurityModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(HandlerCache.class).to(MyHandlerCache.class).in(Singleton.class);
    }
}
