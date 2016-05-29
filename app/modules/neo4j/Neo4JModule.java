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

package modules.neo4j;

import com.google.inject.AbstractModule;
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

public class Neo4JModule  extends AbstractModule {
    @Override
    protected void configure() {
        bind(Neo4JSessionProvider.class).to(Neo4JSessionProviderImpl.class).asEagerSingleton();
        bind(TagService.class).to(TagServiceImpl.class);
        bind(TagController.class).to(TagControllerImpl.class);
        bind(Recommender.class).to(RecommenderImpl.class);
        bind(Matcher.class).to(MatcherImpl.class);
    }
}
