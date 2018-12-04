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

import org.codehaus.plexus.util.xml.pull.XmlSerializer;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/** @author Jason van Zyl */
public class Xpp3DomUtils
{
    public static final String CHILDREN_COMBINATION_MODE_ATTRIBUTE = "combine.children";

    public static final String CHILDREN_COMBINATION_MERGE = "merge";

    public static final String CHILDREN_COMBINATION_APPEND = "append";

    /**
     * This default mode for combining children DOMs during merge means that where element names match, the process will
     * try to merge the element data, rather than putting the dominant and recessive elements (which share the same
     * element name) as siblings in the resulting DOM.
     */
    public static final String DEFAULT_CHILDREN_COMBINATION_MODE = CHILDREN_COMBINATION_MERGE;

    public static final String SELF_COMBINATION_MODE_ATTRIBUTE = "combine.self";

    public static final String SELF_COMBINATION_OVERRIDE = "override";

    public static final String SELF_COMBINATION_MERGE = "merge";

    /**
     * In case of complex XML structures, combining can be done based on id.
     * 
     * @since 3.0.22
     */
    public static final String ID_COMBINATION_MODE_ATTRIBUTE = "combine.id";

    /**
     * This default mode for combining a DOM node during merge means that where element names match, the process will
     * try to merge the element attributes and values, rather than overriding the recessive element completely with the
     * dominant one. This means that wherever the dominant element doesn't provide the value or a particular attribute,
     * that value or attribute will be set from the recessive DOM node.
     */
    public static final String DEFAULT_SELF_COMBINATION_MODE = SELF_COMBINATION_MERGE;

    public void writeToSerializer( String namespace, XmlSerializer serializer, Xpp3Dom dom )
        throws IOException
    {
        // TODO: WARNING! Later versions of plexus-utils psit out an <?xml ?> header due to thinking this is a new
        // document - not the desired behaviour!
        SerializerXMLWriter xmlWriter = new SerializerXMLWriter( namespace, serializer );
        Xpp3DomWriter.write( xmlWriter, dom );
        if ( xmlWriter.getExceptions().size() > 0 )
        {
            throw (IOException) xmlWriter.getExceptions().get( 0 );
        }
    }

    /**
     * Merges one DOM into another, given a specific algorithm and possible override points for that algorithm.<p>
     * The algorithm is as follows:
     * <ol>
     * <li> if the recessive DOM is null, there is nothing to do... return.</li>
     * <li> Determine whether the dominant node will suppress the recessive one (flag=mergeSelf).
     *   <ol type="A">
     *   <li> retrieve the 'combine.self' attribute on the dominant node, and try to match against 'override'...
     *        if it matches 'override', then set mergeSelf == false...the dominant node suppresses the recessive one
     *        completely.</li>
     *   <li> otherwise, use the default value for mergeSelf, which is true...this is the same as specifying
     *        'combine.self' == 'merge' as an attribute of the dominant root node.</li>
     *   </ol></li>
     * <li> If mergeSelf == true
     *   <ol type="A">
     *   <li> if the dominant root node's value is empty, set it to the recessive root node's value</li>
     *   <li> For each attribute in the recessive root node which is not set in the dominant root node, set it.</li>
     *   <li> Determine whether children from the recessive DOM will be merged or appended to the dominant DOM as
     *        siblings (flag=mergeChildren).
     *     <ol type="i">
     *     <li> if childMergeOverride is set (non-null), use that value (true/false)</li>
     *     <li> retrieve the 'combine.children' attribute on the dominant node, and try to match against
     *          'append'...</li>
     *     <li> if it matches 'append', then set mergeChildren == false...the recessive children will be appended as
     *          siblings of the dominant children.</li>
     *     <li> otherwise, use the default value for mergeChildren, which is true...this is the same as specifying
     *          'combine.children' == 'merge' as an attribute on the dominant root node.</li>
     *     </ol></li>
     *   <li> Iterate through the recessive children, and:
     *     <ol type="i">
     *     <li> if 'combine.id' is set and there is a corresponding dominant child (matched by value of 'combine.id'),
     *          merge the two.</li>
     *     <li> if mergeChildren == true and there is a corresponding dominant child (matched by element name),
     *          merge the two.</li>
     *     <li> otherwise, add the recessive child as a new child on the dominant root node.</li>
     *     </ol></li>
     *   </ol></li>
     * </ol>
     */
    private static void mergeIntoXpp3Dom( Xpp3Dom dominant, Xpp3Dom recessive, Boolean childMergeOverride )
    {
        // TODO: share this as some sort of assembler, implement a walk interface?
        if ( recessive == null )
        {
            return;
        }

        boolean mergeSelf = true;

        String selfMergeMode = dominant.getAttribute( SELF_COMBINATION_MODE_ATTRIBUTE );

        if ( isNotEmpty( selfMergeMode ) && SELF_COMBINATION_OVERRIDE.equals( selfMergeMode ) )
        {
            mergeSelf = false;
        }

        if ( mergeSelf )
        {
            if ( isEmpty( dominant.getValue() ) && !isEmpty( recessive.getValue() ) )
            {
                dominant.setValue( recessive.getValue() );
                dominant.setInputLocation( recessive.getInputLocation() );
            }

            String[] recessiveAttrs = recessive.getAttributeNames();
            for ( String attr : recessiveAttrs )
            {
                if ( isEmpty( dominant.getAttribute( attr ) ) )
                {
                    dominant.setAttribute( attr, recessive.getAttribute( attr ) );
                }
            }

            boolean mergeChildren = true;

            if ( childMergeOverride != null )
            {
                mergeChildren = childMergeOverride;
            }
            else
            {
                String childMergeMode = dominant.getAttribute( CHILDREN_COMBINATION_MODE_ATTRIBUTE );

                if ( isNotEmpty( childMergeMode ) && CHILDREN_COMBINATION_APPEND.equals( childMergeMode ) )
                {
                    mergeChildren = false;
                }
            }

            Xpp3Dom[] children = recessive.getChildren();
            for ( Xpp3Dom recessiveChild : children )
            {
                String idValue = recessiveChild.getAttribute( ID_COMBINATION_MODE_ATTRIBUTE );

                Xpp3Dom childDom = null;
                if ( isNotEmpty( idValue ) )
                {
                    for ( Xpp3Dom dominantChild : dominant.getChildren() )
                    {
                        if ( idValue.equals( dominantChild.getAttribute( ID_COMBINATION_MODE_ATTRIBUTE ) ) )
                        {
                            childDom = dominantChild;
                            // we have a match, so don't append but merge
                            mergeChildren = true;
                        }
                    }
                }
                else
                {
                    childDom = dominant.getChild( recessiveChild.getName() );
                }

                if ( mergeChildren && childDom != null )
                {
                    mergeIntoXpp3Dom( childDom, recessiveChild, childMergeOverride );
                }
                else
                {
                    dominant.addChild( new Xpp3Dom( recessiveChild ) );
                }
            }
        }
    }

    /**
     * Merge two DOMs, with one having dominance in the case of collision.
     *
     * @see #CHILDREN_COMBINATION_MODE_ATTRIBUTE
     * @see #SELF_COMBINATION_MODE_ATTRIBUTE
     * @param dominant The dominant DOM into which the recessive value/attributes/children will be merged
     * @param recessive The recessive DOM, which will be merged into the dominant DOM
     * @param childMergeOverride Overrides attribute flags to force merging or appending of child elements into the
     *            dominant DOM
     */
    public static Xpp3Dom mergeXpp3Dom( Xpp3Dom dominant, Xpp3Dom recessive, Boolean childMergeOverride )
    {
        if ( dominant != null )
        {
            mergeIntoXpp3Dom( dominant, recessive, childMergeOverride );
            return dominant;
        }
        return recessive;
    }

    /**
     * Merge two DOMs, with one having dominance in the case of collision. Merge mechanisms (vs. override for nodes, or
     * vs. append for children) is determined by attributes of the dominant root node.
     *
     * @see #CHILDREN_COMBINATION_MODE_ATTRIBUTE
     * @see #SELF_COMBINATION_MODE_ATTRIBUTE
     * @param dominant The dominant DOM into which the recessive value/attributes/children will be merged
     * @param recessive The recessive DOM, which will be merged into the dominant DOM
     */
    public static Xpp3Dom mergeXpp3Dom( Xpp3Dom dominant, Xpp3Dom recessive )
    {
        if ( dominant != null )
        {
            mergeIntoXpp3Dom( dominant, recessive, null );
            return dominant;
        }
        return recessive;
    }

    public static boolean isNotEmpty( String str )
    {
        return ( str != null && str.length() > 0 );
    }

    public static boolean isEmpty( String str )
    {
        return ( str == null || str.trim().length() == 0 );
    }
}
