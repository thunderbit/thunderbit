package modules.storage;

import com.google.inject.Inject;
import play.Configuration;
import play.libs.F;
import play.mvc.Result;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static play.mvc.Results.ok;

public class LocalFilesystemStorage implements Storage {
    private Path storagePath;

    @Inject
    public LocalFilesystemStorage (Configuration configuration) {
        storagePath = Paths.get(configuration.getString("storage.local.path", "storage"));

        if (!Files.exists(storagePath)) try {
            Files.createDirectories(storagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public F.Promise<Void> store(Path file, String key, String name) {
        return F.Promise.promise(() -> {
            Path keyPath = Files.createDirectory(storagePath.resolve(key));
            Files.copy(file, keyPath.resolve(name));
            return null;
        });
    }

    @Override
    public F.Promise<Result> getDownload(String key, String name) {
        return F.Promise.pure(ok(storagePath.resolve(key).resolve(name).toFile()));
    }

    @Override
    public F.Promise<Void> delete(String key) {
        return F.Promise.promise(() -> {
            Files.delete(storagePath.resolve(key));
            return null;
        });
    }
}
