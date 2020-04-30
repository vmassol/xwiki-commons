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
package org.xwiki.logging.internal.tail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.xwiki.component.manager.ComponentLifecycleException;
import org.xwiki.logging.LogLevel;
import org.xwiki.test.annotation.ComponentList;
import org.xwiki.test.junit5.XWikiTempDir;
import org.xwiki.test.junit5.mockito.ComponentTest;
import org.xwiki.test.junit5.mockito.InjectMockComponents;
import org.xwiki.xstream.internal.SafeXStream;
import org.xwiki.xstream.internal.XStreamUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Validate {@link XStreamFileLoggerTail}.
 * 
 * @version $Id$
 */
@ComponentTest
@ComponentList({ SafeXStream.class, XStreamUtils.class })
public class XStreamFileLoggerTailTest
{
    @InjectMockComponents
    private XStreamFileLoggerTail tail;

    @XWikiTempDir
    private File tmpDir;

    @AfterEach
    public void afterEach() throws Exception
    {
        this.tail.close();
    }

    @Test
    public void writeread() throws IOException
    {
        this.tail.initialize(new File(this.tmpDir, "log").toPath(), false);

        this.tail.error("error0");
        this.tail.error("error1");

        assertEquals("error0", this.tail.getLogEvent(0).getMessage());
        assertEquals("error1", this.tail.getLogEvent(1).getMessage());
    }

    @Test
    public void readonly() throws IOException, ComponentLifecycleException
    {
        this.tail.initialize(new File(this.tmpDir, "log").toPath(), false);

        this.tail.error("error0");
        this.tail.error("error1");

        this.tail.dispose();

        assertNull(this.tail.logStore);
        assertTrue(this.tail.open());
        assertNotNull(this.tail.logStore);
        this.tail.close(true);
        assertNull(this.tail.logStore);

        this.tail.initialize(new File(this.tmpDir, "log").toPath(), true);

        assertEquals("error0", this.tail.getLogEvent(0).getMessage());
        assertEquals("error1", this.tail.getLogEvent(1).getMessage());
        assertEquals(2, this.tail.getLogEvents(0, -1).stream().count());

        assertNull(this.tail.logStore);

        this.tail.error("error2");

        assertNull(this.tail.getLogEvent(2));
    }

    @Test
    public void getFirstLogEvent() throws IOException
    {
        this.tail.initialize(new File(this.tmpDir, "log").toPath(), false);

        assertNull(this.tail.getFirstLogEvent());
        assertNull(this.tail.getFirstLogEvent(LogLevel.ERROR));

        this.tail.info("info0");
        this.tail.info("info1");
        this.tail.warn("warn0");
        this.tail.warn("warn1");
        this.tail.error("error0");
        this.tail.error("error1");

        assertEquals("info0", this.tail.getFirstLogEvent().getMessage());
        assertEquals("info0", this.tail.getFirstLogEvent(LogLevel.TRACE).getMessage());
        assertEquals("info0", this.tail.getFirstLogEvent(LogLevel.INFO).getMessage());
        assertEquals("warn0", this.tail.getFirstLogEvent(LogLevel.WARN).getMessage());
        assertEquals("error0", this.tail.getFirstLogEvent(LogLevel.ERROR).getMessage());
    }

    @Test
    public void getLastLogEvent() throws IOException
    {
        this.tail.initialize(new File(this.tmpDir, "log").toPath(), false);

        assertNull(this.tail.getLastLogEvent());
        assertNull(this.tail.getLastLogEvent(LogLevel.ERROR));

        this.tail.error("error0");
        this.tail.error("error1");
        this.tail.warn("warn0");
        this.tail.warn("warn1");
        this.tail.info("info0");
        this.tail.info("info1");

        assertEquals("info1", this.tail.getLastLogEvent().getMessage());
        assertEquals("info1", this.tail.getLastLogEvent(LogLevel.TRACE).getMessage());
        assertEquals("info1", this.tail.getLastLogEvent(LogLevel.INFO).getMessage());
        assertEquals("warn1", this.tail.getLastLogEvent(LogLevel.WARN).getMessage());
        assertEquals("error1", this.tail.getLastLogEvent(LogLevel.ERROR).getMessage());
    }

    @Test
    public void getLogEvents() throws IOException
    {
        this.tail.initialize(new File(this.tmpDir, "log").toPath(), false);

        assertEquals(0, this.tail.getLogEvents(null).stream().count());
        assertEquals(0, this.tail.getLogEvents(LogLevel.DEBUG).stream().count());

        this.tail.info("info0");
        this.tail.warn("warn0");
        this.tail.error("error0");
        this.tail.info("info1");
        this.tail.warn("warn1");
        this.tail.error("error1");
        this.tail.info("info2");
        this.tail.warn("warn2");
        this.tail.error("error2");

        assertEquals(9, this.tail.getLogEvents(null).stream().count());
        assertEquals(9, this.tail.getLogEvents(LogLevel.DEBUG).stream().count());
        assertEquals(9, this.tail.getLogEvents(LogLevel.INFO).stream().count());
        assertEquals(6, this.tail.getLogEvents(LogLevel.WARN).stream().count());
        assertEquals(3, this.tail.getLogEvents(LogLevel.ERROR).stream().count());

        assertEquals(9, this.tail.getLogEvents(0, -1).stream().count());
        assertEquals(6, this.tail.getLogEvents(3, -1).stream().count());
        assertEquals(3, this.tail.getLogEvents(0, 3).stream().count());
        assertEquals(3, this.tail.getLogEvents(3, 3).stream().count());
        assertEquals(6, this.tail.getLogEvents(3, 42).stream().count());
        assertEquals(9, this.tail.getLogEvents(-1, -1).stream().count());
    }

    @Test
    public void getDeleteLog() throws IOException
    {
        this.tail.initialize(new File(this.tmpDir, "log").toPath(), false);

        this.tail.info("info");

        this.tail.close();

        this.tail.initialize(new File(this.tmpDir, "log").toPath(), true);

        assertEquals("info", this.tail.getLogEvent(0).getMessage());

        this.tail.logFile.delete();

        assertNull(this.tail.getLogEvent(0));
    }

    @Test
    public void getModifiedLog() throws IOException
    {
        this.tail.initialize(new File(this.tmpDir, "log").toPath(), false);

        this.tail.info("info");

        this.tail.close();

        this.tail.initialize(new File(this.tmpDir, "log").toPath(), true);

        assertEquals("info", this.tail.getLogEvent(0).getMessage());

        try (FileOutputStream fw = new FileOutputStream(this.tail.logFile, false)) {
            fw.flush();
        }

        assertNull(this.tail.getLogEvent(0));
    }

    @Test
    public void logStoreDeletedWhileReading() throws IOException
    {
        this.tail.initialize(new File(this.tmpDir, "log").toPath(), false);

        this.tail.info("info0");
        this.tail.info("info1");
        this.tail.info("info2");

        assertEquals("info0", this.tail.getLogEvent(0).getMessage());

        this.tail.flush();

        this.tail.logFile.delete();

        assertNull(this.tail.getLogEvent(1));

        this.tail.error("info3");

    }
}
