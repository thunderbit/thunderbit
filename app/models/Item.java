package models;

import fr.javatic.mongo.jacksonCodec.objectId.Id;

import java.util.ArrayList;
import java.util.List;

public class Item {
    @Id
    public String id;

    public String name;

    public String storageKey;

    public List<String> tags = new ArrayList<>();
}