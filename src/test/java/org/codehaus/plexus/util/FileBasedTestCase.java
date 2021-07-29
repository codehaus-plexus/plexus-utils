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

import static org.junit.Assert.assertTrue;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.util.Arrays;

import junit.framework.AssertionFailedError;

/**
 * Base class for testcases doing tests with files.
 *
 * @author Jeremias Maerki
 * @version $Id: $Id
 * @since 3.4.0
 */
public abstract class FileBasedTestCase
{
    private static File testDir;

    /**
     * <p>getTestDirectory.</p>
     *
     * @return a {@link java.io.File} object.
     */
    public static File getTestDirectory()
    {
        if ( testDir == null )
        {
            testDir = ( new File( "target/test/io/" ) ).getAbsoluteFile();
        }
        return testDir;
    }

    /**
     * <p>createFile.</p>
     *
     * @param file a {@link java.io.File} object.
     * @param size a long.
     * @return an array of {@link byte} objects.
     * @throws java.io.IOException if any.
     */
    protected byte[] createFile( final File file, final long size )
        throws IOException
    {
        if ( !file.getParentFile().exists() )
        {
            throw new IOException( "Cannot create file " + file + " as the parent directory does not exist" );
        }

        byte[] data = generateTestData( size );

        final BufferedOutputStream output = new BufferedOutputStream( Files.newOutputStream( file.toPath() ) );

        try
        {
            output.write( data );

            return data;
        }
        finally
        {
            output.close();
        }
    }

    /**
     * <p>createSymlink.</p>
     *
     * @param link a {@link java.io.File} object.
     * @param target a {@link java.io.File} object.
     * @return a boolean.
     */
    protected boolean createSymlink( final File link, final File target )
    {
        try
        {
            String[] args = { "ln", "-s", target.getAbsolutePath(), link.getAbsolutePath() };
            Process process = Runtime.getRuntime().exec( args );
            process.waitFor();
            if ( 0 != process.exitValue() )
            {
                return false;
            }
        }
        catch ( Exception e )
        {
            // assume platform does not support "ln" command, tests should be skipped
            return false;
        }
        return true;
    }

    /**
     * <p>generateTestData.</p>
     *
     * @param size a long.
     * @return an array of {@link byte} objects.
     */
    protected byte[] generateTestData( final long size )
    {
        try
        {
            ByteArrayOutputStream baout = new ByteArrayOutputStream();
            generateTestData( baout, size );
            return baout.toByteArray();
        }
        catch ( IOException ioe )
        {
            throw new RuntimeException( "This should never happen: " + ioe.getMessage() );
        }
    }

    /**
     * <p>generateTestData.</p>
     *
     * @param out a {@link java.io.OutputStream} object.
     * @param size a long.
     * @throws java.io.IOException if any.
     */
    protected void generateTestData( final OutputStream out, final long size )
        throws IOException
    {
        for ( int i = 0; i < size; i++ )
        {
            // output.write((byte)'X');

            // nice varied byte pattern compatible with Readers and Writers
            out.write( (byte) ( ( i % 127 ) + 1 ) );
        }
    }

    /**
     * <p>newFile.</p>
     *
     * @param filename a {@link java.lang.String} object.
     * @return a {@link java.io.File} object.
     * @throws java.io.IOException if any.
     */
    protected File newFile( String filename )
        throws IOException
    {
        final File destination = new File( getTestDirectory(), filename );
        /*
         * assertTrue( filename + "Test output data file shouldn't previously exist", !destination.exists() );
         */
        if ( destination.exists() )
        {
            FileUtils.forceDelete( destination );
        }
        return destination;
    }

    /**
     * <p>checkFile.</p>
     *
     * @param file a {@link java.io.File} object.
     * @param referenceFile a {@link java.io.File} object.
     * @throws java.lang.Exception if any.
     */
    protected void checkFile( final File file, final File referenceFile )
        throws Exception
    {
        assertTrue( "Check existence of output file", file.exists() );
        assertEqualContent( referenceFile, file );
    }

    /**
     * <p>checkWrite.</p>
     *
     * @param output a {@link java.io.OutputStream} object.
     * @throws java.lang.Exception if any.
     */
    protected void checkWrite( final OutputStream output )
        throws Exception
    {
        try
        {
            new PrintStream( output ).write( 0 );
        }
        catch ( final Throwable t )
        {
            throw new AssertionFailedError( "The copy() method closed the stream " + "when it shouldn't have. "
                + t.getMessage() );
        }
    }

    /**
     * <p>checkWrite.</p>
     *
     * @param output a {@link java.io.Writer} object.
     * @throws java.lang.Exception if any.
     */
    protected void checkWrite( final Writer output )
        throws Exception
    {
        try
        {
            new PrintWriter( output ).write( 'a' );
        }
        catch ( final Throwable t )
        {
            throw new AssertionFailedError( "The copy() method closed the stream " + "when it shouldn't have. "
                + t.getMessage() );
        }
    }

    /**
     * <p>deleteFile.</p>
     *
     * @param file a {@link java.io.File} object.
     * @throws java.lang.Exception if any.
     */
    protected void deleteFile( final File file )
        throws Exception
    {
        if ( file.exists() )
        {
            assertTrue( "Couldn't delete file: " + file, file.delete() );
        }
    }

    // ----------------------------------------------------------------------
    // Assertions
    // ----------------------------------------------------------------------

    /** Assert that the content of two files is the same. */
    private void assertEqualContent( final File f0, final File f1 )
        throws IOException
    {
        /*
         * This doesn't work because the filesize isn't updated until the file is closed. assertTrue( "The files " + f0
         * + " and " + f1 + " have differing file sizes (" + f0.length() + " vs " + f1.length() + ")", ( f0.length() ==
         * f1.length() ) );
         */
        final InputStream is0 = Files.newInputStream( f0.toPath() );
        try
        {
            final InputStream is1 = Files.newInputStream( f1.toPath() );
            try
            {
                final byte[] buf0 = new byte[1024];
                final byte[] buf1 = new byte[1024];
                int n0 = 0;
                int n1 = 0;

                while ( -1 != n0 )
                {
                    n0 = is0.read( buf0 );
                    n1 = is1.read( buf1 );
                    assertTrue( "The files " + f0 + " and " + f1 + " have differing number of bytes available (" + n0
                        + " vs " + n1 + ")", ( n0 == n1 ) );

                    assertTrue( "The files " + f0 + " and " + f1 + " have different content",
                                Arrays.equals( buf0, buf1 ) );
                }
            }
            finally
            {
                is1.close();
            }
        }
        finally
        {
            is0.close();
        }
    }

    /**
     * Assert that the content of a file is equal to that in a byte[].
     *
     * @param b0 an array of {@link byte} objects.
     * @param file a {@link java.io.File} object.
     * @throws java.io.IOException if any.
     */
    protected void assertEqualContent( final byte[] b0, final File file )
        throws IOException
    {
        final InputStream is = Files.newInputStream( file.toPath() );
        try
        {
            byte[] b1 = new byte[b0.length];
            int numRead = is.read( b1 );
            assertTrue( "Different number of bytes", numRead == b0.length && is.available() == 0 );
            for ( int i = 0; i < numRead; assertTrue( "Byte " + i + " differs (" + b0[i] + " != " + b1[i] + ")",
                                                      b0[i] == b1[i] ), i++ )
                ;
        }
        finally
        {
            is.close();
        }
    }

    /**
     * <p>assertIsDirectory.</p>
     *
     * @param file a {@link java.io.File} object.
     */
    protected void assertIsDirectory( File file )
    {
        assertTrue( "The File doesn't exists: " + file.getAbsolutePath(), file.exists() );

        assertTrue( "The File isn't a directory: " + file.getAbsolutePath(), file.isDirectory() );
    }

    /**
     * <p>assertIsFile.</p>
     *
     * @param file a {@link java.io.File} object.
     */
    protected void assertIsFile( File file )
    {
        assertTrue( "The File doesn't exists: " + file.getAbsolutePath(), file.exists() );

        assertTrue( "The File isn't a file: " + file.getAbsolutePath(), file.isFile() );
    }
}
