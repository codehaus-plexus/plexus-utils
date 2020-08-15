package org.codehaus.plexus.util;

/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2002 The Apache Software Foundation.  All rights
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
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.codehaus.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Ant", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact codehaus@codehaus.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
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
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Unzip a file.
 *
 * @author costin@dnt.ro
 * @author <a href="mailto:stefan.bodewig@epost.de">Stefan Bodewig</a>
 * @author <a href="mailto:umagesh@codehaus.org">Magesh Umasankar</a>
 * @since Ant 1.1 @ant.task category="packaging" name="unzip" name="unjar" name="unwar"
 *
 */
public class Expand
{

    private File dest;// req

    private File source;// req

    private boolean overwrite = true;

    /**
     * Do the work.
     *
     * @exception Exception Thrown in unrecoverable error.
     */
    public void execute()
        throws Exception
    {
        expandFile( source, dest );
    }

    protected void expandFile( final File srcF, final File dir )
        throws Exception
    {
        // code from WarExpand
        try ( ZipInputStream zis = new ZipInputStream( new FileInputStream( srcF ) ) )
        {
            for ( ZipEntry ze = zis.getNextEntry(); ze != null; ze = zis.getNextEntry() )
            {
                extractFile( srcF, dir, zis, ze.getName(), new Date( ze.getTime() ), ze.isDirectory() );
            }
        }
        catch ( IOException ioe )
        {
            throw new Exception( "Error while expanding " + srcF.getPath(), ioe );
        }
    }

    protected void extractFile( File srcF, File dir, InputStream compressedInputStream, String entryName,
                                Date entryDate, boolean isDirectory )
        throws Exception
    {
        File f = FileUtils.resolveFile( dir, entryName );

        if ( !f.getAbsolutePath().startsWith( dir.getAbsolutePath() ) )
        {
            throw new IOException( "Entry '" + entryName + "' outside the target directory." );
        }

        try
        {
            if ( !overwrite && f.exists() && f.lastModified() >= entryDate.getTime() )
            {
                return;
            }

            // create intermediary directories - sometimes zip don't add them
            File dirF = f.getParentFile();
            dirF.mkdirs();

            if ( isDirectory )
            {
                f.mkdirs();
            }
            else
            {
                byte[] buffer = new byte[65536];
                
                try ( FileOutputStream fos = new FileOutputStream( f ) )
                {
                    for ( int length = compressedInputStream.read( buffer ); 
                          length >= 0; 
                          fos.write( buffer, 0, length ), length = compressedInputStream.read( buffer ) )
                        ;
                }
            }

            f.setLastModified( entryDate.getTime() );
        }
        catch ( FileNotFoundException ex )
        {
            throw new Exception( "Can't extract file " + srcF.getPath(), ex );
        }

    }

    /**
     * Set the destination directory. File will be unzipped into the destination directory.
     *
     * @param d Path to the directory.
     */
    public void setDest( File d )
    {
        this.dest = d;
    }

    /**
     * Set the path to zip-file.
     *
     * @param s Path to zip-file.
     */
    public void setSrc( File s )
    {
        this.source = s;
    }

    /**
     * @param b Should we overwrite files in dest, even if they are newer than the corresponding entries in the archive?
     */
    public void setOverwrite( boolean b )
    {
        overwrite = b;
    }

}
