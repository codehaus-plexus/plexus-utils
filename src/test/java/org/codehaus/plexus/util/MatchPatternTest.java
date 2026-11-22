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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * <p>MatchPatternTest class.</p>
 *
 * @author Kristian Rosenvold
 * @since 3.4.0
 */
class MatchPatternTest {

    @Test
    void getSource() {
        MatchPattern mp = MatchPattern.fromString("ABC*");
        assertEquals("ABC*", mp.getSource());
        mp = MatchPattern.fromString("%ant[some/ABC*]");
        assertEquals("some/ABC*", mp.getSource());
        mp = MatchPattern.fromString("%regex[[ABC].*]");
        assertEquals("[ABC].*", mp.getSource());
    }

    @Test
    void matchPath() {
        MatchPattern mp = MatchPattern.fromString("ABC*");
        assertTrue(mp.matchPath("ABCD", true));
    }

    /**
     * @see <a href="https://github.com/codehaus-plexus/plexus-utils/issues/63">Issue #63</a>
     */
    @Test
    void matchPatternStart() {
        MatchPattern mp = MatchPattern.fromString("ABC*");

        assertTrue(mp.matchPatternStart("ABCD", true));
        assertFalse(mp.matchPatternStart("AbCD", true));

        assertTrue(mp.matchPatternStart("ABCD", false));
        assertTrue(mp.matchPatternStart("AbCD", false));

        assertFalse(mp.matchPatternStart("XXXX", true));
        assertFalse(mp.matchPatternStart("XXXX", false));
    }

    @Test
    void tokenizePathToString() {
        String[] expected = {"hello", "world"};
        String[] actual = MatchPattern.tokenizePathToString("hello/world", "/");
        assertArrayEquals(expected, actual);

        actual = MatchPattern.tokenizePathToString("/hello/world", "/");
        assertArrayEquals(expected, actual);

        actual = MatchPattern.tokenizePathToString("/hello/world/", "/");
        assertArrayEquals(expected, actual);
    }

    @Test
    void fromStringWithSeparator() {
        MatchPattern mp = MatchPattern.fromString("a;b;*", ";");
        assertArrayEquals(new String[] {"a", "b", "*"}, mp.getTokenizedPathString());
        assertTrue(mp.matchPath("a;b;c", true));
        assertFalse(mp.matchPath("a;c;c", true));
    }

    @Test
    void matchPatternStartUsesPatternSeparator() {
        MatchPattern mp = MatchPattern.fromString("a;b;**", ";");
        assertTrue(mp.matchPatternStart("a;b", true));
        assertFalse(mp.matchPatternStart("a;c", true));
    }

    @Test
    void toStringReturnsPatternAsSupplied() {
        assertEquals("ABC*", MatchPattern.fromString("ABC*").toString());
        assertEquals(
                "%ant[some/ABC*]", MatchPattern.fromString("%ant[some/ABC*]").toString());
        assertEquals(
                "%regex[[ABC].*]", MatchPattern.fromString("%regex[[ABC].*]").toString());
    }
}
