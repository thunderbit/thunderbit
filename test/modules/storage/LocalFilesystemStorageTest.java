package modules.storage;

import com.google.common.collect.ImmutableMap;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import play.Configuration;
import play.inject.Injector;
import play.inject.guice.GuiceInjectorBuilder;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static play.inject.Bindings.bind;

public class LocalFilesystemStorageTest {
    private static Path storagePath;
    private static Injector injector;
    private static Storage storage;

    @BeforeClass
    public static void beforeClass() {
        storagePath = Paths.get("storageTest");
        Map<String, Object> configuration = ImmutableMap.of(
                "storage.local.path", storagePath.toString(),
                "storage.local.createPath", true
        );

        injector = new GuiceInjectorBuilder()
                .bindings(
                        bind(Storage.class).to(LocalFilesystemStorage.class),
                        bind(Configuration.class).toInstance(new Configuration(configuration))
                )
                .build();
    }

    @Before
    public void before () throws IOException {
        Files.walkFileTree(storagePath, new Deleter());
        storage = injector.instanceOf(Storage.class);
    }

    @After
    public void after () throws IOException {
        Files.walkFileTree(storagePath, new Deleter());
    }

    @Test()
    public void testFileIsStored () throws IOException {
        String id = UUID.randomUUID().toString();
        Path testFile = Files.createTempFile("testFile", "temp");
        storage.store(testFile, id, testFile.toFile().getName()).get(500);
        Path storedTestFile = storagePath.resolve(id).resolve(testFile.toFile().getName());
        assertThat(Files.exists(storedTestFile), is(true));
    }

    @Test
    public void testFileIsDeleted () throws IOException {
        Path keyPath = Files.createDirectory(storagePath.resolve(UUID.randomUUID().toString()));
        Path testFile = Files.createFile(keyPath.resolve(UUID.randomUUID().toString()));
        storage.delete(keyPath.getFileName().toString(), testFile.getFileName().toString()).get(500);
        assertThat(Files.notExists(testFile), is(true));
    }

    private class Deleter implements FileVisitor<Path> {
        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            Files.delete(file);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            Files.delete(dir);
            return FileVisitResult.CONTINUE;
        }
    }
}
