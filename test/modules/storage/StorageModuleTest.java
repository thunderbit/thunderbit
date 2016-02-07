package modules.storage;

import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.test.WithApplication;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static play.test.Helpers.running;

public class StorageModuleTest extends WithApplication {
    @Test
    public void testLocalFilesystemStorageMapping () {
        Application application = new GuiceApplicationBuilder()
                .configure("storage.type", "local")
                .build();

        running (application, () -> {
            assertThat("LocalFilesystemStorage has not been mapped", application.injector().instanceOf(Storage.class) instanceof LocalFilesystemStorage, is(true));
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
            assertThat("AmazonS3Storage has not been mapped", application.injector().instanceOf(Storage.class) instanceof AmazonS3Storage, is(true));
        });
    }
}