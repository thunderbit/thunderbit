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

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;
import com.google.inject.Inject;
import modules.neo4j.controller.TagController;
import modules.neo4j.domain.Relation;

import java.util.List;

public class RelationsUpdaterImpl implements RelationsUpdater {
    @Inject
    public TagController tagController;

    @Override
    public void update(List<models.Tag> tags) {
        String sql = "select all_ti.tag_id as tag_id, count(*) as weight " +
                "from tag_item all_ti " +
                "join (select * from tag_item where tag_id = :tag_id) as my_ti on all_ti.item_id = my_ti.item_id " +
                "where all_ti.tag_id <> my_ti.tag_id " +
                "group by all_ti.tag_id " +
                "having count(*) > 0";
        SqlQuery sqlQuery = Ebean.createSqlQuery(sql);

        for (models.Tag tag : tags) {
            sqlQuery.setParameter("tag_id", tag.id);
            List<SqlRow> list = sqlQuery.findList();

            // Get the first tag from the graph
            modules.neo4j.domain.Tag nTagFrom = tagController.findByTagId(tag.id);
            if (nTagFrom == null) {
                nTagFrom = new modules.neo4j.domain.Tag();
                nTagFrom.setName(tag.name);
                nTagFrom.setTagId(tag.id);
                tagController.create(nTagFrom);
            }

            for (SqlRow row : list) {
                // Get the second tag from the graph
                modules.neo4j.domain.Tag nTagTo = tagController.findByTagId(row.getLong("tag_id"));
                if (nTagTo == null) {
                    nTagTo = new modules.neo4j.domain.Tag();
                    nTagTo.setName(row.getString("name"));
                    nTagTo.setTagId(row.getLong("tag_id"));
                    tagController.create(nTagTo);
                }

                List<Relation> relations = nTagFrom.getTags();
                boolean createNew = true;
                for (Relation relation : relations) {
                    if (relation.geteTag().equals(nTagTo)) {
                        relation.setWeight(row.getInteger("weight"));
                        createNew = false;
                        break;
                    }
                }

                if (createNew == true) {
                    nTagFrom.addRelation(new Relation().setNewRelation(nTagFrom, nTagTo, row.getInteger("weight")));
                }
                
                tagController.update(nTagFrom, nTagFrom.getId());
            }
        }
    }
}
