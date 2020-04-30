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
package org.xwiki.test.junit5;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility methods to execute system commands for debugging reasons.
 *
 * @version $Id$
 */
public final class RuntimeUtils
{
    private static final Logger LOGGER = LoggerFactory.getLogger(RuntimeUtils.class);

    private RuntimeUtils()
    {
        // Prevents instantiation.
    }

    /**
     * @param command the command to execute (e.g. "docker ps -a")
     * @return the command result
     */
    public static String run(String command)
    {
        String result;
        try {
            StringBuilder output = new StringBuilder();
            output.append("Execution of '").append(command).append("':");
            ProcessBuilder builder = new ProcessBuilder();
            builder.command("sh", "-c", command);
            builder.directory(new File(System.getProperty("user.home")));
            Process process = builder.start();
            StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), (it) -> {
                output.append('\n').append(it);
            });
            Executors.newSingleThreadExecutor().submit(streamGobbler);
            process.waitFor();
            result = output.toString();
        } catch (Throwable e) {
            // If we fail because of debugging code we shouldn't throw anything or it may hide the root cause.
            // Just log it so that we can fix it.
            String message = String.format("Error in debugging code when executing command [%s]", command);
            LOGGER.error(message, e);
            result = message;
        }
        return result;
    }

    private static class StreamGobbler implements Runnable
    {
        private InputStream inputStream;
        private Consumer<String> consumer;

        StreamGobbler(InputStream inputStream, Consumer<String> consumer)
        {
            this.inputStream = inputStream;
            this.consumer = consumer;
        }

        @Override
        public void run()
        {
            new BufferedReader(new InputStreamReader(inputStream)).lines()
                .forEach(consumer);
        }
    }
}
