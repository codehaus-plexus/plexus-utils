package org.codehaus.plexus.util.xml.pull;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class that execute a particular set of tests associated to a TESCASES tag from the XML W3C Conformance Tests.
 * TESCASES PROFILE: <pre>IBM XML Conformance Test Suite - Production 2</pre>
 * XML test files base folder: <pre>xmlconf/ibm/</pre>
 *
 * @author <a href="mailto:belingueres@gmail.com">Gabriel Belingueres</a>
 * @version $Id: $Id
 * @since 3.4.0
 */
public class IBMXML10Tests_Test_IBMXMLConformanceTestSuite_not_wftests_Test_IBMXMLConformanceTestSuite_Production2_Test {

    final static File testResourcesDir = new File("src/test/resources/", "xmlconf/ibm/");

    MXParser parser;

    /**
     * <p>setUp.</p>
     */
    @Before
    public void setUp() {
        parser = new MXParser();
    }

    /**
     * Test ID: <pre>ibm-not-wf-P02-ibm02n01.xml</pre>
     * Test URI: <pre>not-wf/P02/ibm02n01.xml</pre>
     * Comment: <pre>Tests a comment which contains an illegal Char: #x00</pre>
     * Sections: <pre>2.2</pre>
     * Version:
     *
     * @throws java.io.IOException if there is an I/O error
     */
    @Test
    public void testibm_not_wf_P02_ibm02n01xml()
                    throws IOException
    {
        try ( Reader reader = new FileReader( new File( testResourcesDir, "not-wf/P02/ibm02n01.xml" ) ) )
        {
            parser.setInput( reader );
            while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
                ;
            fail( "Tests a comment which contains an illegal Char: #x00" );
        }
        catch ( XmlPullParserException e )
        {
            assertTrue( e.getMessage().contains( "Illegal character 0x0 found in comment" ) );
        }
    }


    /**
     * Test ID: <pre>ibm-not-wf-P02-ibm02n02.xml</pre>
     * Test URI: <pre>not-wf/P02/ibm02n02.xml</pre>
     * Comment: <pre>Tests a comment which contains an illegal Char: #x01</pre>
     * Sections: <pre>2.2</pre>
     * Version:
     *
     * @throws java.io.IOException if there is an I/O error
     */
    @Test
    public void testibm_not_wf_P02_ibm02n02xml()
                    throws IOException
    {
        try ( Reader reader = new FileReader( new File( testResourcesDir, "not-wf/P02/ibm02n02.xml" ) ) )
        {
            parser.setInput( reader );
            while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
                ;
            fail( "Tests a comment which contains an illegal Char: #x01" );
        }
        catch ( XmlPullParserException e )
        {
            assertTrue( e.getMessage().contains( "Illegal character 0x1 found in comment" ) );
        }
    }

    /**
     * Test ID: <pre>ibm-not-wf-P02-ibm02n03.xml</pre>
     * Test URI: <pre>not-wf/P02/ibm02n03.xml</pre>
     * Comment: <pre>Tests a comment which contains an illegal Char: #x02</pre>
     * Sections: <pre>2.2</pre>
     * Version:
     *
     * @throws java.io.IOException if there is an I/O error
     */
    @Test
    public void testibm_not_wf_P02_ibm02n03xml()
                    throws IOException
    {
        try ( Reader reader = new FileReader( new File( testResourcesDir, "not-wf/P02/ibm02n03.xml" ) ) )
        {
            parser.setInput( reader );
            while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
                ;
            fail( "Tests a comment which contains an illegal Char: #x02" );
        }
        catch ( XmlPullParserException e )
        {
            assertTrue( e.getMessage().contains( "Illegal character 0x2 found in comment" ) );
        }
    }

    /**
     * Test ID: <pre>ibm-not-wf-P02-ibm02n04.xml</pre>
     * Test URI: <pre>not-wf/P02/ibm02n04.xml</pre>
     * Comment: <pre>Tests a comment which contains an illegal Char: #x03</pre>
     * Sections: <pre>2.2</pre>
     * Version:
     *
     * @throws java.io.IOException if there is an I/O error
     */
    @Test
    public void testibm_not_wf_P02_ibm02n04xml()
                    throws IOException
    {
        try ( Reader reader = new FileReader( new File( testResourcesDir, "not-wf/P02/ibm02n04.xml" ) ) )
        {
            parser.setInput( reader );
            while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
                ;
            fail( "Tests a comment which contains an illegal Char: #x03" );
        }
        catch ( XmlPullParserException e )
        {
            assertTrue( e.getMessage().contains( "Illegal character 0x3 found in comment" ) );
        }
    }

    /**
     * Test ID: <pre>ibm-not-wf-P02-ibm02n05.xml</pre>
     * Test URI: <pre>not-wf/P02/ibm02n05.xml</pre>
     * Comment: <pre>Tests a comment which contains an illegal Char: #x04</pre>
     * Sections: <pre>2.2</pre>
     * Version:
     *
     * @throws java.io.IOException if there is an I/O error
     */
    @Test
    public void testibm_not_wf_P02_ibm02n05xml()
                    throws IOException
    {
        try ( Reader reader = new FileReader( new File( testResourcesDir, "not-wf/P02/ibm02n05.xml" ) ) )
        {
            parser.setInput( reader );
            while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
                ;
            fail( "Tests a comment which contains an illegal Char: #x04" );
        }
        catch ( XmlPullParserException e )
        {
            assertTrue( e.getMessage().contains( "Illegal character 0x4 found in comment" ) );
        }
    }

    /**
     * Test ID: <pre>ibm-not-wf-P02-ibm02n06.xml</pre>
     * Test URI: <pre>not-wf/P02/ibm02n06.xml</pre>
     * Comment: <pre>Tests a comment which contains an illegal Char: #x05</pre>
     * Sections: <pre>2.2</pre>
     * Version:
     *
     * @throws java.io.IOException if there is an I/O error
     */
    @Test
    public void testibm_not_wf_P02_ibm02n06xml()
                    throws IOException
    {
        try ( Reader reader = new FileReader( new File( testResourcesDir, "not-wf/P02/ibm02n06.xml" ) ) )
        {
            parser.setInput( reader );
            while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
                ;
            fail( "Tests a comment which contains an illegal Char: #x05" );
        }
        catch ( XmlPullParserException e )
        {
            assertTrue( e.getMessage().contains( "Illegal character 0x5 found in comment" ) );
        }
    }

    /**
     * Test ID: <pre>ibm-not-wf-P02-ibm02n07.xml</pre>
     * Test URI: <pre>not-wf/P02/ibm02n07.xml</pre>
     * Comment: <pre>Tests a comment which contains an illegal Char: #x06</pre>
     * Sections: <pre>2.2</pre>
     * Version:
     *
     * @throws java.io.IOException if there is an I/O error
     */
    @Test
    public void testibm_not_wf_P02_ibm02n07xml() throws IOException {
        try(Reader reader = new FileReader(new File(testResourcesDir, "not-wf/P02/ibm02n07.xml"))) {
            parser.setInput(reader);
            while (parser.nextToken() != XmlPullParser.END_DOCUMENT);
            fail("Tests a comment which contains an illegal Char: #x06");
        }
        catch ( XmlPullParserException e )
        {
            assertTrue( e.getMessage().contains( "Illegal character 0x6 found in comment" ) );
        }
    }


    /**
     * Test ID: <pre>ibm-not-wf-P02-ibm02n08.xml</pre>
     * Test URI: <pre>not-wf/P02/ibm02n08.xml</pre>
     * Comment: <pre>Tests a comment which contains an illegal Char: #x07</pre>
     * Sections: <pre>2.2</pre>
     * Version:
     *
     * @throws java.io.IOException if there is an I/O error
     */
    @Test
    public void testibm_not_wf_P02_ibm02n08xml()
                    throws IOException
    {
        try ( Reader reader = new FileReader( new File( testResourcesDir, "not-wf/P02/ibm02n08.xml" ) ) )
        {
            parser.setInput( reader );
            while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
                ;
            fail( "Tests a comment which contains an illegal Char: #x07" );
        }
        catch ( XmlPullParserException e )
        {
            assertTrue( e.getMessage().contains( "Illegal character 0x7 found in comment" ) );
        }
    }

    /**
     * Test ID: <pre>ibm-not-wf-P02-ibm02n09.xml</pre>
     * Test URI: <pre>not-wf/P02/ibm02n09.xml</pre>
     * Comment: <pre>Tests a comment which contains an illegal Char: #x08</pre>
     * Sections: <pre>2.2</pre>
     * Version:
     *
     * @throws java.io.IOException if there is an I/O error
     */
    @Test
    public void testibm_not_wf_P02_ibm02n09xml()
                    throws IOException
    {
        try ( Reader reader = new FileReader( new File( testResourcesDir, "not-wf/P02/ibm02n09.xml" ) ) )
        {
            parser.setInput( reader );
            while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
                ;
            fail( "Tests a comment which contains an illegal Char: #x08" );
        }
        catch ( XmlPullParserException e )
        {
            assertTrue( e.getMessage().contains( "Illegal character 0x8 found in comment" ) );
        }
    }

    /**
     * Test ID: <pre>ibm-not-wf-P02-ibm02n10.xml</pre>
     * Test URI: <pre>not-wf/P02/ibm02n10.xml</pre>
     * Comment: <pre>Tests a comment which contains an illegal Char: #x0B</pre>
     * Sections: <pre>2.2</pre>
     * Version:
     *
     * @throws java.io.IOException if there is an I/O error
     */
    @Test
    public void testibm_not_wf_P02_ibm02n10xml()
                    throws IOException
    {
        try ( Reader reader = new FileReader( new File( testResourcesDir, "not-wf/P02/ibm02n10.xml" ) ) )
        {
            parser.setInput( reader );
            while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
                ;
            fail( "Tests a comment which contains an illegal Char: #x0B" );
        }
        catch ( XmlPullParserException e )
        {
            assertTrue( e.getMessage().contains( "Illegal character 0xb found in comment" ) );
        }
    }

    /**
     * Test ID: <pre>ibm-not-wf-P02-ibm02n11.xml</pre>
     * Test URI: <pre>not-wf/P02/ibm02n11.xml</pre>
     * Comment: <pre>Tests a comment which contains an illegal Char: #x0C</pre>
     * Sections: <pre>2.2</pre>
     * Version:
     *
     * @throws java.io.IOException if there is an I/O error
     */
    @Test
    public void testibm_not_wf_P02_ibm02n11xml()
                    throws IOException
    {
        try ( Reader reader = new FileReader( new File( testResourcesDir, "not-wf/P02/ibm02n11.xml" ) ) )
        {
            parser.setInput( reader );
            while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
                ;
            fail( "Tests a comment which contains an illegal Char: #x0C" );
        }
        catch ( XmlPullParserException e )
        {
            assertTrue( e.getMessage().contains( "Illegal character 0xc found in comment" ) );
        }
    }

    /**
     * Test ID: <pre>ibm-not-wf-P02-ibm02n12.xml</pre>
     * Test URI: <pre>not-wf/P02/ibm02n12.xml</pre>
     * Comment: <pre>Tests a comment which contains an illegal Char: #x0E</pre>
     * Sections: <pre>2.2</pre>
     * Version:
     *
     * @throws java.io.IOException if there is an I/O error
     */
    @Test
    public void testibm_not_wf_P02_ibm02n12xml()
                    throws IOException
    {
        try ( Reader reader = new FileReader( new File( testResourcesDir, "not-wf/P02/ibm02n12.xml" ) ) )
        {
            parser.setInput( reader );
            while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
                ;
            fail( "Tests a comment which contains an illegal Char: #x0E" );
        }
        catch ( XmlPullParserException e )
        {
            assertTrue( e.getMessage().contains( "Illegal character 0xe found in comment" ) );
        }
    }

    /**
     * Test ID: <pre>ibm-not-wf-P02-ibm02n13.xml</pre>
     * Test URI: <pre>not-wf/P02/ibm02n13.xml</pre>
     * Comment: <pre>Tests a comment which contains an illegal Char: #x0F</pre>
     * Sections: <pre>2.2</pre>
     * Version:
     *
     * @throws java.io.IOException if there is an I/O error
     */
    @Test
    public void testibm_not_wf_P02_ibm02n13xml()
                    throws IOException
    {
        try ( Reader reader = new FileReader( new File( testResourcesDir, "not-wf/P02/ibm02n13.xml" ) ) )
        {
            parser.setInput( reader );
            while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
                ;
            fail( "Tests a comment which contains an illegal Char: #x0F" );
        }
        catch ( XmlPullParserException e )
        {
            assertTrue( e.getMessage().contains( "Illegal character 0xf found in comment" ) );
        }
    }

    /**
     * Test ID: <pre>ibm-not-wf-P02-ibm02n14.xml</pre>
     * Test URI: <pre>not-wf/P02/ibm02n14.xml</pre>
     * Comment: <pre>Tests a comment which contains an illegal Char: #x10</pre>
     * Sections: <pre>2.2</pre>
     * Version:
     *
     * @throws java.io.IOException if there is an I/O error
     */
    @Test
    public void testibm_not_wf_P02_ibm02n14xml()
                    throws IOException
    {
        try ( Reader reader = new FileReader( new File( testResourcesDir, "not-wf/P02/ibm02n14.xml" ) ) )
        {
            parser.setInput( reader );
            while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
                ;
            fail( "Tests a comment which contains an illegal Char: #x10" );
        }
        catch ( XmlPullParserException e )
        {
            assertTrue( e.getMessage().contains( "Illegal character 0x10 found in comment" ) );
        }
    }

    /**
     * Test ID: <pre>ibm-not-wf-P02-ibm02n15.xml</pre>
     * Test URI: <pre>not-wf/P02/ibm02n15.xml</pre>
     * Comment: <pre>Tests a comment which contains an illegal Char: #x11</pre>
     * Sections: <pre>2.2</pre>
     * Version:
     *
     * @throws java.io.IOException if there is an I/O error
     */
    @Test
    public void testibm_not_wf_P02_ibm02n15xml()
                    throws IOException
    {
        try ( Reader reader = new FileReader( new File( testResourcesDir, "not-wf/P02/ibm02n15.xml" ) ) )
        {
            parser.setInput( reader );
            while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
                ;
            fail( "Tests a comment which contains an illegal Char: #x11" );
        }
        catch ( XmlPullParserException e )
        {
            assertTrue( e.getMessage().contains( "Illegal character 0x11 found in comment" ) );
        }
    }

    /**
     * Test ID: <pre>ibm-not-wf-P02-ibm02n16.xml</pre>
     * Test URI: <pre>not-wf/P02/ibm02n16.xml</pre>
     * Comment: <pre>Tests a comment which contains an illegal Char: #x12</pre>
     * Sections: <pre>2.2</pre>
     * Version:
     *
     * @throws java.io.IOException if there is an I/O error
     */
    @Test
    public void testibm_not_wf_P02_ibm02n16xml()
                    throws IOException
    {
        try ( Reader reader = new FileReader( new File( testResourcesDir, "not-wf/P02/ibm02n16.xml" ) ) )
        {
            parser.setInput( reader );
            while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
                ;
            fail( "Tests a comment which contains an illegal Char: #x12" );
        }
        catch ( XmlPullParserException e )
        {
            assertTrue( e.getMessage().contains( "Illegal character 0x12 found in comment" ) );
        }
    }

    /**
     * Test ID: <pre>ibm-not-wf-P02-ibm02n17.xml</pre>
     * Test URI: <pre>not-wf/P02/ibm02n17.xml</pre>
     * Comment: <pre>Tests a comment which contains an illegal Char: #x13</pre>
     * Sections: <pre>2.2</pre>
     * Version:
     *
     * @throws java.io.IOException if there is an I/O error
     */
    @Test
    public void testibm_not_wf_P02_ibm02n17xml()
                    throws IOException
    {
        try ( Reader reader = new FileReader( new File( testResourcesDir, "not-wf/P02/ibm02n17.xml" ) ) )
        {
            parser.setInput( reader );
            while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
                ;
            fail( "Tests a comment which contains an illegal Char: #x13" );
        }
        catch ( XmlPullParserException e )
        {
            assertTrue( e.getMessage().contains( "Illegal character 0x13 found in comment" ) );
        }
    }

    /**
     * Test ID: <pre>ibm-not-wf-P02-ibm02n18.xml</pre>
     * Test URI: <pre>not-wf/P02/ibm02n18.xml</pre>
     * Comment: <pre>Tests a comment which contains an illegal Char: #x14</pre>
     * Sections: <pre>2.2</pre>
     * Version:
     *
     * @throws java.io.IOException if there is an I/O error
     */
    @Test
    public void testibm_not_wf_P02_ibm02n18xml()
                    throws IOException
    {
        try ( Reader reader = new FileReader( new File( testResourcesDir, "not-wf/P02/ibm02n18.xml" ) ) )
        {
            parser.setInput( reader );
            while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
                ;
            fail( "Tests a comment which contains an illegal Char: #x14" );
        }
        catch ( XmlPullParserException e )
        {
            assertTrue( e.getMessage().contains( "Illegal character 0x14 found in comment" ) );
        }
    }

    /**
     * Test ID: <pre>ibm-not-wf-P02-ibm02n19.xml</pre>
     * Test URI: <pre>not-wf/P02/ibm02n19.xml</pre>
     * Comment: <pre>Tests a comment which contains an illegal Char: #x15</pre>
     * Sections: <pre>2.2</pre>
     * Version:
     *
     * @throws java.io.IOException if there is an I/O error
     */
    @Test
    public void testibm_not_wf_P02_ibm02n19xml()
                    throws IOException
    {
        try ( Reader reader = new FileReader( new File( testResourcesDir, "not-wf/P02/ibm02n19.xml" ) ) )
        {
            parser.setInput( reader );
            while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
                ;
            fail( "Tests a comment which contains an illegal Char: #x15" );
        }
        catch ( XmlPullParserException e )
        {
            assertTrue( e.getMessage().contains( "Illegal character 0x15 found in comment" ) );
        }
    }

    /**
     * Test ID: <pre>ibm-not-wf-P02-ibm02n20.xml</pre>
     * Test URI: <pre>not-wf/P02/ibm02n20.xml</pre>
     * Comment: <pre>Tests a comment which contains an illegal Char: #x16</pre>
     * Sections: <pre>2.2</pre>
     * Version:
     *
     * @throws java.io.IOException if there is an I/O error
     */
    @Test
    public void testibm_not_wf_P02_ibm02n20xml()
                    throws IOException
    {
        try ( Reader reader = new FileReader( new File( testResourcesDir, "not-wf/P02/ibm02n20.xml" ) ) )
        {
            parser.setInput( reader );
            while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
                ;
            fail( "Tests a comment which contains an illegal Char: #x16" );
        }
        catch ( XmlPullParserException e )
        {
            assertTrue( e.getMessage().contains( "Illegal character 0x16 found in comment" ) );
        }
    }

    /**
     * Test ID: <pre>ibm-not-wf-P02-ibm02n21.xml</pre>
     * Test URI: <pre>not-wf/P02/ibm02n21.xml</pre>
     * Comment: <pre>Tests a comment which contains an illegal Char: #x17</pre>
     * Sections: <pre>2.2</pre>
     * Version:
     *
     * @throws java.io.IOException if there is an I/O error
     */
    @Test
    public void testibm_not_wf_P02_ibm02n21xml()
                    throws IOException
    {
        try ( Reader reader = new FileReader( new File( testResourcesDir, "not-wf/P02/ibm02n21.xml" ) ) )
        {
            parser.setInput( reader );
            while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
                ;
            fail( "Tests a comment which contains an illegal Char: #x17" );
        }
        catch ( XmlPullParserException e )
        {
            assertTrue( e.getMessage().contains( "Illegal character 0x17 found in comment" ) );
        }
    }

    /**
     * Test ID: <pre>ibm-not-wf-P02-ibm02n22.xml</pre>
     * Test URI: <pre>not-wf/P02/ibm02n22.xml</pre>
     * Comment: <pre>Tests a comment which contains an illegal Char: #x18</pre>
     * Sections: <pre>2.2</pre>
     * Version:
     *
     * @throws java.io.IOException if there is an I/O error
     */
    @Test
    public void testibm_not_wf_P02_ibm02n22xml()
                    throws IOException
    {
        try ( Reader reader = new FileReader( new File( testResourcesDir, "not-wf/P02/ibm02n22.xml" ) ) )
        {
            parser.setInput( reader );
            while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
                ;
            fail( "Tests a comment which contains an illegal Char: #x18" );
        }
        catch ( XmlPullParserException e )
        {
            assertTrue( e.getMessage().contains( "Illegal character 0x18 found in comment" ) );
        }
    }

    /**
     * Test ID: <pre>ibm-not-wf-P02-ibm02n23.xml</pre>
     * Test URI: <pre>not-wf/P02/ibm02n23.xml</pre>
     * Comment: <pre>Tests a comment which contains an illegal Char: #x19</pre>
     * Sections: <pre>2.2</pre>
     * Version:
     *
     * @throws java.io.IOException if there is an I/O error
     */
    @Test
    public void testibm_not_wf_P02_ibm02n23xml()
                    throws IOException
    {
        try ( Reader reader = new FileReader( new File( testResourcesDir, "not-wf/P02/ibm02n23.xml" ) ) )
        {
            parser.setInput( reader );
            while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
                ;
            fail( "Tests a comment which contains an illegal Char: #x19" );
        }
        catch ( XmlPullParserException e )
        {
            assertTrue( e.getMessage().contains( "Illegal character 0x19 found in comment" ) );
        }
    }

    /**
     * Test ID: <pre>ibm-not-wf-P02-ibm02n24.xml</pre>
     * Test URI: <pre>not-wf/P02/ibm02n24.xml</pre>
     * Comment: <pre>Tests a comment which contains an illegal Char: #x1A</pre>
     * Sections: <pre>2.2</pre>
     * Version:
     *
     * @throws java.io.IOException if there is an I/O error
     */
    @Test
    public void testibm_not_wf_P02_ibm02n24xml()
                    throws IOException
    {
        try ( Reader reader = new FileReader( new File( testResourcesDir, "not-wf/P02/ibm02n24.xml" ) ) )
        {
            parser.setInput( reader );
            while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
                ;
            fail( "Tests a comment which contains an illegal Char: #x1A" );
        }
        catch ( XmlPullParserException e )
        {
            assertTrue( e.getMessage().contains( "Illegal character 0x1a found in comment" ) );
        }
    }

    /**
     * Test ID: <pre>ibm-not-wf-P02-ibm02n25.xml</pre>
     * Test URI: <pre>not-wf/P02/ibm02n25.xml</pre>
     * Comment: <pre>Tests a comment which contains an illegal Char: #x1B</pre>
     * Sections: <pre>2.2</pre>
     * Version:
     *
     * @throws java.io.IOException if there is an I/O error
     */
    @Test
    public void testibm_not_wf_P02_ibm02n25xml()
                    throws IOException
    {
        try ( Reader reader = new FileReader( new File( testResourcesDir, "not-wf/P02/ibm02n25.xml" ) ) )
        {
            parser.setInput( reader );
            while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
                ;
            fail( "Tests a comment which contains an illegal Char: #x1B" );
        }
        catch ( XmlPullParserException e )
        {
            assertTrue( e.getMessage().contains( "Illegal character 0x1b found in comment" ) );
        }
    }

    /**
     * Test ID: <pre>ibm-not-wf-P02-ibm02n26.xml</pre>
     * Test URI: <pre>not-wf/P02/ibm02n26.xml</pre>
     * Comment: <pre>Tests a comment which contains an illegal Char: #x1C</pre>
     * Sections: <pre>2.2</pre>
     * Version:
     *
     * @throws java.io.IOException if there is an I/O error
     */
    @Test
    public void testibm_not_wf_P02_ibm02n26xml()
                    throws IOException
    {
        try ( Reader reader = new FileReader( new File( testResourcesDir, "not-wf/P02/ibm02n26.xml" ) ) )
        {
            parser.setInput( reader );
            while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
                ;
            fail( "Tests a comment which contains an illegal Char: #x1C" );
        }
        catch ( XmlPullParserException e )
        {
            assertTrue( e.getMessage().contains( "Illegal character 0x1c found in comment" ) );
        }
    }

    /**
     * Test ID: <pre>ibm-not-wf-P02-ibm02n27.xml</pre>
     * Test URI: <pre>not-wf/P02/ibm02n27.xml</pre>
     * Comment: <pre>Tests a comment which contains an illegal Char: #x1D</pre>
     * Sections: <pre>2.2</pre>
     * Version:
     *
     * @throws java.io.IOException if there is an I/O error
     */
    @Test
    public void testibm_not_wf_P02_ibm02n27xml()
                    throws IOException
    {
        try ( Reader reader = new FileReader( new File( testResourcesDir, "not-wf/P02/ibm02n27.xml" ) ) )
        {
            parser.setInput( reader );
            while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
                ;
            fail( "Tests a comment which contains an illegal Char: #x1D" );
        }
        catch ( XmlPullParserException e )
        {
            assertTrue( e.getMessage().contains( "Illegal character 0x1d found in comment" ) );
        }
    }

    /**
     * Test ID: <pre>ibm-not-wf-P02-ibm02n28.xml</pre>
     * Test URI: <pre>not-wf/P02/ibm02n28.xml</pre>
     * Comment: <pre>Tests a comment which contains an illegal Char: #x1E</pre>
     * Sections: <pre>2.2</pre>
     * Version:
     *
     * @throws java.io.IOException if there is an I/O error
     */
    @Test
    public void testibm_not_wf_P02_ibm02n28xml()
                    throws IOException
    {
        try ( Reader reader = new FileReader( new File( testResourcesDir, "not-wf/P02/ibm02n28.xml" ) ) )
        {
            parser.setInput( reader );
            while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
                ;
            fail( "Tests a comment which contains an illegal Char: #x1E" );
        }
        catch ( XmlPullParserException e )
        {
            assertTrue( e.getMessage().contains( "Illegal character 0x1e found in comment" ) );
        }
    }

    /**
     * Test ID: <pre>ibm-not-wf-P02-ibm02n29.xml</pre>
     * Test URI: <pre>not-wf/P02/ibm02n29.xml</pre>
     * Comment: <pre>Tests a comment which contains an illegal Char: #x1F</pre>
     * Sections: <pre>2.2</pre>
     * Version:
     *
     * @throws java.io.IOException if there is an I/O error
     */
    @Test
    public void testibm_not_wf_P02_ibm02n29xml()
                    throws IOException
    {
        try ( Reader reader = new FileReader( new File( testResourcesDir, "not-wf/P02/ibm02n29.xml" ) ) )
        {
            parser.setInput( reader );
            while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
                ;
            fail( "Tests a comment which contains an illegal Char: #x1F" );
        }
        catch ( XmlPullParserException e )
        {
            assertTrue( e.getMessage().contains( "Illegal character 0x1f found in comment" ) );
        }
    }

    /**
     * Test ID: <pre>ibm-not-wf-P02-ibm02n30.xml</pre>
     * Test URI: <pre>not-wf/P02/ibm02n30.xml</pre>
     * Comment: <pre>Tests a comment which contains an illegal Char: #xD800</pre>
     * Sections: <pre>2.2</pre>
     * Version:
     *
     * @throws java.io.IOException if there is an I/O error
     *
     * NOTE: This test file is malformed into the original test suite, so I skip it.
     */
    //@Test
    public void testibm_not_wf_P02_ibm02n30xml()
                    throws IOException
    {
        try ( BufferedReader reader =
                        Files.newBufferedReader( Paths.get( testResourcesDir.getCanonicalPath(), "not-wf/P02/ibm02n30.xml" ),
                                                 Charset.forName( "ISO-8859-15" ) ) )
        {
            parser.setInput( reader );
            while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
                ;
            fail( "Tests a comment which contains an illegal Char: #xD800" );
        }
        catch ( XmlPullParserException e )
        {
            assertTrue( e.getMessage().contains( "Illegal character 0xd800 found in comment" ) );
        }
    }

    /**
     * Test ID: <pre>ibm-not-wf-P02-ibm02n31.xml</pre>
     * Test URI: <pre>not-wf/P02/ibm02n31.xml</pre>
     * Comment: <pre>Tests a comment which contains an illegal Char: #xDFFF</pre>
     * Sections: <pre>2.2</pre>
     * Version:
     *
     * @throws java.io.IOException if there is an I/O error
     *
     * NOTE: This test file is malformed into the original test suite, so I skip it.
     */
    //@Test
    public void testibm_not_wf_P02_ibm02n31xml()
                    throws IOException
    {
        try ( FileInputStream is = new FileInputStream( new File( testResourcesDir, "not-wf/P02/ibm02n31.xml" ) );
                        InputStreamReader reader = new InputStreamReader( is, "ISO-8859-15" ) )
        {
            parser.setInput( reader );
            while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
                ;
            fail( "Tests a comment which contains an illegal Char: #xDFFF" );
        }
        catch ( XmlPullParserException e )
        {
            assertTrue( e.getMessage().contains( "Illegal character 0xdfff found in comment" ) );
        }
    }

    /**
     * Test ID: <pre>ibm-not-wf-P02-ibm02n32.xml</pre>
     * Test URI: <pre>not-wf/P02/ibm02n32.xml</pre>
     * Comment: <pre>Tests a comment which contains an illegal Char: #xFFFE</pre>
     * Sections: <pre>2.2</pre>
     * Version:
     *
     * @throws java.io.IOException if there is an I/O error
     */
    @Test
    public void testibm_not_wf_P02_ibm02n32xml()
                    throws IOException
    {
        try ( FileInputStream is = new FileInputStream( new File( testResourcesDir, "not-wf/P02/ibm02n32.xml" ) );
                        InputStreamReader reader = new InputStreamReader( is, StandardCharsets.UTF_8 ) )
        {
            parser.setInput( reader );
            while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
                ;
            fail( "Tests a comment which contains an illegal Char: #xFFFE" );
        }
        catch ( XmlPullParserException e )
        {
            assertTrue( e.getMessage().contains( "Illegal character 0xfffe found in comment" ) );
        }
    }

    /**
     * Test ID: <pre>ibm-not-wf-P02-ibm02n33.xml</pre>
     * Test URI: <pre>not-wf/P02/ibm02n33.xml</pre>
     * Comment: <pre>Tests a comment which contains an illegal Char: #xFFFF</pre>
     * Sections: <pre>2.2</pre>
     * Version:
     *
     * @throws java.io.IOException if there is an I/O error
     */
    @Test
    public void testibm_not_wf_P02_ibm02n33xml()
                    throws IOException
    {
        try ( FileInputStream is = new FileInputStream( new File( testResourcesDir, "not-wf/P02/ibm02n33.xml" ) );
                        InputStreamReader reader = new InputStreamReader( is, StandardCharsets.UTF_8 ) )
        {
            parser.setInput( reader );
            while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
                ;
            fail( "Tests a comment which contains an illegal Char: #xFFFF" );
        }
        catch ( XmlPullParserException e )
        {
            assertTrue( e.getMessage().contains( "Illegal character 0xffff found in comment" ) );
        }
    }

}
