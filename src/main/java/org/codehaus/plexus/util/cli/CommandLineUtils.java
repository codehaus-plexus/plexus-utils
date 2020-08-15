package org.codehaus.plexus.util.cli;

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
import java.io.InputStream;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import org.codehaus.plexus.util.Os;
import org.codehaus.plexus.util.StringUtils;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l </a>
 *
 */
public abstract class CommandLineUtils
{

    /**
     * A {@code StreamConsumer} providing consumed lines as a {@code String}.
     *
     * @see #getOutput()
     */
    public static class StringStreamConsumer
        implements StreamConsumer
    {

        private StringBuffer string = new StringBuffer();

        private String ls = System.getProperty( "line.separator" );

        @Override
        public void consumeLine( String line )
        {
            string.append( line ).append( ls );
        }

        public String getOutput()
        {
            return string.toString();
        }

    }

    /**
     * Number of milliseconds per second.
     */
    private static final long MILLIS_PER_SECOND = 1000L;

    /**
     * Number of nanoseconds per second.
     */
    private static final long NANOS_PER_SECOND = 1000000000L;

    public static int executeCommandLine( Commandline cl, StreamConsumer systemOut, StreamConsumer systemErr )
        throws CommandLineException
    {
        return executeCommandLine( cl, null, systemOut, systemErr, 0 );
    }

    public static int executeCommandLine( Commandline cl, StreamConsumer systemOut, StreamConsumer systemErr,
                                          int timeoutInSeconds )
        throws CommandLineException
    {
        return executeCommandLine( cl, null, systemOut, systemErr, timeoutInSeconds );
    }

    public static int executeCommandLine( Commandline cl, InputStream systemIn, StreamConsumer systemOut,
                                          StreamConsumer systemErr )
        throws CommandLineException
    {
        return executeCommandLine( cl, systemIn, systemOut, systemErr, 0 );
    }

    /**
     * @param cl The command line to execute
     * @param systemIn The input to read from, must be thread safe
     * @param systemOut A consumer that receives output, must be thread safe
     * @param systemErr A consumer that receives system error stream output, must be thread safe
     * @param timeoutInSeconds Positive integer to specify timeout, zero and negative integers for no timeout.
     * @return A return value, see {@link Process#exitValue()}
     * @throws CommandLineException or CommandLineTimeOutException if time out occurs
     */
    public static int executeCommandLine( Commandline cl, InputStream systemIn, StreamConsumer systemOut,
                                          StreamConsumer systemErr, int timeoutInSeconds )
        throws CommandLineException
    {
        final CommandLineCallable future =
            executeCommandLineAsCallable( cl, systemIn, systemOut, systemErr, timeoutInSeconds );
        return future.call();
    }

    /**
     * Immediately forks a process, returns a callable that will block until process is complete.
     * 
     * @param cl The command line to execute
     * @param systemIn The input to read from, must be thread safe
     * @param systemOut A consumer that receives output, must be thread safe
     * @param systemErr A consumer that receives system error stream output, must be thread safe
     * @param timeoutInSeconds Positive integer to specify timeout, zero and negative integers for no timeout.
     * @return A CommandLineCallable that provides the process return value, see {@link Process#exitValue()}. "call"
     *         must be called on this to be sure the forked process has terminated, no guarantees is made about any
     *         internal state before after the completion of the call statements
     * @throws CommandLineException or CommandLineTimeOutException if time out occurs
     */
    public static CommandLineCallable executeCommandLineAsCallable( final Commandline cl, final InputStream systemIn,
                                                                    final StreamConsumer systemOut,
                                                                    final StreamConsumer systemErr,
                                                                    final int timeoutInSeconds )
        throws CommandLineException
    {
        if ( cl == null )
        {
            throw new IllegalArgumentException( "cl cannot be null." );
        }

        final Process p = cl.execute();

        final Thread processHook = new Thread()
        {

            {
                this.setName( "CommandLineUtils process shutdown hook" );
                this.setContextClassLoader( null );
            }

            @Override
            public void run()
            {
                p.destroy();
            }

        };

        ShutdownHookUtils.addShutDownHook( processHook );

        return new CommandLineCallable()
        {

            @Override
            public Integer call()
                throws CommandLineException
            {
                StreamFeeder inputFeeder = null;
                StreamPumper outputPumper = null;
                StreamPumper errorPumper = null;
                boolean success = false;
                try
                {
                    if ( systemIn != null )
                    {
                        inputFeeder = new StreamFeeder( systemIn, p.getOutputStream() );
                        inputFeeder.start();
                    }

                    outputPumper = new StreamPumper( p.getInputStream(), systemOut );
                    outputPumper.start();

                    errorPumper = new StreamPumper( p.getErrorStream(), systemErr );
                    errorPumper.start();

                    int returnValue;
                    if ( timeoutInSeconds <= 0 )
                    {
                        returnValue = p.waitFor();
                    }
                    else
                    {
                        final long now = System.nanoTime();
                        final long timeout = now + NANOS_PER_SECOND * timeoutInSeconds;

                        while ( isAlive( p ) && ( System.nanoTime() < timeout ) )
                        {
                            // The timeout is specified in seconds. Therefore we must not sleep longer than one second
                            // but we should sleep as long as possible to reduce the number of iterations performed.
                            Thread.sleep( MILLIS_PER_SECOND - 1L );
                        }

                        if ( isAlive( p ) )
                        {
                            throw new InterruptedException( String.format( "Process timed out after %d seconds.",
                                                                           timeoutInSeconds ) );
                        }

                        returnValue = p.exitValue();
                    }

                    // TODO Find out if waitUntilDone needs to be called using a try-finally construct. The method may
                    // throw an
                    // InterruptedException so that calls to waitUntilDone may be skipped.
                    // try
                    // {
                    // if ( inputFeeder != null )
                    // {
                    // inputFeeder.waitUntilDone();
                    // }
                    // }
                    // finally
                    // {
                    // try
                    // {
                    // outputPumper.waitUntilDone();
                    // }
                    // finally
                    // {
                    // errorPumper.waitUntilDone();
                    // }
                    // }
                    if ( inputFeeder != null )
                    {
                        inputFeeder.waitUntilDone();
                    }

                    outputPumper.waitUntilDone();
                    errorPumper.waitUntilDone();

                    if ( inputFeeder != null )
                    {
                        inputFeeder.close();
                        handleException( inputFeeder, "stdin" );
                    }

                    outputPumper.close();
                    handleException( outputPumper, "stdout" );

                    errorPumper.close();
                    handleException( errorPumper, "stderr" );

                    success = true;
                    return returnValue;
                }
                catch ( InterruptedException ex )
                {
                    throw new CommandLineTimeOutException( "Error while executing external command, process killed.",
                                                           ex );

                }
                finally
                {
                    if ( inputFeeder != null )
                    {
                        inputFeeder.disable();
                    }
                    if ( outputPumper != null )
                    {
                        outputPumper.disable();
                    }
                    if ( errorPumper != null )
                    {
                        errorPumper.disable();
                    }

                    try
                    {
                        ShutdownHookUtils.removeShutdownHook( processHook );
                        processHook.run();
                    }
                    finally
                    {
                        try
                        {
                            if ( inputFeeder != null )
                            {
                                inputFeeder.close();

                                if ( success )
                                {
                                    success = false;
                                    handleException( inputFeeder, "stdin" );
                                    success = true; // Only reached when no exception has been thrown.
                                }
                            }
                        }
                        finally
                        {
                            try
                            {
                                if ( outputPumper != null )
                                {
                                    outputPumper.close();

                                    if ( success )
                                    {
                                        success = false;
                                        handleException( outputPumper, "stdout" );
                                        success = true; // Only reached when no exception has been thrown.
                                    }
                                }
                            }
                            finally
                            {
                                if ( errorPumper != null )
                                {
                                    errorPumper.close();

                                    if ( success )
                                    {
                                        handleException( errorPumper, "stderr" );
                                    }
                                }
                            }
                        }
                    }
                }
            }

        };
    }

    private static void handleException( final StreamPumper streamPumper, final String streamName )
        throws CommandLineException
    {
        if ( streamPumper.getException() != null )
        {
            throw new CommandLineException( String.format( "Failure processing %s.", streamName ),
                                            streamPumper.getException() );

        }
    }

    private static void handleException( final StreamFeeder streamFeeder, final String streamName )
        throws CommandLineException
    {
        if ( streamFeeder.getException() != null )
        {
            throw new CommandLineException( String.format( "Failure processing %s.", streamName ),
                                            streamFeeder.getException() );

        }
    }

    /**
     * Gets the shell environment variables for this process. Note that the returned mapping from variable names to
     * values will always be case-sensitive regardless of the platform, i.e. <code>getSystemEnvVars().get("path")</code>
     * and <code>getSystemEnvVars().get("PATH")</code> will in general return different values. However, on platforms
     * with case-insensitive environment variables like Windows, all variable names will be normalized to upper case.
     *
     * @return The shell environment variables, can be empty but never <code>null</code>.
     * @throws IOException If the environment variables could not be queried from the shell.
     * @see System#getenv() System.getenv() API, new in JDK 5.0, to get the same result <b>since 2.0.2 System#getenv()
     *      will be used if available in the current running jvm.</b>
     */
    public static Properties getSystemEnvVars()
        throws IOException
    {
        return getSystemEnvVars( !Os.isFamily( Os.FAMILY_WINDOWS ) );
    }

    /**
     * Return the shell environment variables. If <code>caseSensitive == true</code>, then envar keys will all be
     * upper-case.
     *
     * @param caseSensitive Whether environment variable keys should be treated case-sensitively.
     * @return Properties object of (possibly modified) envar keys mapped to their values.
     * @throws IOException .
     * @see System#getenv() System.getenv() API, new in JDK 5.0, to get the same result <b>since 2.0.2 System#getenv()
     *      will be used if available in the current running jvm.</b>
     */
    public static Properties getSystemEnvVars( boolean caseSensitive )
        throws IOException
    {
        Properties envVars = new Properties();
        Map<String, String> envs = System.getenv();
        for ( String key : envs.keySet() )
        {
            String value = envs.get( key );
            if ( !caseSensitive )
            {
                key = key.toUpperCase( Locale.ENGLISH );
            }
            envVars.put( key, value );
        }
        return envVars;
    }

    public static boolean isAlive( Process p )
    {
        if ( p == null )
        {
            return false;
        }

        try
        {
            p.exitValue();
            return false;
        }
        catch ( IllegalThreadStateException e )
        {
            return true;
        }
    }

    public static String[] translateCommandline( String toProcess )
        throws Exception
    {
        if ( ( toProcess == null ) || ( toProcess.length() == 0 ) )
        {
            return new String[0];
        }

        // parse with a simple finite state machine

        final int normal = 0;
        final int inQuote = 1;
        final int inDoubleQuote = 2;
        int state = normal;
        StringTokenizer tok = new StringTokenizer( toProcess, "\"\' ", true );
        Vector<String> v = new Vector<String>();
        StringBuilder current = new StringBuilder();

        while ( tok.hasMoreTokens() )
        {
            String nextTok = tok.nextToken();
            switch ( state )
            {
                case inQuote:
                    if ( "\'".equals( nextTok ) )
                    {
                        state = normal;
                    }
                    else
                    {
                        current.append( nextTok );
                    }
                    break;
                case inDoubleQuote:
                    if ( "\"".equals( nextTok ) )
                    {
                        state = normal;
                    }
                    else
                    {
                        current.append( nextTok );
                    }
                    break;
                default:
                    if ( "\'".equals( nextTok ) )
                    {
                        state = inQuote;
                    }
                    else if ( "\"".equals( nextTok ) )
                    {
                        state = inDoubleQuote;
                    }
                    else if ( " ".equals( nextTok ) )
                    {
                        if ( current.length() != 0 )
                        {
                            v.addElement( current.toString() );
                            current.setLength( 0 );
                        }
                    }
                    else
                    {
                        current.append( nextTok );
                    }
                    break;
            }
        }

        if ( current.length() != 0 )
        {
            v.addElement( current.toString() );
        }

        if ( ( state == inQuote ) || ( state == inDoubleQuote ) )
        {
            throw new CommandLineException( "unbalanced quotes in " + toProcess );
        }

        String[] args = new String[v.size()];
        v.copyInto( args );
        return args;
    }

    /**
     * <p>
     * Put quotes around the given String if necessary.
     * </p>
     * <p>
     * If the argument doesn't include spaces or quotes, return it as is. If it contains double quotes, use single
     * quotes - else surround the argument by double quotes.
     * </p>
     * @param argument the argument
     * @return the transformed command line
     * @throws CommandLineException if the argument contains both, single and double quotes.
     * @deprecated Use {@link StringUtils#quoteAndEscape(String, char, char[], char[], char, boolean)},
     *             {@link StringUtils#quoteAndEscape(String, char, char[], char, boolean)}, or
     *             {@link StringUtils#quoteAndEscape(String, char)} instead.
     */
    @Deprecated
    @SuppressWarnings( { "JavaDoc", "deprecation" } )
    public static String quote( String argument )
        throws CommandLineException
    {
        return quote( argument, false, false, true );
    }

    /**
     * <p>
     * Put quotes around the given String if necessary.
     * </p>
     * <p>
     * If the argument doesn't include spaces or quotes, return it as is. If it contains double quotes, use single
     * quotes - else surround the argument by double quotes.
     * </p>
     * @param argument see name
     * @param wrapExistingQuotes see name
     * @return the transformed command line
     * @throws CommandLineException if the argument contains both, single and double quotes.
     * @deprecated Use {@link StringUtils#quoteAndEscape(String, char, char[], char[], char, boolean)},
     *             {@link StringUtils#quoteAndEscape(String, char, char[], char, boolean)}, or
     *             {@link StringUtils#quoteAndEscape(String, char)} instead.
     */
    @Deprecated
    @SuppressWarnings( { "JavaDoc", "UnusedDeclaration", "deprecation" } )
    public static String quote( String argument, boolean wrapExistingQuotes )
        throws CommandLineException
    {
        return quote( argument, false, false, wrapExistingQuotes );
    }

    /**
     * @param argument the argument
     * @param escapeSingleQuotes see name
     * @param escapeDoubleQuotes see name
     * @param wrapExistingQuotes see name
     * @return the transformed command line
     * @throws CommandLineException some trouble
     * @deprecated Use {@link StringUtils#quoteAndEscape(String, char, char[], char[], char, boolean)},
     *             {@link StringUtils#quoteAndEscape(String, char, char[], char, boolean)}, or
     *             {@link StringUtils#quoteAndEscape(String, char)} instead.
     */
    @Deprecated
    @SuppressWarnings( { "JavaDoc" } )
    public static String quote( String argument, boolean escapeSingleQuotes, boolean escapeDoubleQuotes,
                                boolean wrapExistingQuotes )
        throws CommandLineException
    {
        if ( argument.contains( "\"" ) )
        {
            if ( argument.contains( "\'" ) )
            {
                throw new CommandLineException( "Can't handle single and double quotes in same argument" );
            }
            else
            {
                if ( escapeSingleQuotes )
                {
                    return "\\\'" + argument + "\\\'";
                }
                else if ( wrapExistingQuotes )
                {
                    return '\'' + argument + '\'';
                }
            }
        }
        else if ( argument.contains( "\'" ) )
        {
            if ( escapeDoubleQuotes )
            {
                return "\\\"" + argument + "\\\"";
            }
            else if ( wrapExistingQuotes )
            {
                return '\"' + argument + '\"';
            }
        }
        else if ( argument.contains( " " ) )
        {
            if ( escapeDoubleQuotes )
            {
                return "\\\"" + argument + "\\\"";
            }
            else
            {
                return '\"' + argument + '\"';
            }
        }

        return argument;
    }

    public static String toString( String[] line )
    {
        // empty path return empty string
        if ( ( line == null ) || ( line.length == 0 ) )
        {
            return "";
        }

        // path containing one or more elements
        final StringBuilder result = new StringBuilder();
        for ( int i = 0; i < line.length; i++ )
        {
            if ( i > 0 )
            {
                result.append( ' ' );
            }
            try
            {
                result.append( StringUtils.quoteAndEscape( line[i], '\"' ) );
            }
            catch ( Exception e )
            {
                System.err.println( "Error quoting argument: " + e.getMessage() );
            }
        }
        return result.toString();
    }

}
