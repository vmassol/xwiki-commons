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
package org.xwiki.velocity.internal.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xwiki.velocity.internal.util.VelocityBlock.VelocityType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class VelocityParserTest
{
    private VelocityParser parser;

    @BeforeEach
    public void setUp()
    {
        this.parser = new VelocityParser();
    }

    // Tests

    @Test
    public void isValidVelocityIdentifierChar()
    {
        assertTrue(this.parser.isValidVelocityIdentifierChar('a'));
        assertTrue(this.parser.isValidVelocityIdentifierChar('_'));
        assertTrue(this.parser.isValidVelocityIdentifierChar('-'));

        assertFalse(this.parser.isValidVelocityIdentifierChar('.'));
    }

    @Test
    public void getKeyWordComment() throws InvalidVelocityException
    {
        VelocityParserContext context = new VelocityParserContext();
        StringBuffer buffer = new StringBuffer();

        int index = this.parser.getKeyWord("## some comment\n  ".toCharArray(), 0, buffer, context);

        assertEquals("## some comment\n".length(), index);
        assertEquals("## some comment\n", buffer.toString());
        assertFalse(context.isInVelocityBlock());
        assertEquals(VelocityType.COMMENT, context.getType());
    }

    @Test
    public void getKeyWordMultiLinesComment() throws InvalidVelocityException
    {
        VelocityParserContext context = new VelocityParserContext();
        StringBuffer buffer = new StringBuffer();

        int index = this.parser.getKeyWord("#*\n some comment\n*#  ".toCharArray(), 0, buffer, context);

        assertEquals("#*\n some comment\n*#".length(), index);
        assertEquals("#*\n some comment\n*#", buffer.toString());
        assertFalse(context.isInVelocityBlock());
        assertEquals(VelocityType.COMMENT, context.getType());
    }

    @Test
    public void getKeyWordDirective() throws InvalidVelocityException
    {
        VelocityParserContext context = new VelocityParserContext();
        StringBuffer buffer = new StringBuffer();

        int index = this.parser.getKeyWord("#directive(param1 param2, param2)  ".toCharArray(), 0, buffer, context);

        assertEquals("#directive(param1 param2, param2)".length(), index);
        assertEquals("#directive(param1 param2, param2)", buffer.toString());
        assertFalse(context.isInVelocityBlock());
        assertEquals(VelocityType.MACRO, context.getType());
    }

    @Test
    public void getDirective() throws InvalidVelocityException
    {
        VelocityParserContext context = new VelocityParserContext();
        StringBuffer buffer = new StringBuffer();

        int index = this.parser.getDirective("#if($a==1)true enough#elseno way!#end".toCharArray(), 0, buffer, context);

        assertEquals("#if($a==1)".length(), index);
        assertEquals("#if($a==1)", buffer.toString());
        assertTrue(context.isInVelocityBlock());
    }
}
