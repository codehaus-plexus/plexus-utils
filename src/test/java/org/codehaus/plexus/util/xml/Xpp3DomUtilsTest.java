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

import java.io.StringReader;

import org.codehaus.plexus.util.xml.pull.XmlPullParser;
import org.junit.Test;

/**
 * <p>Xpp3DomUtilsTest class.</p>
 *
 * @author herve
 * @version $Id: $Id
 * @since 3.4.0
 */
public class Xpp3DomUtilsTest
{
    /**
     * <p>testCombineId.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    public void testCombineId()
        throws Exception
    {
        String lhs = "<props>" + "<property combine.id='LHS-ONLY'><name>LHS-ONLY</name><value>LHS</value></property>"
            + "<property combine.id='TOOVERWRITE'><name>TOOVERWRITE</name><value>LHS</value></property>" + "</props>";

        String rhs = "<props>" + "<property combine.id='RHS-ONLY'><name>RHS-ONLY</name><value>RHS</value></property>"
            + "<property combine.id='TOOVERWRITE'><name>TOOVERWRITE</name><value>RHS</value></property>" + "</props>";

        Xpp3Dom leftDom = Xpp3DomBuilder.build( new StringReader( lhs ), new FixedInputLocationBuilder( "left" ) );
        Xpp3Dom rightDom = Xpp3DomBuilder.build( new StringReader( rhs ), new FixedInputLocationBuilder( "right" ) );

        Xpp3Dom mergeResult = Xpp3DomUtils.mergeXpp3Dom( leftDom, rightDom, true );
        assertEquals( 3, mergeResult.getChildren( "property" ).length );

        Xpp3Dom p0 = mergeResult.getChildren( "property" )[0];
        assertEquals( "LHS-ONLY", p0.getChild( "name" ).getValue() );
        assertEquals( "left", p0.getChild( "name" ).getInputLocation() );
        assertEquals( "LHS", p0.getChild( "value" ).getValue() );
        assertEquals( "left", p0.getChild( "value" ).getInputLocation() );
        
        Xpp3Dom p1 = mergeResult.getChildren( "property" )[1];
        assertEquals( "TOOVERWRITE", mergeResult.getChildren( "property" )[1].getChild( "name" ).getValue() );
        assertEquals( "left", p1.getChild( "name" ).getInputLocation() );
        assertEquals( "LHS", mergeResult.getChildren( "property" )[1].getChild( "value" ).getValue() );
        assertEquals( "left", p1.getChild( "value" ).getInputLocation() );

        Xpp3Dom p2 = mergeResult.getChildren( "property" )[2];
        assertEquals( "RHS-ONLY", mergeResult.getChildren( "property" )[2].getChild( "name" ).getValue() );
        assertEquals( "right", p2.getChild( "name" ).getInputLocation() );
        assertEquals( "RHS", mergeResult.getChildren( "property" )[2].getChild( "value" ).getValue() );
        assertEquals( "right", p2.getChild( "value" ).getInputLocation() );
    }

    /**
     * <p>testCombineKeys.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    public void testCombineKeys()
        throws Exception
    {
        String lhs = "<props combine.keys='key'>" + "<property key=\"LHS-ONLY\"><name>LHS-ONLY</name><value>LHS</value></property>"
                        + "<property combine.keys='name'><name>TOOVERWRITE</name><value>LHS</value></property>" + "</props>";

        String rhs = "<props combine.keys='key'>" + "<property key=\"RHS-ONLY\"><name>RHS-ONLY</name><value>RHS</value></property>"
            + "<property combine.keys='name'><name>TOOVERWRITE</name><value>RHS</value></property>" + "</props>";

        Xpp3Dom leftDom = Xpp3DomBuilder.build( new StringReader( lhs ), new FixedInputLocationBuilder( "left" ) );
        Xpp3Dom rightDom = Xpp3DomBuilder.build( new StringReader( rhs ), new FixedInputLocationBuilder( "right" ) );

        Xpp3Dom mergeResult = Xpp3DomUtils.mergeXpp3Dom( leftDom, rightDom, true );
        assertEquals( 3, mergeResult.getChildren( "property" ).length );

        Xpp3Dom p0 = mergeResult.getChildren( "property" )[0];
        assertEquals( "LHS-ONLY", p0.getChild( "name" ).getValue() );
        assertEquals( "left", p0.getChild( "name" ).getInputLocation() );
        assertEquals( "LHS", p0.getChild( "value" ).getValue() );
        assertEquals( "left", p0.getChild( "value" ).getInputLocation() );
        
        Xpp3Dom p1 = mergeResult.getChildren( "property" )[1];
        assertEquals( "TOOVERWRITE", mergeResult.getChildren( "property" )[1].getChild( "name" ).getValue() );
        assertEquals( "left", p1.getChild( "name" ).getInputLocation() );
        assertEquals( "LHS", mergeResult.getChildren( "property" )[1].getChild( "value" ).getValue() );
        assertEquals( "left", p1.getChild( "value" ).getInputLocation() );

        Xpp3Dom p2 = mergeResult.getChildren( "property" )[2];
        assertEquals( "RHS-ONLY", mergeResult.getChildren( "property" )[2].getChild( "name" ).getValue() );
        assertEquals( "right", p2.getChild( "name" ).getInputLocation() );
        assertEquals( "RHS", mergeResult.getChildren( "property" )[2].getChild( "value" ).getValue() );
        assertEquals( "right", p2.getChild( "value" ).getInputLocation() );
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
