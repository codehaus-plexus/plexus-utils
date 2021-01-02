package org.codehaus.plexus.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

/**
 * Implementation specific to Java SE 10 version.
 */
final class VersionSpecifics extends CommonImplementation
{
    static final VersionSpecifics INSTANCE = new VersionSpecifics();

    private VersionSpecifics() {
        // singleton
    }

    void copy( final InputStream input, final OutputStream output )
        throws IOException
    {
        input.transferTo( output );
    }

    void copy( final Reader input, final Writer output )
        throws IOException
    {
        input.transferTo( output );
    }
}
