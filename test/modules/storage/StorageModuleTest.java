package modules.storage;

import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.test.WithApplication;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static play.test.Helpers.running;

public class StorageModuleTest extends WithApplication {
    @Test
    public void testLocalFilesystemStorageMapping () {
        Application application = new GuiceApplicationBuilder()
                .configure("storage.type", "local")
                .build();

        running (application, () -> {
            Storage instance = application.injector().instanceOf(Storage.class);
            assertThat("Storage has not been mapped", instance, notNullValue());
            assertThat("Storage has been mapped to a wrong type", instance instanceof LocalFilesystemStorage, is(true));
        });
    }

    @Test
    public void testAmazonS3StorageMapping () {
        Application application = new GuiceApplicationBuilder()
                .configure("storage.type", "s3")
                .configure("storage.s3.accesskey", "")
                .configure("storage.s3.secretkey", "")
                .build();

        running (application, () -> {
            Storage instance = application.injector().instanceOf(Storage.class);
            assertThat("Storage has not been mapped", instance, notNullValue());
            assertThat("Storage has been mapped to a wrong type", instance instanceof AmazonS3Storage, is(true));
        });
    }
}