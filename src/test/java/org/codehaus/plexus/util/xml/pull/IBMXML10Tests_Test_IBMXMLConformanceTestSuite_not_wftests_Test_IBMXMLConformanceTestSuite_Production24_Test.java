package org.codehaus.plexus.util.xml.pull;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class that execute a particular set of tests associated to a TESCASES tag from the XML W3C Conformance Tests.
 * TESCASES PROFILE: <pre>IBM XML Conformance Test Suite - Production 24</pre>
 * XML test files base folder: <pre>xmlconf/ibm/</pre>
 *
 * @author <a href="mailto:belingueres@gmail.com">Gabriel Belingueres</a>
 * @version $Id: $Id
 * @since 3.4.0
 */
public class IBMXML10Tests_Test_IBMXMLConformanceTestSuite_not_wftests_Test_IBMXMLConformanceTestSuite_Production24_Test
{

    final static File testResourcesDir = new File( "src/test/resources/", "xmlconf/ibm/" );

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
     * Test ID: <pre>ibm-not-wf-P24-ibm24n01.xml</pre>
     * Test URI: <pre>not-wf/P24/ibm24n01.xml</pre>
     * Comment: <pre>Tests VersionInfo with a required field missing. The VersionNum is     missing in the VersionInfo in the XMLDecl.</pre>
     * Sections: <pre>2.8</pre>
     * Version:
     *
     * @throws java.io.IOException if there is an I/O error
     */
    @Test
    public void testibm_not_wf_P24_ibm24n01xml()
        throws IOException
    {
        try ( Reader reader = new FileReader( new File( testResourcesDir, "not-wf/P24/ibm24n01.xml" ) ) )
        {
            parser.setInput( reader );
            while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
                ;
            fail( "Tests VersionInfo with a required field missing. The VersionNum is     missing in the VersionInfo in the XMLDecl." );
        }
        catch ( XmlPullParserException e )
        {
            assertTrue( e.getMessage().contains( "expected apostrophe (') or quotation mark (\") after version and not ?" ) );
        }
    }

  /**
   * Test ID: <pre>ibm-not-wf-P24-ibm24n02.xml</pre>
   * Test URI: <pre>not-wf/P24/ibm24n02.xml</pre>
   * Comment: <pre>Tests VersionInfo with a required field missing. The white space is     missing between the key word "xml" and the VersionInfo in the XMLDecl.</pre>
   * Sections: <pre>2.8</pre>
   * Version:
   *
   * @throws java.io.IOException if there is an I/O error
   */
  @Test
  public void testibm_not_wf_P24_ibm24n02xml()
      throws IOException
  {
      try ( Reader reader = new FileReader( new File( testResourcesDir, "not-wf/P24/ibm24n02.xml" ) ) )
      {
          parser.setInput( reader );
          while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
              ;
          fail( "Tests VersionInfo with a required field missing. The white space is     missing between the key word \"xml\" and the VersionInfo in the XMLDecl." );
      }
      catch ( XmlPullParserException e )
      {
          assertTrue( e.getMessage().contains( "expected v in version and not ?" ) );
      }
  }

  /**
   * Test ID: <pre>ibm-not-wf-P24-ibm24n03.xml</pre>
   * Test URI: <pre>not-wf/P24/ibm24n03.xml</pre>
   * Comment: <pre>Tests VersionInfo with a required field missing. The "="      (equal sign) is missing between the key word "version" and the VersionNum.</pre>
   * Sections: <pre>2.8</pre>
   * Version:
   *
   * @throws java.io.IOException if there is an I/O error
   */
  @Test
  public void testibm_not_wf_P24_ibm24n03xml()
      throws IOException
  {
      try ( Reader reader = new FileReader( new File( testResourcesDir, "not-wf/P24/ibm24n03.xml" ) ) )
      {
          parser.setInput( reader );
          while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
              ;
          fail( "Tests VersionInfo with a required field missing. The \"=\"      (equal sign) is missing between the key word \"version\" and the VersionNum." );
      }
      catch ( XmlPullParserException e )
      {
          assertTrue( e.getMessage().contains( "expected equals sign (=) after version and not \\'" ) );
      }
  }

  /**
   * Test ID: <pre>ibm-not-wf-P24-ibm24n04.xml</pre>
   * Test URI: <pre>not-wf/P24/ibm24n04.xml</pre>
   * Comment: <pre>Tests VersionInfo with wrong field ordering. The VersionNum     occurs before "=" and "version".</pre>
   * Sections: <pre>2.8</pre>
   * Version:
   *
   * @throws java.io.IOException if there is an I/O error
   */
  @Test
  public void testibm_not_wf_P24_ibm24n04xml()
      throws IOException
  {
      try ( Reader reader = new FileReader( new File( testResourcesDir, "not-wf/P24/ibm24n04.xml" ) ) )
      {
          parser.setInput( reader );
          while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
              ;
          fail( "Tests VersionInfo with wrong field ordering. The VersionNum     occurs before \"=\" and \"version\"." );
      }
      catch ( XmlPullParserException e )
      {
          assertTrue( e.getMessage().contains( "expected v in version and not \\'" ) );
      }
  }

  /**
   * Test ID: <pre>ibm-not-wf-P24-ibm24n05.xml</pre>
   * Test URI: <pre>not-wf/P24/ibm24n05.xml</pre>
   * Comment: <pre>Tests VersionInfo with wrong field ordering. The "=" occurs     after "version" and the VersionNum.</pre>
   * Sections: <pre>2.8</pre>
   * Version:
   *
   * @throws java.io.IOException if there is an I/O error
   */
  @Test
  public void testibm_not_wf_P24_ibm24n05xml()
      throws IOException
  {
      try ( Reader reader = new FileReader( new File( testResourcesDir, "not-wf/P24/ibm24n05.xml" ) ) )
      {
          parser.setInput( reader );
          while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
              ;
          fail( "Tests VersionInfo with wrong field ordering. The \"=\" occurs     after \"version\" and the VersionNum." );
      }
      catch ( XmlPullParserException e )
      {
          assertTrue( e.getMessage().contains( "expected equals sign (=) after version and not \\'" ) );
      }
  }

  /**
   * Test ID: <pre>ibm-not-wf-P24-ibm24n06.xml</pre>
   * Test URI: <pre>not-wf/P24/ibm24n06.xml</pre>
   * Comment: <pre>Tests VersionInfo with the wrong key word "Version".</pre>
   * Sections: <pre>2.8</pre>
   * Version:
   *
   * @throws java.io.IOException if there is an I/O error
   */
  @Test
  public void testibm_not_wf_P24_ibm24n06xml()
      throws IOException
  {
      try ( Reader reader = new FileReader( new File( testResourcesDir, "not-wf/P24/ibm24n06.xml" ) ) )
      {
          parser.setInput( reader );
          while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
              ;
          fail( "Tests VersionInfo with the wrong key word \"Version\"." );
      }
      catch ( XmlPullParserException e )
      {
          assertTrue( e.getMessage().contains( "expected v in version and not V" ) );
      }
  }

  /**
   * Test ID: <pre>ibm-not-wf-P24-ibm24n07.xml</pre>
   * Test URI: <pre>not-wf/P24/ibm24n07.xml</pre>
   * Comment: <pre>Tests VersionInfo with the wrong key word "versioN".</pre>
   * Sections: <pre>2.8</pre>
   * Version:
   *
   * @throws java.io.IOException if there is an I/O error
   */
  @Test
  public void testibm_not_wf_P24_ibm24n07xml()
      throws IOException
  {
      try ( Reader reader = new FileReader( new File( testResourcesDir, "not-wf/P24/ibm24n07.xml" ) ) )
      {
          parser.setInput( reader );
          while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
              ;
          fail( "Tests VersionInfo with the wrong key word \"versioN\"." );
      }
      catch ( XmlPullParserException e )
      {
          assertTrue( e.getMessage().contains( "expected n in version and not N" ) );
      }
  }

  /**
   * Test ID: <pre>ibm-not-wf-P24-ibm24n08.xml</pre>
   * Test URI: <pre>not-wf/P24/ibm24n08.xml</pre>
   * Comment: <pre>Tests VersionInfo with mismatched quotes around the VersionNum.      version = '1.0" is used as the VersionInfo.</pre>
   * Sections: <pre>2.8</pre>
   * Version:
   *
   * @throws java.io.IOException if there is an I/O error
   */
  @Test
  public void testibm_not_wf_P24_ibm24n08xml()
      throws IOException
  {
      try ( Reader reader = new FileReader( new File( testResourcesDir, "not-wf/P24/ibm24n08.xml" ) ) )
      {
          parser.setInput( reader );
          while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
              ;
          fail( "Tests VersionInfo with mismatched quotes around the VersionNum.      version = '1.0\" is used as the VersionInfo." );
      }
      catch ( XmlPullParserException e )
      {
          assertTrue( e.getMessage().contains( "<?xml version value expected to be in ([a-zA-Z0-9_.:] | '-') not \"" ) );
      }
  }

  /**
   * Test ID: <pre>ibm-not-wf-P24-ibm24n09.xml</pre>
   * Test URI: <pre>not-wf/P24/ibm24n09.xml</pre>
   * Comment: <pre>Tests VersionInfo with mismatched quotes around the VersionNum.      The closing bracket for the VersionNum is missing.</pre>
   * Sections: <pre>2.8</pre>
   * Version:
   *
   * @throws java.io.IOException if there is an I/O error
   */
  @Test
  public void testibm_not_wf_P24_ibm24n09xml()
      throws IOException
  {
      try ( Reader reader = new FileReader( new File( testResourcesDir, "not-wf/P24/ibm24n09.xml" ) ) )
      {
          parser.setInput( reader );
          while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
              ;
          fail( "Tests VersionInfo with mismatched quotes around the VersionNum.      The closing bracket for the VersionNum is missing." );
      }
      catch ( XmlPullParserException e )
      {
          assertTrue( e.getMessage().contains( "<?xml version value expected to be in ([a-zA-Z0-9_.:] | '-') not  " ) );
      }
  }

}
