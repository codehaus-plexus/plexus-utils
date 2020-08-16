package org.codehaus.plexus.util;

import java.util.Objects;

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

import java.util.Properties;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

/**
 * Static methods to create Properties loaded from various sources.
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:mmaczka@interia.pl">Michal Maczka</a>
 */
public class PropertyUtils
{

    public static Properties loadProperties( final URL url )
        throws IOException
    {
        return loadProperties( Objects.requireNonNull( url, "url" ).openStream() );
    }

    public static Properties loadProperties( final File file )
        throws IOException
    {
        return loadProperties( Files.newInputStream( Objects.requireNonNull( file, "file" ).toPath() ) );
    }

    public static Properties loadProperties( final InputStream is )
        throws IOException
    {
        final Properties properties = new Properties();
        
        // Make sure the properties stream is valid
        if ( is != null )
        {
            try ( InputStream in = is ) 
            {
                properties.load( in );
            }
        }

        return properties;
    }

}
