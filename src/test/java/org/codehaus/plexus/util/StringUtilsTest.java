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
import java.util.Locale;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test string utils.
 *
 * @author <a href="mailto:brett@apache.org">Brett Porter</a>
 * @version $Id: $Id
 * @since 3.4.0
 */
class StringUtilsTest {

    /**
     * <p>testIsEmpty.</p>
     */
    @Test
    void isEmpty() {
        assertTrue(StringUtils.isEmpty(null));
        assertTrue(StringUtils.isEmpty(""));
        assertFalse(StringUtils.isEmpty(" "));
        assertFalse(StringUtils.isEmpty("foo"));
        assertFalse(StringUtils.isEmpty("  foo  "));
    }

    /**
     * <p>testIsNotEmpty.</p>
     */
    @Test
    void isNotEmpty() {
        assertFalse(StringUtils.isNotEmpty(null));
        assertFalse(StringUtils.isNotEmpty(""));
        assertTrue(StringUtils.isNotEmpty(" "));
        assertTrue(StringUtils.isNotEmpty("foo"));
        assertTrue(StringUtils.isNotEmpty("  foo  "));
    }

    @Test
    void isNotEmptyNegatesIsEmpty() {
        assertEquals(!StringUtils.isEmpty(null), StringUtils.isNotEmpty(null));
        assertEquals(!StringUtils.isEmpty(""), StringUtils.isNotEmpty(""));
        assertEquals(!StringUtils.isEmpty(" "), StringUtils.isNotEmpty(" "));
        assertEquals(!StringUtils.isEmpty("foo"), StringUtils.isNotEmpty("foo"));
        assertEquals(!StringUtils.isEmpty("  foo  "), StringUtils.isNotEmpty("  foo  "));
    }

    /**
     * <p>testIsBlank.</p>
     */
    @Test
    void isBlank() {
        assertTrue(StringUtils.isBlank(null));
        assertTrue(StringUtils.isBlank(""));
        assertTrue(StringUtils.isBlank(" \t\r\n"));
        assertFalse(StringUtils.isBlank("foo"));
        assertFalse(StringUtils.isBlank("  foo  "));
    }

    /**
     * <p>testIsNotBlank.</p>
     */
    @Test
    void isNotBlank() {
        assertFalse(StringUtils.isNotBlank(null));
        assertFalse(StringUtils.isNotBlank(""));
        assertFalse(StringUtils.isNotBlank(" \t\r\n"));
        assertTrue(StringUtils.isNotBlank("foo"));
        assertTrue(StringUtils.isNotBlank("  foo  "));
    }

    /**
     * <p>testCapitalizeFirstLetter.</p>
     */
    @Test
    void capitalizeFirstLetter() {
        assertEquals("Id", StringUtils.capitalizeFirstLetter("id"));
        assertEquals("Id", StringUtils.capitalizeFirstLetter("Id"));
    }

    /**
     * <p>testCapitalizeFirstLetterTurkish.</p>
     */
    @Test
    void capitalizeFirstLetterTurkish() {
        Locale l = Locale.getDefault();
        Locale.setDefault(new Locale("tr"));
        assertEquals("Id", StringUtils.capitalizeFirstLetter("id"));
        assertEquals("Id", StringUtils.capitalizeFirstLetter("Id"));
        Locale.setDefault(l);
    }

    /**
     * <p>testLowerCaseFirstLetter.</p>
     */
    @Test
    void lowerCaseFirstLetter() {
        assertEquals("id", StringUtils.lowercaseFirstLetter("id"));
        assertEquals("id", StringUtils.lowercaseFirstLetter("Id"));
    }

    /**
     * <p>testLowerCaseFirstLetterTurkish.</p>
     */
    @Test
    void lowerCaseFirstLetterTurkish() {
        Locale l = Locale.getDefault();
        Locale.setDefault(new Locale("tr"));
        assertEquals("id", StringUtils.lowercaseFirstLetter("id"));
        assertEquals("id", StringUtils.lowercaseFirstLetter("Id"));
        Locale.setDefault(l);
    }

    /**
     * <p>testRemoveAndHump.</p>
     */
    @Test
    void removeAndHump() {
        assertEquals("Id", StringUtils.removeAndHump("id", "-"));
        assertEquals("SomeId", StringUtils.removeAndHump("some-id", "-"));
    }

    /**
     * <p>testRemoveAndHumpTurkish.</p>
     */
    @Test
    void removeAndHumpTurkish() {
        Locale l = Locale.getDefault();
        Locale.setDefault(new Locale("tr"));
        assertEquals("Id", StringUtils.removeAndHump("id", "-"));
        assertEquals("SomeId", StringUtils.removeAndHump("some-id", "-"));
        Locale.setDefault(l);
    }

    /**
     * <p>testQuote_EscapeEmbeddedSingleQuotes.</p>
     */
    @Test
    void quoteEscapeEmbeddedSingleQuotes() {
        String src = "This \'is a\' test";
        String check = "\'This \\\'is a\\\' test\'";

        char[] escaped = {'\'', '\"'};
        String result = StringUtils.quoteAndEscape(src, '\'', escaped, '\\', false);

        assertEquals(check, result);
    }

    /**
     * <p>testQuote_EscapeEmbeddedSingleQuotesWithPattern.</p>
     */
    @Test
    void quoteEscapeEmbeddedSingleQuotesWithPattern() {
        String src = "This \'is a\' test";
        String check = "\'This pre'postis apre'post test\'";

        char[] escaped = {'\'', '\"'};
        String result = StringUtils.quoteAndEscape(src, '\'', escaped, new char[] {' '}, "pre%spost", false);

        assertEquals(check, result);
    }

    /**
     * <p>testQuote_EscapeEmbeddedDoubleQuotesAndSpaces.</p>
     */
    @Test
    void quoteEscapeEmbeddedDoubleQuotesAndSpaces() {
        String src = "This \"is a\" test";
        String check = "\'This\\ \\\"is\\ a\\\"\\ test\'";

        char[] escaped = {'\'', '\"', ' '};
        String result = StringUtils.quoteAndEscape(src, '\'', escaped, '\\', false);

        assertEquals(check, result);
    }

    /**
     * <p>testQuote_DontQuoteIfUnneeded.</p>
     */
    @Test
    void quoteDontQuoteIfUnneeded() {
        String src = "ThisIsATest";

        char[] escaped = {'\'', '\"'};
        String result = StringUtils.quoteAndEscape(src, '\'', escaped, '\\', false);

        assertEquals(src, result);
    }

    /**
     * <p>testQuote_WrapWithSingleQuotes.</p>
     */
    @Test
    void quoteWrapWithSingleQuotes() {
        String src = "This is a test";
        String check = "\'This is a test\'";

        char[] escaped = {'\'', '\"'};
        String result = StringUtils.quoteAndEscape(src, '\'', escaped, '\\', false);

        assertEquals(check, result);
    }

    /**
     * <p>testQuote_PreserveExistingQuotes.</p>
     */
    @Test
    void quotePreserveExistingQuotes() {
        String src = "\'This is a test\'";

        char[] escaped = {'\'', '\"'};
        String result = StringUtils.quoteAndEscape(src, '\'', escaped, '\\', false);

        assertEquals(src, result);
    }

    /**
     * <p>testQuote_WrapExistingQuotesWhenForceIsTrue.</p>
     */
    @Test
    void quoteWrapExistingQuotesWhenForceIsTrue() {
        String src = "\'This is a test\'";
        String check = "\'\\\'This is a test\\\'\'";

        char[] escaped = {'\'', '\"'};
        String result = StringUtils.quoteAndEscape(src, '\'', escaped, '\\', true);

        assertEquals(check, result);
    }

    /**
     * <p>testQuote_ShortVersion_SingleQuotesPreserved.</p>
     */
    @Test
    void quoteShortVersionSingleQuotesPreserved() {
        String src = "\'This is a test\'";

        String result = StringUtils.quoteAndEscape(src, '\'');

        assertEquals(src, result);
    }

    /**
     * <p>testSplit.</p>
     */
    @Test
    void split() {
        String[] tokens;

        tokens = StringUtils.split("", ", ");
        assertNotNull(tokens);
        assertEquals(Arrays.asList(new String[0]), Arrays.asList(tokens));

        tokens = StringUtils.split(", ,,,   ,", ", ");
        assertNotNull(tokens);
        assertEquals(Arrays.asList(new String[0]), Arrays.asList(tokens));

        tokens = StringUtils.split("this", ", ");
        assertNotNull(tokens);
        assertEquals(Arrays.asList(new String[] {"this"}), Arrays.asList(tokens));

        tokens = StringUtils.split("this is a test", ", ");
        assertNotNull(tokens);
        assertEquals(Arrays.asList(new String[] {"this", "is", "a", "test"}), Arrays.asList(tokens));

        tokens = StringUtils.split("   this   is   a   test  ", ", ");
        assertNotNull(tokens);
        assertEquals(Arrays.asList(new String[] {"this", "is", "a", "test"}), Arrays.asList(tokens));

        tokens = StringUtils.split("this is a test, really", ", ");
        assertNotNull(tokens);
        assertEquals(Arrays.asList(new String[] {"this", "is", "a", "test", "really"}), Arrays.asList(tokens));
    }

    /**
     * <p>testRemoveDuplicateWhitespace.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void removeDuplicateWhitespace() throws Exception {
        String s = "this     is     test   ";
        assertEquals("this is test ", StringUtils.removeDuplicateWhitespace(s));
        s = "this  \r\n   is \n  \r  test   ";
        assertEquals("this is test ", StringUtils.removeDuplicateWhitespace(s));
        s = "     this  \r\n   is \n  \r  test";
        assertEquals(" this is test", StringUtils.removeDuplicateWhitespace(s));
        s = "this  \r\n   is \n  \r  test   \n ";
        assertEquals("this is test ", StringUtils.removeDuplicateWhitespace(s));
    }

    /**
     * <p>testUnifyLineSeparators.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void unifyLineSeparators() throws Exception {
        String s = "this\r\nis\na\r\ntest";

        try {
            StringUtils.unifyLineSeparators(s, "abs");
            assertTrue(false, "Exception NOT catched");
        } catch (IllegalArgumentException e) {
            assertTrue(true, "Exception catched");
        }

        assertEquals("this\nis\na\ntest", StringUtils.unifyLineSeparators(s, "\n"));
        assertEquals("this\r\nis\r\na\r\ntest", StringUtils.unifyLineSeparators(s, "\r\n"));
    }
}
