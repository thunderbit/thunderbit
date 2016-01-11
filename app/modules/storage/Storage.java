package modules.storage;

import play.libs.F;

import java.nio.file.Path;

public interface Storage {
    /**
     * Guardar un fichero
     *
     * @param   file
     *          El fichero a guardar
     * @param   key
     *          El identificador que se le va a asignar
     */
    F.Promise<Void> store (Path file, String key);

    /**
     * Recuperar un fichero
     *
     * @param   key
     *          El identificador del fichero
     */
    F.Promise<Path> retrieve (String key);

    /**
     * Eliminar un fichero
     *
     * @param   key
     *          El identificador del fichero
     */
    F.Promise<Void> delete (String key);
}
