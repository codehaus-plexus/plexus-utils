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

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

/**
 * DirectoryWalker
 * 
 *
 */
public class DirectoryWalker
{
    /**
     * DirStackEntry is an Item on the {@link DirectoryWalker#dirStack}
     */
    class DirStackEntry
    {
        /**
         * Count of files in the directory.
         */
        public int count;

        /**
         * Current Directory.
         */
        public File dir;

        /**
         * Index (or offset) within the directory count.
         */
        public int index;

        /**
         * Offset for percentage calculations. Based on parent DirStackEntry.
         */
        public double percentageOffset;

        /**
         * Size of percentage space to work with.
         */
        public double percentageSize;

        /**
         * Create a DirStackEntry.
         *
         * @param d the directory to track
         * @param length the length of entries in the directory.
         */
        public DirStackEntry( File d, int length )
        {
            dir = d;
            count = length;
        }

        /**
         * Calculate the next percentage offset. Used by the next DirStackEntry.
         *
         * @return the value for the next percentage offset.
         */
        public double getNextPercentageOffset()
        {
            return percentageOffset + ( index * ( percentageSize / count ) );
        }

        /**
         * Calculate the next percentage size. Used by the next DirStackEntry.
         *
         * @return the value for the next percentage size.
         */
        public double getNextPercentageSize()
        {
            return ( percentageSize / count );
        }

        /**
         * The percentage of the DirStackEntry right now. Based on count, index, percentageOffset, and percentageSize.
         *
         * @return the percentage right now.
         */
        public int getPercentage()
        {
            double percentageWithinDir = (double) index / (double) count;
            return (int) Math.floor( percentageOffset + ( percentageWithinDir * percentageSize ) );
        }

        @Override
        public String toString()
        {
            return "DirStackEntry[" + "dir=" + dir.getAbsolutePath() + ",count=" + count + ",index=" + index
                + ",percentageOffset=" + percentageOffset + ",percentageSize=" + percentageSize + ",percentage()="
                + getPercentage() + ",getNextPercentageOffset()=" + getNextPercentageOffset()
                + ",getNextPercentageSize()=" + getNextPercentageSize() + "]";
        }
    }

    private File baseDir;

    private int baseDirOffset;

    private Stack<DirectoryWalker.DirStackEntry> dirStack;

    private List<String> excludes;

    private List<String> includes;

    private boolean isCaseSensitive = true;

    private List<DirectoryWalkListener> listeners;

    private boolean debugEnabled = false;

    public DirectoryWalker()
    {
        includes = new ArrayList<String>();
        excludes = new ArrayList<String>();
        listeners = new ArrayList<DirectoryWalkListener>();
    }

    public void addDirectoryWalkListener( DirectoryWalkListener listener )
    {
        listeners.add( listener );
    }

    public void addExclude( String exclude )
    {
        excludes.add( fixPattern( exclude ) );
    }

    public void addInclude( String include )
    {
        includes.add( fixPattern( include ) );
    }

    /**
     * Add's to the Exclude List the default list of SCM excludes.
     */
    public void addSCMExcludes()
    {
        String scmexcludes[] = AbstractScanner.DEFAULTEXCLUDES;
        for ( String scmexclude : scmexcludes )
        {
            addExclude( scmexclude );
        }
    }

    private void fireStep( File file )
    {
        DirStackEntry dsEntry = dirStack.peek();
        int percentage = dsEntry.getPercentage();
        for ( Object listener1 : listeners )
        {
            DirectoryWalkListener listener = (DirectoryWalkListener) listener1;
            listener.directoryWalkStep( percentage, file );
        }
    }

    private void fireWalkFinished()
    {
        for ( DirectoryWalkListener listener1 : listeners )
        {
            listener1.directoryWalkFinished();
        }
    }

    private void fireWalkStarting()
    {
        for ( DirectoryWalkListener listener1 : listeners )
        {
            listener1.directoryWalkStarting( baseDir );
        }
    }

    private void fireDebugMessage( String message )
    {
        for ( DirectoryWalkListener listener1 : listeners )
        {
            listener1.debug( message );
        }
    }

    private String fixPattern( String pattern )
    {
        String cleanPattern = pattern;

        if ( File.separatorChar != '/' )
        {
            cleanPattern = cleanPattern.replace( '/', File.separatorChar );
        }

        if ( File.separatorChar != '\\' )
        {
            cleanPattern = cleanPattern.replace( '\\', File.separatorChar );
        }

        return cleanPattern;
    }

    public void setDebugMode( boolean debugEnabled )
    {
        this.debugEnabled = debugEnabled;
    }

    /**
     * @return Returns the baseDir.
     */
    public File getBaseDir()
    {
        return baseDir;
    }

    /**
     * @return Returns the excludes.
     */
    public List<String> getExcludes()
    {
        return excludes;
    }

    /**
     * @return Returns the includes.
     */
    public List<String> getIncludes()
    {
        return includes;
    }

    private boolean isExcluded( String name )
    {
        return isMatch( excludes, name );
    }

    private boolean isIncluded( String name )
    {
        return isMatch( includes, name );
    }

    private boolean isMatch( List<String> patterns, String name )
    {
        for ( String pattern1 : patterns )
        {
            if ( SelectorUtils.matchPath( pattern1, name, isCaseSensitive ) )
            {
                return true;
            }
        }

        return false;
    }

    private String relativeToBaseDir( File file )
    {
        return file.getAbsolutePath().substring( baseDirOffset + 1 );
    }

    /**
     * Removes a DirectoryWalkListener.
     *
     * @param listener the listener to remove.
     */
    public void removeDirectoryWalkListener( DirectoryWalkListener listener )
    {
        listeners.remove( listener );
    }

    /**
     * Performs a Scan against the provided {@link #setBaseDir(File)}
     */
    public void scan()
    {
        if ( baseDir == null )
        {
            throw new IllegalStateException( "Scan Failure.  BaseDir not specified." );
        }

        if ( !baseDir.exists() )
        {
            throw new IllegalStateException( "Scan Failure.  BaseDir does not exist." );
        }

        if ( !baseDir.isDirectory() )
        {
            throw new IllegalStateException( "Scan Failure.  BaseDir is not a directory." );
        }

        if ( includes.isEmpty() )
        {
            // default to include all.
            addInclude( "**" );
        }

        if ( debugEnabled )
        {
            Iterator<String> it;
            StringBuilder dbg = new StringBuilder();
            dbg.append( "DirectoryWalker Scan" );
            dbg.append( "\n  Base Dir: " ).append( baseDir.getAbsolutePath() );
            dbg.append( "\n  Includes: " );
            it = includes.iterator();
            while ( it.hasNext() )
            {
                String include = it.next();
                dbg.append( "\n    - \"" ).append( include ).append( "\"" );
            }
            dbg.append( "\n  Excludes: " );
            it = excludes.iterator();
            while ( it.hasNext() )
            {
                String exclude = it.next();
                dbg.append( "\n    - \"" ).append( exclude ).append( "\"" );
            }
            fireDebugMessage( dbg.toString() );
        }

        fireWalkStarting();
        dirStack = new Stack<DirStackEntry>();
        scanDir( baseDir );
        fireWalkFinished();
    }

    private void scanDir( File dir )
    {
        File[] files = dir.listFiles();

        if ( files == null )
        {
            return;
        }

        DirectoryWalker.DirStackEntry curStackEntry = new DirectoryWalker.DirStackEntry( dir, files.length );
        if ( dirStack.isEmpty() )
        {
            curStackEntry.percentageOffset = 0;
            curStackEntry.percentageSize = 100;
        }
        else
        {
            DirectoryWalker.DirStackEntry previousStackEntry = dirStack.peek();
            curStackEntry.percentageOffset = previousStackEntry.getNextPercentageOffset();
            curStackEntry.percentageSize = previousStackEntry.getNextPercentageSize();
        }

        dirStack.push( curStackEntry );

        for ( int idx = 0; idx < files.length; idx++ )
        {
            curStackEntry.index = idx;
            String name = relativeToBaseDir( files[idx] );

            if ( isExcluded( name ) )
            {
                fireDebugMessage( name + " is excluded." );
                continue;
            }

            if ( files[idx].isDirectory() )
            {
                scanDir( files[idx] );
            }
            else
            {
                if ( isIncluded( name ) )
                {
                    fireStep( files[idx] );
                }
            }
        }

        dirStack.pop();
    }

    /**
     * @param baseDir The baseDir to set.
     */
    public void setBaseDir( File baseDir )
    {
        this.baseDir = baseDir;
        baseDirOffset = baseDir.getAbsolutePath().length();
    }

    /**
     * @param entries The excludes to set.
     */
    public void setExcludes( List<String> entries )
    {
        excludes.clear();
        if ( entries != null )
        {
            for ( String entry : entries )
            {
                excludes.add( fixPattern( entry ) );
            }
        }
    }

    /**
     * @param entries The includes to set.
     */
    public void setIncludes( List<String> entries )
    {
        includes.clear();
        if ( entries != null )
        {
            for ( String entry : entries )
            {
                includes.add( fixPattern( entry ) );
            }
        }
    }

}
