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

package modules.neo4j.function;

import com.google.inject.Inject;
import modules.neo4j.domain.Tag;
import modules.neo4j.session.Neo4JSessionProvider;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MatcherImpl implements Matcher {
    @Inject
    public Neo4JSessionProvider sessionProvider;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> matchTagsString(String... tagsName) {
        String querySuffix = "RETURN matched ORDER BY r.weight DESC";
        String query = "";
        boolean first = true;
        for (String tname : tagsName) {
            query += createQuery(tname, first);
            first = false;
        }
        query += querySuffix;
        List<String> recTagsName = new LinkedList<>();
        sendQuery(query).forEach(t -> recTagsName.add(t.getName()));
        return recTagsName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<Tag> matchTags(String... tagsName) {
        String querySuffix = " RETURN matched ORDER BY r.weight DESC";
        String query = "";
        boolean first = true;
        for (String tname : tagsName) {
            query += createQuery(tname, first);
            first = false;
        }
        query += querySuffix;
        return sendQuery(query);
    }

    /**
     * Create a query
     */
    private String createQuery(String tname, boolean first) {
        String query = "";
        if (first) {
            query = "MATCH (:Tag {name:\"" + tname + "\"})-[r:RELATED_TO]-(matched)";
        } else {
            query += ", (:Tag {name:\"" + tname + "\"})-[:RELATED_TO]-(matched)";
        }
        return query;
    }

    /**
     * Executes the query and returns the result
     */
    private Iterable<Tag> sendQuery(String q) {
        return sessionProvider.getSession().query(Tag.class, q, Collections.EMPTY_MAP);
    }
}
