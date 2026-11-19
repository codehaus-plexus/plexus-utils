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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import org.codehaus.plexus.util.cli.WindowsExecutableResolver.Kind;
import org.codehaus.plexus.util.cli.WindowsExecutableResolver.Resolution;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Runs on every platform: the file system and the environment are faked.
 */
class WindowsExecutableResolverTest {
    private static final String CWD = "C:\\work";

    private final Map<String, String> env = new HashMap<>();

    private final Set<String> files = new HashSet<>();

    private final Predicate<String> isFile = new Predicate<String>() {
        @Override
        public boolean test(String path) {
            // NTFS is case-insensitive
            return files.contains(path.toLowerCase(Locale.ROOT));
        }
    };

    WindowsExecutableResolverTest() {
        env.put("Path", "C:\\tools;C:\\Program Files (x86)\\Git\\cmd;");
        env.put("PATHEXT", ".COM;.EXE;.BAT;.CMD;.VBS");
    }

    private void file(String... paths) {
        for (String path : paths) {
            files.add(path.toLowerCase(Locale.ROOT));
        }
    }

    private Resolution resolve(String executable) {
        return WindowsExecutableResolver.resolve(executable, env, CWD, isFile);
    }

    private void assertResolved(Kind kind, String path, String executable) {
        Resolution resolution = resolve(executable);
        assertEquals(kind, resolution.getKind(), executable);
        // the extension comes from PATHEXT as written there, usually in upper case
        String expected = path == null ? null : path.toLowerCase(Locale.ROOT);
        String actual =
                resolution.getPath() == null ? null : resolution.getPath().toLowerCase(Locale.ROOT);
        assertEquals(expected, actual, executable);
    }

    @Test
    void extensionlessNameOnPathResolvesToExe() {
        file("C:\\Program Files (x86)\\Git\\cmd\\git.exe");
        assertResolved(Kind.NATIVE, "C:\\Program Files (x86)\\Git\\cmd\\git.exe", "git");
        assertResolved(Kind.NATIVE, "C:\\Program Files (x86)\\Git\\cmd\\git.exe", "GIT.EXE");
        assertEquals(
                "C:\\Program Files (x86)\\Git\\cmd\\git.EXE", resolve("git").getPath());
        assertEquals(
                "C:\\Program Files (x86)\\Git\\cmd\\GIT.EXE", resolve("GIT.EXE").getPath());
    }

    @Test
    void extensionlessNameOnPathResolvesToScript() {
        // plexus-utils#17: mvn, npm and friends are .cmd shims
        file("C:\\tools\\mvn.cmd", "C:\\tools\\npm.bat");
        assertResolved(Kind.SCRIPT, "C:\\tools\\mvn.cmd", "mvn");
        assertResolved(Kind.SCRIPT, "C:\\tools\\npm.bat", "npm");
    }

    @Test
    void pathextOrderDecidesWithinOneDirectory() {
        file("C:\\tools\\tool.exe", "C:\\tools\\tool.cmd", "C:\\tools\\tool.com");
        assertResolved(Kind.NATIVE, "C:\\tools\\tool.com", "tool");

        env.put("PATHEXT", ".CMD;.EXE");
        assertResolved(Kind.SCRIPT, "C:\\tools\\tool.cmd", "tool");
    }

    @Test
    void earlierDirectoryWinsOverEarlierExtension() {
        file("C:\\tools\\tool.cmd", "C:\\Program Files (x86)\\Git\\cmd\\tool.exe");
        assertResolved(Kind.SCRIPT, "C:\\tools\\tool.cmd", "tool");
    }

    @Test
    void workingDirectoryIsSearchedBeforePath() {
        file("C:\\work\\tool.bat", "C:\\tools\\tool.exe");
        assertResolved(Kind.SCRIPT, "C:\\work\\tool.bat", "tool");

        env.put("NoDefaultCurrentDirectoryInExePath", "1");
        assertResolved(Kind.NATIVE, "C:\\tools\\tool.exe", "tool");
    }

    @Test
    void nameWithExtensionIsTriedAsGivenThenWithPathext() {
        file("C:\\tools\\tool.exe.cmd");
        assertResolved(Kind.SCRIPT, "C:\\tools\\tool.exe.cmd", "tool.exe");

        file("C:\\tools\\tool.exe");
        assertResolved(Kind.NATIVE, "C:\\tools\\tool.exe", "tool.exe");
    }

    @Test
    void extensionlessFileIsNotACommand() {
        file("C:\\tools\\tool");
        assertResolved(Kind.UNRESOLVED, null, "tool");
    }

    @Test
    void otherFileTypesAreLeftToTheShell() {
        file("C:\\tools\\report.vbs", "C:\\tools\\notes.txt");
        assertResolved(Kind.ASSOCIATED, "C:\\tools\\report.vbs", "report");
        assertResolved(Kind.ASSOCIATED, "C:\\tools\\notes.txt", "notes.txt");
    }

    @Test
    void builtinsWinOverFiles() {
        file("C:\\tools\\echo.exe", "C:\\tools\\dir.exe");
        for (String builtin : Arrays.asList("echo", "ECHO", "dir", "set", "cd", "copy", "type", "start")) {
            assertResolved(Kind.BUILTIN, null, builtin);
        }
        // naming the file is how cmd.exe is told to run it
        assertResolved(Kind.NATIVE, "C:\\tools\\echo.exe", "echo.exe");
        assertResolved(Kind.NATIVE, "C:\\tools\\echo.exe", "C:\\tools\\echo");
    }

    @Test
    void pathGivenIsTheOnlyPlaceSearched() {
        file("C:\\tools\\java.exe", "C:\\jdk\\bin\\java.exe", "C:\\work\\bin\\run.cmd", "\\\\server\\share\\x.exe");
        assertResolved(Kind.NATIVE, "C:\\jdk\\bin\\java.exe", "C:\\jdk\\bin\\java.exe");
        assertResolved(Kind.NATIVE, "C:\\jdk\\bin\\java.exe", "C:/jdk/bin/java");
        assertResolved(Kind.SCRIPT, "C:\\work\\bin\\run.cmd", "bin\\run");
        // the fake file system does not normalise paths
        file("C:\\work\\.\\bin\\run.cmd");
        assertResolved(Kind.SCRIPT, "C:\\work\\.\\bin\\run.cmd", "./bin/run.cmd");
        assertResolved(Kind.NATIVE, "\\\\server\\share\\x.exe", "\\\\server\\share\\x");
        assertResolved(Kind.UNRESOLVED, null, "C:\\other\\java.exe");
        assertResolved(Kind.UNRESOLVED, null, "bin\\java");
    }

    @Test
    void formsThatNeedMoreThanTheWorkingDirectoryAreUnresolved() {
        file("C:\\work\\tool.exe", "C:\\tool.exe", "C:\\tools\\tool.exe");
        for (String executable : Arrays.asList("C:tool.exe", "\\tool.exe", "C:\\tools\\", "", " tool", "tool.")) {
            assertResolved(Kind.UNRESOLVED, null, executable);
        }
        assertEquals(
                Kind.UNRESOLVED,
                WindowsExecutableResolver.resolve(null, env, CWD, isFile).getKind());
        assertEquals(
                Kind.UNRESOLVED,
                WindowsExecutableResolver.resolve("tool", env, null, isFile).getKind());
        assertEquals(
                Kind.UNRESOLVED,
                WindowsExecutableResolver.resolve("tool", env, "work", isFile).getKind());
    }

    @Test
    void namesThatCmdInterpretsAreUnresolved() {
        file("C:\\tools\\tool.exe", "C:\\tools\\a&b.exe", "C:\\tools\\%tool%.exe");
        for (String executable : Arrays.asList(
                "\"C:\\tools\\tool.exe\"",
                "\"tool\"",
                "a&b",
                "%tool%",
                "%TOOLS%\\tool.exe",
                "tool.exe>out",
                "to*l",
                "tool|more",
                "^tool",
                "tool;x",
                "tool,x",
                "tool=x")) {
            assertResolved(Kind.UNRESOLVED, null, executable);
        }
    }

    @Test
    void pathEntryThatCannotBeInterpretedStopsTheSearch() {
        file("C:\\tools\\tool.exe");

        env.put("Path", "C:\\first;\"C:\\quoted dir\";C:\\tools");
        assertResolved(Kind.UNRESOLVED, null, "tool");

        env.put("Path", "\\rooted;C:\\tools");
        assertResolved(Kind.UNRESOLVED, null, "tool");

        // but a hit before that entry is what cmd.exe finds as well
        env.put("Path", "C:\\tools;D:relative");
        assertResolved(Kind.NATIVE, "C:\\tools\\tool.exe", "tool");
    }

    @Test
    void relativeAndEmptyPathEntries() {
        file("C:\\work\\bin\\tool.exe");
        env.put("Path", ";; bin ;");
        assertResolved(Kind.NATIVE, "C:\\work\\bin\\tool.exe", "tool");
    }

    @Test
    void environmentDefaults() {
        file("C:\\work\\tool.cmd", "C:\\work\\other.vbs");
        env.clear();
        assertResolved(Kind.SCRIPT, "C:\\work\\tool.cmd", "tool");
        // .VBS is not in the default PATHEXT
        assertResolved(Kind.UNRESOLVED, null, "other");
        assertNull(resolve("missing").getPath());
    }
}
