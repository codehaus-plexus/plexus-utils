package org.codehaus.plexus.util.xml.pull;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class that execute a particular set of tests associated to a TESCASES tag from the XML W3C Conformance Tests.
 * TESCASES PROFILE: <pre>Bjoern Hoehrmann via HST 2013-09-18</pre>
 * XML test files base folder: <pre>xmlconf/eduni/misc/</pre>
 *
 * @author <a href="mailto:belingueres@gmail.com">Gabriel Belingueres</a>
 * @version $Id: $Id
 * @since 3.4.0
 */
public class eduni_misc_Test_BjoernHoehrmannviaHST2013_09_18_Test
{

    final static File testResourcesDir = new File("src/test/resources/", "xmlconf/eduni/misc/");

    MXParser parser;

    /**
     * <p>setUp.</p>
     */
    @Before
    public void setUp()
    {
        parser = new MXParser();
    }

    /**
     * Test ID: <pre>hst-bh-001</pre>
     * Test URI: <pre>001.xml</pre>
     * Comment: <pre>decimal charref &#38;#62; 10FFFF, indeed &#38;#62; max 32 bit integer, checking for recovery from possible overflow</pre>
     * Sections: <pre>2.2 [2], 4.1 [66]</pre>
     * Version:
     *
     * @throws java.io.IOException if there is an I/O error
     */
    @Test
    public void testhst_bh_001()
        throws IOException
    {
        try ( Reader reader = new FileReader( new File( testResourcesDir, "001.xml" ) ) )
        {
            parser.setInput( reader );
            while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
                ;
            fail( "decimal charref > 10FFFF, indeed > max 32 bit integer, checking for recovery from possible overflow" );
        }
        catch ( XmlPullParserException e )
        {
            assertTrue( e.getMessage().contains( "character reference (with hex value FF000000F6) is invalid" ) );
        }
    }

    /**
     * Test ID: <pre>hst-bh-002</pre>
     * Test URI: <pre>002.xml</pre>
     * Comment: <pre>hex charref &#38;#62; 10FFFF, indeed &#38;#62; max 32 bit integer, checking for recovery from possible overflow</pre>
     * Sections: <pre>2.2 [2], 4.1 [66]</pre>
     * Version:
     *
     * @throws java.io.IOException if there is an I/O error
     */
    @Test
    public void testhst_bh_002()
        throws IOException
    {
        try ( Reader reader = new FileReader( new File( testResourcesDir, "002.xml" ) ) )
        {
            parser.setInput( reader );
            while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
                ;
            fail( "hex charref > 10FFFF, indeed > max 32 bit integer, checking for recovery from possible overflow" );
        }
        catch ( XmlPullParserException e )
        {
            assertTrue( e.getMessage().contains( "character reference (with decimal value 4294967542) is invalid" ) );
        }
    }

    /**
     * Test ID: <pre>hst-bh-003</pre>
     * Test URI: <pre>003.xml</pre>
     * Comment: <pre>decimal charref &#38;#62; 10FFFF, indeed &#38;#62; max 64 bit integer, checking for recovery from possible overflow</pre>
     * Sections: <pre>2.2 [2], 4.1 [66]</pre>
     * Version:
     *
     * @throws java.io.IOException if there is an I/O error
     */
    @Test
    public void testhst_bh_003()
        throws IOException
    {
        try ( Reader reader = new FileReader( new File( testResourcesDir, "003.xml" ) ) )
        {
            parser.setInput( reader );
            while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
                ;
            fail( "decimal charref > 10FFFF, indeed > max 64 bit integer, checking for recovery from possible overflow" );
        }
        catch ( XmlPullParserException e )
        {
            assertTrue( e.getMessage().contains( "character reference (with hex value FFFFFFFF000000F6) is invalid" ) );
        }
    }

    /**
     * Test ID: <pre>hst-bh-004</pre>
     * Test URI: <pre>004.xml</pre>
     * Comment: <pre>hex charref &#38;#62; 10FFFF, indeed &#38;#62; max 64 bit integer, checking for recovery from possible overflow</pre>
     * Sections: <pre>2.2 [2], 4.1 [66]</pre>
     * Version:
     *
     * @throws java.io.IOException if there is an I/O error
     */
    @Test
    public void testhst_bh_004()
        throws IOException
    {
        try ( Reader reader = new FileReader( new File( testResourcesDir, "004.xml" ) ) )
        {
            parser.setInput( reader );
            while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
                ;
            fail( "hex charref > 10FFFF, indeed > max 64 bit integer, checking for recovery from possible overflow" );
        }
        catch ( XmlPullParserException e )
        {
            assertTrue( e.getMessage().contains( "character reference (with decimal value 18446744073709551862) is invalid" ) );
        }
    }

    /**
     * Test ID: <pre>hst-bh-005</pre>
     * Test URI: <pre>005.xml</pre>
     * Comment: <pre>xmlns:xml is an attribute as far as validation is concerned and must be declared</pre>
     * Sections: <pre>3.1 [41]</pre>
     * Version:
     *
     * @throws java.io.IOException if there is an I/O error
     *
     * NOTE: This test is SKIPPED as MXParser do not supports DOCDECL parsing.
     */
    // @Test
    public void testhst_bh_005()
        throws IOException
    {
        try ( Reader reader = new FileReader( new File( testResourcesDir, "005.xml" ) ) )
        {
            parser.setInput( reader );
            while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
                ;
            fail( "xmlns:xml is an attribute as far as validation is concerned and must be declared" );
        }
        catch ( XmlPullParserException e )
        {
            assertTrue( true );
        }
    }

    /**
     * Test ID: <pre>hst-bh-006</pre>
     * Test URI: <pre>006.xml</pre>
     * Comment: <pre>xmlns:foo is an attribute as far as validation is concerned and must be declared</pre>
     * Sections: <pre>3.1 [41]</pre>
     * Version:
     *
     * @throws java.io.IOException if there is an I/O error
     *
     * NOTE: This test is SKIPPED as MXParser do not supports DOCDECL parsing.
     */
    // @Test
    public void testhst_bh_006()
        throws IOException
    {
        try ( Reader reader = new FileReader( new File( testResourcesDir, "006.xml" ) ) )
        {
            parser.setInput( reader );
            while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
                ;
            fail( "xmlns:foo is an attribute as far as validation is concerned and must be declared" );
        }
        catch ( XmlPullParserException e )
        {
            assertTrue( true );
        }
    }

    /**
     * Test ID: <pre>hst-lhs-007</pre>
     * Test URI: <pre>007.xml</pre>
     * Comment: <pre>UTF-8 BOM plus xml decl of iso-8859-1 incompatible</pre>
     * Sections: <pre>4.3.3</pre>
     * Version:
     *
     * @throws java.io.IOException if there is an I/O error
     */
    @Test
    public void testhst_lhs_007()
        throws IOException
    {
        try ( FileInputStream is = new FileInputStream( new File( testResourcesDir, "007.xml" ) );
                        InputStreamReader reader = new InputStreamReader( is, StandardCharsets.UTF_8 ) )
        {
            parser.setInput( reader );
            while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
                ;
            fail( "UTF-8 BOM plus xml decl of iso-8859-1 incompatible" );
        }
        catch ( XmlPullParserException e )
        {
            assertTrue( e.getMessage().contains( "UTF-8 BOM plus xml decl of iso-8859-1 is incompatible" ) );
        }
    }

    /**
     * Test ID: <pre>hst-lhs-008</pre>
     * Test URI: <pre>008.xml</pre>
     * Comment: <pre>UTF-16 BOM plus xml decl of utf-8 (using UTF-16 coding) incompatible</pre>
     * Sections: <pre>4.3.3</pre>
     * Version:
     *
     * @throws java.io.IOException if there is an I/O error
     */
    @Test
    public void testhst_lhs_008()
        throws IOException
    {
        try ( FileInputStream is = new FileInputStream( new File( testResourcesDir, "008.xml" ) );
                        InputStreamReader reader = new InputStreamReader( is, StandardCharsets.UTF_16 ) )
        {
            parser.setInput( reader );
            while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
                ;
            fail( "UTF-16 BOM plus xml decl of utf-8 (using UTF-16 coding) incompatible" );
        }
        catch ( XmlPullParserException e )
        {
            assertTrue( e.getMessage().contains( "UTF-16 BOM plus xml decl of utf-8 is incompatible" ) );
        }
    }

    /**
     * Test ID: <pre>hst-lhs-009</pre>
     * Test URI: <pre>009.xml</pre>
     * Comment: <pre>UTF-16 BOM plus xml decl of utf-8 (using UTF-8 coding) incompatible</pre>
     * Sections: <pre>4.3.3</pre>
     * Version:
     *
     * @throws java.io.IOException if there is an I/O error
     */
    @Test
    public void testhst_lhs_009()
        throws IOException
    {
        try ( FileInputStream is = new FileInputStream( new File( testResourcesDir, "009.xml" ) );
                        InputStreamReader reader = new InputStreamReader( is, StandardCharsets.UTF_8 ) )
       {
            parser.setInput( reader );
            while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
                ;
            fail( "UTF-16 BOM plus xml decl of utf-8 (using UTF-8 coding) incompatible" );
        }
        catch ( XmlPullParserException e )
        {
            assertTrue( e.getMessage().contains( "UTF-16 BOM in a UTF-8 encoded file is incompatible" ) );
        }
    }

}
