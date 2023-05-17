package org.codehaus.plexus.util;

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

import java.io.File;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * <p>DirectoryWalkerTest class.</p>
 *
 * @author herve
 * @version $Id: $Id
 * @since 3.4.0
 */
public class DirectoryWalkerTest {
    /**
     * <p>testDirectoryWalk.</p>
     */
    @Test
    public void testDirectoryWalk() {
        DirectoryWalker walker = new DirectoryWalker();

        walker.addSCMExcludes();

        walker.setBaseDir(new File("src/test/resources/directorywalker"));

        WalkCollector collector = new WalkCollector();
        walker.addDirectoryWalkListener(collector);

        walker.scan();

        assertEquals(1, collector.startCount, "Walk Collector / Starting Count");
        assertNotNull(collector.startingDir, "Walk Collector / Starting Dir");
        assertEquals(1, collector.finishCount, "Walk Collector / Finish Count");
        assertEquals(4, collector.steps.size(), "Walk Collector / Steps Count");
        assertTrue(collector.percentageLow >= 0, "Walk Collector / percentage low >= 0");
        assertTrue(collector.percentageHigh <= 100, "Walk Collector / percentage high <= 100");
    }
}
