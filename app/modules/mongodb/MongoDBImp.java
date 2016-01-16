package modules.mongodb;

import com.mongodb.ConnectionString;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoDatabase;
import fr.javatic.mongo.jacksonCodec.JacksonCodecProvider;
import fr.javatic.mongo.jacksonCodec.ObjectMapperFactory;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import play.Configuration;
import play.inject.ApplicationLifecycle;
import play.libs.F;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MongoDBImp implements MongoDB {
    private MongoClient mongoClient;
    private MongoDatabase database;

    @Inject
    public MongoDBImp(Configuration configuration, ApplicationLifecycle lifecycle) {
        ConnectionString connectionString = new ConnectionString(configuration.getString("mongodb.uri", "mongodb://127.0.0.1:27017/test"));
        mongoClient = MongoClients.create(connectionString);

        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
                MongoClients.getDefaultCodecRegistry(), CodecRegistries.fromProviders(
                        new JacksonCodecProvider(ObjectMapperFactory.createObjectMapper())));
        database = mongoClient.getDatabase(connectionString.getDatabase()).withCodecRegistry(codecRegistry);

        lifecycle.addStopHook(() -> {
            mongoClient.close();
            return F.Promise.pure(null);
        });
    }

    @Override
    public MongoDatabase getDatabase() {
        return database;
    }
}