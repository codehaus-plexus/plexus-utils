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

import java.io.StringReader;

import junit.framework.TestCase;

public class Xpp3DomUtilsTest
    extends TestCase
{

    public void testCombineId()
        throws Exception
    {
        String lhs = "<props>" + "<property combine.id='LHS-ONLY'><name>LHS-ONLY</name><value>LHS</value></property>"
            + "<property combine.id='TOOVERWRITE'><name>TOOVERWRITE</name><value>LHS</value></property>" + "</props>";

        String rhs = "<props>" + "<property combine.id='RHS-ONLY'><name>RHS-ONLY</name><value>RHS</value></property>"
            + "<property combine.id='TOOVERWRITE'><name>TOOVERWRITE</name><value>RHS</value></property>" + "</props>";

        Xpp3Dom leftDom = Xpp3DomBuilder.build( new StringReader( lhs ) );
        Xpp3Dom rightDom = Xpp3DomBuilder.build( new StringReader( rhs ) );

        Xpp3Dom mergeResult = Xpp3DomUtils.mergeXpp3Dom( leftDom, rightDom, true );
        assertEquals( 3, mergeResult.getChildren( "property" ).length );

        assertEquals( "LHS-ONLY", mergeResult.getChildren( "property" )[0].getChild( "name" ).getValue() );
        assertEquals( "LHS", mergeResult.getChildren( "property" )[0].getChild( "value" ).getValue() );
        assertEquals( "TOOVERWRITE", mergeResult.getChildren( "property" )[1].getChild( "name" ).getValue() );
        assertEquals( "LHS", mergeResult.getChildren( "property" )[1].getChild( "value" ).getValue() );
        assertEquals( "RHS-ONLY", mergeResult.getChildren( "property" )[2].getChild( "name" ).getValue() );
        assertEquals( "RHS", mergeResult.getChildren( "property" )[2].getChild( "value" ).getValue() );
    }
}
