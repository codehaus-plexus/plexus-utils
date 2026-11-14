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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Finds the file that <code>cmd.exe</code> would run for a command name, so that {@link Commandline} can start native
 * images without a shell.
 * <p>
 * The search follows <code>cmd.exe</code>, not <code>CreateProcess</code>: internal commands win over files, the
 * working directory is searched before <code>PATH</code>, and in each directory the name is tried as given (when it
 * has an extension) and then with each <code>PATHEXT</code> extension in order.
 * </p>
 * <p>
 * The only answer that changes how a process is started is {@link Kind#NATIVE}. Whenever the resolver cannot tell
 * what <code>cmd.exe</code> would do, it answers {@link Kind#UNRESOLVED} and the caller keeps the shell, so that a
 * mistake here costs the optimisation and never the behaviour.
 * </p>
 * <p>
 * All path handling is done on strings with Windows rules, so that the logic can be tested on any platform.
 * </p>
 */
final class WindowsExecutableResolver {
    /**
     * What a command name stands for.
     */
    enum Kind {
        /** An existing <code>.exe</code> or <code>.com</code> file, which <code>CreateProcess</code> runs as is. */
        NATIVE,
        /** An existing <code>.bat</code> or <code>.cmd</code> file, which only <code>cmd.exe</code> can interpret. */
        SCRIPT,
        /** An existing file of another type, run by <code>cmd.exe</code> through its file association. */
        ASSOCIATED,
        /** A command implemented by <code>cmd.exe</code> itself, such as <code>echo</code> or <code>dir</code>. */
        BUILTIN,
        /** Nothing was found, or the name is in a form this class does not interpret. */
        UNRESOLVED
    }

    static final class Resolution {
        private static final Resolution UNRESOLVED = new Resolution(Kind.UNRESOLVED, null);

        private static final Resolution BUILTIN = new Resolution(Kind.BUILTIN, null);

        private final Kind kind;

        private final String path;

        private Resolution(Kind kind, String path) {
            this.kind = kind;
            this.path = path;
        }

        Kind getKind() {
            return kind;
        }

        /**
         * @return the absolute path of the file found, or <code>null</code> for {@link Kind#BUILTIN} and
         *         {@link Kind#UNRESOLVED}
         */
        String getPath() {
            return path;
        }

        @Override
        public String toString() {
            return path == null ? kind.toString() : kind + " " + path;
        }
    }

    private static final String DEFAULT_PATHEXT = ".COM;.EXE;.BAT;.CMD";

    /**
     * Characters that make <code>cmd.exe</code> do more with a command name than look up a file: quoting, wildcards,
     * redirection, command chaining, escaping, variable expansion and its own token separators.
     */
    private static final String UNSUPPORTED_CHARS = "\"*?<>|&^%!,;=\t";

    private static final Set<String> BUILTINS = new HashSet<>(Arrays.asList(
            "assoc",
            "break",
            "call",
            "cd",
            "chcp",
            "chdir",
            "cls",
            "color",
            "copy",
            "date",
            "del",
            "dir",
            "dpath",
            "echo",
            "endlocal",
            "erase",
            "exit",
            "for",
            "ftype",
            "goto",
            "if",
            "keys",
            "md",
            "mkdir",
            "mklink",
            "move",
            "path",
            "pause",
            "popd",
            "prompt",
            "pushd",
            "rd",
            "rem",
            "ren",
            "rename",
            "rmdir",
            "set",
            "setlocal",
            "shift",
            "start",
            "time",
            "title",
            "type",
            "ver",
            "verify",
            "vol"));

    private WindowsExecutableResolver() {}

    /**
     * @param executable the command name as the caller gave it, with or without directory and extension
     * @param environment the environment of the process to start; names are matched case-insensitively
     * @param workingDirectory the absolute working directory of the process to start
     * @param isFile tells whether an absolute path denotes an existing regular file
     * @return what <code>cmd.exe</code> would run, never <code>null</code>
     */
    static Resolution resolve(
            String executable, Map<String, String> environment, String workingDirectory, Predicate<String> isFile) {
        if (executable == null || workingDirectory == null) {
            return Resolution.UNRESOLVED;
        }
        String name = executable.replace('/', '\\');
        if (name.isEmpty() || !name.equals(name.trim()) || containsAny(name, UNSUPPORTED_CHARS)) {
            return Resolution.UNRESOLVED;
        }
        String cwd = workingDirectory.replace('/', '\\');
        if (!isAbsolute(cwd)) {
            return Resolution.UNRESOLVED;
        }

        List<String> extensions = pathExtensions(getIgnoreCase(environment, "PATHEXT"));

        int lastSeparator = name.lastIndexOf('\\');
        if (lastSeparator >= 0 || name.indexOf(':') >= 0) {
            // a directory was given: cmd.exe looks there and nowhere else
            if (lastSeparator < 0 || lastSeparator == name.length() - 1) {
                return Resolution.UNRESOLVED;
            }
            String directory = absoluteDirectory(name.substring(0, lastSeparator + 1), cwd);
            if (directory == null) {
                return Resolution.UNRESOLVED;
            }
            Resolution found = findIn(directory, name.substring(lastSeparator + 1), extensions, isFile);
            return found != null ? found : Resolution.UNRESOLVED;
        }

        if (BUILTINS.contains(name.toLowerCase(Locale.ROOT))) {
            return Resolution.BUILTIN;
        }

        List<String> directories = new ArrayList<>();
        if (getIgnoreCase(environment, "NoDefaultCurrentDirectoryInExePath") == null) {
            directories.add(cwd);
        }
        String path = getIgnoreCase(environment, "PATH");
        if (path != null) {
            for (String entry : path.split(";")) {
                entry = entry.trim();
                if (entry.isEmpty()) {
                    continue;
                }
                String directory = containsAny(entry, UNSUPPORTED_CHARS) ? null : absoluteDirectory(entry, cwd);
                if (directory == null) {
                    // cmd.exe may find the command here, and we cannot tell: do not look any further
                    return findFirst(directories, name, extensions, isFile, Resolution.UNRESOLVED);
                }
                directories.add(directory);
            }
        }
        return findFirst(directories, name, extensions, isFile, Resolution.UNRESOLVED);
    }

    private static Resolution findFirst(
            List<String> directories,
            String name,
            List<String> extensions,
            Predicate<String> isFile,
            Resolution otherwise) {
        for (String directory : directories) {
            Resolution found = findIn(directory, name, extensions, isFile);
            if (found != null) {
                return found;
            }
        }
        return otherwise;
    }

    private static Resolution findIn(String directory, String name, List<String> extensions, Predicate<String> isFile) {
        String base = directory.endsWith("\\") ? directory + name : directory + '\\' + name;
        if (name.endsWith(".") || name.endsWith(" ")) {
            // Win32 drops trailing dots and spaces, so the file found would not be the one named
            return Resolution.UNRESOLVED;
        }
        if (name.indexOf('.') >= 0 && isFile.test(base)) {
            return new Resolution(kindOf(base), base);
        }
        for (String extension : extensions) {
            String candidate = base + extension;
            if (isFile.test(candidate)) {
                return new Resolution(kindOf(candidate), candidate);
            }
        }
        return null;
    }

    private static Kind kindOf(String path) {
        String lower = path.toLowerCase(Locale.ROOT);
        if (lower.endsWith(".exe") || lower.endsWith(".com")) {
            return Kind.NATIVE;
        }
        if (lower.endsWith(".bat") || lower.endsWith(".cmd")) {
            return Kind.SCRIPT;
        }
        return Kind.ASSOCIATED;
    }

    private static List<String> pathExtensions(String pathext) {
        List<String> result = new ArrayList<>();
        for (String extension : (pathext == null || pathext.trim().isEmpty() ? DEFAULT_PATHEXT : pathext).split(";")) {
            extension = extension.trim();
            if (extension.length() > 1 && extension.charAt(0) == '.' && !containsAny(extension, "\\/:")) {
                result.add(extension);
            }
        }
        return result;
    }

    /**
     * @return the directory as an absolute path, or <code>null</code> if it is in a form that depends on more than the
     *         working directory (<code>C:foo</code>, <code>\foo</code>)
     */
    private static String absoluteDirectory(String directory, String cwd) {
        String dir = directory.replace('/', '\\');
        if (isAbsolute(dir)) {
            return dir;
        }
        if (dir.startsWith("\\") || dir.indexOf(':') >= 0) {
            return null;
        }
        return cwd.endsWith("\\") ? cwd + dir : cwd + '\\' + dir;
    }

    /**
     * @return <code>true</code> for <code>C:\...</code> and <code>\\server\share...</code>
     */
    private static boolean isAbsolute(String path) {
        if (path.startsWith("\\\\")) {
            return path.length() > 2 && path.indexOf(':') < 0;
        }
        return path.length() >= 3
                && isLetter(path.charAt(0))
                && path.charAt(1) == ':'
                && path.charAt(2) == '\\'
                && path.indexOf(':', 2) < 0;
    }

    private static boolean isLetter(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    private static boolean containsAny(String value, String chars) {
        for (int i = 0; i < value.length(); i++) {
            if (chars.indexOf(value.charAt(i)) >= 0) {
                return true;
            }
        }
        return false;
    }

    private static String getIgnoreCase(Map<String, String> environment, String name) {
        String value = environment.get(name);
        if (value != null) {
            return value;
        }
        for (Map.Entry<String, String> entry : environment.entrySet()) {
            if (name.equalsIgnoreCase(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }
}
