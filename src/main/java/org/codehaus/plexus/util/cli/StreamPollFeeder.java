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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Poll InputStream for available data and write the output to an OutputStream.
 * <p>
 * This class is designed to avoid blocking when reading from streams like System.in.
 * It polls the input stream for available data instead of blocking on read operations.
 *
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 */
public class StreamPollFeeder extends AbstractStreamHandler {

    public static final int BUF_LEN = 80;

    private InputStream input;

    private OutputStream output;

    private volatile Throwable exception = null;

    private final Object lock = new Object();

    /**
     * Create a new StreamPollFeeder
     *
     * @param input Stream to read from
     * @param output Stream to write to
     */
    public StreamPollFeeder(InputStream input, OutputStream output) {
        super();
        this.input = input;
        this.output = output;
    }

    @Override
    public void run() {
        byte[] buf = new byte[BUF_LEN];

        try {
            while (!isDone()) {
                if (input.available() > 0) {
                    int i = input.read(buf);
                    if (i > 0) {
                        output.write(buf, 0, i);
                        output.flush();
                    } else {
                        setDone();
                    }
                } else {
                    synchronized (lock) {
                        if (!isDone()) {
                            lock.wait(100);
                        }
                    }
                }
            }
        } catch (IOException e) {
            exception = e;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            close();
        }
    }

    public void close() {
        if (input != null) {
            synchronized (input) {
                try {
                    input.close();
                } catch (IOException ex) {
                    if (exception == null) {
                        exception = ex;
                    }
                }

                input = null;
            }
        }

        if (output != null) {
            synchronized (output) {
                try {
                    output.close();
                } catch (IOException ex) {
                    if (exception == null) {
                        exception = ex;
                    }
                }

                output = null;
            }
        }
    }

    /**
     * @since 3.1.0
     * @return the Exception
     */
    public Throwable getException() {
        return exception;
    }

    @Override
    public synchronized void waitUntilDone() throws InterruptedException {
        synchronized (lock) {
            setDone();
            lock.notifyAll();
        }

        join();
    }
}
