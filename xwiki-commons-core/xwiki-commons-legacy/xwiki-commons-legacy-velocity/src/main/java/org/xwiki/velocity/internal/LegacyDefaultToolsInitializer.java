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
package org.xwiki.velocity.internal;

import java.util.Properties;

import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.velocity.tools.generic.ListTool;
import org.apache.velocity.tools.generic.SortTool;
import org.xwiki.component.annotation.Component;
import org.xwiki.velocity.tools.CollectionsTool;

/**
 * Add legacy tools.
 * 
 * @version $Id$
 * @since 12.0RC1
 */
@Component
@Named("legacy")
@Singleton
public class LegacyDefaultToolsInitializer implements DefaultToolsInitializer
{
    @Override
    public void initialize(Properties defaultTools)
    {
        defaultTools.setProperty("listtool", ListTool.class.getName());
        defaultTools.setProperty("sorttool", SortTool.class.getName());
        defaultTools.setProperty("collectionstool", CollectionsTool.class.getName());
    }
}
