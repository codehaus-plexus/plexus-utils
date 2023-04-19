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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Locale;

import org.junit.jupiter.api.Test;

/**
 * Test string utils.
 *
 * @author <a href="mailto:brett@apache.org">Brett Porter</a>
 * @version $Id: $Id
 * @since 3.4.0
 */
public class StringUtilsTest
{

    /**
     * <p>testIsEmpty.</p>
     */
    @Test
    public void testIsEmpty()
    {
        assertEquals( true, StringUtils.isEmpty( null ) );
        assertEquals( true, StringUtils.isEmpty( "" ) );
        assertEquals( false, StringUtils.isEmpty( " " ) );
        assertEquals( false, StringUtils.isEmpty( "foo" ) );
        assertEquals( false, StringUtils.isEmpty( "  foo  " ) );
    }

    /**
     * <p>testIsNotEmpty.</p>
     */
    @Test
    public void testIsNotEmpty()
    {
        assertEquals( false, StringUtils.isNotEmpty( null ) );
        assertEquals( false, StringUtils.isNotEmpty( "" ) );
        assertEquals( true, StringUtils.isNotEmpty( " " ) );
        assertEquals( true, StringUtils.isNotEmpty( "foo" ) );
        assertEquals( true, StringUtils.isNotEmpty( "  foo  " ) );
    }

    @org.junit.jupiter.api.Test
    public void testIsNotEmptyNegatesIsEmpty()
    {
        assertEquals( !StringUtils.isEmpty( null ), StringUtils.isNotEmpty( null ) );
        assertEquals( !StringUtils.isEmpty( "" ), StringUtils.isNotEmpty( "" ) );
        assertEquals( !StringUtils.isEmpty( " " ), StringUtils.isNotEmpty( " " ) );
        assertEquals( !StringUtils.isEmpty( "foo" ), StringUtils.isNotEmpty( "foo" ) );
        assertEquals( !StringUtils.isEmpty( "  foo  " ), StringUtils.isNotEmpty( "  foo  " ) );
    }

    /**
     * <p>testIsBlank.</p>
     */
    @Test
    public void testIsBlank()
    {
        assertEquals( true, StringUtils.isBlank( null ) );
        assertEquals( true, StringUtils.isBlank( "" ) );
        assertEquals( true, StringUtils.isBlank( " \t\r\n" ) );
        assertEquals( false, StringUtils.isBlank( "foo" ) );
        assertEquals( false, StringUtils.isBlank( "  foo  " ) );
    }

    /**
     * <p>testIsNotBlank.</p>
     */
    @Test
    public void testIsNotBlank()
    {
        assertEquals( false, StringUtils.isNotBlank( null ) );
        assertEquals( false, StringUtils.isNotBlank( "" ) );
        assertEquals( false, StringUtils.isNotBlank( " \t\r\n" ) );
        assertEquals( true, StringUtils.isNotBlank( "foo" ) );
        assertEquals( true, StringUtils.isNotBlank( "  foo  " ) );
    }

    /**
     * <p>testCapitalizeFirstLetter.</p>
     */
    @Test
    public void testCapitalizeFirstLetter()
    {
        assertEquals( "Id", StringUtils.capitalizeFirstLetter( "id" ) );
        assertEquals( "Id", StringUtils.capitalizeFirstLetter( "Id" ) );
    }

    /**
     * <p>testCapitalizeFirstLetterTurkish.</p>
     */
    @Test
    public void testCapitalizeFirstLetterTurkish()
    {
        Locale l = Locale.getDefault();
        Locale.setDefault( new Locale( "tr" ) );
        assertEquals( "Id", StringUtils.capitalizeFirstLetter( "id" ) );
        assertEquals( "Id", StringUtils.capitalizeFirstLetter( "Id" ) );
        Locale.setDefault( l );
    }

    /**
     * <p>testLowerCaseFirstLetter.</p>
     */
    @org.junit.jupiter.api.Test
    public void testLowerCaseFirstLetter()
    {
        assertEquals( "id", StringUtils.lowercaseFirstLetter( "id" ) );
        assertEquals( "id", StringUtils.lowercaseFirstLetter( "Id" ) );
    }

    /**
     * <p>testLowerCaseFirstLetterTurkish.</p>
     */
    @org.junit.jupiter.api.Test
    public void testLowerCaseFirstLetterTurkish()
    {
        Locale l = Locale.getDefault();
        Locale.setDefault( new Locale( "tr" ) );
        assertEquals( "id", StringUtils.lowercaseFirstLetter( "id" ) );
        assertEquals( "id", StringUtils.lowercaseFirstLetter( "Id" ) );
        Locale.setDefault( l );
    }

    /**
     * <p>testRemoveAndHump.</p>
     */
    @org.junit.jupiter.api.Test
    public void testRemoveAndHump()
    {
        assertEquals( "Id", StringUtils.removeAndHump( "id", "-" ) );
        assertEquals( "SomeId", StringUtils.removeAndHump( "some-id", "-" ) );
    }

    /**
     * <p>testRemoveAndHumpTurkish.</p>
     */
    @Test
    public void testRemoveAndHumpTurkish()
    {
        Locale l = Locale.getDefault();
        Locale.setDefault( new Locale( "tr" ) );
        assertEquals( "Id", StringUtils.removeAndHump( "id", "-" ) );
        assertEquals( "SomeId", StringUtils.removeAndHump( "some-id", "-" ) );
        Locale.setDefault( l );
    }

    /**
     * <p>testQuote_EscapeEmbeddedSingleQuotes.</p>
     */
    @Test
    public void testQuote_EscapeEmbeddedSingleQuotes()
    {
        String src = "This \'is a\' test";
        String check = "\'This \\\'is a\\\' test\'";

        char[] escaped = { '\'', '\"' };
        String result = StringUtils.quoteAndEscape( src, '\'', escaped, '\\', false );

        assertEquals( check, result );
    }

    /**
     * <p>testQuote_EscapeEmbeddedSingleQuotesWithPattern.</p>
     */
    @org.junit.jupiter.api.Test
    public void testQuote_EscapeEmbeddedSingleQuotesWithPattern()
    {
        String src = "This \'is a\' test";
        String check = "\'This pre'postis apre'post test\'";

        char[] escaped = { '\'', '\"' };
        String result = StringUtils.quoteAndEscape( src, '\'', escaped, new char[] { ' ' }, "pre%spost", false );

        assertEquals( check, result );
    }

    /**
     * <p>testQuote_EscapeEmbeddedDoubleQuotesAndSpaces.</p>
     */
    @Test
    public void testQuote_EscapeEmbeddedDoubleQuotesAndSpaces()
    {
        String src = "This \"is a\" test";
        String check = "\'This\\ \\\"is\\ a\\\"\\ test\'";

        char[] escaped = { '\'', '\"', ' ' };
        String result = StringUtils.quoteAndEscape( src, '\'', escaped, '\\', false );

        assertEquals( check, result );
    }

    /**
     * <p>testQuote_DontQuoteIfUnneeded.</p>
     */
    @org.junit.jupiter.api.Test
    public void testQuote_DontQuoteIfUnneeded()
    {
        String src = "ThisIsATest";

        char[] escaped = { '\'', '\"' };
        String result = StringUtils.quoteAndEscape( src, '\'', escaped, '\\', false );

        assertEquals( src, result );
    }

    /**
     * <p>testQuote_WrapWithSingleQuotes.</p>
     */
    @Test
    public void testQuote_WrapWithSingleQuotes()
    {
        String src = "This is a test";
        String check = "\'This is a test\'";

        char[] escaped = { '\'', '\"' };
        String result = StringUtils.quoteAndEscape( src, '\'', escaped, '\\', false );

        assertEquals( check, result );
    }

    /**
     * <p>testQuote_PreserveExistingQuotes.</p>
     */
    @org.junit.jupiter.api.Test
    public void testQuote_PreserveExistingQuotes()
    {
        String src = "\'This is a test\'";

        char[] escaped = { '\'', '\"' };
        String result = StringUtils.quoteAndEscape( src, '\'', escaped, '\\', false );

        assertEquals( src, result );
    }

    /**
     * <p>testQuote_WrapExistingQuotesWhenForceIsTrue.</p>
     */
    @org.junit.jupiter.api.Test
    public void testQuote_WrapExistingQuotesWhenForceIsTrue()
    {
        String src = "\'This is a test\'";
        String check = "\'\\\'This is a test\\\'\'";

        char[] escaped = { '\'', '\"' };
        String result = StringUtils.quoteAndEscape( src, '\'', escaped, '\\', true );

        assertEquals( check, result );
    }

    /**
     * <p>testQuote_ShortVersion_SingleQuotesPreserved.</p>
     */
    @org.junit.jupiter.api.Test
    public void testQuote_ShortVersion_SingleQuotesPreserved()
    {
        String src = "\'This is a test\'";

        String result = StringUtils.quoteAndEscape( src, '\'' );

        assertEquals( src, result );
    }

    /**
     * <p>testSplit.</p>
     */
    @org.junit.jupiter.api.Test
    public void testSplit()
    {
        String[] tokens;

        tokens = StringUtils.split( "", ", " );
        assertNotNull( tokens );
        assertEquals( Arrays.asList( new String[0] ), Arrays.asList( tokens ) );

        tokens = StringUtils.split( ", ,,,   ,", ", " );
        assertNotNull( tokens );
        assertEquals( Arrays.asList( new String[0] ), Arrays.asList( tokens ) );

        tokens = StringUtils.split( "this", ", " );
        assertNotNull( tokens );
        assertEquals( Arrays.asList( new String[] { "this" } ), Arrays.asList( tokens ) );

        tokens = StringUtils.split( "this is a test", ", " );
        assertNotNull( tokens );
        assertEquals( Arrays.asList( new String[] { "this", "is", "a", "test" } ), Arrays.asList( tokens ) );

        tokens = StringUtils.split( "   this   is   a   test  ", ", " );
        assertNotNull( tokens );
        assertEquals( Arrays.asList( new String[] { "this", "is", "a", "test" } ), Arrays.asList( tokens ) );

        tokens = StringUtils.split( "this is a test, really", ", " );
        assertNotNull( tokens );
        assertEquals( Arrays.asList( new String[] { "this", "is", "a", "test", "really" } ), Arrays.asList( tokens ) );
    }

    /**
     * <p>testRemoveDuplicateWhitespace.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @org.junit.jupiter.api.Test
    public void testRemoveDuplicateWhitespace()
        throws Exception
    {
        String s = "this     is     test   ";
        assertEquals( "this is test ", StringUtils.removeDuplicateWhitespace( s ) );
        s = "this  \r\n   is \n  \r  test   ";
        assertEquals( "this is test ", StringUtils.removeDuplicateWhitespace( s ) );
        s = "     this  \r\n   is \n  \r  test";
        assertEquals( " this is test", StringUtils.removeDuplicateWhitespace( s ) );
        s = "this  \r\n   is \n  \r  test   \n ";
        assertEquals( "this is test ", StringUtils.removeDuplicateWhitespace( s ) );

    }

    /**
     * <p>testUnifyLineSeparators.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    public void testUnifyLineSeparators()
        throws Exception
    {
        String s = "this\r\nis\na\r\ntest";

        try
        {
            StringUtils.unifyLineSeparators( s, "abs" );
            assertTrue( false, "Exception NOT catched" );
        }
        catch ( IllegalArgumentException e )
        {
            assertTrue( true, "Exception catched" );
        }

        assertEquals( "this\nis\na\ntest", StringUtils.unifyLineSeparators( s, "\n" ) );
        assertEquals( "this\r\nis\r\na\r\ntest", StringUtils.unifyLineSeparators( s, "\r\n" ) );
    }
}
