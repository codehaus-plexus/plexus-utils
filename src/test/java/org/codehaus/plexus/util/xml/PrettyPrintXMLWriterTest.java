package org.codehaus.plexus.util.xml;

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

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.util.NoSuchElementException;

import javax.swing.text.html.HTML.Tag;

import org.codehaus.plexus.util.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test of {@link org.codehaus.plexus.util.xml.PrettyPrintXMLWriter}
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @author <a href="mailto:belingueres@gmail.com">Gabriel Belingueres</a>
 * @version $Id: $Id
 * @since 3.4.0
 */
public class PrettyPrintXMLWriterTest
{
    StringWriter w;

    PrettyPrintXMLWriter writer;

    /**
     * <p>setUp.</p>
     */
    @Before
    public void setUp()
    {
        initWriter();
    }

    /**
     * <p>tearDown.</p>
     */
    @After
    public void tearDown()
    {
        writer = null;
        w = null;
    }

    private void initWriter()
    {
        w = new StringWriter();
        writer = new PrettyPrintXMLWriter( w );
    }

    /**
     * <p>testDefaultPrettyPrintXMLWriter.</p>
     */
    @Test
    public void testDefaultPrettyPrintXMLWriter()
    {
        writer.startElement( Tag.HTML.toString() );

        writeXhtmlHead( writer );

        writeXhtmlBody( writer );

        writer.endElement(); // Tag.HTML

        assertEquals( expectedResult( PrettyPrintXMLWriter.LS ), w.toString() );
    }

    /**
     * <p>testPrettyPrintXMLWriterWithGivenLineSeparator.</p>
     */
    @Test
    public void testPrettyPrintXMLWriterWithGivenLineSeparator()
    {
        writer.setLineSeparator( "\n" );

        writer.startElement( Tag.HTML.toString() );

        writeXhtmlHead( writer );

        writeXhtmlBody( writer );

        writer.endElement(); // Tag.HTML

        assertEquals( expectedResult( "\n" ), w.toString() );
    }

    /**
     * <p>testPrettyPrintXMLWriterWithGivenLineIndenter.</p>
     */
    @Test
    public void testPrettyPrintXMLWriterWithGivenLineIndenter()
    {
        writer.setLineIndenter( "    " );

        writer.startElement( Tag.HTML.toString() );

        writeXhtmlHead( writer );

        writeXhtmlBody( writer );

        writer.endElement(); // Tag.HTML

        assertEquals( expectedResult( "    ", PrettyPrintXMLWriter.LS ), w.toString() );
    }

    /**
     * <p>testEscapeXmlAttribute.</p>
     */
    @Test
    public void testEscapeXmlAttribute()
    {
        // Windows
        writer.startElement( Tag.DIV.toString() );
        writer.addAttribute( "class", "sect\r\nion" );
        writer.endElement(); // Tag.DIV
        assertEquals( "<div class=\"sect&#10;ion\"/>", w.toString() );

        // Mac
        initWriter();
        writer.startElement( Tag.DIV.toString() );
        writer.addAttribute( "class", "sect\rion" );
        writer.endElement(); // Tag.DIV
        assertEquals( "<div class=\"sect&#13;ion\"/>", w.toString() );

        // Unix
        initWriter();
        writer.startElement( Tag.DIV.toString() );
        writer.addAttribute( "class", "sect\nion" );
        writer.endElement(); // Tag.DIV
        assertEquals( "<div class=\"sect&#10;ion\"/>", w.toString() );
    }

    /**
     * <p>testendElementAlreadyClosed.</p>
     */
    @Test
    public void testendElementAlreadyClosed()
    {
        try
        {
            writer.startElement( Tag.DIV.toString() );
            writer.addAttribute( "class", "someattribute" );
            writer.endElement(); // Tag.DIV closed
            writer.endElement(); // Tag.DIV already closed, and there is no other outer tag!
            fail( "Should throw a NoSuchElementException" );
        }
        catch ( NoSuchElementException e )
        {
            assert ( true );
        }
    }

    /**
     * Issue #51: https://github.com/codehaus-plexus/plexus-utils/issues/51 Purpose: test if concatenation string
     * optimization bug is present. Target environment: Java 7 (u79 and u80 verified) running on Windows. Detection
     * strategy: Tries to build a big XML file (~750MB size) and with many nested tags to force the JVM to trigger the
     * concatenation string optimization bug that throws a NoSuchElementException when calling endElement() method.
     *
     * @throws java.io.IOException if an I/O error occurs
     */
    @Test
    public void testIssue51DetectJava7ConcatenationBug()
        throws IOException
    {
        File dir = new File( "target/test-xml" );
        if ( !dir.exists() )
        {
            assertTrue( "cannot create directory test-xml", dir.mkdir() );
        }
        File xmlFile = new File( dir, "test-issue-51.xml" );
        OutputStreamWriter osw = new OutputStreamWriter( Files.newOutputStream( xmlFile.toPath() ), "UTF-8" );
        writer = new PrettyPrintXMLWriter( osw );

        int iterations = 20000;

        try
        {
            for ( int i = 0; i < iterations; ++i )
            {
                writer.startElement( Tag.DIV.toString() + i );
                writer.addAttribute( "class", "someattribute" );
            }
            for ( int i = 0; i < iterations; ++i )
            {
                writer.endElement(); // closes Tag.DIV + i
            }
        }
        catch ( NoSuchElementException e )
        {
            fail( "Should not throw a NoSuchElementException" );
        }
        finally
        {
            if ( osw != null )
            {
                osw.close();
            }
        }
    }

    private void writeXhtmlHead( XMLWriter writer )
    {
        writer.startElement( Tag.HEAD.toString() );
        writer.startElement( Tag.TITLE.toString() );
        writer.writeText( "title" );
        writer.endElement(); // Tag.TITLE
        writer.startElement( Tag.META.toString() );
        writer.addAttribute( "name", "author" );
        writer.addAttribute( "content", "Author" );
        writer.endElement(); // Tag.META
        writer.startElement( Tag.META.toString() );
        writer.addAttribute( "name", "date" );
        writer.addAttribute( "content", "Date" );
        writer.endElement(); // Tag.META
        writer.endElement(); // Tag.HEAD
    }

    private void writeXhtmlBody( XMLWriter writer )
    {
        writer.startElement( Tag.BODY.toString() );
        writer.startElement( Tag.P.toString() );
        writer.writeText( "Paragraph 1, line 1. Paragraph 1, line 2." );
        writer.endElement(); // Tag.P
        writer.startElement( Tag.DIV.toString() );
        writer.addAttribute( "class", "section" );
        writer.startElement( Tag.H2.toString() );
        writer.writeText( "Section title" );
        writer.endElement(); // Tag.H2
        writer.endElement(); // Tag.DIV
        writer.endElement(); // Tag.BODY
    }

    private String expectedResult( String lineSeparator )
    {
        return expectedResult( "  ", lineSeparator );
    }

    private String expectedResult( String lineIndenter, String lineSeparator )
    {
        StringBuilder expected = new StringBuilder();

        expected.append( "<html>" ).append( lineSeparator );
        expected.append( StringUtils.repeat( lineIndenter, 1 ) ).append( "<head>" ).append( lineSeparator );
        expected.append( StringUtils.repeat( lineIndenter,
                                             2 ) ).append( "<title>title</title>" ).append( lineSeparator );
        expected.append( StringUtils.repeat( lineIndenter,
                                             2 ) ).append( "<meta name=\"author\" content=\"Author\"/>" ).append( lineSeparator );
        expected.append( StringUtils.repeat( lineIndenter,
                                             2 ) ).append( "<meta name=\"date\" content=\"Date\"/>" ).append( lineSeparator );
        expected.append( StringUtils.repeat( lineIndenter, 1 ) ).append( "</head>" ).append( lineSeparator );
        expected.append( StringUtils.repeat( lineIndenter, 1 ) ).append( "<body>" ).append( lineSeparator );
        expected.append( StringUtils.repeat( lineIndenter,
                                             2 ) ).append( "<p>Paragraph 1, line 1. Paragraph 1, line 2.</p>" ).append( lineSeparator );
        expected.append( StringUtils.repeat( lineIndenter,
                                             2 ) ).append( "<div class=\"section\">" ).append( lineSeparator );
        expected.append( StringUtils.repeat( lineIndenter,
                                             3 ) ).append( "<h2>Section title</h2>" ).append( lineSeparator );
        expected.append( StringUtils.repeat( lineIndenter, 2 ) ).append( "</div>" ).append( lineSeparator );
        expected.append( StringUtils.repeat( lineIndenter, 1 ) ).append( "</body>" ).append( lineSeparator );
        expected.append( "</html>" );

        return expected.toString();
    }
}
