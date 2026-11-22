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

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * <p>MatchPatternsTest class.</p>
 *
 * @author herve
 * @since 3.4.0
 */
class MatchPatternsTest {

    @Test
    void getSources() {
        List<String> expected = Arrays.asList("ABC**", "some/ABC*", "[ABC].*");
        MatchPatterns from = MatchPatterns.from("ABC**", "%ant[some/ABC*]", "%regex[[ABC].*]");
        List<String> actual = from.getSources();
        assertEquals(expected, actual);
    }

    @Test
    void matches() {
        MatchPatterns from = MatchPatterns.from("ABC**", "CDE**");
        assertTrue(from.matches("ABCDE", true));
        assertTrue(from.matches("CDEF", true));
        assertFalse(from.matches("XYZ", true));
    }

    @Test
    void withSeparatorMatchesNamesUsingThatSeparator() {
        MatchPatterns patterns = MatchPatterns.withSeparator(";", "**;target;**", "%regex[.*\\.log]");
        assertTrue(patterns.matches("module;target;classes", true));
        assertTrue(patterns.matches("build.log", true));
        assertFalse(patterns.matches("module;src;classes", true));
    }

    @Test
    void withSeparatorIterable() {
        MatchPatterns patterns = MatchPatterns.withSeparator(";", Arrays.asList("a;*", "b;*"));
        assertTrue(patterns.matches("b;c", true));
        assertFalse(patterns.matches("c;b", true));
    }

    @Test
    void withSeparatorWithoutSourcesMatchesNothing() {
        MatchPatterns patterns = MatchPatterns.withSeparator("/");
        assertTrue(patterns.getPatterns().isEmpty());
        assertFalse(patterns.matches("anything", true));
    }

    @Test
    void getPatterns() {
        MatchPatterns patterns = MatchPatterns.from("ABC**", "%ant[some/ABC*]", "%regex[[ABC].*]");
        List<MatchPattern> actual = patterns.getPatterns();
        assertEquals(3, actual.size());
        assertEquals("%ant[some/ABC*]", actual.get(1).toString());
        assertThrows(UnsupportedOperationException.class, () -> actual.add(MatchPattern.fromString("x")));
    }

    @Test
    void toStringListsPatternsAsSupplied() {
        MatchPatterns patterns = MatchPatterns.from("ABC**", "%ant[some/ABC*]", "%regex[[ABC].*]");
        assertEquals("[ABC**, %ant[some/ABC*], %regex[[ABC].*]]", patterns.toString());
    }
}
