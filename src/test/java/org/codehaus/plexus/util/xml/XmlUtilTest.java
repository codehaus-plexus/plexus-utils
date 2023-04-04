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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;

import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;
import org.junit.Test;

/**
 * Test the {@link org.codehaus.plexus.util.xml.XmlUtil} class.
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id: $Id
 * @since 3.4.0
 */
public class XmlUtilTest
{
    private String basedir;

    /**
     * <p>Getter for the field <code>basedir</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public final String getBasedir()
    {
        if ( null == basedir )
        {
            basedir = System.getProperty( "basedir", new File( "" ).getAbsolutePath() );
        }
        return basedir;
    }

    private File getTestOutputFile( String relPath )
        throws IOException
    {
        final File file = new File( getBasedir(), relPath );
        final File parentFile = file.getParentFile();
        if ( !parentFile.isDirectory() && !parentFile.mkdirs() )
        {
            throw new IOException( "Could not create test directory " + parentFile );
        }
        return file;
    }

    /**
     * <p>testPrettyFormatInputStreamOutputStream.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    public void testPrettyFormatInputStreamOutputStream()
        throws Exception
    {
        File testDocument = new File( getBasedir(), "src/test/resources/testDocument.xhtml" );
        assertTrue( testDocument.exists() );

        try ( InputStream is = Files.newInputStream( testDocument.toPath() );
              OutputStream os = Files.newOutputStream( getTestOutputFile( "target/test/prettyFormatTestDocumentOutputStream.xml" ).toPath() ) )
        {
            assertNotNull( is );
            assertNotNull( os );

            XmlUtil.prettyFormat( is, os );
        }
    }

    /**
     * <p>testPrettyFormatReaderWriter.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    public void testPrettyFormatReaderWriter()
        throws Exception
    {
        File testDocument = new File( getBasedir(), "src/test/resources/testDocument.xhtml" );
        assertTrue( testDocument.exists() );

        try ( Reader reader = new XmlStreamReader( testDocument );
              Writer writer = new XmlStreamWriter( getTestOutputFile( "target/test/prettyFormatTestDocumentWriter.xml" ) ) )
        {
            assertNotNull( reader );
            assertNotNull( writer );

            XmlUtil.prettyFormat( reader, writer );
        }
    }

    /**
     * <p>testPrettyFormatString.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    public void testPrettyFormatString()
        throws Exception
    {
        File testDocument = new File( getBasedir(), "src/test/resources/testDocument.xhtml" );
        assertTrue( testDocument.exists() );

        String content;
        try ( Reader reader = new XmlStreamReader( testDocument ) )
        {
            content = IOUtil.toString( reader );
        }

        Writer writer = new StringWriter();
        try ( Reader reader = new XmlStreamReader( testDocument ) )
        {
            XmlUtil.prettyFormat( reader, writer );
        }

        assertNotNull( content );

        int countEOL = StringUtils.countMatches( content, XmlUtil.DEFAULT_LINE_SEPARATOR );
        assertTrue( countEOL < StringUtils.countMatches( writer.toString(), XmlUtil.DEFAULT_LINE_SEPARATOR ) );
    }

    /**
     * <p>testPrettyFormatReaderWriter2.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    public void testPrettyFormatReaderWriter2()
        throws Exception
    {
        File testDocument = new File( getBasedir(), "src/test/resources/test.xdoc.xhtml" );
        assertTrue( testDocument.exists() );

        try ( Reader reader = new XmlStreamReader( testDocument );
              Writer writer = new XmlStreamWriter( getTestOutputFile( "target/test/prettyFormatTestXdocWriter.xml" ) ) )
        {
            assertNotNull( reader );
            assertNotNull( writer );

            XmlUtil.prettyFormat( reader, writer );
        }
    }
}
