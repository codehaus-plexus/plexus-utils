package org.codehaus.plexus.util.cli.shell;


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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.junit.Test;

public class BourneShellTest
{

    protected Shell newShell()
    {
        return new BourneShell();
    }

    @Test
    public void testQuoteWorkingDirectoryAndExecutable()
    {
        Shell sh = newShell();

        sh.setWorkingDirectory( "/usr/local/bin" );
        sh.setExecutable( "chmod" );

        String executable = StringUtils.join( sh.getShellCommandLine( new String[] {} ).iterator(), " " );

        assertEquals( "/bin/sh -c cd '/usr/local/bin' && 'chmod'", executable );
    }

    @Test
    public void testQuoteWorkingDirectoryAndExecutable_WDPathWithSingleQuotes()
    {
        Shell sh = newShell();

        sh.setWorkingDirectory( "/usr/local/'something else'" );
        sh.setExecutable( "chmod" );

        String executable = StringUtils.join( sh.getShellCommandLine( new String[] {} ).iterator(), " " );

        assertEquals( "/bin/sh -c cd '/usr/local/'\"'\"'something else'\"'\"'' && 'chmod'", executable );
    }

    @Test
    public void testQuoteWorkingDirectoryAndExecutable_WDPathWithSingleQuotes_BackslashFileSep()
    {
        Shell sh = newShell();

        sh.setWorkingDirectory( "\\usr\\local\\'something else'" );
        sh.setExecutable( "chmod" );

        String executable = StringUtils.join( sh.getShellCommandLine( new String[] {} ).iterator(), " " );

        assertEquals( "/bin/sh -c cd '\\usr\\local\\\'\"'\"'something else'\"'\"'' && 'chmod'", executable );
    }

    @Test
    public void testPreserveSingleQuotesOnArgument()
    {
        Shell sh = newShell();

        sh.setWorkingDirectory( "/usr/bin" );
        sh.setExecutable( "chmod" );

        String[] args = { "\'some arg with spaces\'" };

        List<String> shellCommandLine = sh.getShellCommandLine( args );

        String cli = StringUtils.join( shellCommandLine.iterator(), " " );
        System.out.println( cli );
        assertTrue( cli.endsWith( "''\"'\"'some arg with spaces'\"'\"''" ) );
    }

    @Test
    public void testAddSingleQuotesOnArgumentWithSpaces()
    {
        Shell sh = newShell();

        sh.setWorkingDirectory( "/usr/bin" );
        sh.setExecutable( "chmod" );

        String[] args = { "some arg with spaces" };

        List<String> shellCommandLine = sh.getShellCommandLine( args );

        String cli = StringUtils.join( shellCommandLine.iterator(), " " );
        System.out.println( cli );
        assertTrue( cli.endsWith( "\'" + args[0] + "\'" ) );
    }

    @Test
    public void testEscapeSingleQuotesOnArgument()
    {
        Shell sh = newShell();

        sh.setWorkingDirectory( "/usr/bin" );
        sh.setExecutable( "chmod" );

        String[] args = { "arg'withquote" };

        List<String> shellCommandLine = sh.getShellCommandLine( args );

        String cli = StringUtils.join( shellCommandLine.iterator(), " " );
        System.out.println( cli );
        assertEquals( "cd '/usr/bin' && 'chmod' 'arg'\"'\"'withquote'",
                      shellCommandLine.get( shellCommandLine.size() - 1 ) );
    }

    @Test
    public void testArgumentsWithsemicolon()
    {

        System.out.println( "---- semi colon tests ----" );

        Shell sh = newShell();

        sh.setWorkingDirectory( "/usr/bin" );
        sh.setExecutable( "chmod" );

        String[] args = { ";some&argwithunix$chars" };

        List<String> shellCommandLine = sh.getShellCommandLine( args );

        String cli = StringUtils.join( shellCommandLine.iterator(), " " );
        System.out.println( cli );
        assertTrue( cli.endsWith( "\'" + args[0] + "\'" ) );

        Commandline commandline = new Commandline( newShell() );
        commandline.setExecutable( "chmod" );
        commandline.getShell().setQuotedArgumentsEnabled( true );
        commandline.createArg().setValue( "--password" );
        commandline.createArg().setValue( ";password" );

        String[] lines = commandline.getShellCommandline();
        System.out.println( Arrays.asList( lines ) );

        assertEquals( "/bin/sh", lines[0] );
        assertEquals( "-c", lines[1] );
        assertEquals( "'chmod' '--password' ';password'", lines[2] );

        commandline = new Commandline( newShell() );
        commandline.setExecutable( "chmod" );
        commandline.getShell().setQuotedArgumentsEnabled( true );
        commandline.createArg().setValue( "--password" );
        commandline.createArg().setValue( ";password" );
        lines = commandline.getShellCommandline();
        System.out.println( Arrays.asList( lines ) );

        assertEquals( "/bin/sh", lines[0] );
        assertEquals( "-c", lines[1] );
        assertEquals( "'chmod' '--password' ';password'", lines[2] );

        commandline = new Commandline( new CmdShell() );
        commandline.getShell().setQuotedArgumentsEnabled( true );
        commandline.createArg().setValue( "--password" );
        commandline.createArg().setValue( ";password" );
        lines = commandline.getShellCommandline();
        System.out.println( Arrays.asList( lines ) );

        assertEquals( "cmd.exe", lines[0] );
        assertEquals( "/X", lines[1] );
        assertEquals( "/C", lines[2] );
        assertEquals( "\"--password ;password\"", lines[3] );
    }

    @Test
    public void testBourneShellQuotingCharacters()
        throws Exception
    {
        // { ' ', '$', ';', '&', '|', '<', '>', '*', '?', '(', ')' };
        // test with values http://steve-parker.org/sh/bourne.shtml Appendix B - Meta-characters and Reserved Words
        Commandline commandline = new Commandline( newShell() );
        commandline.setExecutable( "chmod" );
        commandline.getShell().setQuotedArgumentsEnabled( true );
        commandline.createArg().setValue( " " );
        commandline.createArg().setValue( "|" );
        commandline.createArg().setValue( "&&" );
        commandline.createArg().setValue( "||" );
        commandline.createArg().setValue( ";" );
        commandline.createArg().setValue( ";;" );
        commandline.createArg().setValue( "&" );
        commandline.createArg().setValue( "()" );
        commandline.createArg().setValue( "<" );
        commandline.createArg().setValue( "<<" );
        commandline.createArg().setValue( ">" );
        commandline.createArg().setValue( ">>" );
        commandline.createArg().setValue( "*" );
        commandline.createArg().setValue( "?" );
        commandline.createArg().setValue( "[" );
        commandline.createArg().setValue( "]" );
        commandline.createArg().setValue( "{" );
        commandline.createArg().setValue( "}" );
        commandline.createArg().setValue( "`" );

        String[] lines = commandline.getShellCommandline();
        System.out.println( Arrays.asList( lines ) );

        assertEquals( "/bin/sh", lines[0] );
        assertEquals( "-c", lines[1] );
        assertEquals( "'chmod' ' ' '|' '&&' '||' ';' ';;' '&' '()' '<' '<<' '>' '>>' '*' '?' '[' ']' '{' '}' '`'",
                      lines[2] );

    }

}
