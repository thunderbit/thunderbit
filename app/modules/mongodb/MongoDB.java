package modules.mongodb;

import com.mongodb.async.client.MongoDatabase;

public interface MongoDB {
    MongoDatabase getDatabase();
}