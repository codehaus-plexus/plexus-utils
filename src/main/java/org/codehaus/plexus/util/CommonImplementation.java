package org.codehaus.plexus.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

/**
 * Fallback implementation for all Java SE versions not
 * overwriting a method version-specifically.
 */
abstract class CommonImplementation
{
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 16;

    void copy( final InputStream input, final OutputStream output )
        throws IOException
    {
        IOUtil.copy( input, output, DEFAULT_BUFFER_SIZE );
    }

    void copy( final Reader input, final Writer output )
        throws IOException
    {
        IOUtil.copy( input, output, DEFAULT_BUFFER_SIZE );
    }

}
