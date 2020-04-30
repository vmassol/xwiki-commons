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
package org.xwiki.properties.internal.converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.xwiki.properties.internal.DefaultConverterManager;
import org.xwiki.test.annotation.AllComponents;
import org.xwiki.test.junit5.mockito.ComponentTest;
import org.xwiki.test.junit5.mockito.InjectMockComponents;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Validate {@link ArrayListConverter} component.
 *
 * @version $Id$
 */
@ComponentTest
@AllComponents
public class ArrayListConverterTest
{
    @InjectMockComponents
    private DefaultConverterManager converterManager;

    @Test
    public void testConvertToString()
    {
        assertEquals("1, 2, 3",
            this.converterManager.convert(String.class, new ArrayList<>(Arrays.asList("1", "2", "3"))));
    }

    @Test
    public void testConvertFromArrayList()
    {
        ArrayList<String> expect = new ArrayList<>(Arrays.asList("1", "2", "3"));

        assertSame(expect, this.converterManager.convert(ArrayList.class, expect));
    }

    @Test
    public void testConvertFromBoolean()
    {
        assertEquals(Collections.singletonList("false"),
            this.converterManager.convert(ArrayList.class, false));
    }
}
