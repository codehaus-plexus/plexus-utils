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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * Write to an MXSerializer.
 *
 * @author <a href="mailto:brett@codehaus.org">Brett Porter</a>
 *
 */
public class SerializerXMLWriter
    implements XMLWriter
{
    private final XmlSerializer serializer;

    private final String namespace;

    private final Stack<String> elements = new Stack<String>();

    private List<Exception> exceptions;

    public SerializerXMLWriter( String namespace, XmlSerializer serializer )
    {
        this.serializer = serializer;
        this.namespace = namespace;
    }

    @Override
    public void startElement( String name )
    {
        try
        {
            serializer.startTag( namespace, name );
            elements.push( name );
        }
        catch ( IOException e )
        {
            storeException( e );
        }
    }

    @Override
    public void addAttribute( String key, String value )
    {
        try
        {
            serializer.attribute( namespace, key, value );
        }
        catch ( IOException e )
        {
            storeException( e );
        }
    }

    @Override
    public void writeText( String text )
    {
        try
        {
            serializer.text( text );
        }
        catch ( IOException e )
        {
            storeException( e );
        }
    }

    @Override
    public void writeMarkup( String text )
    {
        try
        {
            serializer.cdsect( text );
        }
        catch ( IOException e )
        {
            storeException( e );
        }
    }

    @Override
    public void endElement()
    {
        try
        {
            serializer.endTag( namespace, elements.pop() );
        }
        catch ( IOException e )
        {
            storeException( e );
        }
    }

    /**
     * @todo Maybe the interface should allow IOExceptions on each?
     */
    private void storeException( IOException e )
    {
        if ( exceptions == null )
        {
            exceptions = new ArrayList<Exception>();
        }
        exceptions.add( e );
    }

    public List<Exception> getExceptions()
    {
        return exceptions == null ? Collections.<Exception>emptyList() : exceptions;
    }

}
