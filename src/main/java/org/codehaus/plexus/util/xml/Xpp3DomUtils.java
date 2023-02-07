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

import java.io.IOException;

import org.apache.maven.api.xml.XmlNode;
import org.codehaus.plexus.util.xml.pull.XmlSerializer;

/** @author Jason van Zyl */
public class Xpp3DomUtils
{
    public static final String CHILDREN_COMBINATION_MODE_ATTRIBUTE = XmlNode.CHILDREN_COMBINATION_MODE_ATTRIBUTE;

    public static final String CHILDREN_COMBINATION_MERGE = XmlNode.CHILDREN_COMBINATION_MERGE;

    public static final String CHILDREN_COMBINATION_APPEND = XmlNode.CHILDREN_COMBINATION_APPEND;

    /**
     * This default mode for combining children DOMs during merge means that where element names match, the process will
     * try to merge the element data, rather than putting the dominant and recessive elements (which share the same
     * element name) as siblings in the resulting DOM.
     */
    public static final String DEFAULT_CHILDREN_COMBINATION_MODE = XmlNode.DEFAULT_CHILDREN_COMBINATION_MODE;

    public static final String SELF_COMBINATION_MODE_ATTRIBUTE = XmlNode.SELF_COMBINATION_MODE_ATTRIBUTE;

    public static final String SELF_COMBINATION_OVERRIDE = XmlNode.SELF_COMBINATION_OVERRIDE;

    public static final String SELF_COMBINATION_MERGE = XmlNode.SELF_COMBINATION_MERGE;

    /**
     * In case of complex XML structures, combining can be done based on id.
     * 
     * @since 3.0.22
     */
    public static final String ID_COMBINATION_MODE_ATTRIBUTE = XmlNode.ID_COMBINATION_MODE_ATTRIBUTE;
    
    /**
     * In case of complex XML structures, combining can be done based on keys.
     * This is a comma separated list of attribute names.
     * 
     * @since 3.4.0
     */
    public static final String KEYS_COMBINATION_MODE_ATTRIBUTE = XmlNode.KEYS_COMBINATION_MODE_ATTRIBUTE;

    /**
     * This default mode for combining a DOM node during merge means that where element names match, the process will
     * try to merge the element attributes and values, rather than overriding the recessive element completely with the
     * dominant one. This means that wherever the dominant element doesn't provide the value or a particular attribute,
     * that value or attribute will be set from the recessive DOM node.
     */
    public static final String DEFAULT_SELF_COMBINATION_MODE = XmlNode.DEFAULT_SELF_COMBINATION_MODE;

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
     * Merge two DOMs, with one having dominance in the case of collision.
     *
     * @see #CHILDREN_COMBINATION_MODE_ATTRIBUTE
     * @see #SELF_COMBINATION_MODE_ATTRIBUTE
     * @param dominant The dominant DOM into which the recessive value/attributes/children will be merged
     * @param recessive The recessive DOM, which will be merged into the dominant DOM
     * @param childMergeOverride Overrides attribute flags to force merging or appending of child elements into the
     *            dominant DOM
     * @return merged DOM
     */
    public static Xpp3Dom mergeXpp3Dom( Xpp3Dom dominant, Xpp3Dom recessive, Boolean childMergeOverride )
    {
        return Xpp3Dom.mergeXpp3Dom( dominant, recessive, childMergeOverride );
    }

    /**
     * Merge two DOMs, with one having dominance in the case of collision. Merge mechanisms (vs. override for nodes, or
     * vs. append for children) is determined by attributes of the dominant root node.
     *
     * @see #CHILDREN_COMBINATION_MODE_ATTRIBUTE
     * @see #SELF_COMBINATION_MODE_ATTRIBUTE
     * @param dominant The dominant DOM into which the recessive value/attributes/children will be merged
     * @param recessive The recessive DOM, which will be merged into the dominant DOM
     * @return merged DOM
     */
    public static Xpp3Dom mergeXpp3Dom( Xpp3Dom dominant, Xpp3Dom recessive )
    {
        return mergeXpp3Dom( dominant, recessive, null );
    }

    /**
     * @deprecated Use {@link org.codehaus.plexus.util.StringUtils#isNotEmpty(String)} instead
     */
    @Deprecated
    public static boolean isNotEmpty( String str )
    {
        return ( str != null && str.length() > 0 );
    }

    /**
     * @deprecated Use {@link org.codehaus.plexus.util.StringUtils#isEmpty(String)} instead
     */
    @Deprecated
    public static boolean isEmpty( String str )
    {
        return ( str == null || str.length() == 0 );
    }
}
