package models;

import com.avaje.ebean.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Tag extends Model {
    @Id
    public Long id;

    public String name;

    @ManyToMany(cascade = CascadeType.ALL)
    public List<Item> items;

    public Tag () {
        items = new ArrayList<>();
    }

    public static Finder<Long,Tag> find = new Finder<Long,Tag>(
            "default", Tag.class
    );

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}
