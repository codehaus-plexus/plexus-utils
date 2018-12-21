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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

/**
 * This is used to test IOUtil for correctness. The following checks are performed:
 * <ul>
 * <li>The return must not be null, must be the same type and equals() to the method's second arg</li>
 * <li>All bytes must have been read from the source (available() == 0)</li>
 * <li>The source and destination content must be identical (byte-wise comparison check)</li>
 * <li>The output stream must not have been closed (a byte/char is written to test this, and subsequent size
 * checked)</li>
 * </ul>
 * Due to interdependencies in IOUtils and IOUtilsTestlet, one bug may cause multiple tests to fail.
 *
 * @author <a href="mailto:jefft@apache.org">Jeff Turner</a>
 */
public final class IOUtilTest
{
    /*
     * Note: this is not particularly beautiful code. A better way to check for flush and close status would be to
     * implement "trojan horse" wrapper implementations of the various stream classes, which set a flag when relevant
     * methods are called. (JT)
     */

    private int FILE_SIZE = 1024 * 4 + 1;

    private File testDirectory;

    private File testFile;

    @Before
    public void setUp()
    {
        try
        {
            testDirectory = ( new File( "target/test/io/" ) ).getAbsoluteFile();
            if ( !testDirectory.exists() )
            {
                testDirectory.mkdirs();
            }

            testFile = new File( testDirectory, "file2-test.txt" );

            createFile( testFile, FILE_SIZE );
        }
        catch ( IOException ioe )
        {
            throw new RuntimeException( "Can't run this test because environment could not be built" );
        }
    }

    public void tearDown()
    {
        testFile.delete();
        testDirectory.delete();
    }

    private void createFile( File file, long size )
        throws IOException
    {
        BufferedOutputStream output = new BufferedOutputStream( new FileOutputStream( file ) );

        for ( int i = 0; i < size; i++ )
        {
            output.write( (byte) ( i % 128 ) ); // nice varied byte pattern compatible with Readers and Writers
        }

        output.close();
    }

    /** Assert that the contents of two byte arrays are the same. */
    private void assertEqualContent( byte[] b0, byte[] b1 )
    {
        assertTrue( "Content not equal according to java.util.Arrays#equals()", Arrays.equals( b0, b1 ) );
    }

    /** Assert that the content of two files is the same. */
    private void assertEqualContent( File f0, File f1 )
        throws IOException
    {
        FileInputStream is0 = new FileInputStream( f0 );
        FileInputStream is1 = new FileInputStream( f1 );
        byte[] buf0 = new byte[FILE_SIZE];
        byte[] buf1 = new byte[FILE_SIZE];
        int n0 = 0;
        int n1 = 0;

        try
        {
            while ( 0 <= n0 )
            {
                n0 = is0.read( buf0 );
                n1 = is1.read( buf1 );
                assertTrue( "The files " + f0 + " and " + f1 + " have differing number of bytes available (" + n0
                    + " vs " + n1 + ")", ( n0 == n1 ) );

                assertTrue( "The files " + f0 + " and " + f1 + " have different content", Arrays.equals( buf0, buf1 ) );
            }
        }
        finally
        {
            is0.close();
            is1.close();
        }
    }

    /** Assert that the content of a file is equal to that in a byte[]. */
    private void assertEqualContent( byte[] b0, File file )
        throws IOException
    {
        FileInputStream is = new FileInputStream( file );
        byte[] b1 = new byte[b0.length];
        int numRead = is.read( b1 );
        assertTrue( "Different number of bytes", numRead == b0.length && is.available() == 0 );
        for ( int i = 0; i < numRead; assertTrue( "Byte " + i + " differs (" + b0[i] + " != " + b1[i] + ")",
                                                  b0[i] == b1[i] ), i++ )
            ;
        is.close();
    }

    @Test
    public void testInputStreamToOutputStream()
        throws Exception
    {
        File destination = newFile( "copy1.txt" );
        FileInputStream fin = new FileInputStream( testFile );
        FileOutputStream fout = new FileOutputStream( destination );

        IOUtil.copy( fin, fout );
        assertTrue( "Not all bytes were read", fin.available() == 0 );
        fout.flush();

        checkFile( destination );
        checkWrite( fout );
        fout.close();
        fin.close();
        deleteFile( destination );
    }

    @Test
    public void testInputStreamToWriter()
        throws Exception
    {
        File destination = newFile( "copy2.txt" );
        FileInputStream fin = new FileInputStream( testFile );
        FileWriter fout = new FileWriter( destination );

        IOUtil.copy( fin, fout );

        assertTrue( "Not all bytes were read", fin.available() == 0 );
        fout.flush();

        checkFile( destination );
        checkWrite( fout );
        fout.close();
        fin.close();
        deleteFile( destination );
    }

    @Test
    public void testInputStreamToString()
        throws Exception
    {
        FileInputStream fin = new FileInputStream( testFile );
        String out = IOUtil.toString( fin );
        assertNotNull( out );
        assertTrue( "Not all bytes were read", fin.available() == 0 );
        assertTrue( "Wrong output size: out.length()=" + out.length() + "!=" + FILE_SIZE, out.length() == FILE_SIZE );
        fin.close();
    }

    @Test
    public void testReaderToOutputStream()
        throws Exception
    {
        File destination = newFile( "copy3.txt" );
        FileReader fin = new FileReader( testFile );
        FileOutputStream fout = new FileOutputStream( destination );
        IOUtil.copy( fin, fout );
        // Note: this method *does* flush. It is equivalent to:
        // OutputStreamWriter _out = new OutputStreamWriter(fout);
        // IOUtil.copy( fin, _out, 4096 ); // copy( Reader, Writer, int );
        // _out.flush();
        // out = fout;

        // Note: rely on the method to flush
        checkFile( destination );
        checkWrite( fout );
        fout.close();
        fin.close();
        deleteFile( destination );
    }

    @Test
    public void testReaderToWriter()
        throws Exception
    {
        File destination = newFile( "copy4.txt" );
        FileReader fin = new FileReader( testFile );
        FileWriter fout = new FileWriter( destination );
        IOUtil.copy( fin, fout );

        fout.flush();
        checkFile( destination );
        checkWrite( fout );
        fout.close();
        fin.close();
        deleteFile( destination );
    }

    @Test
    public void testReaderToString()
        throws Exception
    {
        FileReader fin = new FileReader( testFile );
        String out = IOUtil.toString( fin );
        assertNotNull( out );
        assertTrue( "Wrong output size: out.length()=" + out.length() + "!=" + FILE_SIZE, out.length() == FILE_SIZE );
        fin.close();
    }

    @Test
    public void testStringToOutputStream()
        throws Exception
    {
        File destination = newFile( "copy5.txt" );
        FileReader fin = new FileReader( testFile );
        // Create our String. Rely on testReaderToString() to make sure this is valid.
        String str = IOUtil.toString( fin );
        FileOutputStream fout = new FileOutputStream( destination );
        IOUtil.copy( str, fout );
        // Note: this method *does* flush. It is equivalent to:
        // OutputStreamWriter _out = new OutputStreamWriter(fout);
        // IOUtil.copy( str, _out, 4096 ); // copy( Reader, Writer, int );
        // _out.flush();
        // out = fout;
        // note: we don't flush here; this IOUtils method does it for us

        checkFile( destination );
        checkWrite( fout );
        fout.close();
        fin.close();
        deleteFile( destination );
    }

    @Test
    public void testStringToWriter()
        throws Exception
    {
        File destination = newFile( "copy6.txt" );
        FileReader fin = new FileReader( testFile );
        // Create our String. Rely on testReaderToString() to make sure this is valid.
        String str = IOUtil.toString( fin );
        FileWriter fout = new FileWriter( destination );
        IOUtil.copy( str, fout );
        fout.flush();

        checkFile( destination );
        checkWrite( fout );
        fout.close();
        fin.close();

        deleteFile( destination );
    }

    @Test
    public void testInputStreamToByteArray()
        throws Exception
    {
        FileInputStream fin = new FileInputStream( testFile );
        byte[] out = IOUtil.toByteArray( fin );
        assertNotNull( out );
        assertTrue( "Not all bytes were read", fin.available() == 0 );
        assertTrue( "Wrong output size: out.length=" + out.length + "!=" + FILE_SIZE, out.length == FILE_SIZE );
        assertEqualContent( out, testFile );
        fin.close();
    }

    @Test
    public void testStringToByteArray()
        throws Exception
    {
        FileReader fin = new FileReader( testFile );

        // Create our String. Rely on testReaderToString() to make sure this is valid.
        String str = IOUtil.toString( fin );

        byte[] out = IOUtil.toByteArray( str );
        assertEqualContent( str.getBytes(), out );
        fin.close();
    }

    @Test
    public void testByteArrayToWriter()
        throws Exception
    {
        File destination = newFile( "copy7.txt" );
        FileWriter fout = new FileWriter( destination );
        FileInputStream fin = new FileInputStream( testFile );

        // Create our byte[]. Rely on testInputStreamToByteArray() to make sure this is valid.
        byte[] in = IOUtil.toByteArray( fin );
        IOUtil.copy( in, fout );
        fout.flush();
        checkFile( destination );
        checkWrite( fout );
        fout.close();
        fin.close();
        deleteFile( destination );
    }

    @Test
    public void testByteArrayToString()
        throws Exception
    {
        FileInputStream fin = new FileInputStream( testFile );
        byte[] in = IOUtil.toByteArray( fin );
        // Create our byte[]. Rely on testInputStreamToByteArray() to make sure this is valid.
        String str = IOUtil.toString( in );
        assertEqualContent( in, str.getBytes() );
        fin.close();
    }

    @Test
    public void testByteArrayToOutputStream()
        throws Exception
    {
        File destination = newFile( "copy8.txt" );
        FileOutputStream fout = new FileOutputStream( destination );
        FileInputStream fin = new FileInputStream( testFile );

        // Create our byte[]. Rely on testInputStreamToByteArray() to make sure this is valid.
        byte[] in = IOUtil.toByteArray( fin );

        IOUtil.copy( in, fout );

        fout.flush();

        checkFile( destination );
        checkWrite( fout );
        fout.close();
        fin.close();
        deleteFile( destination );
    }

    // ----------------------------------------------------------------------
    // Test closeXXX()
    // ----------------------------------------------------------------------

    @Test
    public void testCloseInputStream()
        throws Exception
    {
        IOUtil.close( (InputStream) null );

        TestInputStream inputStream = new TestInputStream();

        IOUtil.close( inputStream );

        assertTrue( inputStream.closed );
    }

    @Test
    public void testCloseOutputStream()
        throws Exception
    {
        IOUtil.close( (OutputStream) null );

        TestOutputStream outputStream = new TestOutputStream();

        IOUtil.close( outputStream );

        assertTrue( outputStream.closed );
    }

    @Test
    public void testCloseReader()
        throws Exception
    {
        IOUtil.close( (Reader) null );

        TestReader reader = new TestReader();

        IOUtil.close( reader );

        assertTrue( reader.closed );
    }

    @Test
    public void testCloseWriter()
        throws Exception
    {
        IOUtil.close( (Writer) null );

        TestWriter writer = new TestWriter();

        IOUtil.close( writer );

        assertTrue( writer.closed );
    }

    private class TestInputStream
        extends InputStream
    {
        boolean closed;

        public void close()
        {
            closed = true;
        }

        public int read()
        {
            fail( "This method shouldn't be called" );

            return 0;
        }
    }

    private class TestOutputStream
        extends OutputStream
    {
        boolean closed;

        public void close()
        {
            closed = true;
        }

        public void write( int value )
        {
            fail( "This method shouldn't be called" );
        }
    }

    private class TestReader
        extends Reader
    {
        boolean closed;

        public void close()
        {
            closed = true;
        }

        public int read( char cbuf[], int off, int len )
        {
            fail( "This method shouldn't be called" );

            return 0;
        }
    }

    private class TestWriter
        extends Writer
    {
        boolean closed;

        public void close()
        {
            closed = true;
        }

        public void write( char cbuf[], int off, int len )
        {
            fail( "This method shouldn't be called" );
        }

        public void flush()
        {
            fail( "This method shouldn't be called" );
        }
    }

    // ----------------------------------------------------------------------
    // Utility methods
    // ----------------------------------------------------------------------

    private File newFile( String filename )
        throws Exception
    {
        File destination = new File( testDirectory, filename );
        assertTrue( filename + "Test output data file shouldn't previously exist", !destination.exists() );

        return destination;
    }

    private void checkFile( File file )
        throws Exception
    {
        assertTrue( "Check existence of output file", file.exists() );
        assertEqualContent( testFile, file );
    }

    private void checkWrite( OutputStream output )
        throws Exception
    {
        try
        {
            new PrintStream( output ).write( 0 );
        }
        catch ( Throwable t )
        {
            throw new Exception( "The copy() method closed the stream " + "when it shouldn't have. "
                + t.getMessage() );
        }
    }

    private void checkWrite( Writer output )
        throws Exception
    {
        try
        {
            new PrintWriter( output ).write( 'a' );
        }
        catch ( Throwable t )
        {
            throw new Exception( "The copy() method closed the stream " + "when it shouldn't have. "
                + t.getMessage() );
        }
    }

    private void deleteFile( File file )
        throws Exception
    {
        assertTrue( "Wrong output size: file.length()=" + file.length() + "!=" + FILE_SIZE + 1,
                    file.length() == FILE_SIZE + 1 );

        assertTrue( "File would not delete", ( file.delete() || ( !file.exists() ) ) );
    }
}
