package modules.security;

import be.objectify.deadbolt.java.DeadboltHandler;
import be.objectify.deadbolt.java.cache.HandlerCache;

import javax.inject.Singleton;

@Singleton
public class MyHandlerCache implements HandlerCache {
	
	private final DeadboltHandler defaultHandler = new MyDeadboltHandler();

	@Override
	public DeadboltHandler apply(final String key) {
		return this.defaultHandler;
	}

	@Override
	public DeadboltHandler get() {
		return this.defaultHandler;
	}
}