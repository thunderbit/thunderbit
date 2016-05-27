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

import com.avaje.ebean.Query;
import com.google.common.collect.ImmutableMap;
import models.Item;
import models.Tag;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import play.db.Database;
import play.db.Databases;
import play.db.evolutions.Evolutions;

import java.util.*;

import static org.junit.Assert.*;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

public class DatabaseTest {
    private Database database;
    private static Map<String, Object> configOverrides;

    @BeforeClass
    public static void prepareConfigOverrides() {
        configOverrides = new HashMap<>();
        configOverrides.put("db.default.driver", "org.postgresql.Driver");
        configOverrides.put("db.default.url", "jdbc:postgresql://localhost/thunderbit");
        configOverrides.put("db.default.username", "postgres");
        configOverrides.put("db.default.password", "");
    }

    @Before
    public void applyEvolutions() {
        database = Databases.createFrom(
                "default",
                "org.postgresql.Driver",
                "jdbc:postgresql://localhost/thunderbit",
                ImmutableMap.of(
                        "username", "postgres",
                        "password", ""
                ));
        Evolutions.applyEvolutions(database);
    }

    @After
    public void cleanupEvolutions() {
        Evolutions.cleanupEvolutions(database);
        database.shutdown();
    }

    /**
     * Tests CRUD for items
     */
    @Test
    public void testCreateReadUpdateDeleteItem() throws Exception {
        running(fakeApplication(configOverrides), () -> {
            Item item = new Item();
            item.name = "someName";
            item.storageKey = "someStorageKey";
            item.fileSize = 100L;
            Date uploadDate = Calendar.getInstance().getTime();
            item.uploadDate = uploadDate;
            item.save();

            item = Item.find.byId(item.id);
            assertEquals("someName", item.name);
            assertEquals("someStorageKey", item.storageKey);
            assertEquals(100, item.fileSize.intValue());
            assertEquals(uploadDate, item.uploadDate);

            item.name = "someName2";
            item.storageKey = "someStorageKey2";
            item.fileSize = 200L;
            uploadDate = Calendar.getInstance().getTime();
            item.uploadDate = uploadDate;
            item.save();

            item = Item.find.byId(item.id);
            assertEquals("someName2", item.name);
            assertEquals("someStorageKey2", item.storageKey);
            assertEquals(200, item.fileSize.intValue());
            assertEquals(uploadDate, item.uploadDate);

            item.delete();
            assertNull(Item.find.byId(item.id));
        });
    }

    /**
     * Tests CRUD for tags
     */
    @Test
    public void testCreateReadUpdateDeleteTag() throws Exception {
        running(fakeApplication(configOverrides), () -> {
            Tag tag = new Tag();
            tag.name = "someName";
            tag.save();

            tag = Tag.find.byId(tag.id);
            assertEquals("someName", tag.name);

            tag.name = "someName2";
            tag.save();

            tag = Tag.find.byId(tag.id);
            assertEquals("someName2", tag.name);

            tag.delete();
            assertNull(Tag.find.byId(tag.id));
        });
    }

    /**
     * Tests the ManyToMany relation from items to tags
     */
    @Test
    public void testItemTagRelation() throws Exception {
        running(fakeApplication(configOverrides), () -> {
            Tag t1 = new Tag();
            t1.save();

            Tag t2 = new Tag();
            t2.save();

            Tag t3 = new Tag();
            t3.save();

            Item i1 = new Item();
            i1.getTags().add(t1);
            i1.getTags().add(t2);
            i1.save();

            Item i2 = new Item();
            i2.getTags().add(t2);
            i2.getTags().add(t3);
            i2.save();

            Item i3 = new Item();
            i3.getTags().add(t3);
            i3.getTags().add(t1);
            i3.save();

            Item retrievedItem1 = Item.find.byId(i1.id);
            assertEquals("Item 1 tags count is not 2", 2, retrievedItem1.getTags().size());
            assertTrue("Item 1 does not contain tag 1", retrievedItem1.getTags().contains(t1));
            assertTrue("Item 1 does not contain tag 2", retrievedItem1.getTags().contains(t2));

            Item retrievedItem2 = Item.find.byId(i2.id);
            assertEquals("Item 2 tags count is not 2", 2, retrievedItem2.getTags().size());
            assertTrue("Item 2 does not contain tag 2", retrievedItem2.getTags().contains(t2));
            assertTrue("Item 2 does not contain tag 3", retrievedItem2.getTags().contains(t3));

            Item retrievedItem3 = Item.find.byId(i3.id);
            assertEquals("Item 3 tags count is not 2", 2, retrievedItem3.getTags().size());
            assertTrue("Item 3 does not contain tag 3", retrievedItem3.getTags().contains(t3));
            assertTrue("Item 3 does not contain tag 1", retrievedItem3.getTags().contains(t1));
        });
    }

    /**
     * Tests the ManyToMany relation from tags to items
     */
    @Test
    public void testTagItemRelation() throws Exception {
        running(fakeApplication(configOverrides), () -> {
            Item i1 = new Item();
            i1.save();

            Item i2 = new Item();
            i2.save();

            Item i3 = new Item();
            i3.save();

            Tag t1 = new Tag();
            t1.getItems().add(i1);
            t1.getItems().add(i2);
            t1.save();

            Tag t2 = new Tag();
            t2.getItems().add(i2);
            t2.getItems().add(i3);
            t2.save();

            Tag t3 = new Tag();
            t3.getItems().add(i3);
            t3.getItems().add(i1);
            t3.save();

            Tag retrievedTag1 = Tag.find.byId(t1.id);
            assertEquals("Tag 1 items count is not 2", 2, retrievedTag1.getItems().size());
            assertTrue("Tag 1 does not contain item 1", retrievedTag1.getItems().contains(i1));
            assertTrue("Tag 1 does not contain item 2", retrievedTag1.getItems().contains(i2));

            Tag retrievedTag2 = Tag.find.byId(t2.id);
            assertEquals("Tag 2 items count is not 2", 2, retrievedTag2.getItems().size());
            assertTrue("Tag 2 does not contain item 2", retrievedTag2.getItems().contains(i2));
            assertTrue("Tag 2 does not contain item 3", retrievedTag2.getItems().contains(i3));

            Tag retrievedTag3 = Tag.find.byId(t3.id);
            assertEquals("Tag 3 items count is not 2", 2, retrievedTag3.getItems().size());
            assertTrue("Tag 3 does not contain item 3", retrievedTag3.getItems().contains(i3));
            assertTrue("Tag 3 does not contain item 1", retrievedTag3.getItems().contains(i1));
        });
    }

    /**
     * Tests items' search by tags
     */
    @Test
    public void testItemsSearchByTags() throws Exception {
        running(fakeApplication(configOverrides), () -> {
            Tag t1 = new Tag();
            t1.save();

            Tag t2 = new Tag();
            t2.save();

            Tag t3 = new Tag();
            t3.save();

            Item i1 = new Item();
            i1.getTags().add(t1);
            i1.getTags().add(t2);
            i1.save();

            Item i2 = new Item();
            i2.getTags().add(t2);
            i2.getTags().add(t3);
            i2.save();

            Item i3 = new Item();
            i3.getTags().add(t3);
            i3.getTags().add(t1);
            i3.save();

            String oql = "find item " +
                    "where tags.id in (:tagList) " +
                    "group by id " +
                    "having count(distinct tags.id) = :tagCount";

            // For one tag
            Query<Item> query = Item.find.setQuery(oql);
            List<Long> tagIds = Arrays.asList(t1.id);
            query.setParameter("tagList", tagIds);
            query.setParameter("tagCount", tagIds.size());
            List<Item> items = query.findList();
            assertEquals("Items list size for tag 1 is not 2", 2, items.size());
            assertTrue("Items list for tag 1 does not contain Item 1", items.contains(i1));
            assertTrue("Items list for tag 1 does not contain Item 3", items.contains(i3));

            // For one tag
            query = Item.find.setQuery(oql);
            tagIds = Arrays.asList(t2.id);
            query.setParameter("tagList", tagIds);
            query.setParameter("tagCount", tagIds.size());
            items = query.findList();
            assertEquals("Items list size for tag 2 is not 2", 2, items.size());
            assertTrue("Items list for tag 2 does not contain Item 1", items.contains(i1));
            assertTrue("Items list for tag 2 does not contain Item 2", items.contains(i2));

            // For one tag
            query = Item.find.setQuery(oql);
            tagIds = Arrays.asList(t3.id);
            query.setParameter("tagList", tagIds);
            query.setParameter("tagCount", tagIds.size());
            items = query.findList();
            assertEquals("Items list size for tag 3 is not 2", 2, items.size());
            assertTrue("Items list for tag 3 does not contain Item 2", items.contains(i2));
            assertTrue("Items list for tag 3 does not contain Item 3", items.contains(i3));

            // For two tags
            query = Item.find.setQuery(oql);
            tagIds = Arrays.asList(t1.id, t2.id);
            query.setParameter("tagList", tagIds);
            query.setParameter("tagCount", tagIds.size());
            items = query.findList();
            assertEquals("Items list size for tags 1 and 2 is not 1", 1, items.size());
            assertTrue("Items list for tags 1 and 2 does not contain Item 1", items.contains(i1));

            // For two tags
            query = Item.find.setQuery(oql);
            tagIds = Arrays.asList(t2.id, t3.id);
            query.setParameter("tagList", tagIds);
            query.setParameter("tagCount", tagIds.size());
            items = query.findList();
            assertEquals("Items list size for tags 2 and 3 is not 1", 1, items.size());
            assertTrue("Items list for tags 2 and 3 does not contain Item 2", items.contains(i2));

            // For two tags
            query = Item.find.setQuery(oql);
            tagIds = Arrays.asList(t3.id, t1.id);
            query.setParameter("tagList", tagIds);
            query.setParameter("tagCount", tagIds.size());
            items = query.findList();
            assertEquals("Items list size for tags 3 and 1 is not 1", 1, items.size());
            assertTrue("Items list for tags 3 and 1 does not contain Item 3", items.contains(i3));

            // For all tags
            query = Item.find.setQuery(oql);
            tagIds = Arrays.asList(t1.id, t2.id, t3.id);
            query.setParameter("tagList", tagIds);
            query.setParameter("tagCount", tagIds.size());
            items = query.findList();
            assertEquals("Items list size for tags 1, 2 and 3 is not 0", 0, items.size());
        });
    }
}
