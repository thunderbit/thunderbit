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

import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.test.WithApplication;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static play.test.Helpers.running;

public class StorageModuleTest {
    @Test
    public void testLocalFilesystemStorageMapping () {
        Application application = new GuiceApplicationBuilder()
                .configure("storage.type", "local")
                .build();

        running (application, () -> {
            Storage instance = application.injector().instanceOf(Storage.class);
            assertThat("Storage has not been mapped", instance, notNullValue());
            assertThat("Storage has been mapped to a wrong type", instance instanceof LocalFilesystemStorage, is(true));
        });
    }

    @Test
    public void testAmazonS3StorageMapping () {
        Application application = new GuiceApplicationBuilder()
                .configure("storage.type", "s3")
                .configure("storage.s3.accesskey", "")
                .configure("storage.s3.secretkey", "")
                .configure("storage.s3.createBucket", false)
                .build();

        running (application, () -> {
            Storage instance = application.injector().instanceOf(Storage.class);
            assertThat("Storage has not been mapped", instance, notNullValue());
            assertThat("Storage has been mapped to a wrong type", instance instanceof AmazonS3Storage, is(true));
        });
    }

    @Test
    public void testMockStorageMapping () {
        Application application = new GuiceApplicationBuilder()
                .configure("storage.type", "mock")
                .build();

        running (application, () -> {
            Storage instance = application.injector().instanceOf(Storage.class);
            assertThat("Storage has not been mapped", instance, notNullValue());
            assertThat("Storage has been mapped to a wrong type", instance instanceof MockStorage, is(true));
        });
    }
}