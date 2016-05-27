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

import flexjson.JSONSerializer;
import models.Tag;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.List;

public class Tags extends Controller {
    /**
     * Finds a tag by it's name
     *
     * @param   name
     *          The name or part of it
     */
    public Result findByName(String name) {
        List<Tag> tags = Tag.find.where().ilike("name", "%" + name.trim() + "%").findList();
        String serialized = new JSONSerializer()
                .include("id")
                .include("name")
                .exclude("*")
                .serialize(tags);
        return ok(serialized);
    }
}
