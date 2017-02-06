package org.codehaus.plexus.util;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.Charset;

import org.codehaus.plexus.util.xml.XmlStreamReader;

/**
 * Utility to create Readers from streams, with explicit encoding choice: platform default,
 * XML, or specified.
 *
 * @author <a href="mailto:hboutemy@codehaus.org">Herve Boutemy</a>
 * @see Charset
 * @see <a href="http://java.sun.com/j2se/1.4.2/docs/guide/intl/encoding.doc.html">Supported encodings</a>
 * @version $Id$
 * @since 1.4.3
 */
public class ReaderFactory
{
    /**
     * ISO Latin Alphabet #1, also known as ISO-LATIN-1.
     * Every implementation of the Java platform is required to support this character encoding.
     * @see Charset
     */
    public static final String ISO_8859_1 = "ISO-8859-1";

    /**
     * Seven-bit ASCII, also known as ISO646-US, also known as the Basic Latin block of the Unicode character set.
     * Every implementation of the Java platform is required to support this character encoding.
     * @see Charset
     */
    public static final String US_ASCII = "US-ASCII";

    /**
     * Sixteen-bit Unicode Transformation Format, byte order specified by a mandatory initial byte-order mark (either
     * order accepted on input, big-endian used on output).
     * Every implementation of the Java platform is required to support this character encoding.
     * @see Charset
     */
    public static final String UTF_16 = "UTF-16";

    /**
     * Sixteen-bit Unicode Transformation Format, big-endian byte order.
     * Every implementation of the Java platform is required to support this character encoding.
     * @see Charset
     */
    public static final String UTF_16BE = "UTF-16BE";

    /**
     * Sixteen-bit Unicode Transformation Format, little-endian byte order.
     * Every implementation of the Java platform is required to support this character encoding.
     * @see Charset
     */
    public static final String UTF_16LE = "UTF-16LE";

    /**
     * Eight-bit Unicode Transformation Format.
     * Every implementation of the Java platform is required to support this character encoding.
     * @see Charset
     */
    public static final String UTF_8 = "UTF-8";

    /**
     * The <code>file.encoding</code> System Property.
     */
    public static final String FILE_ENCODING = System.getProperty( "file.encoding" );

    /**
     * Create a new Reader with XML encoding detection rules.
     *
     * @param in not null input stream.
     * @return an XML reader instance for the input stream.
     * @throws IOException if any.
     * @see XmlStreamReader
     */
    public static XmlStreamReader newXmlReader( InputStream in )
    throws IOException
    {
        return new XmlStreamReader( in );
    }

    /**
     * Create a new Reader with XML encoding detection rules.
     *
     * @param file not null file.
     * @return an XML reader instance for the input file.
     * @throws IOException if any.
     * @see XmlStreamReader
     */
    public static XmlStreamReader newXmlReader( File file )
    throws IOException
    {
        return new XmlStreamReader( file );
    }

    /**
     * Create a new Reader with XML encoding detection rules.
     *
     * @param url not null url.
     * @return an XML reader instance for the input url.
     * @throws IOException if any.
     * @see XmlStreamReader
     */
    public static XmlStreamReader newXmlReader( URL url )
    throws IOException
    {
        return new XmlStreamReader( url );
    }

    /**
     * Create a new Reader with default platform encoding.
     *
     * @param in not null input stream.
     * @return a reader instance for the input stream using the default platform charset.
     * @see Charset#defaultCharset()
     */
    public static Reader newPlatformReader( InputStream in )
    {
        return new InputStreamReader( in );
    }

    /**
     * Create a new Reader with default platform encoding.
     *
     * @param file not null file.
     * @return a reader instance for the input file using the default platform charset.
     * @throws FileNotFoundException if any.
     * @see Charset#defaultCharset()
     */
    public static Reader newPlatformReader( File file )
    throws FileNotFoundException
    {
        return new FileReader( file );
    }

    /**
     * Create a new Reader with default platform encoding.
     *
     * @param url not null url.
     * @return a reader instance for the input url using the default platform charset.
     * @throws IOException if any.
     * @see Charset#defaultCharset()
     */
    public static Reader newPlatformReader( URL url )
    throws IOException
    {
        return new InputStreamReader( url.openStream() );
    }

    /**
     * Create a new Reader with specified encoding.
     *
     * @param in not null input stream.
     * @param encoding not null supported encoding.
     * @return a reader instance for the input stream using the given encoding.
     * @throws UnsupportedEncodingException if any.
     * @see <a href="http://java.sun.com/j2se/1.4.2/docs/guide/intl/encoding.doc.html">Supported encodings</a>
     */
    public static Reader newReader( InputStream in, String encoding )
    throws UnsupportedEncodingException
    {
        return new InputStreamReader( in, encoding );
    }

    /**
     * Create a new Reader with specified encoding.
     *
     * Note that there is no buffering on this reader, which favours clients that read into large buffers (8K+).
     *
     * @param file not null file.
     * @param encoding not null supported encoding.
     * @return a reader instance for the input file using the given encoding.
     * @throws FileNotFoundException if any.
     * @throws UnsupportedEncodingException if any.
     * @see <a href="http://java.sun.com/j2se/1.4.2/docs/guide/intl/encoding.doc.html">Supported encodings</a>
     */
    public static Reader newReader( File file, String encoding )
    throws FileNotFoundException, UnsupportedEncodingException
    {
        return new InputStreamReader( new FileInputStream(file), encoding );
    }

    /**
     * Create a new Reader with specified encoding.
     *
     * @param url not null url.
     * @param encoding not null supported encoding.
     * @return a reader instance for the input url using the given encoding.
     * @throws IOException if any.
     * @see <a href="http://java.sun.com/j2se/1.4.2/docs/guide/intl/encoding.doc.html">Supported encodings</a>
     */
    public static Reader newReader( URL url, String encoding )
    throws IOException
    {
        return new InputStreamReader( url.openStream(), encoding );
    }
}
