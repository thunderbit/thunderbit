/*
 *     Thunderbit is a web application for digital assets management with emphasis on tags
 *     Copyright (C) 2016  thunderbit team
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package modules.security;

import be.objectify.deadbolt.java.DeadboltHandler;
import be.objectify.deadbolt.java.cache.HandlerCache;

import javax.inject.Singleton;

@Singleton
public class MyHandlerCache implements HandlerCache {
	
	private final DeadboltHandler defaultHandler = new MyDeadboltHandler();

	@Override
	public DeadboltHandler apply(final String key) {
		return this.defaultHandler;
	}

	@Override
	public DeadboltHandler get() {
		return this.defaultHandler;
	}
}