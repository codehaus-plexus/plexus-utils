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
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * @version $Id$ NOTE: remove all the util code in here when separated, this class should be pure data.
 */
public class Xpp3Dom
    implements Serializable
{
    private static final long serialVersionUID = 2567894443061173996L;

    protected String name;

    protected String value;

    protected Map<String, String> attributes;

    protected final List<Xpp3Dom> childList;

    protected Xpp3Dom parent;

    /**
     * @since 3.2.0
     */
    protected Object inputLocation;

    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    private static final Xpp3Dom[] EMPTY_DOM_ARRAY = new Xpp3Dom[0];

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
     * This default mode for combining a DOM node during merge means that where element names match, the process will
     * try to merge the element attributes and values, rather than overriding the recessive element completely with the
     * dominant one. This means that wherever the dominant element doesn't provide the value or a particular attribute,
     * that value or attribute will be set from the recessive DOM node.
     */
    public static final String DEFAULT_SELF_COMBINATION_MODE = SELF_COMBINATION_MERGE;

    public Xpp3Dom( String name )
    {
        this.name = name;
        childList = new ArrayList<Xpp3Dom>();
    }

    /**
     * @since 3.2.0
     */
    public Xpp3Dom( String name, Object inputLocation )
    {
        this( name );
        this.inputLocation = inputLocation;
    }

    /**
     * Copy constructor.
     */
    public Xpp3Dom( Xpp3Dom src )
    {
        this( src, src.getName() );
    }

    /**
     * Copy constructor with alternative name.
     */
    public Xpp3Dom( Xpp3Dom src, String name )
    {
        this.name = name;
        this.inputLocation = src.inputLocation;

        int childCount = src.getChildCount();

        childList = new ArrayList<Xpp3Dom>( childCount );

        setValue( src.getValue() );

        String[] attributeNames = src.getAttributeNames();
        for ( String attributeName : attributeNames )
        {
            setAttribute( attributeName, src.getAttribute( attributeName ) );
        }

        for ( int i = 0; i < childCount; i++ )
        {
            addChild( new Xpp3Dom( src.getChild( i ) ) );
        }
    }

    // ----------------------------------------------------------------------
    // Name handling
    // ----------------------------------------------------------------------

    public String getName()
    {
        return name;
    }

    // ----------------------------------------------------------------------
    // Value handling
    // ----------------------------------------------------------------------

    public String getValue()
    {
        return value;
    }

    public void setValue( String value )
    {
        this.value = value;
    }

    // ----------------------------------------------------------------------
    // Attribute handling
    // ----------------------------------------------------------------------

    public String[] getAttributeNames()
    {
        if ( null == attributes || attributes.isEmpty() )
        {
            return EMPTY_STRING_ARRAY;
        }
        else
        {
            return attributes.keySet().toArray( EMPTY_STRING_ARRAY );
        }
    }

    public String getAttribute( String name )
    {
        return ( null != attributes ) ? attributes.get( name ) : null;
    }

    /**
     * Set the attribute value
     * 
     * @param name String not null
     * @param value String not null
     */
    public void setAttribute( String name, String value )
    {
        if ( null == value )
        {
            throw new NullPointerException( "Attribute value can not be null" );
        }
        if ( null == name )
        {
            throw new NullPointerException( "Attribute name can not be null" );
        }
        if ( null == attributes )
        {
            attributes = new HashMap<String, String>();
        }

        attributes.put( name, value );
    }

    // ----------------------------------------------------------------------
    // Child handling
    // ----------------------------------------------------------------------

    public Xpp3Dom getChild( int i )
    {
        return childList.get( i );
    }

    public Xpp3Dom getChild( String name )
    {
        if ( name != null )
        {
            ListIterator<Xpp3Dom> it = childList.listIterator( childList.size() );
            while ( it.hasPrevious() )
            {
                Xpp3Dom child = it.previous();
                if ( name.equals( child.getName() ) )
                {
                    return child;
                }
            }
        }
        return null;
    }

    public void addChild( Xpp3Dom xpp3Dom )
    {
        xpp3Dom.setParent( this );
        childList.add( xpp3Dom );
    }

    public Xpp3Dom[] getChildren()
    {
        if ( null == childList || childList.isEmpty() )
        {
            return EMPTY_DOM_ARRAY;
        }
        else
        {
            return childList.toArray( EMPTY_DOM_ARRAY );
        }
    }

    public Xpp3Dom[] getChildren( String name )
    {
        return getChildrenAsList( name ).toArray( EMPTY_DOM_ARRAY );
    }

    private List<Xpp3Dom> getChildrenAsList( String name )
    {
        if ( null == childList )
        {
            return Collections.emptyList();
        }
        else
        {
            ArrayList<Xpp3Dom> children = null;

            for ( Xpp3Dom configuration : childList )
            {
                if ( name.equals( configuration.getName() ) )
                {
                    if ( children == null )
                    {
                        children = new ArrayList<Xpp3Dom>();
                    }
                    children.add( configuration );
                }
            }

            if ( children != null )
            {
                return children;
            }
            else
            {
                return Collections.emptyList();
            }
        }
    }

    public int getChildCount()
    {
        if ( null == childList )
        {
            return 0;
        }

        return childList.size();
    }

    public void removeChild( int i )
    {
        Xpp3Dom child = getChild( i );
        childList.remove( i );
        // In case of any dangling references
        child.setParent( null );
    }

    // ----------------------------------------------------------------------
    // Parent handling
    // ----------------------------------------------------------------------

    public Xpp3Dom getParent()
    {
        return parent;
    }

    public void setParent( Xpp3Dom parent )
    {
        this.parent = parent;
    }

    // ----------------------------------------------------------------------
    // Input location handling
    // ----------------------------------------------------------------------

    /**
     * @since 3.2.0
     */
    public Object getInputLocation()
    {
        return inputLocation;
    }

    /**
     * @since 3.2.0
     */
    public void setInputLocation( Object inputLocation )
    {
        this.inputLocation = inputLocation;
    }

    // ----------------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------------

    public void writeToSerializer( String namespace, XmlSerializer serializer )
        throws IOException
    {
        // TODO: WARNING! Later versions of plexus-utils psit out an <?xml ?> header due to thinking this is a new
        // document - not the desired behaviour!
        SerializerXMLWriter xmlWriter = new SerializerXMLWriter( namespace, serializer );
        Xpp3DomWriter.write( xmlWriter, this );
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
     *         'combine.children' == 'merge' as an attribute on the dominant root node.</li>
     *     </ol></li>
     *   <li> Iterate through the recessive children, and:
     *     <ol type="i">
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

        if ( SELF_COMBINATION_OVERRIDE.equals( selfMergeMode ) )
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

            if ( recessive.attributes != null )
            {
                for ( String attr : recessive.attributes.keySet() )
                {
                    if ( isEmpty( dominant.getAttribute( attr ) ) )
                    {
                        dominant.setAttribute( attr, recessive.getAttribute( attr ) );
                    }
                }
            }

            if ( recessive.getChildCount() > 0 )
            {
                boolean mergeChildren = true;

                if ( childMergeOverride != null )
                {
                    mergeChildren = childMergeOverride;
                }
                else
                {
                    String childMergeMode = dominant.getAttribute( CHILDREN_COMBINATION_MODE_ATTRIBUTE );

                    if ( CHILDREN_COMBINATION_APPEND.equals( childMergeMode ) )
                    {
                        mergeChildren = false;
                    }
                }

                if ( !mergeChildren )
                {
                    Xpp3Dom[] dominantChildren = dominant.getChildren();
                    // remove these now, so we can append them to the recessive list later.
                    dominant.childList.clear();

                    for ( int i = 0, recessiveChildCount = recessive.getChildCount(); i < recessiveChildCount; i++ )
                    {
                        Xpp3Dom recessiveChild = recessive.getChild( i );
                        dominant.addChild( new Xpp3Dom( recessiveChild ) );
                    }

                    // now, re-add these children so they'll be appended to the recessive list.
                    for ( Xpp3Dom aDominantChildren : dominantChildren )
                    {
                        dominant.addChild( aDominantChildren );
                    }
                }
                else
                {
                    Map<String, Iterator<Xpp3Dom>> commonChildren = new HashMap<String, Iterator<Xpp3Dom>>();

                    for ( Xpp3Dom recChild : recessive.childList )
                    {
                        if ( commonChildren.containsKey( recChild.name ) )
                        {
                            continue;
                        }
                        List<Xpp3Dom> dominantChildren = dominant.getChildrenAsList( recChild.name );
                        if ( dominantChildren.size() > 0 )
                        {
                            commonChildren.put( recChild.name, dominantChildren.iterator() );
                        }
                    }

                    for ( int i = 0, recessiveChildCount = recessive.getChildCount(); i < recessiveChildCount; i++ )
                    {
                        Xpp3Dom recessiveChild = recessive.getChild( i );
                        Iterator<Xpp3Dom> it = commonChildren.get( recessiveChild.getName() );
                        if ( it == null )
                        {
                            dominant.addChild( new Xpp3Dom( recessiveChild ) );
                        }
                        else if ( it.hasNext() )
                        {
                            Xpp3Dom dominantChild = it.next();
                            mergeIntoXpp3Dom( dominantChild, recessiveChild, childMergeOverride );
                        }
                    }
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

    // ----------------------------------------------------------------------
    // Standard object handling
    // ----------------------------------------------------------------------

    public boolean equals( Object obj )
    {
        if ( obj == this )
        {
            return true;
        }

        if ( !( obj instanceof Xpp3Dom ) )
        {
            return false;
        }

        Xpp3Dom dom = (Xpp3Dom) obj;

        if ( name == null ? dom.name != null : !name.equals( dom.name ) )
        {
            return false;
        }
        else if ( value == null ? dom.value != null : !value.equals( dom.value ) )
        {
            return false;
        }
        else if ( attributes == null ? dom.attributes != null : !attributes.equals( dom.attributes ) )
        {
            return false;
        }
        else if ( childList == null ? dom.childList != null : !childList.equals( dom.childList ) )
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public int hashCode()
    {
        int result = 17;
        result = 37 * result + ( name != null ? name.hashCode() : 0 );
        result = 37 * result + ( value != null ? value.hashCode() : 0 );
        result = 37 * result + ( attributes != null ? attributes.hashCode() : 0 );
        result = 37 * result + ( childList != null ? childList.hashCode() : 0 );
        return result;
    }

    public String toString()
    {
        // TODO: WARNING! Later versions of plexus-utils psit out an <?xml ?> header due to thinking this is a new
        // document - not the desired behaviour!
        StringWriter writer = new StringWriter();
        XMLWriter xmlWriter = new PrettyPrintXMLWriter( writer, "UTF-8", null );
        Xpp3DomWriter.write( xmlWriter, this );
        return writer.toString();
    }

    public String toUnescapedString()
    {
        // TODO: WARNING! Later versions of plexus-utils psit out an <?xml ?> header due to thinking this is a new
        // document - not the desired behaviour!
        StringWriter writer = new StringWriter();
        XMLWriter xmlWriter = new PrettyPrintXMLWriter( writer, "UTF-8", null );
        Xpp3DomWriter.write( xmlWriter, this, false );
        return writer.toString();
    }

    public static boolean isNotEmpty( String str )
    {
        return ( ( str != null ) && ( str.length() > 0 ) );
    }

    public static boolean isEmpty( String str )
    {
        return ( ( str == null ) || ( str.trim().length() == 0 ) );
    }

}
