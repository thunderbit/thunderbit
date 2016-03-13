package modules.services;

import com.google.common.collect.ImmutableMap;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import fr.javatic.mongo.jacksonCodec.JacksonCodecProvider;
import fr.javatic.mongo.jacksonCodec.ObjectMapperFactory;
import models.Item;
import modules.mongodb.MongoDBModule;
import modules.services.api.IItemsService;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.types.ObjectId;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import play.Configuration;
import play.inject.ApplicationLifecycle;
import play.inject.Injector;
import play.inject.guice.GuiceInjectorBuilder;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.mongodb.client.model.Filters.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static play.inject.Bindings.bind;

public class ItemsServiceTest {
    private static MongoClient mongoClient;
    private static MongoDatabase database;
    private static IItemsService itemsService;

    @BeforeClass
    public static void setupClass() {
        Map<String, Object> configuration = ImmutableMap.of(
                "mongodb.uri", "mongodb://127.0.0.1:27017/test"
        );

        Injector injector = new GuiceInjectorBuilder()
                .bindings(bind(Configuration.class).toInstance(new Configuration(configuration)))
                .bindings(bind(ApplicationLifecycle.class).toInstance(callable -> {}))
                .bindings(new MongoDBModule())
                .bindings(new ServicesModule())
                .injector();

        itemsService = injector.instanceOf(IItemsService.class);

        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(MongoClient.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(new JacksonCodecProvider(ObjectMapperFactory.createObjectMapper())));

        MongoClientOptions clientOptions = MongoClientOptions.builder()
                .codecRegistry(codecRegistry)
                .build();

        mongoClient = new MongoClient(new ServerAddress ("127.0.0.1:27017"), clientOptions);
        database = mongoClient.getDatabase("test");
    }

    @AfterClass
    public static void teardownClass() {
        database.drop();
        mongoClient.close();
    }

    @Before
    public void setup() {
        database.drop();
    }

    @Test
    public void testCreate () {
        itemsService.create("someItemName", "someItemStorageKey", Arrays.asList("a", "b", "c")).get(5, TimeUnit.SECONDS);
        assertThat ("Item was not created", database.getCollection("items", Item.class).count(and(eq("name", "someItemName"), eq("storageKey", "someItemStorageKey"), size("tags", 3), all("tags", Arrays.asList("a", "b", "c")))), is(new Long(1)));
    }

    @Test
    public void testRead () {
        Document document = new Document("name", "someItemName");
        database.getCollection("items").insertOne(document);
        ObjectId id = (ObjectId) document.get("_id");
        assertThat ("Item was not read", itemsService.read(id.toString()).get(5, TimeUnit.SECONDS), notNullValue());
    }

    @Test
    public void testDelete () {
        Document document = new Document("name", "someItemName");
        database.getCollection("items").insertOne(document);
        ObjectId id = (ObjectId) document.get("_id");
        itemsService.delete(id.toString()).get(5, TimeUnit.SECONDS);
        assertThat ("Item was not deleted", database.getCollection("items", Item.class).count(), is(new Long(0)));
    }

    @Test
    public void testFindTagged () {
        itemsService.create("item1", "item1StorageKey", Arrays.asList("a", "b", "c")).get(5, TimeUnit.SECONDS);
        itemsService.create("item2", "item2StorageKey", Arrays.asList("b", "c", "d")).get(5, TimeUnit.SECONDS);
        assertThat ("Tagged item not found", itemsService.findTagged(Arrays.asList("a", "b")).get(5, TimeUnit.SECONDS).size(), is(1));
        assertThat ("Tagged items not found", itemsService.findTagged(Arrays.asList("b", "c")).get(5, TimeUnit.SECONDS).size(), is(2));
        assertThat ("Tagged item not found", itemsService.findTagged(Arrays.asList("c", "d")).get(5, TimeUnit.SECONDS).size(), is(1));
        assertThat ("Items found where there should be none", itemsService.findTagged(Arrays.asList("a", "d")).get(5, TimeUnit.SECONDS).size(), is(0));
    }
}