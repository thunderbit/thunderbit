package models;

import com.avaje.ebean.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Item extends Model {
    @Id
    public Long id;

    public String name;

    public String storageKey;

    public Date uploadDate;

    public Long fileSize;

    @ManyToMany(mappedBy = "items", cascade = CascadeType.ALL)
    public List<Tag> tags;

    public Item () {
        tags = new ArrayList<>();
    }

    public static Finder<Long,Item> find = new Finder<Long,Item>(
            "default", Item.class
    );

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}