package modules.storage;

import org.junit.Test;
import org.junit.BeforeClass;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.test.WithApplication;
import play.Play;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static play.test.Helpers.running;
import static play.test.Helpers.fakeApplication;

public class StorageModuleTest extends WithApplication {
    private static Map<String, Object> configOverrides;

    @BeforeClass
    public static void prepareConfigOverrides() {
        configOverrides = new HashMap<>();
        configOverrides.put("storage.type", "local");
    }

    @Test
    public void testLocalFilesystemStorageMapping () {
        running (fakeApplication(configOverrides), () -> {
            Storage instance = Play.application().injector().instanceOf(Storage.class);
            assertThat("Storage has not been mapped", instance, notNullValue());
            assertThat("Storage has been mapped to a wrong type", instance instanceof LocalFilesystemStorage, is(true));
        });
    }
}