package modules.storage;

import com.google.inject.AbstractModule;
import play.Configuration;
import play.Environment;

public class StorageModule extends AbstractModule {
    private final Environment environment;
    private final Configuration configuration;

    public StorageModule(Environment environment, Configuration configuration) {
        this.environment = environment;
        this.configuration = configuration;
    }

    @Override
    protected void configure() {
        String storageType = configuration.getString("storage.type", "local");

        switch (storageType) {
            case "local" :
                bind (Storage.class).to(LocalFilesystemStorage.class);
                break;
            case "s3" :
                bind (Storage.class).to(AmazonS3Storage.class);
                break;
        }
    }
}
