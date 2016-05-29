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

package modules.neo4j.session;

import com.google.inject.Inject;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import play.Configuration;

public class Neo4JSessionProviderImpl implements Neo4JSessionProvider {
    private final static SessionFactory sessionFactory = new SessionFactory("modules.neo4j.domain");
    private Session session;

    @Inject
    public Neo4JSessionProviderImpl(Configuration configuration) {
        session = sessionFactory.openSession(configuration.getString("neo4j.url", "http://localhost:7474"), configuration.getString("neo4j.username", ""), configuration.getString("neo4j.password", ""));
    }

    /**
     * {@inheritDoc}
     */
    public Session getSession() {
        return session;
    }
}
