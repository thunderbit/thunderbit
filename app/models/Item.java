package models;

import fr.javatic.mongo.jacksonCodec.objectId.Id;

public class Item {
    @Id
    public String id;

    public String name;

    public String storageKey;
}