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
package org.xwiki.velocity.introspection;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;
import java.util.Properties;

import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.util.introspection.UberspectImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xwiki.test.annotation.AllComponents;
import org.xwiki.test.junit5.mockito.ComponentTest;
import org.xwiki.test.mockito.MockitoComponentManager;
import org.xwiki.velocity.VelocityEngine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link LinkingUberspector}.
 *
 * @version $Id$
 */
@ComponentTest
@AllComponents
public class LinkingUberspectorTest
{
    private VelocityEngine engine;

    @BeforeEach
    public void setUp(MockitoComponentManager componentManager) throws Exception
    {
        // Register in-memory configuration sources for the test.
        componentManager.registerMemoryConfigurationSource();

        this.engine = componentManager.getInstance(VelocityEngine.class);
    }

    /*
     * Tests that the uberspectors in the list are called, and without a real uberspector no methods are found.
     */
    @Test
    public void emptyArray() throws Exception
    {
        Properties prop = new Properties();
        prop.setProperty(RuntimeConstants.UBERSPECT_CLASSNAME, LinkingUberspector.class.getCanonicalName());
        prop.setProperty(LinkingUberspector.UBERSPECT_ARRAY_CLASSNAMES, TestingUberspector.class.getCanonicalName());
        TestingUberspector.methodCalls = 0;
        this.engine.initialize(prop);
        StringWriter writer = new StringWriter();
        this.engine.evaluate(new org.apache.velocity.VelocityContext(), writer, "mytemplate",
            new StringReader("#set($foo = 'hello')#set($bar = $foo.toString())$bar"));
        assertEquals("$bar", writer.toString());
        assertEquals(1, TestingUberspector.methodCalls);
    }

    /*
     * Tests that using several uberspectors in the array works, methods are correctly found by a valid uberspector in
     * the chain, and after a method is found no further calls are performed.
     */
    @Test
    public void basicArray() throws Exception
    {
        Properties prop = new Properties();
        prop.setProperty(RuntimeConstants.UBERSPECT_CLASSNAME, LinkingUberspector.class.getCanonicalName());
        prop.setProperty(LinkingUberspector.UBERSPECT_ARRAY_CLASSNAMES,
            TestingUberspector.class.getCanonicalName() + "," + TestingUberspector.class.getCanonicalName() + ","
                + UberspectImpl.class.getCanonicalName() + "," + TestingUberspector.class.getCanonicalName());
        TestingUberspector.methodCalls = 0;
        TestingUberspector.getterCalls = 0;
        this.engine.initialize(prop);
        StringWriter writer = new StringWriter();
        this.engine.evaluate(new org.apache.velocity.VelocityContext(), writer, "mytemplate",
            new StringReader("#set($foo = 'hello')#set($bar = $foo.toString())$bar"));
        assertEquals("hello", writer.toString());
        assertEquals(2, TestingUberspector.methodCalls);
        assertEquals(0, TestingUberspector.getterCalls);
    }

    /*
     * Tests that invalid uberspectors classnames are ignored.
     */
    @Test
    public void invalidUberspectorsAreIgnored() throws Exception
    {
        Properties prop = new Properties();
        prop.setProperty(RuntimeConstants.UBERSPECT_CLASSNAME, LinkingUberspector.class.getCanonicalName());
        prop.setProperty(LinkingUberspector.UBERSPECT_ARRAY_CLASSNAMES,
            Date.class.getCanonicalName() + "," + AbstractChainableUberspector.class.getCanonicalName() + ","
                + InvalidUberspector.class.getCanonicalName() + "," + TestingUberspector.class.getCanonicalName() + ","
                + UberspectImpl.class.getCanonicalName());
        TestingUberspector.methodCalls = 0;
        InvalidUberspector.methodCalls = 0;
        this.engine.initialize(prop);
        StringWriter writer = new StringWriter();
        this.engine.evaluate(new org.apache.velocity.VelocityContext(), writer, "mytemplate",
            new StringReader("#set($foo = 'hello')#set($bar = $foo.toString())$bar"));
        assertEquals("hello", writer.toString());
        assertEquals(1, TestingUberspector.methodCalls);
        assertEquals(0, InvalidUberspector.methodCalls);
    }

    /*
     * Checks that the default (non-secure) uberspector works and allows calling restricted methods.
     */
    @Test
    public void defaultUberspectorWorks() throws Exception
    {
        Properties prop = new Properties();
        prop.setProperty(RuntimeConstants.UBERSPECT_CLASSNAME, LinkingUberspector.class.getCanonicalName());
        prop.setProperty(LinkingUberspector.UBERSPECT_ARRAY_CLASSNAMES, UberspectImpl.class.getCanonicalName());
        this.engine.initialize(prop);
        StringWriter writer = new StringWriter();
        this.engine.evaluate(new org.apache.velocity.VelocityContext(), writer, "mytemplate",
            new StringReader("#set($foo = 'hello')" + "#set($bar = $foo.getClass().getConstructors())$bar"));
        assertTrue(writer.toString().contains("public java.lang.String(byte[],int,int)"), writer.toString());
    }

    /*
     * Checks that the secure uberspector works and does not allow calling restricted methods.
     */
    @Test
    public void secureUberspectorWorks() throws Exception
    {
        Properties prop = new Properties();
        prop.setProperty(RuntimeConstants.UBERSPECT_CLASSNAME, LinkingUberspector.class.getCanonicalName());
        prop.setProperty(LinkingUberspector.UBERSPECT_ARRAY_CLASSNAMES, SecureUberspector.class.getCanonicalName());
        this.engine.initialize(prop);
        StringWriter writer = new StringWriter();
        this.engine.evaluate(new org.apache.velocity.VelocityContext(), writer, "mytemplate",
            new StringReader("#set($foo = 'hello')" + "#set($bar = $foo.getClass().getConstructors())$foo$bar"));
        assertEquals("hello$bar", writer.toString());
    }

    /*
     * Checks that when the array property is not configured, by default the secure ubespector is used.
     */
    @Test
    public void secureUberspectorEnabledByDefault() throws Exception
    {
        Properties prop = new Properties();
        prop.setProperty(RuntimeConstants.UBERSPECT_CLASSNAME, LinkingUberspector.class.getCanonicalName());
        prop.setProperty(LinkingUberspector.UBERSPECT_ARRAY_CLASSNAMES, "");
        this.engine.initialize(prop);
        StringWriter writer = new StringWriter();
        this.engine.evaluate(new org.apache.velocity.VelocityContext(), writer, "mytemplate",
            new StringReader("#set($foo = 'hello')" + "#set($bar = $foo.getClass().getConstructors())$foo$bar"));
        assertEquals("hello$bar", writer.toString());
    }
}
