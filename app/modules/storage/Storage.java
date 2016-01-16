package modules.storage;

import play.libs.F;

import java.nio.file.Path;

public interface Storage {
    /**
     * Stores a file
     *
     * @param   file
     *          The file to store
     * @param   key
     *          A key for the stored file
     */
    F.Promise<Void> store (Path file, String key);

    /**
     * Retrieves a stored file
     *
     * @param   key
     *          The stored file key
     */
    F.Promise<Path> retrieve (String key);

    /**
     * Deletes a stored file
     *
     * @param   key
     *          The stored file key
     */
    F.Promise<Void> delete (String key);
}
