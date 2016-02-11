package modules.mongodb;

import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static play.test.Helpers.running;

public class MongoDBModuleTest {

    @Test
    public void testMongoDBMapping() throws Exception {
        Application application = new GuiceApplicationBuilder()
                .build();

        running (application, () -> {
            MongoDB instance = application.injector().instanceOf(MongoDB.class);
            assertThat("MongoDB has not been mapped", instance, notNullValue());
            assertThat("MongoDB has been mapped to a wrong type", instance instanceof MongoDBImp, is(true));
        });
    }
}