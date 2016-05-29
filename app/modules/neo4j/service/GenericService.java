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

package modules.neo4j.service;

import com.google.inject.Inject;
import modules.neo4j.domain.Entity;
import modules.neo4j.session.Neo4JSessionProvider;

import java.util.Collection;

public abstract class GenericService<T> implements Service<T> {
    private static final int DEPTH_LIST = 0;
    private static final int DEPTH_ENTITY = 1;

    @Inject
    public Neo4JSessionProvider sessionProvider;

    /**
     * {@inheritDoc}
     */
    public Collection<T> findAll() {
        return sessionProvider.getSession().loadAll(
                getEntityType(), DEPTH_LIST);
    }

    /**
     * {@inheritDoc}
     */
    public T find(Long id) {
        return sessionProvider.getSession().load(
                getEntityType(), id, DEPTH_ENTITY);
    }

    /**
     * {@inheritDoc}
     */
    public void delete(Long id) {
        sessionProvider.getSession().delete(
                sessionProvider.getSession().load(
                        getEntityType(), id));
    }

    /**
     * {@inheritDoc}
     */
    public T createOrUpdate(T entity) {
        sessionProvider.getSession().save(
                entity, DEPTH_ENTITY);
        return find(((Entity) entity).getId());
    }

    protected abstract Class<T> getEntityType();
}
