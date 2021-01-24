package org.codehaus.plexus.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

/**
 * Implementation specific to Java SE 10 version.
 */
abstract class BaseIOUtil
{
    static void copy( final InputStream input, final OutputStream output )
        throws IOException
    {
        input.transferTo( output );
    }

    static void copy( final Reader input, final Writer output )
        throws IOException
    {
        input.transferTo( output );
    }
}
