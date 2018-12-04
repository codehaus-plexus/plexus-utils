package org.codehaus.plexus.util.xml.pull;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class MXParserTest
{
    @Test
    public void testHexadecimalEntities()
        throws Exception
    {
        MXParser parser = new MXParser();

        parser.defineEntityReplacementText( "test", "replacement" );

        String input = "<root>&#x41;</root>";

        parser.setInput( new StringReader( input ) );

        assertEquals( XmlPullParser.START_TAG, parser.next() );

        assertEquals( XmlPullParser.TEXT, parser.next() );

        assertEquals( "A", parser.getText() );

        assertEquals( XmlPullParser.END_TAG, parser.next() );
    }

    @Test
    public void testDecimalEntities()
        throws Exception
    {
        MXParser parser = new MXParser();

        parser.defineEntityReplacementText( "test", "replacement" );

        String input = "<root>&#65;</root>";

        parser.setInput( new StringReader( input ) );

        assertEquals( XmlPullParser.START_TAG, parser.next() );

        assertEquals( XmlPullParser.TEXT, parser.next() );

        assertEquals( "A", parser.getText() );

        assertEquals( XmlPullParser.END_TAG, parser.next() );
    }

    @Test
    public void testPredefinedEntities()
        throws Exception
    {
        MXParser parser = new MXParser();

        parser.defineEntityReplacementText( "test", "replacement" );

        String input = "<root>&lt;&gt;&amp;&apos;&quot;</root>";

        parser.setInput( new StringReader( input ) );

        assertEquals( XmlPullParser.START_TAG, parser.next() );

        assertEquals( XmlPullParser.TEXT, parser.next() );

        assertEquals( "<>&'\"", parser.getText() );

        assertEquals( XmlPullParser.END_TAG, parser.next() );
    }

    @Test
    public void testEntityReplacementMap()
        throws XmlPullParserException, IOException
    {
        EntityReplacementMap erm = new EntityReplacementMap( new String[][] { { "abc", "CDE" }, { "EFG", "HIJ" } } );
        MXParser parser = new MXParser( erm );

        String input = "<root>&EFG;</root>";
        parser.setInput( new StringReader( input ) );

        assertEquals( XmlPullParser.START_TAG, parser.next() );
        assertEquals( XmlPullParser.TEXT, parser.next() );
        assertEquals( "HIJ", parser.getText() );
        assertEquals( XmlPullParser.END_TAG, parser.next() );
    }

    @Test
    public void testCustomEntities()
        throws Exception
    {
        MXParser parser = new MXParser();

        String input = "<root>&myentity;</root>";
        parser.setInput( new StringReader( input ) );
        parser.defineEntityReplacementText( "myentity", "replacement" );
        assertEquals( XmlPullParser.START_TAG, parser.next() );
        assertEquals( XmlPullParser.TEXT, parser.next() );
        assertEquals( "replacement", parser.getText() );
        assertEquals( XmlPullParser.END_TAG, parser.next() );

        parser = new MXParser();
        input = "<root>&myCustom;</root>";
        parser.setInput( new StringReader( input ) );
        parser.defineEntityReplacementText( "fo", "&#65;" );
        parser.defineEntityReplacementText( "myCustom", "&fo;" );
        assertEquals( XmlPullParser.START_TAG, parser.next() );
        assertEquals( XmlPullParser.TEXT, parser.next() );
        assertEquals( "&#65;", parser.getText() );
        assertEquals( XmlPullParser.END_TAG, parser.next() );
    }

    @Test
    public void testUnicodeEntities()
        throws Exception
    {
        MXParser parser = new MXParser();
        String input = "<root>&#x1d7ed;</root>";
        parser.setInput( new StringReader( input ) );

        assertEquals( XmlPullParser.START_TAG, parser.nextToken() );
        assertEquals( XmlPullParser.ENTITY_REF, parser.nextToken() );
        assertEquals( "\uD835\uDFED", parser.getText() );
        assertEquals( XmlPullParser.END_TAG, parser.nextToken() );

        parser = new MXParser();
        input = "<root>&#x159;</root>";
        parser.setInput( new StringReader( input ) );

        assertEquals( XmlPullParser.START_TAG, parser.nextToken() );
        assertEquals( XmlPullParser.ENTITY_REF, parser.nextToken() );
        assertEquals( "\u0159", parser.getText() );
        assertEquals( XmlPullParser.END_TAG, parser.nextToken() );
    }

    @Test
    public void testProcessingInstruction()
        throws Exception
    {
        String input = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><test>nnn</test>";

        MXParser parser = new MXParser();
        parser.setInput( new StringReader( input ) );

        assertEquals( XmlPullParser.PROCESSING_INSTRUCTION, parser.nextToken() );
        assertEquals( XmlPullParser.START_TAG, parser.nextToken() );
        assertEquals( XmlPullParser.TEXT, parser.nextToken() );
        assertEquals( XmlPullParser.END_TAG, parser.nextToken() );
    }

    @Test
    public void testSubsequentProcessingInstructionShort()
        throws Exception
    {
        StringBuffer sb = new StringBuffer();

        sb.append( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" );
        sb.append( "<project>" );
        sb.append( "<!-- comment -->" );
        sb.append( "<?m2e ignore?>" );
        sb.append( "</project>" );

        MXParser parser = new MXParser();
        parser.setInput( new StringReader( sb.toString() ) );

        assertEquals( XmlPullParser.PROCESSING_INSTRUCTION, parser.nextToken() );
        assertEquals( XmlPullParser.START_TAG, parser.nextToken() );
        assertEquals( XmlPullParser.COMMENT, parser.nextToken() );
        assertEquals( XmlPullParser.PROCESSING_INSTRUCTION, parser.nextToken() );
        assertEquals( XmlPullParser.END_TAG, parser.nextToken() );
    }

    @Test
    public void testSubsequentProcessingInstructionMoreThan8k()
        throws Exception
    {
        StringBuffer sb = new StringBuffer();

        sb.append( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" );
        sb.append( "<project>" );

        // add ten times 1000 chars as comment
        for ( int j = 0; j < 10; j++ )
        {

            sb.append( "<!-- " );
            for ( int i = 0; i < 2000; i++ )
            {
                sb.append( "ten bytes " );
            }
            sb.append( " -->" );
        }

        sb.append( "<?m2e ignore?>" );
        sb.append( "</project>" );

        MXParser parser = new MXParser();
        parser.setInput( new StringReader( sb.toString() ) );

        assertEquals( XmlPullParser.PROCESSING_INSTRUCTION, parser.nextToken() );
        assertEquals( XmlPullParser.START_TAG, parser.nextToken() );
        assertEquals( XmlPullParser.COMMENT, parser.nextToken() );
        assertEquals( XmlPullParser.COMMENT, parser.nextToken() );
        assertEquals( XmlPullParser.COMMENT, parser.nextToken() );
        assertEquals( XmlPullParser.COMMENT, parser.nextToken() );
        assertEquals( XmlPullParser.COMMENT, parser.nextToken() );
        assertEquals( XmlPullParser.COMMENT, parser.nextToken() );
        assertEquals( XmlPullParser.COMMENT, parser.nextToken() );
        assertEquals( XmlPullParser.COMMENT, parser.nextToken() );
        assertEquals( XmlPullParser.COMMENT, parser.nextToken() );
        assertEquals( XmlPullParser.COMMENT, parser.nextToken() );
        assertEquals( XmlPullParser.PROCESSING_INSTRUCTION, parser.nextToken() );
        assertEquals( XmlPullParser.END_TAG, parser.nextToken() );
    }

    public void testMalformedProcessingInstructionAfterTag()
        throws Exception
    {
        MXParser parser = new MXParser();

        String input = "<project /><?>";

        parser.setInput( new StringReader( input ) );

        try
        {
            assertEquals( XmlPullParser.START_TAG, parser.next() );

            assertEquals( XmlPullParser.END_TAG, parser.next() );

            assertEquals( XmlPullParser.PROCESSING_INSTRUCTION, parser.next() );

            fail( "Should fail since it has an invalid Processing Instruction" );
        }
        catch ( XmlPullParserException ex )
        {
            assertTrue( ex.getMessage().contains( "processing instruction PITarget name not found" ) );
        }
    }

    public void testMalformedProcessingInstructionBeforeTag()
        throws Exception
    {
        MXParser parser = new MXParser();

        String input = "<?><project />";

        parser.setInput( new StringReader( input ) );

        try
        {
            assertEquals( XmlPullParser.PROCESSING_INSTRUCTION, parser.next() );

            assertEquals( XmlPullParser.START_TAG, parser.next() );

            assertEquals( XmlPullParser.END_TAG, parser.next() );

            fail( "Should fail since it has invalid PI" );
        }
        catch ( XmlPullParserException ex )
        {
            assertTrue( ex.getMessage().contains( "processing instruction PITarget name not found" ) );
        }
    }

    public void testMalformedProcessingInstructionSpaceBeforeName()
        throws Exception
    {
        MXParser parser = new MXParser();

        StringBuilder sb = new StringBuilder();
        sb.append( "<? shouldhavenospace>" );
        sb.append( "<project />" );

        parser.setInput( new StringReader( sb.toString() ) );

        try
        {
            assertEquals( XmlPullParser.PROCESSING_INSTRUCTION, parser.next() );

            assertEquals( XmlPullParser.START_TAG, parser.next() );

            assertEquals( XmlPullParser.END_TAG, parser.next() );

            fail( "Should fail since it has invalid PI" );
        }
        catch ( XmlPullParserException ex )
        {
            assertTrue( ex.getMessage().contains( "processing instruction PITarget must be exactly after <? and not white space character" ) );
        }
    }

    public void testMalformedProcessingInstructionNoClosingQuestionMark()
        throws Exception
    {
        MXParser parser = new MXParser();

        StringBuilder sb = new StringBuilder();
        sb.append( "<?shouldhavenospace>" );
        sb.append( "<project />" );

        parser.setInput( new StringReader( sb.toString() ) );

        try
        {
            assertEquals( XmlPullParser.PROCESSING_INSTRUCTION, parser.next() );

            assertEquals( XmlPullParser.START_TAG, parser.next() );

            assertEquals( XmlPullParser.END_TAG, parser.next() );

            fail( "Should fail since it has invalid PI" );
        }
        catch ( XmlPullParserException ex )
        {
            assertTrue( ex.getMessage().contains( "processing instruction started on line 1 and column 2 was not closed" ) );
        }
    }

    public void testSubsequentMalformedProcessingInstructionNoClosingQuestionMark()
        throws Exception
    {
        MXParser parser = new MXParser();

        StringBuilder sb = new StringBuilder();
        sb.append( "<project />" );
        sb.append( "<?shouldhavenospace>" );

        parser.setInput( new StringReader( sb.toString() ) );

        try
        {
            assertEquals( XmlPullParser.START_TAG, parser.next() );

            assertEquals( XmlPullParser.END_TAG, parser.next() );

            assertEquals( XmlPullParser.PROCESSING_INSTRUCTION, parser.next() );

            fail( "Should fail since it has invalid PI" );
        }
        catch ( XmlPullParserException ex )
        {
            assertTrue( ex.getMessage().contains( "processing instruction started on line 1 and column 13 was not closed" ) );
        }
    }

}
