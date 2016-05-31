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

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

@RelationshipEntity(type = "RELATED_TO")
public class Relation extends Entity {
    @StartNode
    private Tag sTag;
    
    @EndNode
    private Tag eTag;
    
    @Property
    private int weight;
    
    public Relation setNewRelation(Tag start, Tag end, int weight) {
        setsTag(start);
        seteTag(end);
        setWeight(weight);
        return this;
    }

    public Tag getsTag() {
        return sTag;
    }

    public void setsTag(Tag sTag) {
        this.sTag = sTag;
    }

    public Tag geteTag() {
        return eTag;
    }

    public void seteTag(Tag eTag) {
        this.eTag = eTag;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
