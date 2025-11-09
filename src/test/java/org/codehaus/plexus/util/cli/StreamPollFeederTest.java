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
import java.io.IOException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class StreamPollFeederTest {

    @Test
    public void dataShouldBeCopied() throws InterruptedException, IOException {

        StringBuilder TEST_DATA = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            TEST_DATA.append("TestData");
        }

        ByteArrayInputStream inputStream =
                new ByteArrayInputStream(TEST_DATA.toString().getBytes());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        StreamPollFeeder streamPollFeeder = new StreamPollFeeder(inputStream, outputStream);

        streamPollFeeder.start();

        //  wait until all data from steam will be read
        while (outputStream.size() < TEST_DATA.length()) {
            Thread.sleep(100);
        }

        // wait until process finish
        streamPollFeeder.waitUntilDone();
        assertNull(streamPollFeeder.getException());

        assertEquals(TEST_DATA.toString(), outputStream.toString());
    }
}
