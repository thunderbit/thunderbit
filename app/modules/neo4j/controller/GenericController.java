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

package modules.neo4j.controller;

import org.neo4j.ogm.exception.NotFoundException;

public abstract class GenericController<T> implements Controller<T> {
    /**
     * {@inheritDoc}
     */
    public T create(T entity) {
        return getService().createOrUpdate(entity);
    }

    /**
     * {@inheritDoc}
     */
    public T update(T entity, Long id) {
        if (getService().find(id) != null) {
            return getService().createOrUpdate(entity);
        }
        throw new NotFoundException();
    }

    /**
     * {@inheritDoc}
     */
    public void delete(Long id) {
        if (getService().find(id) != null) {
            getService().delete(id);
        } else {
            throw new NotFoundException();
        }
    }

    /**
     * {@inheritDoc}
     */
    public Iterable<T> list() {
        return getService().findAll();
    }

    /**
     * {@inheritDoc}
     */
    public T find(Long id) {
        T entity = getService().find(id);
        if (entity != null) {
            System.out.println("from OGM: " + entity);
            return entity;
        }
        throw new NotFoundException();
    }
}
