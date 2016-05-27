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

import play.libs.F;
import play.mvc.Result;

import java.nio.file.Path;

public interface Storage {
    /**
     * Stores a file
     *
     * @param   file
     *          The file to store
     * @param   key
     *          A key for the stored file
     * @param   name
     *          A name for the stored file
     */
    F.Promise<Void> store (Path file, String key, String name);

    /**
     * Gets a download for a stored file
     *
     * @param   key
     *          The stored file key
     * @param   name
     *          The name for the file
     */
    F.Promise<Result> getDownload (String key, String name);

    /**
     * Deletes a stored file
     *
     * @param   key
     *          The stored file key
     * @param   name
     *          The stored file name
     */
    F.Promise<Void> delete (String key, String name);
}
