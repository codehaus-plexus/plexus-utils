package org.codehaus.plexus.util;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.codehaus.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Turbine" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact codehaus@codehaus.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Turbine", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.codehaus.org/>.
 *
 */

import org.codehaus.plexus.util.io.InputStreamFacade;
import org.codehaus.plexus.util.io.URLInputStreamFacade;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * <p>This class provides basic facilities for manipulating files and file paths.</p>
 * 
 * <b>Path-related methods</b>
 * 
 * <p>Methods exist to retrieve the components of a typical file path. For example
 * <code>/www/hosted/mysite/index.html</code>, can be broken into:
 * <ul>
 * <li><code>/www/hosted/mysite/</code> -- retrievable through {@link #getPath}</li>
 * <li><code>index.html</code> -- retrievable through {@link #removePath}</li>
 * <li><code>/www/hosted/mysite/index</code> -- retrievable through {@link #removeExtension}</li>
 * <li><code>html</code> -- retrievable through {@link #getExtension}</li>
 * </ul>
 * <p>There are also methods to {@link #catPath concatenate two paths}, {@link #resolveFile resolve a path relative to a
 * File} and {@link #normalize} a path.</p>

 * <b>File-related methods</b>
 * 
 * <p>There are methods to create a {@link #toFile File from a URL}, copy a {@link #copyFileToDirectory File to a
 * directory}, copy a {@link #copyFile File to another File}, copy a {@link #copyURLToFile URL's contents to a File}, as
 * well as methods to {@link #deleteDirectory(File) delete} and {@link #cleanDirectory(File) clean} a directory.</p>
 * 
 * <p>Common {@link java.io.File} manipulation routines.</p>
 * 
 * <p>Taken from the commons-utils repo. Also code from Alexandria's FileUtils. And from Avalon Excalibur's IO. And from
 * Ant.</p>
 *
 * @author <a href="mailto:burton@relativity.yi.org">Kevin A. Burton</A>
 * @author <a href="mailto:sanders@codehaus.org">Scott Sanders</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @author <a href="mailto:Christoph.Reck@dlr.de">Christoph.Reck</a>
 * @author <a href="mailto:peter@codehaus.org">Peter Donald</a>
 * @author <a href="mailto:jefft@codehaus.org">Jeff Turner</a>
 *
 */
public class FileUtils
{
    /**
     * The number of bytes in a kilobyte.
     */
    public static final int ONE_KB = 1024;

    /**
     * The number of bytes in a megabyte.
     */
    public static final int ONE_MB = ONE_KB * ONE_KB;

    /**
     * The number of bytes in a gigabyte.
     */
    public static final int ONE_GB = ONE_KB * ONE_MB;

    /**
     * The vm file separator
     */
    public static String FS = File.separator;

    /**
     * Non-valid Characters for naming files, folders under Windows: <code>":", "*", "?", "\"", "<", ">", "|"</code>
     *
     * @see <a href="http://support.microsoft.com/?scid=kb%3Ben-us%3B177506&x=12&y=13">
     *      http://support.microsoft.com/?scid=kb%3Ben-us%3B177506&x=12&y=13</a>
     */
    private static final String[] INVALID_CHARACTERS_FOR_WINDOWS_FILE_NAME = { ":", "*", "?", "\"", "<", ">", "|" };

    /**
     * @return the default excludes pattern
     * @see DirectoryScanner#DEFAULTEXCLUDES
     */
    public static String[] getDefaultExcludes()
    {
        return DirectoryScanner.DEFAULTEXCLUDES;
    }

    /**
     * @return the default excludes pattern as list.
     * @see #getDefaultExcludes()
     */
    public static List<String> getDefaultExcludesAsList()
    {
        return Arrays.asList( getDefaultExcludes() );
    }

    /**
     * @return the default excludes pattern as comma separated string.
     * @see DirectoryScanner#DEFAULTEXCLUDES
     * @see StringUtils#join(Object[], String)
     */
    public static String getDefaultExcludesAsString()
    {
        return StringUtils.join( DirectoryScanner.DEFAULTEXCLUDES, "," );
    }

    /**
     * Returns a human-readable version of the file size (original is in bytes).
     *
     * @param size The number of bytes.
     * @return A human-readable display value (includes units).
     */
    public static String byteCountToDisplaySize( int size )
    {
        String displaySize;

        if ( size / ONE_GB > 0 )
        {
            displaySize = String.valueOf( size / ONE_GB ) + " GB";
        }
        else if ( size / ONE_MB > 0 )
        {
            displaySize = String.valueOf( size / ONE_MB ) + " MB";
        }
        else if ( size / ONE_KB > 0 )
        {
            displaySize = String.valueOf( size / ONE_KB ) + " KB";
        }
        else
        {
            displaySize = String.valueOf( size ) + " bytes";
        }

        return displaySize;
    }

    /**
     * Returns the directory path portion of a file specification string. Matches the equally named unix command.
     *
     * @param filename the file path
     * @return The directory portion excluding the ending file separator.
     */
    public static String dirname( String filename )
    {
        int i = filename.lastIndexOf( File.separator );
        return ( i >= 0 ? filename.substring( 0, i ) : "" );
    }

    /**
     * Returns the filename portion of a file specification string.
     *
     * @param filename the file path
     * @return The filename string with extension.
     */
    public static String filename( String filename )
    {
        int i = filename.lastIndexOf( File.separator );
        return ( i >= 0 ? filename.substring( i + 1 ) : filename );
    }

    /**
     * Returns the filename portion of a file specification string. Matches the equally named unix command.
     *
     * @param filename the file path
     * @return The filename string without extension.
     */
    public static String basename( String filename )
    {
        return basename( filename, extension( filename ) );
    }

    /**
     * Returns the filename portion of a file specification string. Matches the equally named unix command.
     *
     * @param filename the file path
     * @param suffix the file suffix
     * @return the basename of the file
     */
    public static String basename( String filename, String suffix )
    {
        int i = filename.lastIndexOf( File.separator ) + 1;
        int lastDot = ( ( suffix != null ) && ( suffix.length() > 0 ) ) ? filename.lastIndexOf( suffix ) : -1;

        if ( lastDot >= 0 )
        {
            return filename.substring( i, lastDot );
        }
        else if ( i > 0 )
        {
            return filename.substring( i );
        }
        else
        {
            return filename; // else returns all (no path and no extension)
        }
    }

    /**
     * Returns the extension portion of a file specification string. This everything after the last dot '.' in the
     * filename (NOT including the dot).
     *
     * @param filename the file path
     * @return the extension of the file
     */
    public static String extension( String filename )
    {
        // Ensure the last dot is after the last file separator
        int lastSep = filename.lastIndexOf( File.separatorChar );
        int lastDot;
        if ( lastSep < 0 )
        {
            lastDot = filename.lastIndexOf( '.' );
        }
        else
        {
            lastDot = filename.substring( lastSep + 1 ).lastIndexOf( '.' );
            if ( lastDot >= 0 )
            {
                lastDot += lastSep + 1;
            }
        }

        if ( lastDot >= 0 && lastDot > lastSep )
        {
            return filename.substring( lastDot + 1 );
        }

        return "";
    }

    /**
     * Check if a file exits.
     *
     * @param fileName the file path.
     * @return true if file exists.
     */
    public static boolean fileExists( String fileName )
    {
        File file = new File( fileName );
        return file.exists();
    }

    /**
     * Note: the file content is read with platform encoding.
     *
     * @param file the file path
     * @return the file content using the platform encoding.
     * @throws IOException if any
     */
    public static String fileRead( String file )
        throws IOException
    {
        return fileRead( file, null );
    }

    /**
     * @param file the file path
     * @param encoding the wanted encoding
     * @return the file content using the specified encoding.
     * @throws IOException if any
     */
    public static String fileRead( String file, String encoding )
        throws IOException
    {
        return fileRead( new File( file ), encoding );
    }

    /**
     * Note: the file content is read with platform encoding
     *
     * @param file the file path
     * @return the file content using the platform encoding.
     * @throws IOException if any
     */
    public static String fileRead( File file )
        throws IOException
    {
        return fileRead( file, null );
    }

    /**
     * @param file the file path
     * @param encoding the wanted encoding
     * @return the file content using the specified encoding.
     * @throws IOException if any
     */
    public static String fileRead( File file, String encoding )
        throws IOException
    {
        StringBuilder buf = new StringBuilder();

        try ( Reader reader = getInputStreamReader( file, encoding ) )
        {
            int count;
            char[] b = new char[512];
            while ( ( count = reader.read( b ) ) >= 0 ) // blocking read
            {
                buf.append( b, 0, count );
            }
        }

        return buf.toString();
    }

    private static InputStreamReader getInputStreamReader( File file, String encoding ) throws IOException
    {
        if ( encoding != null )
        {
            return new InputStreamReader( Files.newInputStream( file.toPath() ), encoding );
        }
        else
        {
            return new InputStreamReader( Files.newInputStream( file.toPath() ) );
        }
    }
    
    /**
     * Appends data to a file. The file will be created if it does not exist. Note: the data is written with platform
     * encoding
     *
     * @param fileName The path of the file to write.
     * @param data The content to write to the file.
     * @throws IOException if any
     */
    public static void fileAppend( String fileName, String data )
        throws IOException
    {
        fileAppend( fileName, null, data );
    }

    /**
     * Appends data to a file. The file will be created if it does not exist.
     *
     * @param fileName The path of the file to write.
     * @param encoding The encoding of the file.
     * @param data The content to write to the file.
     * @throws IOException if any
     */
    public static void fileAppend( String fileName, String encoding, String data )
        throws IOException
    {
        try ( OutputStream out = Files.newOutputStream( Paths.get(fileName), StandardOpenOption.APPEND ) )
        {
            if ( encoding != null )
            {
                out.write( data.getBytes( encoding ) );
            }
            else
            {
                out.write( data.getBytes() );
            }
        }
    }

    /**
     * Writes data to a file. The file will be created if it does not exist. Note: the data is written with platform
     * encoding
     *
     * @param fileName The path of the file to write.
     * @param data The content to write to the file.
     * @throws IOException if any
     */
    public static void fileWrite( String fileName, String data )
        throws IOException
    {
        fileWrite( fileName, null, data );
    }

    /**
     * Writes data to a file. The file will be created if it does not exist.
     *
     * @param fileName The path of the file to write.
     * @param encoding The encoding of the file.
     * @param data The content to write to the file.
     * @throws IOException if any
     */
    public static void fileWrite( String fileName, String encoding, String data )
        throws IOException
    {
        File file = ( fileName == null ) ? null : new File( fileName );
        fileWrite( file, encoding, data );
    }

    /**
     * Writes data to a file. The file will be created if it does not exist. Note: the data is written with platform
     * encoding
     *
     * @param file The file to write.
     * @param data The content to write to the file.
     * @throws IOException if any
     * @since 2.0.6
     */
    public static void fileWrite( File file, String data )
        throws IOException
    {
        fileWrite( file, null, data );
    }

    /**
     * Writes data to a file. The file will be created if it does not exist.
     *
     * @param file The file to write.
     * @param encoding The encoding of the file.
     * @param data The content to write to the file.
     * @throws IOException if any
     * @since 2.0.6
     */
    public static void fileWrite( File file, String encoding, String data )
        throws IOException
    {
        try ( Writer writer = getOutputStreamWriter( file, encoding ) )
        {
            writer.write( data );
        }
    }
    
    private static OutputStreamWriter getOutputStreamWriter( File file, String encoding ) throws IOException
    {
        OutputStream out = Files.newOutputStream( file.toPath() );
        if ( encoding != null )
        {
            return new OutputStreamWriter( out, encoding );
        }
        else
        {
            return new OutputStreamWriter( out );
        }
    }

    /**
     * Deletes a file.
     *
     * @param fileName The path of the file to delete.
     */
    public static void fileDelete( String fileName )
    {
        File file = new File( fileName );
        try
        {
            NioFiles.deleteIfExists( file );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e );
        }
    }

    /**
     * Waits for NFS to propagate a file creation, imposing a timeout.
     *
     * @param fileName The path of the file.
     * @param seconds The maximum time in seconds to wait.
     * @return True if file exists.
     */
    public static boolean waitFor( String fileName, int seconds )
    {
        return waitFor( new File( fileName ), seconds );
    }

    /**
     * Waits for NFS to propagate a file creation, imposing a timeout.
     *
     * @param file The file.
     * @param seconds The maximum time in seconds to wait.
     * @return True if file exists.
     */
    public static boolean waitFor( File file, int seconds )
    {
        int timeout = 0;
        int tick = 0;
        while ( !file.exists() )
        {
            if ( tick++ >= 10 )
            {
                tick = 0;
                if ( timeout++ > seconds )
                {
                    return false;
                }
            }
            try
            {
                Thread.sleep( 100 );
            }
            catch ( InterruptedException ignore )
            {
                // nop
            }
        }
        return true;
    }

    /**
     * Creates a file handle.
     *
     * @param fileName The path of the file.
     * @return A <code>File</code> manager.
     */
    public static File getFile( String fileName )
    {
        return new File( fileName );
    }

    /**
     * <p>Given a directory and an array of extensions return an array of compliant files.</p>
     * 
     * <p>TODO Should an ignore list be passed in? TODO Should a recurse flag be passed in?</p>
     * 
     * <p>The given extensions should be like "java" and not like ".java"</p>
     *
     * @param directory The path of the directory.
     * @param extensions an array of expected extensions.
     * @return An array of files for the wanted extensions.
     */
    public static String[] getFilesFromExtension( String directory, String[] extensions )
    {
        List<String> files = new ArrayList<String>();

        File currentDir = new File( directory );

        String[] unknownFiles = currentDir.list();

        if ( unknownFiles == null )
        {
            return new String[0];
        }

        for ( String unknownFile : unknownFiles )
        {
            String currentFileName = directory + System.getProperty( "file.separator" ) + unknownFile;
            File currentFile = new File( currentFileName );

            if ( currentFile.isDirectory() )
            {
                // ignore all CVS directories...
                if ( currentFile.getName().equals( "CVS" ) )
                {
                    continue;
                }

                // ok... transverse into this directory and get all the files... then combine
                // them with the current list.

                String[] fetchFiles = getFilesFromExtension( currentFileName, extensions );
                files = blendFilesToVector( files, fetchFiles );
            }
            else
            {
                // ok... add the file

                String add = currentFile.getAbsolutePath();
                if ( isValidFile( add, extensions ) )
                {
                    files.add( add );
                }
            }
        }

        // ok... move the Vector into the files list...
        return files.toArray( new String[0] );
    }

    /**
     * Private helper method for getFilesFromExtension()
     */
    private static List<String> blendFilesToVector( List<String> v, String[] files )
    {
        for ( String file : files )
        {
            v.add( file );
        }

        return v;
    }

    /**
     * Checks to see if a file is of a particular type(s). Note that if the file does not have an extension, an empty
     * string (&quot;&quot;) is matched for.
     */
    private static boolean isValidFile( String file, String[] extensions )
    {
        String extension = extension( file );
        if ( extension == null )
        {
            extension = "";
        }

        // ok.. now that we have the "extension" go through the current know
        // excepted extensions and determine if this one is OK.

        for ( String extension1 : extensions )
        {
            if ( extension1.equals( extension ) )
            {
                return true;
            }
        }

        return false;

    }

    /**
     * Simple way to make a directory
     *
     * @param dir the directory to create
     * @throws IllegalArgumentException if the dir contains illegal Windows characters under Windows OS.
     * @see #INVALID_CHARACTERS_FOR_WINDOWS_FILE_NAME
     */
    public static void mkdir( String dir )
    {
        File file = new File( dir );

        if ( Os.isFamily( Os.FAMILY_WINDOWS ) && !isValidWindowsFileName( file ) )
        {
            throw new IllegalArgumentException( "The file (" + dir
                + ") cannot contain any of the following characters: \n"
                + StringUtils.join( INVALID_CHARACTERS_FOR_WINDOWS_FILE_NAME, " " ) );
        }

        if ( !file.exists() )
        {
            file.mkdirs();
        }
    }

    /**
     * Compare the contents of two files to determine if they are equal or not.
     *
     * @param file1 the first file
     * @param file2 the second file
     * @return true if the content of the files are equal or they both don't exist, false otherwise
     * @throws IOException if any
     */
    public static boolean contentEquals( final File file1, final File file2 )
        throws IOException
    {
        final boolean file1Exists = file1.exists();
        if ( file1Exists != file2.exists() )
        {
            return false;
        }

        if ( !file1Exists )
        {
            // two not existing files are equal
            return true;
        }

        if ( file1.isDirectory() || file2.isDirectory() )
        {
            // don't want to compare directory contents
            return false;
        }
        
        try ( InputStream input1 = Files.newInputStream( file1.toPath() );
              InputStream input2 = Files.newInputStream( file2.toPath() ) )
        {
            return IOUtil.contentEquals( input1, input2 );
        }
    }

    /**
     * Convert from a <code>URL</code> to a <code>File</code>.
     *
     * @param url File URL.
     * @return The equivalent <code>File</code> object, or <code>null</code> if the URL's protocol is not
     *         <code>file</code>
     */
    public static File toFile( final URL url )
    {
        if ( url == null || !url.getProtocol().equalsIgnoreCase( "file" ) )
        {
            return null;
        }

        String filename = url.getFile().replace( '/', File.separatorChar );
        int pos = -1;
        while ( ( pos = filename.indexOf( '%', pos + 1 ) ) >= 0 )
        {
            if ( pos + 2 < filename.length() )
            {
                String hexStr = filename.substring( pos + 1, pos + 3 );
                char ch = (char) Integer.parseInt( hexStr, 16 );
                filename = filename.substring( 0, pos ) + ch + filename.substring( pos + 3 );
            }
        }
        return new File( filename );
    }

    /**
     * Convert the array of Files into a list of URLs.
     *
     * @param files the array of files
     * @return the array of URLs
     * @throws IOException if an error occurs
     */
    public static URL[] toURLs( final File[] files )
        throws IOException
    {
        final URL[] urls = new URL[files.length];

        for ( int i = 0; i < urls.length; i++ )
        {
            urls[i] = files[i].toURI().toURL();
        }

        return urls;
    }

    /**
     * Remove extension from filename. ie
     * 
     * <pre>
     * foo.txt    --&gt; foo
     * a\b\c.jpg  --&gt; a\b\c
     * a\b\c      --&gt; a\b\c
     * </pre>
     *
     * @param filename the path of the file
     * @return the filename minus extension
     */
    public static String removeExtension( final String filename )
    {
        String ext = extension( filename );

        if ( "".equals( ext ) )
        {
            return filename;
        }

        final int index = filename.lastIndexOf( ext ) - 1;
        return filename.substring( 0, index );
    }

    /**
     * Get extension from filename. ie
     * 
     * <pre>
     * foo.txt    --&gt; "txt"
     * a\b\c.jpg  --&gt; "jpg"
     * a\b\c      --&gt; ""
     * </pre>
     *
     * @param filename the path of the file
     * @return the extension of filename or "" if none
     */
    public static String getExtension( final String filename )
    {
        return extension( filename );
    }

    /**
     * Remove path from filename. Equivalent to the unix command <code>basename</code> ie.
     * 
     * <pre>
     * a/b/c.txt --&gt; c.txt
     * a.txt     --&gt; a.txt
     * </pre>
     *
     * @param filepath the path of the file
     * @return the filename minus path
     */
    public static String removePath( final String filepath )
    {
        return removePath( filepath, File.separatorChar );
    }

    /**
     * Remove path from filename. ie.
     * 
     * <pre>
     * a/b/c.txt --&gt; c.txt
     * a.txt     --&gt; a.txt
     * </pre>
     *
     * @param filepath the path of the file
     * @param fileSeparatorChar the file separator character like <b>/</b> on Unix platforms.
     * @return the filename minus path
     */
    public static String removePath( final String filepath, final char fileSeparatorChar )
    {
        final int index = filepath.lastIndexOf( fileSeparatorChar );

        if ( -1 == index )
        {
            return filepath;
        }

        return filepath.substring( index + 1 );
    }

    /**
     * Get path from filename. Roughly equivalent to the unix command <code>dirname</code>. ie.
     * 
     * <pre>
     * a/b/c.txt --&gt; a/b
     * a.txt     --&gt; ""
     * </pre>
     *
     * @param filepath the filepath
     * @return the filename minus path
     */
    public static String getPath( final String filepath )
    {
        return getPath( filepath, File.separatorChar );
    }

    /**
     * Get path from filename. ie.
     * 
     * <pre>
     * a/b/c.txt --&gt; a/b
     * a.txt     --&gt; ""
     * </pre>
     *
     * @param filepath the filepath
     * @param fileSeparatorChar the file separator character like <b>/</b> on Unix platforms.
     * @return the filename minus path
     */
    public static String getPath( final String filepath, final char fileSeparatorChar )
    {
        final int index = filepath.lastIndexOf( fileSeparatorChar );
        if ( -1 == index )
        {
            return "";
        }

        return filepath.substring( 0, index );
    }

    /**
     * Copy file from source to destination. If <code>destinationDirectory</code> does not exist, it (and any parent
     * directories) will be created. If a file <code>source</code> in <code>destinationDirectory</code> exists, it will
     * be overwritten.
     *
     * @param source An existing <code>File</code> to copy.
     * @param destinationDirectory A directory to copy <code>source</code> into.
     * @throws java.io.FileNotFoundException if <code>source</code> isn't a normal file.
     * @throws IllegalArgumentException if <code>destinationDirectory</code> isn't a directory.
     * @throws IOException if <code>source</code> does not exist, the file in <code>destinationDirectory</code> cannot
     *             be written to, or an IO error occurs during copying.
     */
    public static void copyFileToDirectory( final String source, final String destinationDirectory )
        throws IOException
    {
        copyFileToDirectory( new File( source ), new File( destinationDirectory ) );
    }

    /**
     * Copy file from source to destination only if source is newer than the target file. If
     * <code>destinationDirectory</code> does not exist, it (and any parent directories) will be created. If a file
     * <code>source</code> in <code>destinationDirectory</code> exists, it will be overwritten.
     *
     * @param source An existing <code>File</code> to copy.
     * @param destinationDirectory A directory to copy <code>source</code> into.
     * @throws java.io.FileNotFoundException if <code>source</code> isn't a normal file.
     * @throws IllegalArgumentException if <code>destinationDirectory</code> isn't a directory.
     * @throws IOException if <code>source</code> does not exist, the file in <code>destinationDirectory</code> cannot
     *             be written to, or an IO error occurs during copying.
     */
    public static void copyFileToDirectoryIfModified( final String source, final String destinationDirectory )
        throws IOException
    {
        copyFileToDirectoryIfModified( new File( source ), new File( destinationDirectory ) );
    }

    /**
     * Copy file from source to destination. If <code>destinationDirectory</code> does not exist, it (and any parent
     * directories) will be created. If a file <code>source</code> in <code>destinationDirectory</code> exists, it will
     * be overwritten.
     *
     * @param source An existing <code>File</code> to copy.
     * @param destinationDirectory A directory to copy <code>source</code> into.
     * @throws java.io.FileNotFoundException if <code>source</code> isn't a normal file.
     * @throws IllegalArgumentException if <code>destinationDirectory</code> isn't a directory.
     * @throws IOException if <code>source</code> does not exist, the file in <code>destinationDirectory</code> cannot
     *             be written to, or an IO error occurs during copying.
     */
    public static void copyFileToDirectory( final File source, final File destinationDirectory )
        throws IOException
    {
        if ( destinationDirectory.exists() && !destinationDirectory.isDirectory() )
        {
            throw new IllegalArgumentException( "Destination is not a directory" );
        }

        copyFile( source, new File( destinationDirectory, source.getName() ) );
    }

    /**
     * Copy file from source to destination only if source is newer than the target file. If
     * <code>destinationDirectory</code> does not exist, it (and any parent directories) will be created. If a file
     * <code>source</code> in <code>destinationDirectory</code> exists, it will be overwritten.
     *
     * @param source An existing <code>File</code> to copy.
     * @param destinationDirectory A directory to copy <code>source</code> into.
     * @throws java.io.FileNotFoundException if <code>source</code> isn't a normal file.
     * @throws IllegalArgumentException if <code>destinationDirectory</code> isn't a directory.
     * @throws IOException if <code>source</code> does not exist, the file in <code>destinationDirectory</code> cannot
     *             be written to, or an IO error occurs during copying.
     */
    public static void copyFileToDirectoryIfModified( final File source, final File destinationDirectory )
        throws IOException
    {
        if ( destinationDirectory.exists() && !destinationDirectory.isDirectory() )
        {
            throw new IllegalArgumentException( "Destination is not a directory" );
        }

        copyFileIfModified( source, new File( destinationDirectory, source.getName() ) );
    }

    /**
     * Creates a number of directories, as delivered from DirectoryScanner
     * 
     * @param sourceBase The basedir used for the directory scan
     * @param dirs The getIncludedDirs from the dirscanner
     * @param destination The base dir of the output structure
     * @throws IOException io issue
     */
    public static void mkDirs( final File sourceBase, String[] dirs, final File destination )
        throws IOException
    {
        for ( String dir : dirs )
        {
            File src = new File( sourceBase, dir );
            File dst = new File( destination, dir );
            if ( NioFiles.isSymbolicLink( src ) )
            {
                File target = NioFiles.readSymbolicLink( src );
                NioFiles.createSymbolicLink( dst, target );
            }
            else
            {
                dst.mkdirs();
            }
        }
    }

    /**
     * Copy file from source to destination. The directories up to <code>destination</code> will be created if they
     * don't already exist. <code>destination</code> will be overwritten if it already exists.
     *
     * @param source An existing non-directory <code>File</code> to copy bytes from.
     * @param destination A non-directory <code>File</code> to write bytes to (possibly overwriting).
     * @throws IOException if <code>source</code> does not exist, <code>destination</code> cannot be written to, or an
     *             IO error occurs during copying.
     * @throws java.io.FileNotFoundException if <code>destination</code> is a directory (use
     *             {@link #copyFileToDirectory}).
     */
    public static void copyFile( final File source, final File destination )
        throws IOException
    {
        // check source exists
        if ( !source.exists() )
        {
            final String message = "File " + source + " does not exist";
            throw new IOException( message );
        }

        // check source != destination, see PLXUTILS-10
        if ( source.getCanonicalPath().equals( destination.getCanonicalPath() ) )
        {
            // if they are equal, we can exit the method without doing any work
            return;
        }
        mkdirsFor( destination );

        doCopyFile( source, destination );

        if ( source.length() != destination.length() )
        {
            String message = "Failed to copy full contents from " + source + " to " + destination;
            throw new IOException( message );
        }
    }

    private static void doCopyFile( File source, File destination )
        throws IOException
    {
        doCopyFileUsingNewIO( source, destination );
    }

    private static void doCopyFileUsingNewIO( File source, File destination )
        throws IOException
    {
        NioFiles.copy( source, destination );
    }

    /**
     * Link file from destination to source. The directories up to <code>destination</code> will be created if they
     * don't already exist. <code>destination</code> will be overwritten if it already exists.
     *
     * @param source An existing non-directory <code>File</code> to link to.
     * @param destination A non-directory <code>File</code> becoming the link (possibly overwriting).
     * @throws IOException if <code>source</code> does not exist, <code>destination</code> cannot be created, or an
     *             IO error occurs during linking.
     * @throws java.io.FileNotFoundException if <code>destination</code> is a directory (use
     *             {@link #copyFileToDirectory}).
     */
    public static void linkFile( final File source, final File destination )
        throws IOException
    {
        // check source exists
        if ( !source.exists() )
        {
            final String message = "File " + source + " does not exist";
            throw new IOException( message );
        }

        // check source != destination, see PLXUTILS-10
        if ( source.getCanonicalPath().equals( destination.getCanonicalPath() ) )
        {
            // if they are equal, we can exit the method without doing any work
            return;
        }
        mkdirsFor( destination );

        NioFiles.createSymbolicLink( destination, source );
    }

    /**
     * Copy file from source to destination only if source timestamp is later than the destination timestamp. The
     * directories up to <code>destination</code> will be created if they don't already exist. <code>destination</code>
     * will be overwritten if it already exists.
     *
     * @param source An existing non-directory <code>File</code> to copy bytes from.
     * @param destination A non-directory <code>File</code> to write bytes to (possibly overwriting).
     * @return true if no problem occured
     * @throws IOException if <code>source</code> does not exist, <code>destination</code> cannot be written to, or an
     *             IO error occurs during copying.
     */
    public static boolean copyFileIfModified( final File source, final File destination )
        throws IOException
    {
        if ( isSourceNewerThanDestination( source, destination ) )
        {
            copyFile( source, destination );

            return true;
        }

        return false;
    }

    /**
     * Copies bytes from the URL <code>source</code> to a file <code>destination</code>. The directories up to
     * <code>destination</code> will be created if they don't already exist. <code>destination</code> will be
     * overwritten if it already exists.
     *
     * @param source A <code>URL</code> to copy bytes from.
     * @param destination A non-directory <code>File</code> to write bytes to (possibly overwriting).
     * @throws IOException if
     *             <ul>
     *             <li><code>source</code> URL cannot be opened</li>
     *             <li><code>destination</code> cannot be written to</li>
     *             <li>an IO error occurs during copying</li>
     *             </ul>
     */
    public static void copyURLToFile( final URL source, final File destination )
        throws IOException
    {
        copyStreamToFile( new URLInputStreamFacade( source ), destination );
    }

    /**
     * Copies bytes from the {@link InputStream} <code>source</code> to a file <code>destination</code>. The directories
     * up to <code>destination</code> will be created if they don't already exist. <code>destination</code> will be
     * overwritten if it already exists.
     *
     * @param source An {@link InputStream} to copy bytes from. This stream is guaranteed to be closed.
     * @param destination A non-directory <code>File</code> to write bytes to (possibly overwriting).
     * @throws IOException if
     *             <ul>
     *             <li><code>source</code> URL cannot be opened</li>
     *             <li><code>destination</code> cannot be written to</li>
     *             <li>an IO error occurs during copying</li>
     *             </ul>
     */
    public static void copyStreamToFile( final InputStreamFacade source, final File destination )
        throws IOException
    {
        mkdirsFor( destination );
        checkCanWrite( destination );

        try (  InputStream input = source.getInputStream();
               OutputStream output = Files.newOutputStream( destination.toPath() ) )
        {
            IOUtil.copy( input, output );
        }
    }

    private static void checkCanWrite( File destination )
        throws IOException
    {
        // make sure we can write to destination
        if ( destination.exists() && !destination.canWrite() )
        {
            final String message = "Unable to open file " + destination + " for writing.";
            throw new IOException( message );
        }
    }

    private static void mkdirsFor( File destination )
    {
        // does destination directory exist ?
        File parentFile = destination.getParentFile();
        if ( parentFile != null && !parentFile.exists() )
        {
            parentFile.mkdirs();
        }
    }

    /**
     * Normalize a path. Eliminates "/../" and "/./" in a string. Returns <code>null</code> if the ..'s went past the
     * root. Eg:
     * 
     * <pre>
     * /foo//               --&gt;     /foo/
     * /foo/./              --&gt;     /foo/
     * /foo/../bar          --&gt;     /bar
     * /foo/../bar/         --&gt;     /bar/
     * /foo/../bar/../baz   --&gt;     /baz
     * //foo//./bar         --&gt;     /foo/bar
     * /../                 --&gt;     null
     * </pre>
     *
     * @param path the path to normalize
     * @return the normalized String, or <code>null</code> if too many ..'s.
     */
    public static String normalize( final String path )
    {
        String normalized = path;
        // Resolve occurrences of "//" in the normalized path
        while ( true )
        {
            int index = normalized.indexOf( "//" );
            if ( index < 0 )
            {
                break;
            }
            normalized = normalized.substring( 0, index ) + normalized.substring( index + 1 );
        }

        // Resolve occurrences of "/./" in the normalized path
        while ( true )
        {
            int index = normalized.indexOf( "/./" );
            if ( index < 0 )
            {
                break;
            }
            normalized = normalized.substring( 0, index ) + normalized.substring( index + 2 );
        }

        // Resolve occurrences of "/../" in the normalized path
        while ( true )
        {
            int index = normalized.indexOf( "/../" );
            if ( index < 0 )
            {
                break;
            }
            if ( index == 0 )
            {
                return null; // Trying to go outside our context
            }
            int index2 = normalized.lastIndexOf( '/', index - 1 );
            normalized = normalized.substring( 0, index2 ) + normalized.substring( index + 3 );
        }

        // Return the normalized path that we have completed
        return normalized;
    }

    /**
     * <p>Will concatenate 2 paths. Paths with <code>..</code> will be properly handled.</p>
     * 
     * Eg.,
     * <pre>
     * /a/b/c + d = /a/b/d
     * /a/b/c + ../d = /a/d
     * </pre>

     * <p>Thieved from Tomcat sources...</p>
     *
     * @param lookupPath a path
     * @param path the path to concatenate
     * @return The concatenated paths, or null if error occurs
     */
    public static String catPath( final String lookupPath, final String path )
    {
        // Cut off the last slash and everything beyond
        int index = lookupPath.lastIndexOf( "/" );
        String lookup = lookupPath.substring( 0, index );
        String pth = path;

        // Deal with .. by chopping dirs off the lookup path
        while ( pth.startsWith( "../" ) )
        {
            if ( lookup.length() > 0 )
            {
                index = lookup.lastIndexOf( "/" );
                lookup = lookup.substring( 0, index );
            }
            else
            {
                // More ..'s than dirs, return null
                return null;
            }

            index = pth.indexOf( "../" ) + 3;
            pth = pth.substring( index );
        }

        return new StringBuffer( lookup ).append( "/" ).append( pth ).toString();
    }

    /**
     * Resolve a file <code>filename</code> to it's canonical form. If <code>filename</code> is relative (doesn't start
     * with <code>/</code>), it will be resolved relative to <code>baseFile</code>, otherwise it is treated as a normal
     * root-relative path.
     *
     * @param baseFile Where to resolve <code>filename</code> from, if <code>filename</code> is relative.
     * @param filename Absolute or relative file path to resolve.
     * @return The canonical <code>File</code> of <code>filename</code>.
     */
    public static File resolveFile( final File baseFile, String filename )
    {
        String filenm = filename;
        if ( '/' != File.separatorChar )
        {
            filenm = filename.replace( '/', File.separatorChar );
        }

        if ( '\\' != File.separatorChar )
        {
            filenm = filename.replace( '\\', File.separatorChar );
        }

        // deal with absolute files
        if ( filenm.startsWith( File.separator ) || ( Os.isFamily( Os.FAMILY_WINDOWS ) && filenm.indexOf( ":" ) > 0 ) )
        {
            File file = new File( filenm );

            try
            {
                file = file.getCanonicalFile();
            }
            catch ( final IOException ioe )
            {
                // nop
            }

            return file;
        }
        // FIXME: I'm almost certain this // removal is unnecessary, as getAbsoluteFile() strips
        // them. However, I'm not sure about this UNC stuff. (JT)
        final char[] chars = filename.toCharArray();
        final StringBuilder sb = new StringBuilder();

        // remove duplicate file separators in succession - except
        // on win32 at start of filename as UNC filenames can
        // be \\AComputer\AShare\myfile.txt
        int start = 0;
        if ( '\\' == File.separatorChar )
        {
            sb.append( filenm.charAt( 0 ) );
            start++;
        }

        for ( int i = start; i < chars.length; i++ )
        {
            final boolean doubleSeparator = File.separatorChar == chars[i] && File.separatorChar == chars[i - 1];

            if ( !doubleSeparator )
            {
                sb.append( chars[i] );
            }
        }

        filenm = sb.toString();

        // must be relative
        File file = ( new File( baseFile, filenm ) ).getAbsoluteFile();

        try
        {
            file = file.getCanonicalFile();
        }
        catch ( final IOException ioe )
        {
            // nop
        }

        return file;
    }

    /**
     * Delete a file. If file is directory delete it and all sub-directories.
     *
     * @param file the file path
     * @throws IOException if any
     */
    public static void forceDelete( final String file )
        throws IOException
    {
        forceDelete( new File( file ) );
    }

    /**
     * Delete a file. If file is directory delete it and all sub-directories.
     *
     * @param file a file
     * @throws IOException if any
     */
    public static void forceDelete( final File file )
        throws IOException
    {
        if ( file.isDirectory() )
        {
            deleteDirectory( file );
        }
        else
        {
            /*
             * NOTE: Always try to delete the file even if it appears to be non-existent. This will ensure that a
             * symlink whose target does not exist is deleted, too.
             */
            boolean filePresent = file.getCanonicalFile().exists();
            if ( !deleteFile( file ) && filePresent )
            {
                final String message = "File " + file + " unable to be deleted.";
                throw new IOException( message );
            }
        }
    }

    /**
     * Accommodate Windows bug encountered in both Sun and IBM JDKs. Others possible. If the delete does not work, call
     * System.gc(), wait a little and try again.
     *
     * @param file a file
     * @throws IOException if any
     */
    private static boolean deleteFile( File file )
        throws IOException
    {
        if ( file.isDirectory() )
        {
            throw new IOException( "File " + file + " isn't a file." );
        }

        if ( !file.delete() )
        {
            if ( Os.isFamily( Os.FAMILY_WINDOWS ) )
            {
                file = file.getCanonicalFile();
                System.gc();
            }

            try
            {
                Thread.sleep( 10 );
                return file.delete();
            }
            catch ( InterruptedException ignore )
            {
                return file.delete();
            }
        }

        return true;
    }

    /**
     * Schedule a file to be deleted when JVM exits. If file is directory delete it and all sub-directories.
     *
     * @param file a file
     * @throws IOException if any
     */
    public static void forceDeleteOnExit( final File file )
        throws IOException
    {
        if ( !file.exists() )
        {
            return;
        }

        if ( file.isDirectory() )
        {
            deleteDirectoryOnExit( file );
        }
        else
        {
            file.deleteOnExit();
        }
    }

    /**
     * Recursively schedule directory for deletion on JVM exit.
     *
     * @param directory a directory
     * @throws IOException if any
     */
    private static void deleteDirectoryOnExit( final File directory )
        throws IOException
    {
        if ( !directory.exists() )
        {
            return;
        }
        directory.deleteOnExit(); // The hook reverses the list

        cleanDirectoryOnExit( directory );
    }

    /**
     * Clean a directory without deleting it.
     *
     * @param directory a directory
     * @throws IOException if any
     */
    private static void cleanDirectoryOnExit( final File directory )
        throws IOException
    {
        if ( !directory.exists() )
        {
            final String message = directory + " does not exist";
            throw new IllegalArgumentException( message );
        }

        if ( !directory.isDirectory() )
        {
            final String message = directory + " is not a directory";
            throw new IllegalArgumentException( message );
        }

        IOException exception = null;

        final File[] files = directory.listFiles();
        for ( final File file : files )
        {
            try
            {
                forceDeleteOnExit( file );
            }
            catch ( final IOException ioe )
            {
                exception = ioe;
            }
        }

        if ( null != exception )
        {
            throw exception;
        }
    }

    /**
     * Make a directory.
     *
     * @param file not null
     * @throws IOException If there already exists a file with specified name or the directory is unable to be created
     * @throws IllegalArgumentException if the file contains illegal Windows characters under Windows OS.
     * @see #INVALID_CHARACTERS_FOR_WINDOWS_FILE_NAME
     */
    public static void forceMkdir( final File file )
        throws IOException
    {
        if ( Os.isFamily( Os.FAMILY_WINDOWS ) )
        {
            if ( !isValidWindowsFileName( file ) )
            {
                throw new IllegalArgumentException( "The file (" + file.getAbsolutePath()
                    + ") cannot contain any of the following characters: \n"
                    + StringUtils.join( INVALID_CHARACTERS_FOR_WINDOWS_FILE_NAME, " " ) );
            }
        }

        if ( file.exists() )
        {
            if ( file.isFile() )
            {
                final String message =
                    "File " + file + " exists and is " + "not a directory. Unable to create directory.";
                throw new IOException( message );
            }
        }
        else
        {
            if ( false == file.mkdirs() )
            {
                final String message = "Unable to create directory " + file;
                throw new IOException( message );
            }
        }
    }

    /**
     * Recursively delete a directory.
     *
     * @param directory a directory
     * @throws IOException if any
     */
    public static void deleteDirectory( final String directory )
        throws IOException
    {
        deleteDirectory( new File( directory ) );
    }

    /**
     * Recursively delete a directory.
     *
     * @param directory a directory
     * @throws IOException if any
     */
    public static void deleteDirectory( final File directory )
        throws IOException
    {
        if ( !directory.exists() )
        {
            return;
        }

        /*
         * try delete the directory before its contents, which will take care of any directories that are really
         * symbolic links.
         */
        if ( directory.delete() )
        {
            return;
        }

        cleanDirectory( directory );
        if ( !directory.delete() )
        {
            final String message = "Directory " + directory + " unable to be deleted.";
            throw new IOException( message );
        }
    }

    /**
     * Clean a directory without deleting it.
     *
     * @param directory a directory
     * @throws IOException if any
     */
    public static void cleanDirectory( final String directory )
        throws IOException
    {
        cleanDirectory( new File( directory ) );
    }

    /**
     * Clean a directory without deleting it.
     *
     * @param directory a directory
     * @throws IOException if any
     */
    public static void cleanDirectory( final File directory )
        throws IOException
    {
        if ( !directory.exists() )
        {
            final String message = directory + " does not exist";
            throw new IllegalArgumentException( message );
        }

        if ( !directory.isDirectory() )
        {
            final String message = directory + " is not a directory";
            throw new IllegalArgumentException( message );
        }

        IOException exception = null;

        final File[] files = directory.listFiles();

        if ( files == null )
        {
            return;
        }

        for ( final File file : files )
        {
            try
            {
                forceDelete( file );
            }
            catch ( final IOException ioe )
            {
                exception = ioe;
            }
        }

        if ( null != exception )
        {
            throw exception;
        }
    }

    /**
     * Recursively count size of a directory.
     *
     * @param directory a directory
     * @return size of directory in bytes.
     */
    public static long sizeOfDirectory( final String directory )
    {
        return sizeOfDirectory( new File( directory ) );
    }

    /**
     * Recursively count size of a directory.
     *
     * @param directory a directory
     * @return size of directory in bytes.
     */
    public static long sizeOfDirectory( final File directory )
    {
        if ( !directory.exists() )
        {
            final String message = directory + " does not exist";
            throw new IllegalArgumentException( message );
        }

        if ( !directory.isDirectory() )
        {
            final String message = directory + " is not a directory";
            throw new IllegalArgumentException( message );
        }

        long size = 0;

        final File[] files = directory.listFiles();
        for ( final File file : files )
        {
            if ( file.isDirectory() )
            {
                size += sizeOfDirectory( file );
            }
            else
            {
                size += file.length();
            }
        }

        return size;
    }

    /**
     * Return the files contained in the directory, using inclusion and exclusion Ant patterns, including the directory
     * name in each of the files
     *
     * @param directory the directory to scan
     * @param includes the includes pattern, comma separated
     * @param excludes the excludes pattern, comma separated
     * @return a list of File objects
     * @throws IOException io issue
     * @see #getFileNames(File, String, String, boolean)
     */
    public static List<File> getFiles( File directory, String includes, String excludes )
        throws IOException
    {
        return getFiles( directory, includes, excludes, true );
    }

    /**
     * Return the files contained in the directory, using inclusion and exclusion Ant patterns
     *
     * @param directory the directory to scan
     * @param includes the includes pattern, comma separated
     * @param excludes the excludes pattern, comma separated
     * @param includeBasedir true to include the base dir in each file
     * @return a list of File objects
     * @throws IOException io issue
     * @see #getFileNames(File, String, String, boolean)
     */
    public static List<File> getFiles( File directory, String includes, String excludes, boolean includeBasedir )
        throws IOException
    {
        List<String> fileNames = getFileNames( directory, includes, excludes, includeBasedir );

        List<File> files = new ArrayList<File>();

        for ( String filename : fileNames )
        {
            files.add( new File( filename ) );
        }

        return files;
    }

    /**
     * Return a list of files as String depending options. This method use case sensitive file name.
     *
     * @param directory the directory to scan
     * @param includes the includes pattern, comma separated
     * @param excludes the excludes pattern, comma separated
     * @param includeBasedir true to include the base dir in each String of file
     * @return a list of files as String
     * @throws IOException io issue
     */
    public static List<String> getFileNames( File directory, String includes, String excludes, boolean includeBasedir )
        throws IOException
    {
        return getFileNames( directory, includes, excludes, includeBasedir, true );
    }

    /**
     * Return a list of files as String depending options.
     *
     * @param directory the directory to scan
     * @param includes the includes pattern, comma separated
     * @param excludes the excludes pattern, comma separated
     * @param includeBasedir true to include the base dir in each String of file
     * @param isCaseSensitive true if case sensitive
     * @return a list of files as String
     * @throws IOException io issue
     */
    public static List<String> getFileNames( File directory, String includes, String excludes, boolean includeBasedir,
                                             boolean isCaseSensitive )
        throws IOException
    {
        return getFileAndDirectoryNames( directory, includes, excludes, includeBasedir, isCaseSensitive, true, false );
    }

    /**
     * Return a list of directories as String depending options. This method use case sensitive file name.
     *
     * @param directory the directory to scan
     * @param includes the includes pattern, comma separated
     * @param excludes the excludes pattern, comma separated
     * @param includeBasedir true to include the base dir in each String of file
     * @return a list of directories as String
     * @throws IOException io issue
     */
    public static List<String> getDirectoryNames( File directory, String includes, String excludes,
                                                  boolean includeBasedir )
        throws IOException
    {
        return getDirectoryNames( directory, includes, excludes, includeBasedir, true );
    }

    /**
     * Return a list of directories as String depending options.
     *
     * @param directory the directory to scan
     * @param includes the includes pattern, comma separated
     * @param excludes the excludes pattern, comma separated
     * @param includeBasedir true to include the base dir in each String of file
     * @param isCaseSensitive true if case sensitive
     * @return a list of directories as String
     * @throws IOException io issue
     */
    public static List<String> getDirectoryNames( File directory, String includes, String excludes,
                                                  boolean includeBasedir, boolean isCaseSensitive )
        throws IOException
    {
        return getFileAndDirectoryNames( directory, includes, excludes, includeBasedir, isCaseSensitive, false, true );
    }

    /**
     * Return a list of files as String depending options.
     *
     * @param directory the directory to scan
     * @param includes the includes pattern, comma separated
     * @param excludes the excludes pattern, comma separated
     * @param includeBasedir true to include the base dir in each String of file
     * @param isCaseSensitive true if case sensitive
     * @param getFiles true if get files
     * @param getDirectories true if get directories
     * @return a list of files as String
     * @throws IOException io issue
     */
    public static List<String> getFileAndDirectoryNames( File directory, String includes, String excludes,
                                                         boolean includeBasedir, boolean isCaseSensitive,
                                                         boolean getFiles, boolean getDirectories )
        throws IOException
    {
        DirectoryScanner scanner = new DirectoryScanner();

        scanner.setBasedir( directory );

        if ( includes != null )
        {
            scanner.setIncludes( StringUtils.split( includes, "," ) );
        }

        if ( excludes != null )
        {
            scanner.setExcludes( StringUtils.split( excludes, "," ) );
        }

        scanner.setCaseSensitive( isCaseSensitive );

        scanner.scan();

        List<String> list = new ArrayList<String>();

        if ( getFiles )
        {
            String[] files = scanner.getIncludedFiles();

            for ( String file : files )
            {
                if ( includeBasedir )
                {
                    list.add( directory + FileUtils.FS + file );
                }
                else
                {
                    list.add( file );
                }
            }
        }

        if ( getDirectories )
        {
            String[] directories = scanner.getIncludedDirectories();

            for ( String directory1 : directories )
            {
                if ( includeBasedir )
                {
                    list.add( directory + FileUtils.FS + directory1 );
                }
                else
                {
                    list.add( directory1 );
                }
            }
        }

        return list;
    }

    /**
     * Copy a directory to an other one.
     *
     * @param sourceDirectory the source dir
     * @param destinationDirectory the target dir
     * @throws IOException if any
     */
    public static void copyDirectory( File sourceDirectory, File destinationDirectory )
        throws IOException
    {
        copyDirectory( sourceDirectory, destinationDirectory, "**", null );
    }

    /**
     * Copy a directory to an other one.
     *
     * @param sourceDirectory the source dir
     * @param destinationDirectory the target dir
     * @param includes include pattern
     * @param excludes exclude pattern
     * @throws IOException if any
     * @see #getFiles(File, String, String)
     */
    public static void copyDirectory( File sourceDirectory, File destinationDirectory, String includes,
                                      String excludes )
        throws IOException
    {
        if ( !sourceDirectory.exists() )
        {
            return;
        }

        List<File> files = getFiles( sourceDirectory, includes, excludes );

        for ( File file : files )
        {
            copyFileToDirectory( file, destinationDirectory );
        }
    }

    /**
     * <p>Copies a entire directory layout : no files will be copied only directories</p>
     * 
     * Note:
     * <ul>
     * <li>It will include empty directories.
     * <li>The <code>sourceDirectory</code> must exists.
     * </ul>
     *
     * @param sourceDirectory the source dir
     * @param destinationDirectory the target dir
     * @param includes include pattern
     * @param excludes exclude pattern
     * @throws IOException if any
     * @since 1.5.7
     */
    public static void copyDirectoryLayout( File sourceDirectory, File destinationDirectory, String[] includes,
                                            String[] excludes )
        throws IOException
    {
        if ( sourceDirectory == null )
        {
            throw new IOException( "source directory can't be null." );
        }

        if ( destinationDirectory == null )
        {
            throw new IOException( "destination directory can't be null." );
        }

        if ( sourceDirectory.equals( destinationDirectory ) )
        {
            throw new IOException( "source and destination are the same directory." );
        }

        if ( !sourceDirectory.exists() )
        {
            throw new IOException( "Source directory doesn't exists (" + sourceDirectory.getAbsolutePath() + ")." );
        }

        DirectoryScanner scanner = new DirectoryScanner();

        scanner.setBasedir( sourceDirectory );

        if ( includes != null && includes.length >= 1 )
        {
            scanner.setIncludes( includes );
        }
        else
        {
            scanner.setIncludes( new String[] { "**" } );
        }

        if ( excludes != null && excludes.length >= 1 )
        {
            scanner.setExcludes( excludes );
        }

        scanner.addDefaultExcludes();
        scanner.scan();
        List<String> includedDirectories = Arrays.asList( scanner.getIncludedDirectories() );

        for ( String name : includedDirectories )
        {
            File source = new File( sourceDirectory, name );

            if ( source.equals( sourceDirectory ) )
            {
                continue;
            }

            File destination = new File( destinationDirectory, name );
            destination.mkdirs();
        }
    }

    /**
     * <p>Copies a entire directory structure.</p>
     * 
     * Note:
     * <ul>
     * <li>It will include empty directories.
     * <li>The <code>sourceDirectory</code> must exists.
     * </ul>
     *
     * @param sourceDirectory the source dir
     * @param destinationDirectory the target dir
     * @throws IOException if any
     */
    public static void copyDirectoryStructure( File sourceDirectory, File destinationDirectory )
        throws IOException
    {
        copyDirectoryStructure( sourceDirectory, destinationDirectory, destinationDirectory, false );
    }

    /**
     * <p>Copies an entire directory structure but only source files with timestamp later than the destinations'.</p>
     * 
     * Note:
     * <ul>
     * <li>It will include empty directories.
     * <li>The <code>sourceDirectory</code> must exists.
     * </ul>
     *
     * @param sourceDirectory the source dir
     * @param destinationDirectory the target dir
     * @throws IOException if any
     */
    public static void copyDirectoryStructureIfModified( File sourceDirectory, File destinationDirectory )
        throws IOException
    {
        copyDirectoryStructure( sourceDirectory, destinationDirectory, destinationDirectory, true );
    }

    private static void copyDirectoryStructure( File sourceDirectory, File destinationDirectory,
                                                File rootDestinationDirectory, boolean onlyModifiedFiles )
        throws IOException
    {
        if ( sourceDirectory == null )
        {
            throw new IOException( "source directory can't be null." );
        }

        if ( destinationDirectory == null )
        {
            throw new IOException( "destination directory can't be null." );
        }

        if ( sourceDirectory.equals( destinationDirectory ) )
        {
            throw new IOException( "source and destination are the same directory." );
        }

        if ( !sourceDirectory.exists() )
        {
            throw new IOException( "Source directory doesn't exists (" + sourceDirectory.getAbsolutePath() + ")." );
        }

        File[] files = sourceDirectory.listFiles();

        String sourcePath = sourceDirectory.getAbsolutePath();

        for ( File file : files )
        {
            if ( file.equals( rootDestinationDirectory ) )
            {
                // We don't copy the destination directory in itself
                continue;
            }

            String dest = file.getAbsolutePath();

            dest = dest.substring( sourcePath.length() + 1 );

            File destination = new File( destinationDirectory, dest );

            if ( file.isFile() )
            {
                destination = destination.getParentFile();

                if ( onlyModifiedFiles )
                {
                    copyFileToDirectoryIfModified( file, destination );
                }
                else
                {
                    copyFileToDirectory( file, destination );
                }
            }
            else if ( file.isDirectory() )
            {
                if ( !destination.exists() && !destination.mkdirs() )
                {
                    throw new IOException( "Could not create destination directory '" + destination.getAbsolutePath()
                        + "'." );
                }

                copyDirectoryStructure( file, destination, rootDestinationDirectory, onlyModifiedFiles );
            }
            else
            {
                throw new IOException( "Unknown file type: " + file.getAbsolutePath() );
            }
        }
    }

    /**
     * <p>Renames a file, even if that involves crossing file system boundaries.</p>
     * 
     * <p>This will remove <code>to</code> (if it exists), ensure that <code>to</code>'s parent directory exists and move
     * <code>from</code>, which involves deleting <code>from</code> as well.</p>
     *
     * @param from the file to move
     * @param to the new file name
     * @throws IOException if anything bad happens during this process. Note that <code>to</code> may have been deleted
     *             already when this happens.
     */
    public static void rename( File from, File to )
        throws IOException
    {
        if ( to.exists() && !to.delete() )
        {
            throw new IOException( "Failed to delete " + to + " while trying to rename " + from );
        }

        File parent = to.getParentFile();
        if ( parent != null && !parent.exists() && !parent.mkdirs() )
        {
            throw new IOException( "Failed to create directory " + parent + " while trying to rename " + from );
        }

        if ( !from.renameTo( to ) )
        {
            copyFile( from, to );
            if ( !from.delete() )
            {
                throw new IOException( "Failed to delete " + from + " while trying to rename it." );
            }
        }
    }

    /**
     * <p>Create a temporary file in a given directory.</p>
     * 
     * <p>The file denoted by the returned abstract pathname did not exist before this method was invoked, any subsequent
     * invocation of this method will yield a different file name.</p>
     * 
     * <p>The filename is prefixNNNNNsuffix where NNNN is a random number</p>
     * 
     * <p>This method is different to {@link File#createTempFile(String, String, File)} of JDK 1.2 as it doesn't create the
     * file itself. It uses the location pointed to by java.io.tmpdir when the parentDir attribute is null.</p>
     * 
     * <p>To delete automatically the file created by this method, use the {@link File#deleteOnExit()} method.</p>
     *
     * @param prefix prefix before the random number
     * @param suffix file extension; include the '.'
     * @param parentDir Directory to create the temporary file in <code>-java.io.tmpdir</code> used if not specificed
     * @return a File reference to the new temporary file.
     */
    public static File createTempFile( String prefix, String suffix, File parentDir )
    {
        File result = null;
        String parent = System.getProperty( "java.io.tmpdir" );
        if ( parentDir != null )
        {
            parent = parentDir.getPath();
        }
        DecimalFormat fmt = new DecimalFormat( "#####" );
        SecureRandom secureRandom = new SecureRandom();
        long secureInitializer = secureRandom.nextLong();
        Random rand = new Random( secureInitializer + Runtime.getRuntime().freeMemory() );
        synchronized ( rand )
        {
            do
            {
                result = new File( parent, prefix + fmt.format( Math.abs( rand.nextInt() ) ) + suffix );
            }
            while ( result.exists() );
        }

        return result;
    }

    /**
     * <b>If wrappers is null or empty, the file will be copy only if {@code to.lastModified() < from.lastModified()}</b>
     *
     * @param from the file to copy
     * @param to the destination file
     * @param encoding the file output encoding (only if wrappers is not empty)
     * @param wrappers array of {@link FilterWrapper}
     * @throws IOException if an IO error occurs during copying or filtering
     */
    public static void copyFile( File from, File to, String encoding, FilterWrapper[] wrappers )
        throws IOException
    {
        copyFile( from, to, encoding, wrappers, false );
    }

    public static abstract class FilterWrapper
    {
        public abstract Reader getReader( Reader fileReader );
    }

    /**
     * <b>If wrappers is null or empty, the file will be copy only if {@code to.lastModified() < from.lastModified()}, if overwrite is true</b>
     *
     * @param from the file to copy
     * @param to the destination file
     * @param encoding the file output encoding (only if wrappers is not empty)
     * @param wrappers array of {@link FilterWrapper}
     * @param overwrite if true and wrappers is null or empty, the file will be copied even if {@code to.lastModified() < from.lastModified()}
     * @throws IOException if an IO error occurs during copying or filtering
     * @since 1.5.2
     */
    public static void copyFile( File from, File to, String encoding, FilterWrapper[] wrappers, boolean overwrite )
        throws IOException
    {
        if ( wrappers != null && wrappers.length > 0 )
        {
            // buffer so it isn't reading a byte at a time!
            Reader fileReader = null;
            Writer fileWriter = null;
            try
            {
                if ( encoding == null || encoding.length() < 1 )
                {
                    fileReader = Files.newBufferedReader( from.toPath() );
                    fileWriter = Files.newBufferedWriter( to.toPath() );
                }
                else
                {
                    OutputStream outstream = Files.newOutputStream( to.toPath() );

                    fileReader = Files.newBufferedReader( from.toPath(), Charset.forName( encoding ) );

                    fileWriter = new OutputStreamWriter( outstream, encoding );
                }

                Reader reader = fileReader;
                for ( FilterWrapper wrapper : wrappers )
                {
                    reader = wrapper.getReader( reader );
                }

                IOUtil.copy( reader, fileWriter );
                fileWriter.close();
                fileWriter = null;
                fileReader.close();
                fileReader = null;
            }
            finally
            {
                IOUtil.close( fileReader );
                IOUtil.close( fileWriter );
            }
        }
        else
        {
            if ( isSourceNewerThanDestination( from, to ) || overwrite )
            {
                copyFile( from, to );
            }
        }
    }

    private static boolean isSourceNewerThanDestination( File source, File destination ) {
        return ( destination.lastModified() == 0L && source.lastModified() == 0L ) || destination.lastModified() < source.lastModified();
    }

    /**
     * Note: the file content is read with platform encoding
     *
     * @param file the file
     * @return a List containing every every line not starting with # and not empty
     * @throws IOException if any
     */
    public static List<String> loadFile( File file )
        throws IOException
    {
        final List<String> lines = new ArrayList<String>();

        if ( file.exists() )
        {
            try ( BufferedReader reader = Files.newBufferedReader( file.toPath() ) )
            {
                for ( String line = reader.readLine(); line != null; line = reader.readLine() )
                {
                    line = line.trim();

                    if ( !line.startsWith( "#" ) && line.length() != 0 )
                    {
                        lines.add( line );
                    }
                }
            }
        }

        return lines;
    }

    /**
     * For Windows OS, check if the file name contains any of the following characters:
     * <code>":", "*", "?", "\"", "&lt;", "&gt;", "|"</code>
     *
     * @param f not null file
     * @return <code>false</code> if the file path contains any of forbidden Windows characters, <code>true</code> if
     *         the Os is not Windows or if the file path respect the Windows constraints.
     * @see #INVALID_CHARACTERS_FOR_WINDOWS_FILE_NAME
     * @since 1.5.2
     */
    public static boolean isValidWindowsFileName( File f )
    {
        if ( Os.isFamily( Os.FAMILY_WINDOWS ) )
        {
            if ( StringUtils.indexOfAny( f.getName(), INVALID_CHARACTERS_FOR_WINDOWS_FILE_NAME ) != -1 )
            {
                return false;
            }

            File parentFile = f.getParentFile();
            if ( parentFile != null )
            {
                return isValidWindowsFileName( parentFile );
            }
        }

        return true;
    }
}
