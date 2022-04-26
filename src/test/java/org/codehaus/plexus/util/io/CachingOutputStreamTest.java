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
import java.util.Objects;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class CachingOutputStreamTest
{

    Path tempDir;
    Path checkLastModified;

    @Before
    public void setup() throws IOException
    {
        Path dir = Paths.get( "target/io" );
        Files.createDirectories( dir );
        tempDir = Files.createTempDirectory( dir, "temp-" );
        checkLastModified = tempDir.resolve( ".check" );
    }

    private void waitLastModified() throws IOException, InterruptedException
    {
        Files.newOutputStream( checkLastModified ).close();
        FileTime lm = Files.getLastModifiedTime( checkLastModified );
        while ( true )
        {
            Files.newOutputStream( checkLastModified ).close();
            FileTime nlm = Files.getLastModifiedTime( checkLastModified );
            if ( !Objects.equals( nlm, lm ) )
            {
                break;
            }
            Thread.sleep( 10 );
        }
    }

    @Test
    public void testWriteNoExistingFile() throws IOException, InterruptedException
    {
        byte[] data = "Hello world!".getBytes( StandardCharsets.UTF_8 );
        Path path = tempDir.resolve( "file.txt" );
        assertFalse( Files.exists( path ) );

        try ( CachingOutputStream cos = new CachingOutputStream( path, 4 ) )
        {
            cos.write( data );
        }
        assertTrue( Files.exists( path ) );
        byte[] read = Files.readAllBytes( path );
        assertArrayEquals( data, read );
        FileTime modified = Files.getLastModifiedTime( path );

        waitLastModified();

        try ( CachingOutputStream cos = new CachingOutputStream( path, 4 ) )
        {
            cos.write( data );
        }
        assertTrue( Files.exists( path ) );
        read = Files.readAllBytes( path );
        assertArrayEquals( data, read );
        FileTime newModified = Files.getLastModifiedTime( path );
        assertEquals( modified, newModified );
        modified = newModified;

        waitLastModified();

        // write longer data
        data = "Good morning!".getBytes( StandardCharsets.UTF_8 );
        try ( CachingOutputStream cos = new CachingOutputStream( path, 4 ) )
        {
            cos.write( data );
        }
        assertTrue( Files.exists( path ) );
        read = Files.readAllBytes( path );
        assertArrayEquals( data, read );
        newModified = Files.getLastModifiedTime( path );
        assertNotEquals( modified, newModified );
        modified = newModified;

        waitLastModified();

        // different data same size
        data = "Good mornong!".getBytes( StandardCharsets.UTF_8 );
        try ( CachingOutputStream cos = new CachingOutputStream( path, 4 ) )
        {
            cos.write( data );
        }
        assertTrue( Files.exists( path ) );
        read = Files.readAllBytes( path );
        assertArrayEquals( data, read );
        newModified = Files.getLastModifiedTime( path );
        assertNotEquals( modified, newModified );
        modified = newModified;

        waitLastModified();

        // same data but shorter
        data = "Good mornon".getBytes( StandardCharsets.UTF_8 );
        try ( CachingOutputStream cos = new CachingOutputStream( path, 4 ) )
        {
            cos.write( data );
        }
        assertTrue( Files.exists( path ) );
        read = Files.readAllBytes( path );
        assertArrayEquals( data, read );
        newModified = Files.getLastModifiedTime( path );
        assertNotEquals( modified, newModified );
        modified = newModified;
    }

}
