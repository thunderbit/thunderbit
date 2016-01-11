package modules.mongodb;

import com.google.inject.AbstractModule;

public class MongoDBModule extends AbstractModule {
    @Override
    protected void configure() {
        bind (MongoDB.class).to(MongoDBImp.class).asEagerSingleton();
    }
}