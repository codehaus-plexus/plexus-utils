package org.codehaus.plexus.util.cli;

/*
 * Copyright The Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;

import org.codehaus.plexus.util.Os;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StreamPollFeederTest {

    /** An input that never has data and never ends, like an idle terminal. */
    private static final class IdleInputStream extends InputStream {
        private final CountDownLatch never = new CountDownLatch(1);

        @Override
        public int available() {
            return 0;
        }

        @Override
        public int read() throws IOException {
            try {
                never.await();
            } catch (InterruptedException e) {
                throw new IOException(e);
            }
            return -1;
        }
    }

    private static final class CountingOutputStream extends ByteArrayOutputStream {
        private volatile boolean closed;

        @Override
        public void close() {
            closed = true;
        }
    }

    @Test
    void waitUntilDoneWithIdleInput() {
        CountingOutputStream out = new CountingOutputStream();
        StreamPollFeeder feeder = new StreamPollFeeder(new IdleInputStream(), out);

        assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
            feeder.start();
            Thread.sleep(150);
            feeder.waitUntilDone();
        });

        assertNull(feeder.getException());
        assertTrue(feeder.isDone());
        assertTrue(out.closed, "output must be closed when feeding ends");
    }

    @Test
    void waitUntilDoneWithContinuousData() {
        InputStream continuous = new InputStream() {
            @Override
            public int available() {
                return 1;
            }

            @Override
            public int read() {
                return 0;
            }

            @Override
            public int read(byte[] b, int off, int len) {
                return 1;
            }
        };
        StreamPollFeeder feeder = new StreamPollFeeder(continuous, new ByteArrayOutputStream() {
            @Override
            public synchronized void write(byte[] b, int off, int len) {
                // discard
            }
        });

        assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
            feeder.start();
            feeder.waitUntilDone();
        });
        assertNull(feeder.getException());
    }

    @Test
    void dataIsCopied() throws Exception {
        StringBuilder data = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            data.append("TestData");
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        StreamPollFeeder feeder =
                new StreamPollFeeder(new ByteArrayInputStream(data.toString().getBytes(StandardCharsets.UTF_8)), out);

        assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
            feeder.start();
            while (out.size() < data.length()) {
                Thread.sleep(20);
            }
            feeder.waitUntilDone();
        });

        assertNull(feeder.getException());
        assertEquals(data.toString(), out.toString("UTF-8"));
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    void executeCommandLineDoesNotWaitForIdleSystemIn() throws Exception {
        InputStream originalIn = System.in;
        System.setIn(new IdleInputStream());
        try {
            Commandline cli = new Commandline();
            cli.setExecutable(javaExecutable().getAbsolutePath());
            cli.createArg().setValue("-version");

            int exitCode = assertTimeoutPreemptively(
                    Duration.ofSeconds(30),
                    () -> CommandLineUtils.executeCommandLine(
                            cli,
                            System.in,
                            new CommandLineUtils.StringStreamConsumer(),
                            new CommandLineUtils.StringStreamConsumer()));
            assertEquals(0, exitCode);
        } finally {
            System.setIn(originalIn);
        }
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    void executeCommandLineStillDeliversEndOfInputToFiniteStream() throws Exception {
        Commandline cli = new Commandline();
        cli.setExecutable("cat");
        CommandLineUtils.StringStreamConsumer out = new CommandLineUtils.StringStreamConsumer();

        int exitCode = assertTimeoutPreemptively(
                Duration.ofSeconds(30),
                () -> CommandLineUtils.executeCommandLine(
                        cli,
                        new ByteArrayInputStream("hello".getBytes(StandardCharsets.UTF_8)),
                        out,
                        new CommandLineUtils.StringStreamConsumer()));

        assertEquals(0, exitCode);
        assertEquals("hello", out.getOutput().trim());
    }

    private static File javaExecutable() {
        File java =
                new File(System.getProperty("java.home"), Os.isFamily(Os.FAMILY_WINDOWS) ? "bin/java.exe" : "bin/java");
        assertTrue(java.exists(), java + " doesn't exist");
        return java;
    }
}
