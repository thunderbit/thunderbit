package modules.neo4j;

import modules.neo4j.controller.TagController;
import modules.neo4j.controller.TagControllerImpl;
import modules.neo4j.domain.Relation;
import modules.neo4j.domain.Tag;
import modules.neo4j.function.Matcher;
import modules.neo4j.function.MatcherImpl;
import modules.neo4j.function.Recommender;
import modules.neo4j.function.RecommenderImpl;
import modules.neo4j.service.TagService;
import modules.neo4j.service.TagServiceImpl;
import modules.neo4j.session.Neo4JSessionProvider;
import modules.neo4j.session.Neo4JSessionProviderImpl;
import org.junit.After;
import org.junit.Test;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static play.test.Helpers.running;

public class Ne4JModuleTest {
    @After
    public void tearDown () {
        SessionFactory sessionFactory = new SessionFactory("modules.neo4j.domain");
        Session session = sessionFactory.openSession("http://localhost:7474");
        session.purgeDatabase();
    }

    @Test
    public void testMappings () {
        Application application = new GuiceApplicationBuilder()
                .build();

        running (application, () -> {
            Neo4JSessionProvider sessionProvider = application.injector().instanceOf(Neo4JSessionProvider.class);
            assertThat("Neo4JSessionProvider has not been mapped", sessionProvider, notNullValue());
            assertThat("Neo4JSessionProvider has been mapped to a wrong type", sessionProvider instanceof Neo4JSessionProviderImpl, is(true));

            TagService tagService = application.injector().instanceOf(TagService.class);
            assertThat("TagService has not been mapped", tagService, notNullValue());
            assertThat("TagService has been mapped to a wrong type", tagService instanceof TagServiceImpl, is(true));

            TagController tagController = application.injector().instanceOf(TagController.class);
            assertThat("TagController has not been mapped", tagController, notNullValue());
            assertThat("TagController has been mapped to a wrong type", tagController instanceof TagControllerImpl, is(true));

            Recommender recommender = application.injector().instanceOf(Recommender.class);
            assertThat("Recommender has not been mapped", recommender, notNullValue());
            assertThat("Recommender has been mapped to a wrong type", recommender instanceof RecommenderImpl, is(true));

            Matcher matcher = application.injector().instanceOf(Matcher.class);
            assertThat("Matcher has not been mapped", matcher, notNullValue());
            assertThat("Matcher has been mapped to a wrong type", matcher instanceof MatcherImpl, is(true));
        });
    }

    @Test
    public void testDatabase () {
        Application application = new GuiceApplicationBuilder()
                .build();

        running (application, () -> {
            Tag t1 = new Tag();
            t1.setName("t1");
            t1.setTagId(1L);
            Tag t2 = new Tag();
            t2.setName("t2");
            t2.setTagId(2L);
            Tag t3 = new Tag();
            t3.setName("t3");
            t3.setTagId(3L);
            Tag t4 = new Tag();
            t4.setName("t4");
            t4.setTagId(4L);
            Tag t5 = new Tag();
            t5.setName("t5");
            t5.setTagId(5L);
            Tag t6 = new Tag();
            t6.setName("t6");
            t6.setTagId(6L);
            Tag t7 = new Tag();
            t7.setName("t7");
            t7.setTagId(7L);

            TagController tagController = application.injector().instanceOf(TagController.class);

            t5.addRelation(new Relation().setNewRelation(t5, t1, 7));
            t5.addRelation(new Relation().setNewRelation(t5, t4, 4));
            t5.addRelation(new Relation().setNewRelation(t5, t6, 1));
            t5.addRelation(new Relation().setNewRelation(t5, t2, 5));
            t5.addRelation(new Relation().setNewRelation(t5, t3, 2));
            tagController.create(t5);

            t2.addRelation(new Relation().setNewRelation(t2, t1, 4));
            tagController.create(t2);

            t7.addRelation(new Relation().setNewRelation(t7, t5, 7));
            tagController.create(t7);

            t4.addRelation(new Relation().setNewRelation(t4, t2, 8));
            tagController.create(t4);

            t3.addRelation(new Relation().setNewRelation(t1, t3, 4));
            t3.addRelation(new Relation().setNewRelation(t2, t3, 5));
            tagController.create(t3);

            t6.addRelation(new Relation().setNewRelation(t3, t6, 1));
            tagController.create(t6);

            assertThat("Items has not been saved", tagController.list().size(), equalTo(7));
            assertThat("Items has not been saved", tagController.list(), hasItems(t1, t2, t3, t4, t5, t6, t7));

            Matcher m = application.injector().instanceOf(Matcher.class);

            assertThat("Relations has not been saved", m.matchTagsString("t1", "t3").size(), equalTo(2));
            assertThat("Relations has not been saved", m.matchTagsString("t1", "t3"), hasItems("t5", "t2"));

            Recommender r = application.injector().instanceOf(Recommender.class);

            if (r.recommends(t1.getName(), t4.getName(), t3.getName())) {
                r.getrTag().forEach(t -> System.out.println(t.getName()));
            } else {
                r.getrTags().forEach((l1) -> {
                    l1.forEach(t -> System.out.println(t.getName()));
                });
            }
        });
    }
}