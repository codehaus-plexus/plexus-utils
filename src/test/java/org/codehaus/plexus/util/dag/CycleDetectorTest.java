package org.codehaus.plexus.util.dag;

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

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * <p>CycleDetectorTest class.</p>
 *
 * @author <a href="michal.maczka@dimatics.com">Michal Maczka</a>
 * @version $Id: $Id
 * @since 3.4.0
 */
public class CycleDetectorTest {

    /**
     * <p>testCycyleDetection.</p>
     */
    @Test
    public void testCycyleDetection() {
        // No cycle
        //
        // a --> b --->c
        //
        try {
            final DAG dag1 = new DAG();

            dag1.addEdge("a", "b");

            dag1.addEdge("b", "c");

        } catch (CycleDetectedException e) {

            fail("Cycle should not be detected");
        }

        //
        // a --> b --->c
        // ^ |
        // | |
        // -----------|

        try {
            final DAG dag2 = new DAG();

            dag2.addEdge("a", "b");

            dag2.addEdge("b", "c");

            dag2.addEdge("c", "a");

            fail("Cycle should be detected");

        } catch (CycleDetectedException e) {

            final List<String> cycle = e.getCycle();

            assertNotNull(cycle, "Cycle should be not null");

            assertTrue(cycle.contains("a"), "Cycle contains 'a'");

            assertTrue(cycle.contains("b"), "Cycle contains 'b'");

            assertTrue(cycle.contains("c"), "Cycle contains 'c'");
        }

        // | --> c
        // a --> b
        // | | --> d
        // --------->
        try {
            final DAG dag3 = new DAG();

            dag3.addEdge("a", "b");

            dag3.addEdge("b", "c");

            dag3.addEdge("b", "d");

            dag3.addEdge("a", "d");

        } catch (CycleDetectedException e) {
            fail("Cycle should not be detected");
        }

        // ------------
        // | |
        // V | --> c
        // a --> b
        // | | --> d
        // --------->
        try {
            final DAG dag4 = new DAG();

            dag4.addEdge("a", "b");

            dag4.addEdge("b", "c");

            dag4.addEdge("b", "d");

            dag4.addEdge("a", "d");

            dag4.addEdge("c", "a");

            fail("Cycle should be detected");

        } catch (CycleDetectedException e) {
            final List<String> cycle = e.getCycle();

            assertNotNull(cycle, "Cycle should be not null");

            assertEquals("a", (String) cycle.get(0), "Cycle contains 'a'");

            assertEquals("b", cycle.get(1), "Cycle contains 'b'");

            assertEquals("c", cycle.get(2), "Cycle contains 'c'");

            assertEquals("a", (String) cycle.get(3), "Cycle contains 'a'");
        }

        // f --> g --> h
        // |
        // |
        // a --> b ---> c --> d
        // ^ |
        // | V
        // ------------ e

        final DAG dag5 = new DAG();

        try {

            dag5.addEdge("a", "b");

            dag5.addEdge("b", "c");

            dag5.addEdge("b", "f");

            dag5.addEdge("f", "g");

            dag5.addEdge("g", "h");

            dag5.addEdge("c", "d");

            dag5.addEdge("d", "e");

            dag5.addEdge("e", "b");

            fail("Cycle should be detected");

        } catch (CycleDetectedException e) {
            final List<String> cycle = e.getCycle();

            assertNotNull(cycle, "Cycle should be not null");

            assertEquals(5, cycle.size(), "Cycle contains 5 elements");

            assertEquals("b", (String) cycle.get(0), "Cycle contains 'b'");

            assertEquals("c", cycle.get(1), "Cycle contains 'c'");

            assertEquals("d", cycle.get(2), "Cycle contains 'd'");

            assertEquals("e", (String) cycle.get(3), "Cycle contains 'e'");

            assertEquals("b", (String) cycle.get(4), "Cycle contains 'b'");

            assertTrue(dag5.hasEdge("a", "b"), "Edge exists");

            assertTrue(dag5.hasEdge("b", "c"), "Edge exists");

            assertTrue(dag5.hasEdge("b", "f"), "Edge exists");

            assertTrue(dag5.hasEdge("f", "g"), "Edge exists");

            assertTrue(dag5.hasEdge("g", "h"), "Edge exists");

            assertTrue(dag5.hasEdge("c", "d"), "Edge exists");

            assertTrue(dag5.hasEdge("d", "e"), "Edge exists");

            assertFalse(dag5.hasEdge("e", "b"));
        }
    }
}
