package modules.mongodb;

import com.mongodb.ConnectionString;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClientSettings;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.connection.*;
import fr.javatic.mongo.jacksonCodec.ObjectCodecProvider;
import fr.javatic.mongo.jacksonCodec.ObjectMapperFactory;
import org.bson.codecs.BsonValueCodecProvider;
import org.bson.codecs.DocumentCodecProvider;
import org.bson.codecs.ValueCodecProvider;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import play.Configuration;
import play.inject.ApplicationLifecycle;
import play.libs.F;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;

@Singleton
public class MongoDB {
    private MongoClient mongoClient;
    private MongoDatabase database;

    @Inject
    public MongoDB(Configuration configuration, ApplicationLifecycle lifecycle) {
        ConnectionString connectionString = new ConnectionString(configuration.getString("mongodb.uri", "mongodb://127.0.0.1:27017/test"));

        CodecRegistry defaultCodecRegistry = CodecRegistries.fromProviders(Arrays.asList(new CodecProvider[]{
                        new ValueCodecProvider(),
                        new DocumentCodecProvider(),
                        new BsonValueCodecProvider()
                }));

        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
                defaultCodecRegistry, CodecRegistries.fromProviders(
                        new ObjectCodecProvider(ObjectMapperFactory.createObjectMapper())));

        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .clusterSettings(ClusterSettings.builder().applyConnectionString(connectionString).build())
                .connectionPoolSettings(ConnectionPoolSettings.builder().applyConnectionString(connectionString).build())
                .serverSettings(ServerSettings.builder().build()).credentialList(connectionString.getCredentialList())
                .sslSettings(SslSettings.builder().applyConnectionString(connectionString).build())
                .socketSettings(SocketSettings.builder().applyConnectionString(connectionString).build())
                .codecRegistry(codecRegistry)
                .build();

        mongoClient = MongoClients.create(clientSettings);

        database = mongoClient.getDatabase(connectionString.getDatabase());

        lifecycle.addStopHook(() -> {
            mongoClient.close();
            return F.Promise.pure(null);
        });
    }

    public MongoDatabase getDatabase() {
        return database;
    }
}