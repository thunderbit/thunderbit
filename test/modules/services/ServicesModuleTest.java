package modules.services;

import modules.services.api.IItemsService;
import modules.services.imp.ItemsService;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static play.test.Helpers.running;

public class ServicesModuleTest {
    @Test
    public void testItemsSericeMapping() throws Exception {
        Application application = new GuiceApplicationBuilder()
                .build();

        running (application, () -> {
            IItemsService instance = application.injector().instanceOf(IItemsService.class);
            assertThat("IItemsService has not been mapped", instance, notNullValue());
            assertThat("IItemsService has been mapped to a wrong type", instance instanceof ItemsService, is(true));
        });
    }
}