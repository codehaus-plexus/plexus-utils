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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class CachingWriterTest
{

    Path tempDir;

    @Before
    public void setup() throws IOException
    {
        Path dir = Paths.get( "target/io" );
        Files.createDirectories( dir );
        tempDir = Files.createTempDirectory( dir, "temp-" );
    }

    @Test
    public void testWriteNoExistingFile() throws IOException, InterruptedException
    {
        String data = "Hello world!";
        Path path = tempDir.resolve( "file.txt" );
        assertFalse( Files.exists( path ) );

        try ( CachingWriter cos = new CachingWriter( path, StandardCharsets.UTF_8, 4 ) )
        {
            cos.write( data );
        }
        assertTrue( Files.exists( path ) );
        String read = new String( Files.readAllBytes( path ), StandardCharsets.UTF_8 );
        assertEquals( data, read );
        FileTime modified = Files.getLastModifiedTime( path );

        Thread.sleep( 250 );

        try ( CachingWriter cos = new CachingWriter( path, StandardCharsets.UTF_8, 4 ) )
        {
            cos.write( data );
        }
        assertTrue( Files.exists( path ) );
        read = new String( Files.readAllBytes( path ), StandardCharsets.UTF_8 );
        assertEquals( data, read );
        FileTime newModified = Files.getLastModifiedTime( path );
        assertEquals( modified, newModified );

        Thread.sleep( 250 );

        // write longer data
        data = "Good morning!";
        try ( CachingWriter cos = new CachingWriter( path, StandardCharsets.UTF_8, 4 ) )
        {
            cos.write( data );
        }
        assertTrue( Files.exists( path ) );
        read = new String( Files.readAllBytes( path ), StandardCharsets.UTF_8 );
        assertEquals( data, read );
        newModified = Files.getLastModifiedTime( path );
        assertNotEquals( modified, newModified );
        modified = newModified;

        Thread.sleep( 250 );

        // different data same size
        data = "Good mornong!";
        try ( CachingWriter cos = new CachingWriter( path, StandardCharsets.UTF_8, 4 ) )
        {
            cos.write( data );
        }
        assertTrue( Files.exists( path ) );
        read = new String( Files.readAllBytes( path ), StandardCharsets.UTF_8 );
        assertEquals( data, read );
        newModified = Files.getLastModifiedTime( path );
        assertNotEquals( modified, newModified );
        modified = newModified;

        Thread.sleep( 250 );

        // same data but shorter
        data = "Good mornon";
        try ( CachingWriter cos = new CachingWriter( path, StandardCharsets.UTF_8, 4 ) )
        {
            cos.write( data );
        }
        assertTrue( Files.exists( path ) );
        read = new String( Files.readAllBytes( path ), StandardCharsets.UTF_8 );
        assertEquals( data, read );
        newModified = Files.getLastModifiedTime( path );
        assertNotEquals( modified, newModified );
        modified = newModified;
    }
}
