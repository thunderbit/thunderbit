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

package controllers;

import be.objectify.deadbolt.java.actions.SubjectPresent;
import com.google.inject.Inject;
import models.Item;
import models.Tag;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.error;

import java.util.*;

public class Storage extends Controller {
    @Inject
    public modules.storage.Storage storage;

    private final String[] EMPTY_ARRAY = {};

    @SubjectPresent
    public F.Promise<Result> upload() {
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart file = body.getFile("file");
        List<String> tags = Arrays.asList(body.asFormUrlEncoded().getOrDefault("tags", EMPTY_ARRAY));

        if (file != null) {
            String fileName = file.getFilename();
            String uuid = UUID.randomUUID().toString();
            Date uploadDate = Calendar.getInstance().getTime();
            Long fileSize = file.getFile().length();

            // Returns a promise of storing the file
            return storage.store(file.getFile().toPath(), uuid, file.getFilename())
                    // If the file storage is successful saves the Item to the database
                    .map(aVoid -> {
                        // Get the list of Tag entities for the new Item
                        List<Tag> tagsList = new ArrayList<>();
                        for (String tagName : tags) {
                            Tag tag = Tag.find.where().eq("name", tagName.toLowerCase()).findUnique();
                            // Create new tags if they doesn't already exist
                            if (tag == null) {
                                tag = new Tag();
                                tag.name = tagName.toLowerCase();
                                tag.save();
                            }
                            tagsList.add(tag);
                        }

                        Item item = new Item();
                        item.name = fileName;
                        item.storageKey = uuid;
                        item.uploadDate = uploadDate;
                        item.fileSize = fileSize;
                        item.setTags(tagsList);
                        item.save();

                        return ok();
                    });
        } else {
            return F.Promise.pure(badRequest());
        }
    }

    public F.Promise<Result> download(Long id) {
        // Retrieves the item from the database
        Item item = Item.find.byId(id);
        if (item == null) {
            // If there is no item with the provided id returns a 404 Result
            return F.Promise.pure(notFound());
        } else {
            // Returns a promise of retrieving a download URL for the stored file
            return storage.getDownload(item.storageKey, item.name)
                    // If an error occurs when retrieving the download URL returns a 500 Result
                    .recover(throwable -> internalServerError(error.render()));
        }
    }
}
