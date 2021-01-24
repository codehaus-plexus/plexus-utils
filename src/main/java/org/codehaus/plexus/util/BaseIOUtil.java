package org.codehaus.plexus.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

/**
 * Implementation specific to Java SE 8 version.
 */
abstract class BaseIOUtil
{
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 16;

    static void copy( final InputStream input, final OutputStream output )
        throws IOException
    {
        IOUtil.copy( input, output, DEFAULT_BUFFER_SIZE );
    }

    static void copy( final Reader input, final Writer output )
        throws IOException
    {
        IOUtil.copy( input, output, DEFAULT_BUFFER_SIZE );
    }
}
