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
import play.Play;
import play.libs.F;
import play.mvc.Result;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static play.mvc.Results.ok;

public class MockStorage implements Storage {
    private static final Logger.ALogger logger = Logger.of(MockStorage.class);

    private Path mockFilePath;

    @Inject
    public MockStorage(Configuration configuration) {
        mockFilePath = Paths.get(configuration.getString("storage.mock.mockFilePath", Play.application().getFile("mockStorage/mockFile").getAbsolutePath()));
        if (!Files.exists(mockFilePath)) logger.error("Mock file does not exist");
    }

    @Override
    public F.Promise<Void> store(Path file, String key, String name) {
        return F.Promise.pure(null);
    }

    @Override
    public F.Promise<Result> getDownload(String key, String name) {
        return F.Promise.pure(ok(mockFilePath.toFile()));
    }

    @Override
    public F.Promise<Void> delete(String key, String name) {
        return F.Promise.pure(null);
    }
}
