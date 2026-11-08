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
import java.util.Objects;

/**
 * Polls an {@link InputStream} for available data and writes it to an {@link OutputStream}. Unlike
 * {@link StreamFeeder} it never blocks in a read, so it can be stopped at any time even when the input is a stream
 * such as {@code System.in} that has no data and no end.
 * <p>
 * Ported from {@code org.apache.maven.shared.utils.cli.StreamPollFeeder} of Apache Maven Shared Utils
 * (<a href="https://issues.apache.org/jira/browse/MSHARED-1072">MSHARED-1072</a>). The input stream is never closed
 * by this class.
 * </p>
 */
class StreamPollFeeder extends StreamFeeder {

    private static final int BUF_LEN = 80;

    private static final long POLL_INTERVAL_MILLIS = 100;

    private final InputStream pollInput;

    private final OutputStream pollOutput;

    private final Object lock = new Object();

    private volatile Throwable pollException;

    private volatile boolean stop;

    /**
     * Create a new StreamPollFeeder.
     *
     * @param input stream to read from
     * @param output stream to write to
     */
    StreamPollFeeder(InputStream input, OutputStream output) {
        super(input, output);
        this.pollInput = Objects.requireNonNull(input);
        this.pollOutput = Objects.requireNonNull(output);
        setDaemon(true);
    }

    @Override
    public void run() {
        byte[] buf = new byte[BUF_LEN];

        try {
            while (!stop) {
                if (pollInput.available() > 0) {
                    int i = pollInput.read(buf);
                    if (i > 0) {
                        if (!isDisabled()) {
                            pollOutput.write(buf, 0, i);
                            pollOutput.flush();
                        }
                    } else {
                        stop = true;
                    }
                } else {
                    synchronized (lock) {
                        if (!stop) {
                            lock.wait(POLL_INTERVAL_MILLIS);
                        }
                    }
                }
            }
        } catch (IOException e) {
            pollException = e;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            closeOutput();
            synchronized (this) {
                setDone();
                notifyAll();
            }
        }
    }

    private void closeOutput() {
        try {
            pollOutput.close();
        } catch (IOException e) {
            if (pollException == null) {
                pollException = e;
            }
        }
    }

    /**
     * Stops polling and waits for the feeder thread to finish.
     */
    @Override
    public void waitUntilDone() throws InterruptedException {
        stopPolling();
        join();
    }

    @Override
    public void disable() {
        super.disable();
        stopPolling();
    }

    /**
     * The output stream is closed when the thread ends, and the input stream is left open, so there is nothing to do.
     */
    @Override
    public void close() {
        // nothing to close here
    }

    @Override
    public Throwable getException() {
        return pollException;
    }

    private void stopPolling() {
        synchronized (lock) {
            stop = true;
            lock.notifyAll();
        }
    }
}
