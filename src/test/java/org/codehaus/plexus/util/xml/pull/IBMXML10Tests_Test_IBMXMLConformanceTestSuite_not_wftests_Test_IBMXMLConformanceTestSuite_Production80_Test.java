package org.codehaus.plexus.util.xml.pull;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class that execute a particular set of tests associated to a TESCASES tag from the XML W3C Conformance Tests.
 * TESCASES PROFILE: <pre>IBM XML Conformance Test Suite - Production 80</pre>
 * XML test files base folder: <pre>xmlconf/ibm/</pre>
 *
 * @author <a href="mailto:belingueres@gmail.com">Gabriel Belingueres</a>
 * @version $Id: $Id
 * @since 3.4.0
 */
public class IBMXML10Tests_Test_IBMXMLConformanceTestSuite_not_wftests_Test_IBMXMLConformanceTestSuite_Production80_Test
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
   * Test ID: <pre>ibm-not-wf-P80-ibm80n01.xml</pre>
   * Test URI: <pre>not-wf/P80/ibm80n01.xml</pre>
   * Comment: <pre>Tests EncodingDecl with a required field missing. The leading white      space is missing in the EncodingDecl in the XMLDecl.</pre>
   * Sections: <pre>4.3.3</pre>
   * Version:
   *
   * @throws java.io.IOException if there is an I/O error
   */
  @Test
  public void testibm_not_wf_P80_ibm80n01xml()
      throws IOException
  {
      try ( Reader reader = new FileReader( new File( testResourcesDir, "not-wf/P80/ibm80n01.xml" ) ) )
      {
          parser.setInput( reader );
          while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
              ;
          fail( "Tests EncodingDecl with a required field missing. The leading white      space is missing in the EncodingDecl in the XMLDecl." );
      }
      catch ( XmlPullParserException e )
      {
          assertTrue( e.getMessage().contains( "expected a space after version and not e" ) );
      }
  }

  /**
   * Test ID: <pre>ibm-not-wf-P80-ibm80n02.xml</pre>
   * Test URI: <pre>not-wf/P80/ibm80n02.xml</pre>
   * Comment: <pre>Tests EncodingDecl with a required field missing. The "=" sign is      missing in the EncodingDecl in the XMLDecl.</pre>
   * Sections: <pre>4.3.3</pre>
   * Version:
   *
   * @throws java.io.IOException if there is an I/O error
   */
  @Test
  public void testibm_not_wf_P80_ibm80n02xml()
      throws IOException
  {
      try ( Reader reader = new FileReader( new File( testResourcesDir, "not-wf/P80/ibm80n02.xml" ) ) )
      {
          parser.setInput( reader );
          while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
              ;
          fail( "Tests EncodingDecl with a required field missing. The \"=\" sign is      missing in the EncodingDecl in the XMLDecl." );
      }
      catch ( XmlPullParserException e )
      {
          assertTrue( e.getMessage().contains( "expected equals sign (=) after encoding and not \"" ) );
      }
  }

  /**
   * Test ID: <pre>ibm-not-wf-P80-ibm80n03.xml</pre>
   * Test URI: <pre>not-wf/P80/ibm80n03.xml</pre>
   * Comment: <pre>Tests EncodingDecl with a required field missing. The double quoted      EncName are missing in the EncodingDecl in the XMLDecl.</pre>
   * Sections: <pre>4.3.3</pre>
   * Version:
   *
   * @throws java.io.IOException if there is an I/O error
   */
  @Test
  public void testibm_not_wf_P80_ibm80n03xml()
      throws IOException
  {
      try ( Reader reader = new FileReader( new File( testResourcesDir, "not-wf/P80/ibm80n03.xml" ) ) )
      {
          parser.setInput( reader );
          while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
              ;
          fail( "Tests EncodingDecl with a required field missing. The double quoted      EncName are missing in the EncodingDecl in the XMLDecl." );
      }
      catch ( XmlPullParserException e )
      {
          assertTrue( e.getMessage().contains( "expected apostrophe (') or quotation mark (\") after encoding and not ?" ) );
      }
  }

  /**
   * Test ID: <pre>ibm-not-wf-P80-ibm80n04.xml</pre>
   * Test URI: <pre>not-wf/P80/ibm80n04.xml</pre>
   * Comment: <pre>Tests EncodingDecl with wrong field ordering. The string "encoding="    occurs after the double quoted EncName in the EncodingDecl in the XMLDecl.</pre>
   * Sections: <pre>4.3.3</pre>
   * Version:
   *
   * @throws java.io.IOException if there is an I/O error
   */
  @Test
  public void testibm_not_wf_P80_ibm80n04xml()
      throws IOException
  {
      try ( Reader reader = new FileReader( new File( testResourcesDir, "not-wf/P80/ibm80n04.xml" ) ) )
      {
          parser.setInput( reader );
          while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
              ;
          fail( "Tests EncodingDecl with wrong field ordering. The string \"encoding=\"    occurs after the double quoted EncName in the EncodingDecl in the XMLDecl." );
      }
      catch ( XmlPullParserException e )
      {
          assertTrue( e.getMessage().contains( "unexpected character \"" ) );
      }
  }

  /**
   * Test ID: <pre>ibm-not-wf-P80-ibm80n05.xml</pre>
   * Test URI: <pre>not-wf/P80/ibm80n05.xml</pre>
   * Comment: <pre>Tests EncodingDecl with wrong field ordering. The "encoding" occurs     after the double quoted EncName in the EncodingDecl in the XMLDecl.</pre>
   * Sections: <pre>4.3.3</pre>
   * Version:
   *
   * @throws java.io.IOException if there is an I/O error
   */
  @Test
  public void testibm_not_wf_P80_ibm80n05xml()
      throws IOException
  {
      try ( Reader reader = new FileReader( new File( testResourcesDir, "not-wf/P80/ibm80n05.xml" ) ) )
      {
          parser.setInput( reader );
          while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
              ;
          fail( "Tests EncodingDecl with wrong field ordering. The \"encoding\" occurs     after the double quoted EncName in the EncodingDecl in the XMLDecl." );
      }
      catch ( XmlPullParserException e )
      {
          assertTrue( e.getMessage().contains( "unexpected character \"" ) );
      }
  }

  /**
   * Test ID: <pre>ibm-not-wf-P80-ibm80n06.xml</pre>
   * Test URI: <pre>not-wf/P80/ibm80n06.xml</pre>
   * Comment: <pre>Tests EncodingDecl with wrong key word. The string "Encoding" is      used as the key word in the EncodingDecl in the XMLDecl.</pre>
   * Sections: <pre>4.3.3</pre>
   * Version:
   *
   * @throws java.io.IOException if there is an I/O error
   */
  @Test
  public void testibm_not_wf_P80_ibm80n06xml()
      throws IOException
  {
      try ( Reader reader = new FileReader( new File( testResourcesDir, "not-wf/P80/ibm80n06.xml" ) ) )
      {
          parser.setInput( reader );
          while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
              ;
          fail( "Tests EncodingDecl with wrong key word. The string \"Encoding\" is      used as the key word in the EncodingDecl in the XMLDecl." );
      }
      catch ( XmlPullParserException e )
      {
          assertTrue( e.getMessage().contains( "unexpected character E" ) );
      }
  }

}
