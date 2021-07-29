
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
 * TESCASES PROFILE: <pre>IBM XML Conformance Test Suite - Production 66</pre>
 * XML test files base folder: <pre>xmlconf/ibm/</pre>
 *
 * @author <a href="mailto:belingueres@gmail.com">Gabriel Belingueres</a>
 * @version $Id: $Id
 * @since 3.4.0
 */
public class IBMXML10Tests_Test_IBMXMLConformanceTestSuite_not_wftests_Test_IBMXMLConformanceTestSuite_Production66_Test
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
   * Test ID: <pre>ibm-not-wf-P66-ibm66n01.xml</pre>
   * Test URI: <pre>not-wf/P66/ibm66n01.xml</pre>
   * Comment: <pre>Tests CharRef with an illegal character referred to. The "#002f" is      used as the referred character in the CharRef in the EntityDecl in the DTD.</pre>
   * Sections: <pre>4.1</pre>
   * Version:
   *
   * @throws java.io.IOException if there is an I/O error
   */
  @Test
  public void testibm_not_wf_P66_ibm66n01xml()
      throws IOException
  {
      try ( Reader reader = new FileReader( new File( testResourcesDir, "not-wf/P66/ibm66n01.xml" ) ) )
      {
          parser.setInput( reader );
          while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
              ;
          fail( "Tests CharRef with an illegal character referred to. The \"#002f\" is      used as the referred character in the CharRef in the EntityDecl in the DTD." );
      }
      catch ( XmlPullParserException e )
      {
          assertTrue( e.getMessage().contains( "character reference (with decimal value) may not contain f" ) );
      }
  }

  /**
   * Test ID: <pre>ibm-not-wf-P66-ibm66n02.xml</pre>
   * Test URI: <pre>not-wf/P66/ibm66n02.xml</pre>
   * Comment: <pre>Tests CharRef with the semicolon character missing. The semicolon      character is missing at the end of the CharRef in the attribute value in     the STag of element "root".</pre>
   * Sections: <pre>4.1</pre>
   * Version:
   *
   * @throws java.io.IOException if there is an I/O error
   */
  @Test
  public void testibm_not_wf_P66_ibm66n02xml()
      throws IOException
  {
      try ( Reader reader = new FileReader( new File( testResourcesDir, "not-wf/P66/ibm66n02.xml" ) ) )
      {
          parser.setInput( reader );
          while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
              ;
          fail( "Tests CharRef with the semicolon character missing. The semicolon      character is missing at the end of the CharRef in the attribute value in     the STag of element \"root\"." );
      }
      catch ( XmlPullParserException e )
      {
          assertTrue( e.getMessage().contains( "character reference (with hex value) may not contain \"" ) );
      }
  }

  /**
   * Test ID: <pre>ibm-not-wf-P66-ibm66n03.xml</pre>
   * Test URI: <pre>not-wf/P66/ibm66n03.xml</pre>
   * Comment: <pre>Tests CharRef with an illegal character referred to. The "49" is      used as the referred character in the CharRef in the EntityDecl in the DTD.</pre>
   * Sections: <pre>4.1</pre>
   * Version:
   *
   * @throws java.io.IOException if there is an I/O error
   */
  @Test
  public void testibm_not_wf_P66_ibm66n03xml()
      throws IOException
  {
      try ( Reader reader = new FileReader( new File( testResourcesDir, "not-wf/P66/ibm66n03.xml" ) ) )
      {
          parser.setInput( reader );
          while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
              ;
          fail( "Tests CharRef with an illegal character referred to. The \"49\" is      used as the referred character in the CharRef in the EntityDecl in the DTD." );
      }
      catch ( XmlPullParserException e )
      {
          assertTrue( e.getMessage().contains( "entity reference names can not start with character '4'" ) );
      }
  }

  /**
   * Test ID: <pre>ibm-not-wf-P66-ibm66n04.xml</pre>
   * Test URI: <pre>not-wf/P66/ibm66n04.xml</pre>
   * Comment: <pre>Tests CharRef with an illegal character referred to. The "#5~0" is      used as the referred character in the attribute value in the EmptyElemTag     of the element "root".</pre>
   * Sections: <pre>4.1</pre>
   * Version:
   *
   * @throws java.io.IOException if there is an I/O error
   */
  @Test
  public void testibm_not_wf_P66_ibm66n04xml()
      throws IOException
  {
      try ( Reader reader = new FileReader( new File( testResourcesDir, "not-wf/P66/ibm66n04.xml" ) ) )
      {
          parser.setInput( reader );
          while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
              ;
          fail( "Tests CharRef with an illegal character referred to. The \"#5~0\" is      used as the referred character in the attribute value in the EmptyElemTag     of the element \"root\"." );
      }
      catch ( XmlPullParserException e )
      {
          assertTrue( e.getMessage().contains( "character reference (with decimal value) may not contain ~" ) );
      }
  }

  /**
   * Test ID: <pre>ibm-not-wf-P66-ibm66n05.xml</pre>
   * Test URI: <pre>not-wf/P66/ibm66n05.xml</pre>
   * Comment: <pre>Tests CharRef with an illegal character referred to. The "#x002g" is     used as the referred character in the CharRef in the EntityDecl in the DTD.</pre>
   * Sections: <pre>4.1</pre>
   * Version:
   *
   * @throws java.io.IOException if there is an I/O error
   * @throws java.io.FileNotFoundException if any.
   * @throws org.codehaus.plexus.util.xml.pull.XmlPullParserException if any.
   */
  @Test
  public void testibm_not_wf_P66_ibm66n05xml()
      throws FileNotFoundException, IOException, XmlPullParserException
  {
      try ( Reader reader = new FileReader( new File( testResourcesDir, "not-wf/P66/ibm66n05.xml" ) ) )
      {
          parser.setInput( reader );
          while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
              ;
          fail( "Tests CharRef with an illegal character referred to. The \"#x002g\" is     used as the referred character in the CharRef in the EntityDecl in the DTD." );
      }
      catch ( XmlPullParserException e )
      {
          assertTrue( e.getMessage().contains( "character reference (with hex value) may not contain g" ) );
      }
  }

  /**
   * Test ID: <pre>ibm-not-wf-P66-ibm66n06.xml</pre>
   * Test URI: <pre>not-wf/P66/ibm66n06.xml</pre>
   * Comment: <pre>Tests CharRef with an illegal character referred to. The "#x006G" is     used as the referred character in the attribute value in the EmptyElemTag      of the element "root".</pre>
   * Sections: <pre>4.1</pre>
   * Version:
   *
   * @throws java.io.IOException if there is an I/O error
   */
  @Test
  public void testibm_not_wf_P66_ibm66n06xml()
      throws IOException
  {
      try ( Reader reader = new FileReader( new File( testResourcesDir, "not-wf/P66/ibm66n06.xml" ) ) )
      {
          parser.setInput( reader );
          while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
              ;
          fail( "Tests CharRef with an illegal character referred to. The \"#x006G\" is     used as the referred character in the attribute value in the EmptyElemTag      of the element \"root\"." );
      }
      catch ( XmlPullParserException e )
      {
          assertTrue( e.getMessage().contains( "character reference (with hex value) may not contain G" ) );
      }
  }

  /**
   * Test ID: <pre>ibm-not-wf-P66-ibm66n07.xml</pre>
   * Test URI: <pre>not-wf/P66/ibm66n07.xml</pre>
   * Comment: <pre>Tests CharRef with an illegal character referred to. The "#0=2f" is      used as the referred character in the CharRef in the EntityDecl in the DTD.</pre>
   * Sections: <pre>4.1</pre>
   * Version:
   *
   * @throws java.io.IOException if there is an I/O error
   */
  @Test
  public void testibm_not_wf_P66_ibm66n07xml()
      throws IOException
  {
      try ( Reader reader = new FileReader( new File( testResourcesDir, "not-wf/P66/ibm66n07.xml" ) ) )
      {
          parser.setInput( reader );
          while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
              ;
          fail( "Tests CharRef with an illegal character referred to. The \"#0=2f\" is      used as the referred character in the CharRef in the EntityDecl in the DTD." );
      }
      catch ( XmlPullParserException e )
      {
          assertTrue( e.getMessage().contains( "character reference (with hex value) may not contain =" ) );
      }
  }

  /**
   * Test ID: <pre>ibm-not-wf-P66-ibm66n08.xml</pre>
   * Test URI: <pre>not-wf/P66/ibm66n08.xml</pre>
   * Comment: <pre>Tests CharRef with an illegal character referred to. The "#56.0" is      used as the referred character in the attribute value in the EmptyElemTag      of the element "root".</pre>
   * Sections: <pre>4.1</pre>
   * Version:
   *
   * @throws java.io.IOException if there is an I/O error
   */
  @Test
  public void testibm_not_wf_P66_ibm66n08xml()
      throws IOException
  {
      try ( Reader reader = new FileReader( new File( testResourcesDir, "not-wf/P66/ibm66n08.xml" ) ) )
      {
          parser.setInput( reader );
          while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
              ;
          fail( "Tests CharRef with an illegal character referred to. The \"#56.0\" is      used as the referred character in the attribute value in the EmptyElemTag      of the element \"root\"." );
      }
      catch ( XmlPullParserException e )
      {
          assertTrue( e.getMessage().contains( "character reference (with decimal value) may not contain ." ) );
      }
  }

  /**
   * Test ID: <pre>ibm-not-wf-P66-ibm66n09.xml</pre>
   * Test URI: <pre>not-wf/P66/ibm66n09.xml</pre>
   * Comment: <pre>Tests CharRef with an illegal character referred to. The "#x00/2f"      is used as the referred character in the CharRef in the EntityDecl in the      DTD.</pre>
   * Sections: <pre>4.1</pre>
   * Version:
   *
   * @throws java.io.IOException if there is an I/O error
   */
  @Test
  public void testibm_not_wf_P66_ibm66n09xml()
      throws IOException
  {
      try ( Reader reader = new FileReader( new File( testResourcesDir, "not-wf/P66/ibm66n09.xml" ) ) )
      {
          parser.setInput( reader );
          while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
              ;
          fail( "Tests CharRef with an illegal character referred to. The \"#x00/2f\"      is used as the referred character in the CharRef in the EntityDecl in the      DTD." );
      }
      catch ( XmlPullParserException e )
      {
          assertTrue( e.getMessage().contains( "character reference (with hex value) may not contain /" ) );
      }
  }

  /**
   * Test ID: <pre>ibm-not-wf-P66-ibm66n10.xml</pre>
   * Test URI: <pre>not-wf/P66/ibm66n10.xml</pre>
   * Comment: <pre>Tests CharRef with an illegal character referred to. The "#51)" is      used as the referred character in the attribute value in the EmptyElemTag      of the element "root".</pre>
   * Sections: <pre>4.1</pre>
   * Version:
   *
   * @throws java.io.IOException if there is an I/O error
   */
  @Test
  public void testibm_not_wf_P66_ibm66n10xml()
      throws IOException
  {
      try ( Reader reader = new FileReader( new File( testResourcesDir, "not-wf/P66/ibm66n10.xml" ) ) )
      {
          parser.setInput( reader );
          while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
              ;
          fail( "Tests CharRef with an illegal character referred to. The \"#51)\" is      used as the referred character in the attribute value in the EmptyElemTag      of the element \"root\"." );
      }
      catch ( XmlPullParserException e )
      {
          assertTrue( e.getMessage().contains( "character reference (with decimal value) may not contain )" ) );
      }
  }

  /**
   * Test ID: <pre>ibm-not-wf-P66-ibm66n11.xml</pre>
   * Test URI: <pre>not-wf/P66/ibm66n11.xml</pre>
   * Comment: <pre>Tests CharRef with an illegal character referred to. The "#00 2f"     is used as the referred character in the CharRef in the EntityDecl in the      DTD.</pre>
   * Sections: <pre>4.1</pre>
   * Version:
   *
   * @throws java.io.IOException if there is an I/O error
   */
  @Test
  public void testibm_not_wf_P66_ibm66n11xml()
      throws IOException
  {
      try ( Reader reader = new FileReader( new File( testResourcesDir, "not-wf/P66/ibm66n11.xml" ) ) )
      {
          parser.setInput( reader );
          while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
              ;
          fail( "Tests CharRef with an illegal character referred to. The \"#00 2f\"     is used as the referred character in the CharRef in the EntityDecl in the      DTD." );
      }
      catch ( XmlPullParserException e )
      {
          assertTrue( e.getMessage().contains( "character reference (with hex value) may not contain  " ) );
      }
  }

  /**
   * Test ID: <pre>ibm-not-wf-P66-ibm66n12.xml</pre>
   * Test URI: <pre>not-wf/P66/ibm66n12.xml</pre>
   * Comment: <pre>Tests CharRef with an illegal character referred to. The "#x0000"      is used as the referred character in the attribute value in the EmptyElemTag     of the element "root".</pre>
   * Sections: <pre>4.1</pre>
   * Version:
   *
   * @throws java.io.IOException if there is an I/O error
   */
  @Test
  public void testibm_not_wf_P66_ibm66n12xml()
      throws IOException
  {
      try ( Reader reader = new FileReader( new File( testResourcesDir, "not-wf/P66/ibm66n12.xml" ) ) )
      {
          parser.setInput( reader );
          while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
              ;
          fail( "Tests CharRef with an illegal character referred to. The \"#x0000\"      is used as the referred character in the attribute value in the EmptyElemTag     of the element \"root\"." );
      }
      catch ( XmlPullParserException e )
      {
          assertTrue( e.getMessage().contains( "character reference (with hex value 0000) is invalid" ) );
      }
  }

  /**
   * Test ID: <pre>ibm-not-wf-P66-ibm66n13.xml</pre>
   * Test URI: <pre>not-wf/P66/ibm66n13.xml</pre>
   * Comment: <pre>Tests CharRef with an illegal character referred to. The "#x001f"      is used as the referred character in the attribute value in the EmptyElemTag     of the element "root".</pre>
   * Sections: <pre>4.1</pre>
   * Version:
   *
   * @throws java.io.IOException if there is an I/O error
   */
  @Test
  public void testibm_not_wf_P66_ibm66n13xml()
      throws IOException
  {
      try ( Reader reader = new FileReader( new File( testResourcesDir, "not-wf/P66/ibm66n13.xml" ) ) )
      {
          parser.setInput( reader );
          while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
              ;
          fail( "Tests CharRef with an illegal character referred to. The \"#x001f\"      is used as the referred character in the attribute value in the EmptyElemTag     of the element \"root\"." );
      }
      catch ( XmlPullParserException e )
      {
          assertTrue( e.getMessage().contains( "character reference (with hex value 001f) is invalid" ) );
      }
  }

  /**
   * Test ID: <pre>ibm-not-wf-P66-ibm66n14.xml</pre>
   * Test URI: <pre>not-wf/P66/ibm66n14.xml</pre>
   * Comment: <pre>Tests CharRef with an illegal character referred to. The "#xfffe"      is used as the referred character in the attribute value in the EmptyElemTag     of the element "root".</pre>
   * Sections: <pre>4.1</pre>
   * Version:
   *
   * @throws java.io.IOException if there is an I/O error
   */
  @Test
  public void testibm_not_wf_P66_ibm66n14xml()
      throws IOException
  {
      try ( Reader reader = new FileReader( new File( testResourcesDir, "not-wf/P66/ibm66n14.xml" ) ) )
      {
          parser.setInput( reader );
          while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
              ;
          fail( "Tests CharRef with an illegal character referred to. The \"#xfffe\"      is used as the referred character in the attribute value in the EmptyElemTag     of the element \"root\"." );
      }
      catch ( XmlPullParserException e )
      {
          assertTrue( e.getMessage().contains( "character reference (with hex value fffe) is invalid" ) );
      }
  }

  /**
   * Test ID: <pre>ibm-not-wf-P66-ibm66n15.xml</pre>
   * Test URI: <pre>not-wf/P66/ibm66n15.xml</pre>
   * Comment: <pre>Tests CharRef with an illegal character referred to. The "#xffff"      is used as the referred character in the attribute value in the EmptyElemTag     of the element "root".</pre>
   * Sections: <pre>4.1</pre>
   * Version:
   *
   * @throws java.io.IOException if there is an I/O error
   */
  @Test
  public void testibm_not_wf_P66_ibm66n15xml()
      throws IOException
  {
      try ( Reader reader = new FileReader( new File( testResourcesDir, "not-wf/P66/ibm66n15.xml" ) ) )
      {
          parser.setInput( reader );
          while ( parser.nextToken() != XmlPullParser.END_DOCUMENT )
              ;
          fail( "Tests CharRef with an illegal character referred to. The \"#xffff\"      is used as the referred character in the attribute value in the EmptyElemTag     of the element \"root\"." );
      }
      catch ( XmlPullParserException e )
      {
          assertTrue( e.getMessage().contains( "character reference (with hex value ffff) is invalid" ) );
      }
  }

}
