package modules.neo4j;

import modules.neo4j.controller.TagController;
import modules.neo4j.controller.TagControllerImpl;
import modules.neo4j.function.Matcher;
import modules.neo4j.function.MatcherImpl;
import modules.neo4j.function.Recommender;
import modules.neo4j.function.RecommenderImpl;
import modules.neo4j.service.TagService;
import modules.neo4j.service.TagServiceImpl;
import modules.neo4j.session.Neo4JSessionProvider;
import modules.neo4j.session.Neo4JSessionProviderImpl;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static play.test.Helpers.running;

public class Ne4JModuleTest {
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
}
