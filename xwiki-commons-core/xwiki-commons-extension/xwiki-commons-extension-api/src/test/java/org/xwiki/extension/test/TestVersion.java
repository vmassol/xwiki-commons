/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.extension.test;

import org.xwiki.extension.version.Version;
import org.xwiki.extension.version.internal.DefaultVersion;

/**
 * Just here to have an implementation of {@link Version} which is not {@link DefaultVersion}.
 * 
 * @version $Id$
 * @since 10.7RC1
 */
public class TestVersion implements Version
{
    private final String version;

    public TestVersion(String version)
    {
        this.version = version;
    }

    @Override
    public int compareTo(Version o)
    {
        return 0;
    }

    @Override
    public String getValue()
    {
        return this.version;
    }

    @Override
    public Type getType()
    {
        return Type.STABLE;
    }
}
