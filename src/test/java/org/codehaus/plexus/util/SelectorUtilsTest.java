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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * <p>SelectorUtilsTest class.</p>
 *
 * @author herve
 * @since 3.4.0
 */
class SelectorUtilsTest {
    /**
     * <p>testExtractPattern.</p>
     */
    @Test
    void extractPattern() {
        assertEquals("[A-Z].*", SelectorUtils.extractPattern("%regex[[A-Z].*]", "/"));
        assertEquals("ABC*", SelectorUtils.extractPattern("%ant[ABC*]", "/"));
        assertEquals("some/ABC*", SelectorUtils.extractPattern("%ant[some/ABC*]", "/"));
        assertEquals("some\\ABC*", SelectorUtils.extractPattern("%ant[some\\ABC*]", "\\"));
        assertEquals("some/ABC*", SelectorUtils.extractPattern("%ant[some\\ABC*]", "/"));
        assertEquals("some\\ABC*", SelectorUtils.extractPattern("%ant[some/ABC*]", "\\"));
    }

    /**
     * <p>testIsAntPrefixedPattern.</p>
     */
    @Test
    void isAntPrefixedPattern() {
        assertTrue(SelectorUtils.isAntPrefixedPattern("%ant[A]")); // single char not allowed
        assertTrue(SelectorUtils.isAntPrefixedPattern("%ant[AB]"));
        assertFalse(SelectorUtils.isAntPrefixedPattern("%ant[]"));
        assertFalse(SelectorUtils.isAntPrefixedPattern("*"));
    }

    /**
     * <p>testIsRegexPrefixedPattern.</p>
     */
    @Test
    void isRegexPrefixedPattern() {
        assertTrue(SelectorUtils.isRegexPrefixedPattern("%regex[A]")); // single char not allowed
        assertTrue(SelectorUtils.isRegexPrefixedPattern("%regex[.*]"));
        assertFalse(SelectorUtils.isRegexPrefixedPattern("%regex[]"));
        assertFalse(SelectorUtils.isRegexPrefixedPattern("*"));
    }

    /**
     * <p>testMatchPath_DefaultFileSeparator.</p>
     */
    @Test
    void matchPathDefaultFileSeparator() {
        String separator = File.separator;

        // Pattern and target start with file separator
        assertTrue(
                SelectorUtils.matchPath(separator + "*" + separator + "a.txt", separator + "b" + separator + "a.txt"));
        // Pattern starts with file separator, target doesn't
        assertFalse(SelectorUtils.matchPath(separator + "*" + separator + "a.txt", "b" + separator + "a.txt"));
        // Pattern doesn't start with file separator, target does
        assertFalse(SelectorUtils.matchPath("*" + separator + "a.txt", separator + "b" + separator + "a.txt"));
        // Pattern and target don't start with file separator
        assertTrue(SelectorUtils.matchPath("*" + separator + "a.txt", "b" + separator + "a.txt"));
    }

    /**
     * <p>testMatchPath_UnixFileSeparator.</p>
     */
    @Test
    void matchPathUnixFileSeparator() {
        String separator = "/";

        // Pattern and target start with file separator
        assertTrue(SelectorUtils.matchPath(
                separator + "*" + separator + "a.txt", separator + "b" + separator + "a.txt", separator, false));
        // Pattern starts with file separator, target doesn't
        assertFalse(SelectorUtils.matchPath(
                separator + "*" + separator + "a.txt", "b" + separator + "a.txt", separator, false));
        // Pattern doesn't start with file separator, target does
        assertFalse(SelectorUtils.matchPath(
                "*" + separator + "a.txt", separator + "b" + separator + "a.txt", separator, false));
        // Pattern and target don't start with file separator
        assertTrue(SelectorUtils.matchPath("*" + separator + "a.txt", "b" + separator + "a.txt", separator, false));
    }

    /**
     * <p>testMatchPath_WindowsFileSeparator.</p>
     */
    @Test
    void matchPathWindowsFileSeparator() {
        String separator = "\\";

        // Pattern and target start with file separator
        assertTrue(SelectorUtils.matchPath(
                separator + "*" + separator + "a.txt", separator + "b" + separator + "a.txt", separator, false));
        // Pattern starts with file separator, target doesn't
        assertFalse(SelectorUtils.matchPath(
                separator + "*" + separator + "a.txt", "b" + separator + "a.txt", separator, false));
        // Pattern doesn't start with file separator, target does
        assertFalse(SelectorUtils.matchPath(
                "*" + separator + "a.txt", separator + "b" + separator + "a.txt", separator, false));
        // Pattern and target don't start with file separator
        assertTrue(SelectorUtils.matchPath("*" + separator + "a.txt", "b" + separator + "a.txt", separator, false));
    }

    @Test
    void patternMatchSingleWildcardPosix() {
        assertFalse(SelectorUtils.matchPath("/com/test/*", "/com/test/test/hallo"));
    }

    @Test
    void patternMatchDoubleWildcardCaseInsensitivePosix() {
        assertTrue(SelectorUtils.matchPath("/com/test/**", "/com/test/test/hallo"));
    }

    @Test
    void patternMatchDoubleWildcardPosix() {
        assertTrue(SelectorUtils.matchPath("/com/test/**", "/com/test/test/hallo"));
    }

    @Test
    void patternMatchSingleWildcardWindows() {
        assertFalse(SelectorUtils.matchPath("D:\\com\\test\\*", "D:\\com\\test\\test\\hallo"));

        assertFalse(SelectorUtils.matchPath("D:/com/test/*", "D:/com/test/test/hallo"));
    }

    @Test
    void patternMatchDoubleWildcardWindows() {
        assertTrue(SelectorUtils.matchPath("D:\\com\\test\\**", "D:\\com\\test\\test\\hallo"));

        assertTrue(SelectorUtils.matchPath("D:\\com\\test\\**", "D:/com/test/test/hallo"));
    }
}
