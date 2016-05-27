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
            case "mock" :
                bind (Storage.class).to(MockStorage.class);
                break;
            case "local" :
                bind (Storage.class).to(LocalFilesystemStorage.class);
                break;
            case "s3" :
                bind (Storage.class).to(AmazonS3Storage.class);
                break;
        }
    }
}
