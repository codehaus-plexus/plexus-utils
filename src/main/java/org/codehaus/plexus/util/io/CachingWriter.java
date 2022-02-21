package org.codehaus.plexus.util.io;

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
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

/**
 * Caching Writer to avoid overwriting a file with
 * the same content.
 */
public class CachingWriter extends StringWriter
{
    private final Path path;
    private final Charset charset;
    private boolean modified;

    public CachingWriter( File path, Charset charset )
    {
        this( Objects.requireNonNull( path ).toPath(), charset );
    }

    public CachingWriter( Path path, Charset charset )
    {
        this.path = Objects.requireNonNull( path );
        this.charset = Objects.requireNonNull( charset );
    }

    @Override
    public void close() throws IOException
    {
        byte[] data = getBuffer().toString().getBytes( charset );
        if ( Files.exists( path ) && Files.size( path ) == data.length )
        {
            byte[] ba = Files.readAllBytes( path );
            if ( Arrays.equals( data, ba ) )
            {
                return;
            }
        }
        Files.write( path, data );
        modified = true;
    }

    public boolean isModified()
    {
        return modified;
    }
}
