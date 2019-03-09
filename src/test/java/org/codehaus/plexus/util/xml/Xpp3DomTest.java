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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

import org.codehaus.plexus.util.xml.pull.XmlPullParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.junit.Test;

public class Xpp3DomTest
{
    @Test
    public void testShouldPerformAppendAtFirstSubElementLevel()
    {
        // create the dominant DOM
        Xpp3Dom t1 = new Xpp3Dom( "top" );
        t1.setAttribute( Xpp3Dom.CHILDREN_COMBINATION_MODE_ATTRIBUTE, Xpp3Dom.CHILDREN_COMBINATION_APPEND );
        t1.setInputLocation( "t1top" );

        Xpp3Dom t1s1 = new Xpp3Dom( "topsub1" );
        t1s1.setValue( "t1s1Value" );
        t1s1.setInputLocation( "t1s1" );

        t1.addChild( t1s1 );

        // create the recessive DOM
        Xpp3Dom t2 = new Xpp3Dom( "top" );
        t2.setInputLocation( "t2top" );

        Xpp3Dom t2s1 = new Xpp3Dom( "topsub1" );
        t2s1.setValue( "t2s1Value" );
        t2s1.setInputLocation( "t2s1" );

        t2.addChild( t2s1 );

        // merge and check results.
        Xpp3Dom result = Xpp3Dom.mergeXpp3Dom( t1, t2 );

        assertEquals( 2, result.getChildren( "topsub1" ).length );
        assertEquals( "t2s1Value", result.getChildren( "topsub1" )[0].getValue() );
        assertEquals( "t1s1Value", result.getChildren( "topsub1" )[1].getValue() );

        assertEquals( "t1top", result.getInputLocation() );
        assertEquals( "t2s1", result.getChildren( "topsub1" )[0].getInputLocation() );
        assertEquals( "t1s1", result.getChildren( "topsub1" )[1].getInputLocation() );
    }

    @Test
    public void testShouldOverrideAppendAndDeepMerge()
    {
        // create the dominant DOM
        Xpp3Dom t1 = new Xpp3Dom( "top" );
        t1.setAttribute( Xpp3Dom.CHILDREN_COMBINATION_MODE_ATTRIBUTE, Xpp3Dom.CHILDREN_COMBINATION_APPEND );
        t1.setInputLocation( "t1top" );

        Xpp3Dom t1s1 = new Xpp3Dom( "topsub1" );
        t1s1.setValue( "t1s1Value" );
        t1s1.setInputLocation( "t1s1" );

        t1.addChild( t1s1 );

        // create the recessive DOM
        Xpp3Dom t2 = new Xpp3Dom( "top" );
        t2.setInputLocation( "t2top" );

        Xpp3Dom t2s1 = new Xpp3Dom( "topsub1" );
        t2s1.setValue( "t2s1Value" );
        t2s1.setInputLocation( "t2s1" );

        t2.addChild( t2s1 );

        // merge and check results.
        Xpp3Dom result = Xpp3Dom.mergeXpp3Dom( t1, t2, Boolean.TRUE );

        assertEquals( 1, result.getChildren( "topsub1" ).length );
        assertEquals( "t1s1Value", result.getChildren( "topsub1" )[0].getValue() );

        assertEquals( "t1top", result.getInputLocation() );
        assertEquals( "t1s1", result.getChildren( "topsub1" )[0].getInputLocation() );
    }

    @Test
    public void testShouldPerformSelfOverrideAtTopLevel()
    {
        // create the dominant DOM
        Xpp3Dom t1 = new Xpp3Dom( "top" );
        t1.setAttribute( "attr", "value" );
        t1.setInputLocation( "t1top" );

        t1.setAttribute( Xpp3Dom.SELF_COMBINATION_MODE_ATTRIBUTE, Xpp3Dom.SELF_COMBINATION_OVERRIDE );

        // create the recessive DOM
        Xpp3Dom t2 = new Xpp3Dom( "top" );
        t2.setAttribute( "attr2", "value2" );
        t2.setValue( "t2Value" );
        t2.setInputLocation( "t2top" );

        // merge and check results.
        Xpp3Dom result = Xpp3Dom.mergeXpp3Dom( t1, t2 );

        assertEquals( 2, result.getAttributeNames().length );
        assertNull( result.getValue() );
        assertEquals( "t1top", result.getInputLocation() );
    }

    @Test
    public void testShouldMergeValuesAtTopLevelByDefault()
    {
        // create the dominant DOM
        Xpp3Dom t1 = new Xpp3Dom( "top" );
        t1.setAttribute( "attr", "value" );
        t1.setInputLocation( "t1top" );

        // create the recessive DOM
        Xpp3Dom t2 = new Xpp3Dom( "top" );
        t2.setAttribute( "attr2", "value2" );
        t2.setValue( "t2Value" );
        t2.setInputLocation( "t2top" );

        // merge and check results.
        Xpp3Dom result = Xpp3Dom.mergeXpp3Dom( t1, t2 );

        // this is still 2, since we're not using the merge-control attribute.
        assertEquals( 2, result.getAttributeNames().length );

        assertEquals( result.getValue(), t2.getValue() );
        assertEquals( "t2top", result.getInputLocation() );
    }

    @Test
    public void testShouldMergeValuesAtTopLevel()
    {
        // create the dominant DOM
        Xpp3Dom t1 = new Xpp3Dom( "top" );
        t1.setAttribute( "attr", "value" );

        t1.setAttribute( Xpp3Dom.SELF_COMBINATION_MODE_ATTRIBUTE, Xpp3Dom.SELF_COMBINATION_MERGE );

        // create the recessive DOM
        Xpp3Dom t2 = new Xpp3Dom( "top" );
        t2.setAttribute( "attr2", "value2" );
        t2.setValue( "t2Value" );

        // merge and check results.
        Xpp3Dom result = Xpp3Dom.mergeXpp3Dom( t1, t2 );

        assertEquals( 3, result.getAttributeNames().length );
        assertEquals( result.getValue(), t2.getValue() );
    }

    @Test
    public void testNullAttributeNameOrValue()
    {
        Xpp3Dom t1 = new Xpp3Dom( "top" );
        try
        {
            t1.setAttribute( "attr", null );
            fail( "null attribute values shouldn't be allowed" );
        }
        catch ( NullPointerException e )
        {
        }
        t1.toString();
        try
        {
            t1.setAttribute( null, "value" );
            fail( "null attribute names shouldn't be allowed" );
        }
        catch ( NullPointerException e )
        {
        }
        t1.toString();
    }

    @Test
    public void testEquals()
    {
        Xpp3Dom dom = new Xpp3Dom( "top" );

        assertEquals( dom, dom );
        assertFalse( dom.equals( null ) );
        assertFalse( dom.equals( new Xpp3Dom( (String) null ) ) );
    }

    @Test
    public void testEqualsIsNullSafe()
        throws XmlPullParserException, IOException
    {
        String testDom = "<configuration><items thing='blah'><item>one</item><item>two</item></items></configuration>";
        Xpp3Dom dom = Xpp3DomBuilder.build( new StringReader( testDom ) );
        Xpp3Dom dom2 = Xpp3DomBuilder.build( new StringReader( testDom ) );

        try
        {
            dom2.attributes = new HashMap();
            dom2.attributes.put( "nullValue", null );
            dom2.attributes.put( null, "nullKey" );
            dom2.childList.clear();
            dom2.childList.add( null );

            assertFalse( dom.equals( dom2 ) );
            assertFalse( dom2.equals( dom ) );

        }
        catch ( NullPointerException ex )
        {
            ex.printStackTrace();
            fail( "\nNullPointerExceptions should not be thrown." );
        }
    }

    @Test
    public void testShouldOverwritePluginConfigurationSubItemsByDefault()
        throws XmlPullParserException, IOException
    {
        String parentConfigStr = "<configuration><items><item>one</item><item>two</item></items></configuration>";
        Xpp3Dom parentConfig =
            Xpp3DomBuilder.build( new StringReader( parentConfigStr ), new FixedInputLocationBuilder( "parent" ) );

        String childConfigStr = "<configuration><items><item>three</item></items></configuration>";
        Xpp3Dom childConfig =
            Xpp3DomBuilder.build( new StringReader( childConfigStr ), new FixedInputLocationBuilder( "child" ) );

        Xpp3Dom result = Xpp3Dom.mergeXpp3Dom( childConfig, parentConfig );
        Xpp3Dom items = result.getChild( "items" );

        assertEquals( 1, items.getChildCount() );

        Xpp3Dom item = items.getChild( 0 );
        assertEquals( "three", item.getValue() );
        assertEquals( "child", item.getInputLocation() );
    }

    @Test
    public void testShouldMergePluginConfigurationSubItemsWithMergeAttributeSet()
        throws XmlPullParserException, IOException
    {
        String parentConfigStr = "<configuration><items><item>one</item><item>two</item></items></configuration>";
        Xpp3Dom parentConfig =
            Xpp3DomBuilder.build( new StringReader( parentConfigStr ), new FixedInputLocationBuilder( "parent" ) );

        String childConfigStr =
            "<configuration><items combine.children=\"append\"><item>three</item></items></configuration>";
        Xpp3Dom childConfig =
            Xpp3DomBuilder.build( new StringReader( childConfigStr ), new FixedInputLocationBuilder( "child" ) );

        Xpp3Dom result = Xpp3Dom.mergeXpp3Dom( childConfig, parentConfig );
        Xpp3Dom items = result.getChild( "items" );

        assertEquals( 3, items.getChildCount() );

        Xpp3Dom[] item = items.getChildren();

        assertEquals( "one", item[0].getValue() );
        assertEquals( "parent", item[0].getInputLocation() );
        assertEquals( "two", item[1].getValue() );
        assertEquals( "parent", item[1].getInputLocation() );
        assertEquals( "three", item[2].getValue() );
        assertEquals( "child", item[2].getInputLocation() );
    }

    @Test
    public void testShouldNotChangeUponMergeWithItselfWhenFirstOrLastSubItemIsEmpty()
        throws Exception
    {
        String configStr = "<configuration><items><item/><item>test</item><item/></items></configuration>";
        Xpp3Dom dominantConfig = Xpp3DomBuilder.build( new StringReader( configStr ) );
        Xpp3Dom recessiveConfig = Xpp3DomBuilder.build( new StringReader( configStr ) );

        Xpp3Dom result = Xpp3Dom.mergeXpp3Dom( dominantConfig, recessiveConfig );
        Xpp3Dom items = result.getChild( "items" );

        assertEquals( 3, items.getChildCount() );

        assertEquals( null, items.getChild( 0 ).getValue() );
        assertEquals( "test", items.getChild( 1 ).getValue() );
        assertEquals( null, items.getChild( 2 ).getValue() );
    }

    @Test
    public void testShouldCopyRecessiveChildrenNotPresentInTarget()
        throws Exception
    {
        String dominantStr = "<configuration><foo>x</foo></configuration>";
        String recessiveStr = "<configuration><bar>y</bar></configuration>";
        Xpp3Dom dominantConfig = Xpp3DomBuilder.build( new StringReader( dominantStr ) );
        Xpp3Dom recessiveConfig = Xpp3DomBuilder.build( new StringReader( recessiveStr ) );

        Xpp3Dom result = Xpp3Dom.mergeXpp3Dom( dominantConfig, recessiveConfig );

        assertEquals( 2, result.getChildCount() );

        assertEquals( "x", result.getChild( "foo" ).getValue() );
        assertEquals( "y", result.getChild( "bar" ).getValue() );
        assertNotSame( result.getChild( "bar" ), recessiveConfig.getChild( "bar" ) );
    }

    @Test
    public void testDupeChildren()
        throws IOException, XmlPullParserException
    {
        String dupes = "<configuration><foo>x</foo><foo>y</foo></configuration>";
        Xpp3Dom dom = Xpp3DomBuilder.build( new StringReader( dupes ) );
        assertNotNull( dom );
        assertEquals( "y", dom.getChild( "foo" ).getValue() );
    }

    private static class FixedInputLocationBuilder
        implements Xpp3DomBuilder.InputLocationBuilder
    {
        private final Object location;

        public FixedInputLocationBuilder( Object location )
        {
            this.location = location;
        }

        public Object toInputLocation( XmlPullParser parser )
        {
            return location;
        }
    }
}
