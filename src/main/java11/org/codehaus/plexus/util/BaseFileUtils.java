package org.codehaus.plexus.util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Implementation specific to Java SE 11 version.
 */
abstract class BaseFileUtils
{
    static String fileRead( Path path, String encoding ) throws IOException
    {
        return encoding != null ? Files.readString( path, Charset.forName( encoding ) ) : Files.readString( path );
    }

    static void fileWrite( Path path, String encoding, String data, OpenOption... openOptions ) throws IOException
    {
        if ( encoding != null )
        {
            Files.writeString( path, data, Charset.forName( encoding ), openOptions );
        }
        else
        {
            Files.writeString( path, data, openOptions );
        }
    }
}
