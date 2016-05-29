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

package modules.neo4j.domain;

import java.util.LinkedList;
import java.util.List;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Tag extends Entity {
    @Property
    private Long tagId;

    @Property
    private String name;
    
    @Relationship(type = "RELATED_TO", direction = Relationship.UNDIRECTED)
    private List<Relation> tags = new LinkedList<>();
    
    public void addRelation(Relation relation) {
        relation.getsTag().getTags().add(relation);
        relation.geteTag().getTags().add(relation);
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Relation> getTags() {
        return tags;
    }

    public void setTags(List<Relation> tags) {
        this.tags = tags;
    }
}
