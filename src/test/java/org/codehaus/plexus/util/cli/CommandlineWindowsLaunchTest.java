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

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import org.codehaus.plexus.util.cli.shell.BourneShell;
import org.codehaus.plexus.util.cli.shell.CmdShell;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * How {@link Commandline} starts a process on Windows: directly for native images, through <code>cmd.exe</code> for
 * everything else. The tests of the decision run on every platform against a faked file system and environment; the
 * tests that start processes run on Windows only.
 */
class CommandlineWindowsLaunchTest {
    private static final String CWD = "C:\\work";

    private static final String[] SHELL = {"cmd.exe", "/X", "/D", "/C"};

    private final Map<String, String> env = new HashMap<>();

    private final Set<String> files = new HashSet<>();

    private final Predicate<String> isFile = new Predicate<String>() {
        @Override
        public boolean test(String path) {
            return files.contains(path.toLowerCase(Locale.ROOT));
        }
    };

    CommandlineWindowsLaunchTest() {
        env.put("PATH", "C:\\tools");
        env.put("PATHEXT", ".com;.exe;.bat;.cmd");
        for (String path : Arrays.asList("C:\\tools\\git.exe", "C:\\tools\\mvn.cmd", "C:\\tools\\echo.exe")) {
            files.add(path.toLowerCase(Locale.ROOT));
        }
    }

    /**
     * @return a command line as <code>new Commandline()</code> builds it on Windows
     */
    private static Commandline windowsCommandline(String executable, String... arguments) {
        Commandline cl = new Commandline();
        cl.setDefaultShell(new CmdShell());
        cl.setExecutable(executable);
        cl.addArguments(arguments);
        return cl;
    }

    private String[] launch(Commandline cl) {
        return cl.getWindowsCommandline(env, CWD, isFile);
    }

    private static void assertShell(String expectedCommand, String[] commandline) {
        assertEquals(5, commandline.length, Arrays.toString(commandline));
        assertArrayEquals(SHELL, Arrays.copyOf(commandline, 4));
        if (expectedCommand != null) {
            assertEquals(expectedCommand, commandline[4]);
        }
    }

    @Test
    void nativeImageIsStartedDirectlyWithArgumentsUntouched() {
        String[] commandline = launch(windowsCommandline("git", "log", "a&b", "x y", "%PATH%", "^", "a|b", ">out"));
        assertArrayEquals(
                new String[] {"C:\\tools\\git.exe", "log", "a&b", "x y", "%PATH%", "^", "a|b", ">out"}, commandline);
    }

    @Test
    void scriptKeepsTheShell() {
        assertShell("\"mvn -version\"", launch(windowsCommandline("mvn", "-version")));
    }

    @Test
    void builtinKeepsTheShellEvenWithAnExeOfThatName() {
        assertShell("\"echo Hello\"", launch(windowsCommandline("echo", "Hello")));
    }

    @Test
    void unresolvedNameKeepsTheShell() {
        assertShell("\"svn --version\"", launch(windowsCommandline("svn", "--version")));
    }

    @Test
    void noExecutableKeepsTheShell() {
        Commandline cl = new Commandline();
        cl.setDefaultShell(new CmdShell());
        cl.addArguments(new String[] {"git", "status"});
        assertShell("\"git status\"", launch(cl));
    }

    @Test
    void shellSetByCallerIsKept() {
        Commandline cl = windowsCommandline("git", "status");
        cl.setShell(new CmdShell());
        assertShell("\"git status\"", launch(cl));

        cl = new Commandline(new CmdShell());
        cl.setExecutable("git");
        assertShell("\"git\"", launch(cl));

        cl = new Commandline("git status", new BourneShell());
        assertEquals("/bin/sh", launch(cl)[0]);
    }

    @Test
    void reconfiguredDefaultShellIsKept() {
        Commandline cl = windowsCommandline("git", "status");
        cl.getShell().setQuotedArgumentsEnabled(false);
        assertShell(null, launch(cl));

        cl = windowsCommandline("git", "status");
        cl.getShell().setUnconditionalQuoting(true);
        assertShell(null, launch(cl));

        cl = windowsCommandline("git", "status");
        cl.getShell().setQuotedExecutableEnabled(false);
        assertShell(null, launch(cl));

        cl = windowsCommandline("git", "status");
        cl.getShell().setShellArgs(new String[] {"/X", "/C"});
        assertArrayEquals(new String[] {"cmd.exe", "/X", "/C", "\"git status\""}, launch(cl));

        cl = windowsCommandline("git", "status");
        cl.getShell().setShellCommand("C:\\Windows\\System32\\cmd.exe");
        assertEquals("C:\\Windows\\System32\\cmd.exe", launch(cl)[0]);

        // reading from the shell changes nothing
        cl = windowsCommandline("git", "status");
        assertEquals("git", cl.getShell().getOriginalExecutable());
        assertEquals("C:\\tools\\git.exe", launch(cl)[0]);
    }

    @Test
    void cloneKeepsTheCallersChoice() {
        Commandline cl = windowsCommandline("git", "status");
        cl.setShell(new CmdShell());
        assertNotEquals("C:\\tools\\git.exe", launch((Commandline) cl.clone())[0]);
    }

    @Test
    void systemPropertyRestoresTheShellForEverything() {
        Commandline cl = windowsCommandline("git", "status");
        System.setProperty(Commandline.WINDOWS_DIRECT_EXECUTION_PROPERTY, "false");
        try {
            assertShell("\"git status\"", launch(cl));
        } finally {
            System.clearProperty(Commandline.WINDOWS_DIRECT_EXECUTION_PROPERTY);
        }
        assertEquals("C:\\tools\\git.exe", launch(cl)[0]);
    }

    @Test
    void failingFileCheckKeepsTheShell() {
        Commandline cl = windowsCommandline("git", "status");
        String[] commandline = cl.getWindowsCommandline(env, CWD, new Predicate<String>() {
            @Override
            public boolean test(String path) {
                throw new SecurityException(path);
            }
        });
        assertShell("\"git status\"", commandline);
    }

    @Test
    void shellExecutableSetBehindTheCommandlineIsKept() {
        Commandline cl = windowsCommandline("git", "status");
        cl.getShell().setExecutable("mvn");
        assertShell("\"mvn status\"", launch(cl));

        // the separators the shell rewrites are not a difference
        files.add("c:\\jdk/bin\\java.exe".replace('/', '\\'));
        cl = windowsCommandline("C:/jdk/bin\\java");
        assertEquals("c:\\jdk\\bin\\java.exe", launch(cl)[0].toLowerCase(Locale.ROOT));
    }

    @Test
    void lookupEnvironmentIsTheChildsAndLeavesTheCommandlineAlone() {
        String systemName = null;
        for (String name : System.getenv().keySet()) {
            if ("PATH".equalsIgnoreCase(name)) {
                systemName = name;
            }
        }
        assertTrue(systemName != null, "this test needs a PATH");

        Commandline cl = windowsCommandline("git", "status");
        cl.addEnvironment("ONLY", "this");
        // a different spelling than the system's, as Windows callers may use
        String ownName = systemName.equals("Path") ? "PATH" : "Path";
        cl.addEnvironment(ownName, "C:\\own");

        Map<String, String> environment = cl.windowsChildEnvironment();

        assertEquals(2, cl.envVars.size(), "the system environment must not leak into the command line");
        assertEquals("this", environment.get("ONLY"));
        assertEquals("C:\\own", environment.get(ownName));
        int paths = 0;
        for (String name : environment.keySet()) {
            if ("PATH".equalsIgnoreCase(name)) {
                paths++;
            }
        }
        assertEquals(1, paths, environment.keySet().toString());
        assertTrue(environment.size() > 2, "the system environment is part of the child's");
    }

    @Test
    void toStringStaysTheShellForm() {
        assertEquals(
                "cmd.exe /X /D /C \"git status\"",
                windowsCommandline("git", "status").toString());
    }

    // ----------------------------------------------------------------------
    // Windows only: real processes
    // ----------------------------------------------------------------------

    private static File javaExe() {
        return new File(System.getProperty("java.home"), OS.WINDOWS.isCurrentOs() ? "bin\\java.exe" : "bin/java");
    }

    private static String testClasses() throws Exception {
        return new File(ArgumentEcho.class
                        .getProtectionDomain()
                        .getCodeSource()
                        .getLocation()
                        .toURI())
                .getAbsolutePath();
    }

    private static String run(Commandline cl, int expectedExitCode) throws Exception {
        CommandLineUtils.StringStreamConsumer out = new CommandLineUtils.StringStreamConsumer();
        CommandLineUtils.StringStreamConsumer err = new CommandLineUtils.StringStreamConsumer();
        int exitCode = CommandLineUtils.executeCommandLine(cl, out, err);
        assertEquals(expectedExitCode, exitCode, err.getOutput());
        return out.getOutput();
    }

    private static String pathWith(Path dir) {
        return dir + File.pathSeparator + System.getenv("PATH");
    }

    /**
     * Runs everywhere, so that a failure on Windows is about Windows and not about the way the test starts Java.
     */
    @Test
    void exeReceivesShellMetacharactersLiterally() throws Exception {
        String[] arguments = {"a&b", "x y", "%PATH%", "a|b", ">out", "^caret", "100%", "a&echo injected"};

        Commandline cl = new Commandline();
        cl.setExecutable(javaExe().getAbsolutePath());
        cl.addArguments(new String[] {"-cp", testClasses(), ArgumentEcho.class.getName()});
        cl.addArguments(arguments);

        assertEquals(javaExe().getAbsolutePath(), cl.getCommandline()[0]);
        String expected = String.join(System.lineSeparator(), arguments) + System.lineSeparator();
        assertEquals(expected, run(cl, 0));
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    void exeIsFoundThroughPathAndPathext(@TempDir Path dir) throws Exception {
        Files.copy(javaExe().toPath(), dir.resolve("plexus-utils-probe.exe"));

        Commandline cl = new Commandline();
        cl.addEnvironment("PATH", pathWith(dir));
        cl.setExecutable("plexus-utils-probe");

        String[] commandline = cl.getCommandline();
        assertEquals(1, commandline.length);
        assertTrue(new File(commandline[0]).isAbsolute(), commandline[0]);
        assertTrue(commandline[0].toLowerCase(Locale.ROOT).endsWith("plexus-utils-probe.exe"), commandline[0]);
    }

    /**
     * plexus-utils#17
     */
    @Test
    @EnabledOnOs(OS.WINDOWS)
    void extensionlessScriptOnPathRunsThroughTheShell(@TempDir Path dir) throws Exception {
        Files.write(dir.resolve("plexus-utils-shim.cmd"), "@echo shim %1\r\n".getBytes(StandardCharsets.US_ASCII));

        Commandline cl = new Commandline();
        cl.addEnvironment("PATH", pathWith(dir));
        cl.setExecutable("plexus-utils-shim");
        cl.createArg().setValue("called");

        assertEquals("cmd.exe", cl.getCommandline()[0]);
        assertEquals("shim called", run(cl, 0).trim());
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    void scriptInWorkingDirectoryRunsThroughTheShell(@TempDir Path dir) throws Exception {
        Files.write(dir.resolve("plexus-utils-local.bat"), "@echo local\r\n".getBytes(StandardCharsets.US_ASCII));

        Commandline cl = new Commandline();
        cl.setWorkingDirectory(dir.toFile());
        cl.setExecutable("plexus-utils-local");

        assertEquals("cmd.exe", cl.getCommandline()[0]);
        assertEquals("local", run(cl, 0).trim());
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    void builtinRunsThroughTheShell() throws Exception {
        Commandline cl = new Commandline();
        cl.setExecutable("echo");
        cl.createArg().setValue("Hello");

        assertEquals("cmd.exe", cl.getCommandline()[0]);
        assertEquals("Hello", run(cl, 0).trim());
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    void unknownCommandIsReportedByTheShellNotByAnException() throws Exception {
        Commandline cl = new Commandline();
        cl.setExecutable("plexus-utils-no-such-command");

        assertEquals("cmd.exe", cl.getCommandline()[0]);
        CommandLineUtils.StringStreamConsumer err = new CommandLineUtils.StringStreamConsumer();
        int exitCode = CommandLineUtils.executeCommandLine(cl, new CommandLineUtils.StringStreamConsumer(), err);
        assertNotEquals(0, exitCode);
        assertFalse(err.getOutput().isEmpty());
    }

    /**
     * The direct start of a file that is no image fails; <code>cmd.exe</code> then reports it as it always did.
     */
    @Test
    @EnabledOnOs(OS.WINDOWS)
    void failedDirectStartFallsBackToTheShell(@TempDir Path dir) throws Exception {
        Path bogus = dir.resolve("plexus-utils-bogus.exe");
        Files.write(bogus, "not an image".getBytes(StandardCharsets.US_ASCII));

        Commandline cl = new Commandline();
        cl.setExecutable(bogus.toString());

        assertEquals(bogus.toString(), cl.getCommandline()[0]);
        int exitCode = CommandLineUtils.executeCommandLine(
                cl, new CommandLineUtils.StringStreamConsumer(), new CommandLineUtils.StringStreamConsumer());
        assertNotEquals(0, exitCode);
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    void callerCanStillAskForTheShell() throws Exception {
        Commandline cl = new Commandline();
        cl.setShell(new CmdShell());
        cl.setExecutable(javaExe().getAbsolutePath());
        cl.addArguments(new String[] {"-cp", testClasses(), ArgumentEcho.class.getName(), "one&echo two"});

        assertEquals("cmd.exe", cl.getCommandline()[0]);
        // quoted because of the space, so cmd.exe leaves it alone
        assertEquals("one&echo two", run(cl, 0).trim());

        cl = new Commandline();
        cl.setShell(new CmdShell());
        cl.setExecutable(javaExe().getAbsolutePath());
        cl.addArguments(new String[] {"-cp", testClasses(), ArgumentEcho.class.getName(), "one&echo", "two"});
        // unquoted, cmd.exe treats & as a command separator: the behaviour direct execution removes
        assertEquals("one" + System.lineSeparator() + "two", run(cl, 0).trim());
    }

    /**
     * Prints each argument on a line of its own.
     */
    public static final class ArgumentEcho {
        public static void main(String[] args) {
            for (String arg : args) {
                System.out.println(arg);
            }
        }
    }
}
