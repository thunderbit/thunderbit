/*
 * Copyright (C) 2016 Thunderbit team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package modules;

import com.google.common.collect.ImmutableMap;
import models.Item;
import models.Tag;
import modules.neo4j.controller.TagController;
import modules.neo4j.function.RelationsUpdater;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import play.Application;
import play.db.Database;
import play.db.Databases;
import play.db.evolutions.Evolutions;
import play.inject.guice.GuiceApplicationBuilder;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static play.test.Helpers.running;

public class UpdaterTest {
    private Database database;

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

        SessionFactory sessionFactory = new SessionFactory("modules.neo4j.domain");
        Session session = sessionFactory.openSession("http://localhost:7474");
        session.purgeDatabase();
    }

    /**
     * Tests the tags relation update
     */
    @Test
    public void testTagItemRelation() throws Exception {
        Application application = new GuiceApplicationBuilder()
                .build();

        running (application, () -> {
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

            RelationsUpdater relationsUpdater = application.injector().instanceOf(RelationsUpdater.class);
            relationsUpdater.update(Arrays.asList(t1, t2, t3));

            TagController tagController = application.injector().instanceOf(TagController.class);

            modules.neo4j.domain.Tag graphTag1 = tagController.findByTagId(t1.id);
            modules.neo4j.domain.Tag graphTag2 = tagController.findByTagId(t2.id);
            modules.neo4j.domain.Tag graphTag3 = tagController.findByTagId(t3.id);

            assertThat("Tag 1 has not been saved to graph", graphTag1, notNullValue());
            assertThat("Tag 2 has not been saved to graph", graphTag2, notNullValue());
            assertThat("Tag 3 has not been saved to graph", graphTag3, notNullValue());

            assertThat("Tag 1 has no relations", graphTag1.getTags().size(), equalTo(4));
            assertThat("Tag 2 has no relations", graphTag2.getTags().size(), equalTo(4));
            assertThat("Tag 3 has no relations", graphTag3.getTags().size(), equalTo(4));
        });
    }
}
