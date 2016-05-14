package controllers;

import flexjson.JSONSerializer;
import models.Tag;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.List;

public class Tags extends Controller {
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
