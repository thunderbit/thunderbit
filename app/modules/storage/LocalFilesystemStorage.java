/*
 *     Thunderbit is a web application for digital assets management with emphasis on tags
 *     Copyright (C) 2016  thunderbit team
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package modules.storage;

import com.google.inject.Inject;
import play.Configuration;
import play.Logger;
import play.libs.F;
import play.mvc.Result;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static play.mvc.Results.ok;

public class LocalFilesystemStorage implements Storage {
    private static final Logger.ALogger logger = Logger.of(LocalFilesystemStorage.class);

    private Path storagePath;

    @Inject
    public LocalFilesystemStorage (Configuration configuration) {
        storagePath = Paths.get(configuration.getString("storage.local.path", "storage"));

        if (configuration.getBoolean("storage.local.createPath", true)) {
            if (!Files.exists(storagePath)) try {
                Files.createDirectories(storagePath);
            } catch (IOException e) {
                logger.error("Could not create storage directory", e);
            }
        }
    }

    @Override
    public F.Promise<Void> store(Path file, String key, String name) {
        F.Promise<Void> promise = F.Promise.promise(() -> {
            Path keyPath = Files.createDirectory(storagePath.resolve(key));
            Files.copy(file, keyPath.resolve(name));
            return null;
        });
        promise.onFailure(throwable -> logger.error("Could not store file", throwable));
        return promise;
    }

    @Override
    public F.Promise<Result> getDownload(String key, String name) {
        return F.Promise.pure(ok(storagePath.resolve(key).resolve(name).toFile()));
    }

    @Override
    public F.Promise<Void> delete(String key, String name) {
        F.Promise<Void> promise = F.Promise.promise(() -> {
            Files.delete(storagePath.resolve(key).resolve(name));
            Files.delete(storagePath.resolve(key));
            return null;
        });
        promise.onFailure(throwable -> logger.error("Could not delete file", throwable));
        return promise;
    }
}
