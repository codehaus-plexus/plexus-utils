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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is used to test FileUtils for correctness.
 *
 * @author Peter Donald
 * @author Matthew Hawthorne
 * @see FileUtils
 * @since 3.4.0
 */
public final class FileUtilsTest extends FileBasedTestCase {
    // Test data

    /**
     * Size of test directory.
     */
    private static final int TEST_DIRECTORY_SIZE = 0;

    private final File testFile1;

    private final File testFile2;

    private static int testFile1Size;

    private static int testFile2Size;

    public FileUtilsTest() {
        testFile1 = new File(getTestDirectory(), "file1-test.txt");
        testFile2 = new File(getTestDirectory(), "file1a-test.txt");

        testFile1Size = (int) testFile1.length();
        testFile2Size = (int) testFile2.length();
    }

    @BeforeEach
    void setUp() throws Exception {
        getTestDirectory().mkdirs();
        createFile(testFile1, testFile1Size);
        createFile(testFile2, testFile2Size);
        FileUtils.deleteDirectory(getTestDirectory());
        getTestDirectory().mkdirs();
        createFile(testFile1, testFile1Size);
        createFile(testFile2, testFile2Size);
    }

    @Test
    void byteCountToDisplaySize() {
        assertEquals("0 bytes", FileUtils.byteCountToDisplaySize(0));
        assertEquals("1 KB", FileUtils.byteCountToDisplaySize(1024));
        assertEquals("1 MB", FileUtils.byteCountToDisplaySize(1024 * 1024));
        assertEquals("1 GB", FileUtils.byteCountToDisplaySize(1024 * 1024 * 1024));
    }

    @Test
    void waitFor() {
        FileUtils.waitFor("", -1);
        FileUtils.waitFor("", 2);
    }

    @Test
    void toFile() throws Exception {
        URL url = getClass().getResource("/test.txt");
        url = new URL(url.toString() + "/name%20%23%2520%3F%7B%7D%5B%5D%3C%3E.txt");
        File file = FileUtils.toFile(url);
        assertEquals("name #%20?{}[]<>.txt", file.getName());
    }

    @Test
    void toFileBadProtocol() throws Exception {
        URL url = new URL("http://maven.apache.org/");
        assertNull(FileUtils.toFile(url));
    }

    @Test
    void toFileNull() {
        File file = FileUtils.toFile(null);
        assertNull(file);
    }

    // Hacked to sanity by Trygve
    @Test
    void toURLs() throws Exception {
        File[] files = new File[] {
            new File("file1"), new File("file2"),
        };

        URL[] urls = FileUtils.toURLs(files);

        assertEquals(
                files.length,
                urls.length,
                "The length of the generated URL's is not equals to the length of files. " + "Was " + files.length
                        + ", expected " + urls.length);

        for (int i = 0; i < urls.length; i++) {
            assertEquals(files[i].toURI().toURL(), urls[i]);
        }
    }

    @Test
    void getFilesFromExtension() {
        // TODO I'm not sure what is supposed to happen here
        FileUtils.getFilesFromExtension("dir", null);

        // Non-existent files
        String[] emptyFileNames =
                FileUtils.getFilesFromExtension(getTestDirectory().getAbsolutePath(), new String[] {"java"});
        assertEquals(0, emptyFileNames.length);

        // Existing files
        // TODO Figure out how to test this
        /*
         * final String[] fileNames = FileUtils.getFilesFromExtension( getClass().getResource("/java/util/").getFile(),
         * new String[] { "class" }); assertTrue(fileNames.length > 0);
         */
    }

    @Test
    void mkdir() {
        final File dir = new File(getTestDirectory(), "testdir");
        FileUtils.mkdir(dir.getAbsolutePath());
        dir.deleteOnExit();

        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            assertThrows(IllegalArgumentException.class, () -> {
                File winFile = new File(getTestDirectory(), "bla*bla");
                winFile.deleteOnExit();
                FileUtils.mkdir(winFile.getAbsolutePath());
            });
        }
    }

    @Test
    void contentEquals() throws Exception {
        // Non-existent files
        final File file = new File(getTestDirectory(), getTestMethodName());
        assertTrue(FileUtils.contentEquals(file, file));

        // TODO Should comparing 2 directories throw an Exception instead of returning false?
        // Directories
        assertFalse(FileUtils.contentEquals(getTestDirectory(), getTestDirectory()));

        // Different files
        final File objFile1 = new File(getTestDirectory(), getTestMethodName() + ".object");
        objFile1.deleteOnExit();
        FileUtils.copyURLToFile(getClass().getResource("/java/lang/Object.class"), objFile1);

        final File objFile2 = new File(getTestDirectory(), getTestMethodName() + ".collection");
        objFile2.deleteOnExit();
        FileUtils.copyURLToFile(getClass().getResource("/java/util/Collection.class"), objFile2);

        assertFalse(FileUtils.contentEquals(objFile1, objFile2), "Files should not be equal.");

        // Equal files
        file.createNewFile();
        assertTrue(FileUtils.contentEquals(file, file));
    }

    @Test
    void removePath() {
        String fileName = FileUtils.removePath(new File(getTestDirectory(), getTestMethodName()).getAbsolutePath());
        assertEquals(getTestMethodName(), fileName);
    }

    @Test
    void getPath() {
        final String fileName = FileUtils.getPath(new File(getTestDirectory(), getTestMethodName()).getAbsolutePath());
        assertEquals(getTestDirectory().getAbsolutePath(), fileName);
    }

    @Test
    void copyURLToFile() throws Exception {
        // Creates file
        final File file = new File(getTestDirectory(), getTestMethodName());
        file.deleteOnExit();

        // Loads resource
        final String resourceName = "/java/lang/Object.class";
        FileUtils.copyURLToFile(getClass().getResource(resourceName), file);

        // Tests that resource was copied correctly
        try (InputStream fis = Files.newInputStream(file.toPath())) {
            assertTrue(
                    IOUtil.contentEquals(getClass().getResourceAsStream(resourceName), fis), "Content is not equal.");
        }
    }

    @Test
    void catPath() {
        // TODO StringIndexOutOfBoundsException thrown if file doesn't contain slash.
        // Is this acceptable?
        // assertEquals("", FileUtils.catPath("a", "b"));

        assertEquals("/a/c", FileUtils.catPath("/a/b", "c"));
        assertEquals("/a/d", FileUtils.catPath("/a/b/c", "../d"));
    }

    @Test
    void forceMkdir() throws Exception {
        // Tests with existing directory
        FileUtils.forceMkdir(getTestDirectory());

        // Creates test file
        final File testFile = new File(getTestDirectory(), getTestMethodName());
        testFile.deleteOnExit();
        testFile.createNewFile();
        assertTrue(testFile.exists(), "Test file does not exist.");

        // Tests with existing file
        assertThrows(IOException.class, () -> FileUtils.forceMkdir(testFile));

        testFile.delete();

        // Tests with non-existent directory
        FileUtils.forceMkdir(testFile);
        assertTrue(testFile.exists(), "Directory was not created.");

        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            assertThrows(IllegalArgumentException.class, () -> {
                File winFile = new File(getTestDirectory(), "bla*bla");
                winFile.deleteOnExit();
                FileUtils.forceMkdir(winFile);
            });
        }
    }

    @Test
    void sizeOfDirectory() throws Exception {
        final File file = new File(getTestDirectory(), getTestMethodName());

        assertThrows(IllegalArgumentException.class, () -> {
            // Non-existent file
            FileUtils.sizeOfDirectory(file);
        });

        // Creates file
        file.createNewFile();
        file.deleteOnExit();

        // Existing file
        assertThrows(IllegalArgumentException.class, () -> FileUtils.sizeOfDirectory(file));

        // Existing directory
        file.delete();
        file.mkdir();

        assertEquals(TEST_DIRECTORY_SIZE, FileUtils.sizeOfDirectory(file), "Unexpected directory size");
    }

    @Test
    void copyFile1() throws Exception {
        final File destination = new File(getTestDirectory(), "copy1.txt");
        FileUtils.copyFile(testFile1, destination);
        assertTrue(destination.exists(), "Check Exist");
        assertEquals(destination.length(), testFile1Size, "Check Full copy");
    }

    @Test
    void copyFile2() throws Exception {
        final File destination = new File(getTestDirectory(), "copy2.txt");
        FileUtils.copyFile(testFile1, destination);
        assertTrue(destination.exists(), "Check Exist");
        assertEquals(destination.length(), testFile2Size, "Check Full copy");
    }

    /**
     * ensure we create directory tree for destination
     */
    @Test
    void copyFile3() throws Exception {
        File destDirectory = new File(getTestDirectory(), "foo/bar/testcopy");
        if (destDirectory.exists()) {
            destDirectory.delete();
        }
        final File destination = new File(destDirectory, "copy2.txt");
        FileUtils.copyFile(testFile1, destination);
        assertTrue(destination.exists(), "Check Exist");
        assertEquals(destination.length(), testFile2Size, "Check Full copy");
    }

    @Test
    void linkFile1() throws Exception {
        final File destination = new File(getTestDirectory(), "link1.txt");
        FileUtils.linkFile(testFile1, destination);
        assertTrue(destination.exists(), "Check Exist");
        assertEquals(destination.length(), testFile1Size, "Check File length");
        assertTrue(Files.isSymbolicLink(destination.toPath()), "Check is link");
    }

    @Test
    void linkFile2() throws Exception {
        final File destination = new File(getTestDirectory(), "link2.txt");
        FileUtils.linkFile(testFile1, destination);
        assertTrue(destination.exists(), "Check Exist");
        assertEquals(destination.length(), testFile2Size, "Check File length");
        assertTrue(Files.isSymbolicLink(destination.toPath()), "Check is link");
    }

    /**
     * ensure we create directory tree for destination
     */
    @Test
    void linkFile3() throws Exception {
        File destDirectory = new File(getTestDirectory(), "foo/bar/testlink");
        if (destDirectory.exists()) {
            destDirectory.delete();
        }
        final File destination = new File(destDirectory, "link2.txt");
        FileUtils.linkFile(testFile1, destination);
        assertTrue(destination.exists(), "Check Exist");
        assertEquals(destination.length(), testFile2Size, "Check File length");
        assertTrue(Files.isSymbolicLink(destination.toPath()), "Check is link");
    }

    @Test
    void copyIfModifiedWhenSourceIsNewer() throws Exception {
        FileUtils.forceMkdir(new File(getTestDirectory() + "/temp"));

        // Place destination
        File destination = new File(getTestDirectory() + "/temp/copy1.txt");
        FileUtils.copyFile(testFile1, destination);

        // Make sure source is newer
        reallySleep(1000);

        // Place source
        File source = new File(getTestDirectory(), "copy1.txt");
        FileUtils.copyFile(testFile1, source);
        source.setLastModified(System.currentTimeMillis());

        // Copy will occur when source is newer
        assertTrue(
                FileUtils.copyFileIfModified(source, destination),
                "Failed copy. Target file should have been updated.");
    }

    @Test
    void copyIfModifiedWhenSourceIsOlder() throws Exception {
        FileUtils.forceMkdir(new File(getTestDirectory() + "/temp"));

        // Place source
        File source = new File(getTestDirectory() + "copy1.txt");
        FileUtils.copyFile(testFile1, source);

        // Make sure destination is newer
        reallySleep(1000);

        // Place destination
        File destination = new File(getTestDirectory(), "/temp/copy1.txt");
        FileUtils.copyFile(testFile1, destination);

        // Copy will occur when destination is newer
        assertFalse(FileUtils.copyFileIfModified(source, destination), "Source file should not have been copied.");
    }

    @Test
    void copyIfModifiedWhenSourceHasZeroDate() throws Exception {
        FileUtils.forceMkdir(new File(getTestDirectory(), "temp"));

        // Source modified on 1970-01-01T00:00Z
        File source = new File(getTestDirectory(), "copy1.txt");
        FileUtils.copyFile(testFile1, source);
        source.setLastModified(0L);

        // A non existing destination
        File destination = new File(getTestDirectory(), "temp/copy1.txt");

        // Should copy the source to the non existing destination.
        assertTrue(FileUtils.copyFileIfModified(source, destination), "Source file should have been copied.");
    }

    @Test
    void forceDeleteAFile1() throws Exception {
        final File destination = new File(getTestDirectory(), "copy1.txt");
        destination.createNewFile();
        assertTrue(destination.exists(), "Copy1.txt doesn't exist to delete");
        FileUtils.forceDelete(destination);
        assertFalse(destination.exists(), "Check No Exist");
    }

    @Test
    void forceDeleteAFile2() throws Exception {
        final File destination = new File(getTestDirectory(), "copy2.txt");
        destination.createNewFile();
        assertTrue(destination.exists(), "Copy2.txt doesn't exist to delete");
        FileUtils.forceDelete(destination);
        assertFalse(destination.exists(), "Check No Exist");
    }

    @Test
    void copyFile1ToDir() throws Exception {
        final File directory = new File(getTestDirectory(), "subdir");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        final File destination = new File(directory, testFile1.getName());
        FileUtils.copyFileToDirectory(testFile1, directory);
        assertTrue(destination.exists(), "Check Exist");
        assertEquals(destination.length(), testFile1Size, "Check Full copy");
    }

    @Test
    void copyFile2ToDir() throws Exception {
        final File directory = new File(getTestDirectory(), "subdir");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        final File destination = new File(directory, testFile1.getName());
        FileUtils.copyFileToDirectory(testFile1, directory);
        assertTrue(destination.exists(), "Check Exist");
        assertEquals(destination.length(), testFile2Size, "Check Full copy");
    }

    @Test
    void copyFile1ToDirIfModified() throws Exception {
        final File directory = new File(getTestDirectory(), "subdir");
        if (directory.exists()) {
            FileUtils.forceDelete(directory);
        }
        directory.mkdirs();

        final File destination = new File(directory, testFile1.getName());

        FileUtils.copyFileToDirectoryIfModified(testFile1, directory);

        final File target = new File(getTestDirectory() + "/subdir", testFile1.getName());
        long timestamp = target.lastModified();

        assertTrue(destination.exists(), "Check Exist");
        assertEquals(destination.length(), testFile1Size, "Check Full copy");

        FileUtils.copyFileToDirectoryIfModified(testFile1, directory);

        assertEquals(timestamp, target.lastModified(), "Timestamp was changed");
    }

    @Test
    void copyFile2ToDirIfModified() throws Exception {
        final File directory = new File(getTestDirectory(), "subdir");
        if (directory.exists()) {
            FileUtils.forceDelete(directory);
        }
        directory.mkdirs();

        final File destination = new File(directory, testFile2.getName());

        FileUtils.copyFileToDirectoryIfModified(testFile2, directory);

        final File target = new File(getTestDirectory() + "/subdir", testFile2.getName());
        long timestamp = target.lastModified();

        assertTrue(destination.exists(), "Check Exist");
        assertEquals(destination.length(), testFile2Size, "Check Full copy");

        FileUtils.copyFileToDirectoryIfModified(testFile2, directory);

        assertEquals(timestamp, target.lastModified(), "Timestamp was changed");
    }

    @Test
    void forceDeleteDir() throws Exception {
        FileUtils.forceDelete(getTestDirectory().getParentFile());
        assertFalse(getTestDirectory().getParentFile().exists(), "Check No Exist");
    }

    @Test
    void resolveFileDotDot() {
        final File file = FileUtils.resolveFile(getTestDirectory(), "..");
        assertEquals(file, getTestDirectory().getParentFile(), "Check .. operator");
    }

    @Test
    void resolveFileDot() {
        final File file = FileUtils.resolveFile(getTestDirectory(), ".");
        assertEquals(file, getTestDirectory(), "Check . operator");
    }

    @Test
    void normalize() {
        final String[] src = {
            "",
            "/",
            "///",
            "/foo",
            "/foo//",
            "/./",
            "/foo/./",
            "/foo/./bar",
            "/foo/../bar",
            "/foo/../bar/../baz",
            "/foo/bar/../../baz",
            "/././",
            "/foo/./../bar",
            "/foo/.././bar/",
            "//foo//./bar",
            "/../",
            "/foo/../../"
        };

        final String[] dest = {
            "",
            "/",
            "/",
            "/foo",
            "/foo/",
            "/",
            "/foo/",
            "/foo/bar",
            "/bar",
            "/baz",
            "/baz",
            "/",
            "/bar",
            "/bar/",
            "/foo/bar",
            null,
            null
        };

        assertEquals(src.length, dest.length, "Oops, test writer goofed");

        for (int i = 0; i < src.length; i++) {
            assertEquals(
                    dest[i], FileUtils.normalize(src[i]), "Check if '" + src[i] + "' normalized to '" + dest[i] + "'");
        }
    }

    @SuppressWarnings("deprecation")
    @Test
    void fileUtils() throws Exception {
        // Loads file from classpath
        final String path = "/test.txt";
        final URL url = this.getClass().getResource(path);
        assertNotNull(url, path + " was not found.");

        final String filename = Paths.get(url.toURI()).toString();
        final String filename2 = "test2.txt";

        assertEquals("txt", FileUtils.getExtension(filename), "test.txt extension == \"txt\"");

        assertTrue(new File(filename).exists(), "Test file does exist: " + filename);

        assertFalse(new File(filename2).exists(), "Second test file does not exist");

        FileUtils.fileWrite(filename2, filename);
        assertTrue(new File(filename2).exists(), "Second file was written");

        final String file2contents = FileUtils.fileRead(filename2);
        assertEquals(FileUtils.fileRead(filename2), file2contents, "Second file's contents correct");

        FileUtils.fileAppend(filename2, filename);
        assertEquals(FileUtils.fileRead(filename2), file2contents + file2contents, "Second file's contents correct");

        FileUtils.fileDelete(filename2);
        assertFalse(new File(filename2).exists(), "Second test file does not exist");

        final String contents = FileUtils.fileRead(filename);
        assertEquals("This is a test", contents, "FileUtils.fileRead()");
    }

    @Test
    void getExtension() {
        final String[][] tests = {
            {"filename.ext", "ext"},
            {"README", ""},
            {"domain.dot.com", "com"},
            {"image.jpeg", "jpeg"},
            {"folder" + File.separator + "image.jpeg", "jpeg"},
            {"folder" + File.separator + "README", ""}
        };

        for (String[] test : tests) {
            assertEquals(test[1], FileUtils.getExtension(test[0]));
            // assertEquals(tests[i][1], FileUtils.extension(tests[i][0]));
        }
    }

    @Test
    void getExtensionWithPaths() {
        // Since the utilities are based on the separator for the platform
        // running the test, ensure we are using the right separator
        final String sep = File.separator;
        final String[][] testsWithPaths = {
            {sep + "tmp" + sep + "foo" + sep + "filename.ext", "ext"},
            {"C:" + sep + "temp" + sep + "foo" + sep + "filename.ext", "ext"},
            {sep + "tmp" + sep + "foo.bar" + sep + "filename.ext", "ext"},
            {"C:" + sep + "temp" + sep + "foo.bar" + sep + "filename.ext", "ext"},
            {sep + "tmp" + sep + "foo.bar" + sep + "README", ""},
            {"C:" + sep + "temp" + sep + "foo.bar" + sep + "README", ""},
            {".." + sep + "filename.ext", "ext"},
            {"blabla", ""}
        };
        for (String[] testsWithPath : testsWithPaths) {
            assertEquals(testsWithPath[1], FileUtils.getExtension(testsWithPath[0]));
            // assertEquals(testsWithPaths[i][1], FileUtils.extension(testsWithPaths[i][0]));
        }
    }

    @Test
    void removeExtension() {
        final String[][] tests = {
            {"filename.ext", "filename"},
            {"first.second.third.ext", "first.second.third"},
            {"README", "README"},
            {"domain.dot.com", "domain.dot"},
            {"image.jpeg", "image"}
        };

        for (String[] test : tests) {
            assertEquals(test[1], FileUtils.removeExtension(test[0]));
            // assertEquals(tests[i][1], FileUtils.basename(tests[i][0]));
        }
    }

    @Test
    void removeExtensionWithPaths() {
        // Since the utilities are based on the separator for the platform
        // running the test, ensure we are using the right separator
        final String sep = File.separator;
        final String[][] testsWithPaths = {
            {sep + "tmp" + sep + "foo" + sep + "filename.ext", sep + "tmp" + sep + "foo" + sep + "filename"},
            {
                "C:" + sep + "temp" + sep + "foo" + sep + "filename.ext",
                "C:" + sep + "temp" + sep + "foo" + sep + "filename"
            },
            {sep + "tmp" + sep + "foo.bar" + sep + "filename.ext", sep + "tmp" + sep + "foo.bar" + sep + "filename"},
            {
                "C:" + sep + "temp" + sep + "foo.bar" + sep + "filename.ext",
                "C:" + sep + "temp" + sep + "foo.bar" + sep + "filename"
            },
            {sep + "tmp" + sep + "foo.bar" + sep + "README", sep + "tmp" + sep + "foo.bar" + sep + "README"},
            {
                "C:" + sep + "temp" + sep + "foo.bar" + sep + "README",
                "C:" + sep + "temp" + sep + "foo.bar" + sep + "README"
            },
            {".." + sep + "filename.ext", ".." + sep + "filename"}
        };

        for (String[] testsWithPath : testsWithPaths) {
            assertEquals(testsWithPath[1], FileUtils.removeExtension(testsWithPath[0]));
            // assertEquals(testsWithPaths[i][1], FileUtils.basename(testsWithPaths[i][0]));
        }
    }

    @Test
    void copyDirectoryStructureWithAEmptyDirectoryStructure() throws Exception {
        File from = new File(getTestDirectory(), "from");

        FileUtils.deleteDirectory(from);

        assertTrue(from.mkdirs());

        File to = new File(getTestDirectory(), "to");

        assertTrue(to.mkdirs());

        FileUtils.copyDirectoryStructure(from, to);
    }

    @Test
    void copyDirectoryStructureWithAPopulatedStructure() throws Exception {
        // Make a structure to copy
        File from = new File(getTestDirectory(), "from");

        FileUtils.deleteDirectory(from);

        File fRoot = new File(from, "root.txt");

        File d1 = new File(from, "1");

        File d1_1 = new File(d1, "1_1");

        File d2 = new File(from, "2");

        File f2 = new File(d2, "2.txt");

        File d2_1 = new File(d2, "2_1");

        File f2_1 = new File(d2_1, "2_1.txt");

        assertTrue(from.mkdir());

        assertTrue(d1.mkdir());

        assertTrue(d1_1.mkdir());

        assertTrue(d2.mkdir());

        assertTrue(d2_1.mkdir());

        createFile(fRoot, 100);

        createFile(f2, 100);

        createFile(f2_1, 100);

        File to = new File(getTestDirectory(), "to");

        assertTrue(to.mkdirs());

        FileUtils.copyDirectoryStructure(from, to);

        checkFile(fRoot, new File(to, "root.txt"));

        assertIsDirectory(new File(to, "1"));

        assertIsDirectory(new File(to, "1/1_1"));

        assertIsDirectory(new File(to, "2"));

        assertIsDirectory(new File(to, "2/2_1"));

        checkFile(f2, new File(to, "2/2.txt"));

        checkFile(f2_1, new File(to, "2/2_1/2_1.txt"));
    }

    @Test
    void copyDirectoryStructureIfModified() throws Exception {
        // Make a structure to copy
        File from = new File(getTestDirectory(), "from");

        FileUtils.deleteDirectory(from);

        File fRoot = new File(from, "root.txt");

        File d1 = new File(from, "1");

        File d1_1 = new File(d1, "1_1");

        File d2 = new File(from, "2");

        File f2 = new File(d2, "2.txt");

        File d2_1 = new File(d2, "2_1");

        File f2_1 = new File(d2_1, "2_1.txt");

        assertTrue(from.mkdir());

        assertTrue(d1.mkdir());

        assertTrue(d1_1.mkdir());

        assertTrue(d2.mkdir());

        assertTrue(d2_1.mkdir());

        createFile(fRoot, 100);

        createFile(f2, 100);

        createFile(f2_1, 100);

        File to = new File(getTestDirectory(), "to");

        assertTrue(to.mkdirs());

        FileUtils.copyDirectoryStructureIfModified(from, to);

        File[] files = {new File(to, "root.txt"), new File(to, "2/2.txt"), new File(to, "2/2_1/2_1.txt")};

        long[] timestamps = {files[0].lastModified(), files[1].lastModified(), files[2].lastModified()};

        checkFile(fRoot, files[0]);

        assertIsDirectory(new File(to, "1"));

        assertIsDirectory(new File(to, "1/1_1"));

        assertIsDirectory(new File(to, "2"));

        assertIsDirectory(new File(to, "2/2_1"));

        checkFile(f2, files[1]);

        checkFile(f2_1, files[2]);

        FileUtils.copyDirectoryStructureIfModified(from, to);

        assertEquals(timestamps[0], files[0].lastModified(), "Unmodified file was overwritten");
        assertEquals(timestamps[1], files[1].lastModified(), "Unmodified file was overwritten");
        assertEquals(timestamps[2], files[2].lastModified(), "Unmodified file was overwritten");

        files[1].setLastModified(f2.lastModified() - 5000L);
        timestamps[1] = files[1].lastModified();

        FileUtils.copyDirectoryStructureIfModified(from, to);

        assertEquals(timestamps[0], files[0].lastModified(), "Unmodified file was overwritten");
        assertTrue(timestamps[1] < files[1].lastModified(), "Outdated file was not overwritten");
        assertEquals(timestamps[2], files[2].lastModified(), "Unmodified file was overwritten");
    }

    @Test
    void copyDirectoryStructureToSelf() throws Exception {
        // Make a structure to copy
        File toFrom = new File(getTestDirectory(), "tofrom");

        FileUtils.deleteDirectory(toFrom);

        File fRoot = new File(toFrom, "root.txt");

        File dSub = new File(toFrom, "subdir");

        File f1 = new File(dSub, "notempty.txt");

        File dSubSub = new File(dSub, "subsubdir");

        File f2 = new File(dSubSub, "notemptytoo.txt");

        assertTrue(toFrom.mkdir());

        assertTrue(dSub.mkdir());

        assertTrue(dSubSub.mkdir());

        createFile(fRoot, 100);

        createFile(f1, 100);

        createFile(f2, 100);

        assertThrows(IOException.class, () -> FileUtils.copyDirectoryStructure(toFrom, toFrom));
    }

    @Test
    void filteredFileCopy() throws Exception {
        File compareFile = new File(getTestDirectory(), "compare.txt");
        FileUtils.fileWrite(compareFile.getAbsolutePath(), "UTF-8", "This is a test.  Test sample text\n");

        File destFile = new File(getTestDirectory(), "target.txt");

        final Properties filterProperties = new Properties();
        filterProperties.setProperty("s", "sample text");

        // test ${token}
        FileUtils.FilterWrapper[] wrappers1 = new FileUtils.FilterWrapper[] {
            new FileUtils.FilterWrapper() {
                public Reader getReader(Reader reader) {
                    return new InterpolationFilterReader(reader, filterProperties, "${", "}");
                }
            }
        };

        File srcFile = new File(getTestDirectory(), "root.txt");
        FileUtils.fileWrite(srcFile.getAbsolutePath(), "UTF-8", "This is a test.  Test ${s}\n");

        FileUtils.copyFile(srcFile, destFile, "UTF-8", wrappers1);
        assertTrue(FileUtils.contentEquals(compareFile, destFile), "Files should be equal.");

        srcFile.delete();
        destFile.delete();
        compareFile.delete();
    }

    @Test
    void filteredWithoutFilterAndOlderFile() throws Exception {
        String content = "This is a test.";
        File sourceFile = new File(getTestDirectory(), "source.txt");
        FileUtils.fileWrite(sourceFile.getAbsolutePath(), "UTF-8", content);

        File destFile = new File(getTestDirectory(), "target.txt");
        if (destFile.exists()) {
            destFile.delete();
        }
        FileUtils.copyFile(sourceFile, destFile, null, null);
        assertEqualContent(content.getBytes(StandardCharsets.UTF_8), destFile);

        String newercontent = "oldercontent";
        File olderFile = new File(getTestDirectory(), "oldersource.txt");

        FileUtils.fileWrite(olderFile.getAbsolutePath(), "UTF-8", newercontent);

        // very old file ;-)
        olderFile.setLastModified(1);
        destFile = new File(getTestDirectory(), "target.txt");
        FileUtils.copyFile(olderFile, destFile, null, null);
        String destFileContent = FileUtils.fileRead(destFile, "UTF-8");
        assertEquals(content, destFileContent);
    }

    @Test
    void filteredWithoutFilterAndOlderFileAndOverwrite() throws Exception {
        String content = "This is a test.";
        File sourceFile = new File(getTestDirectory(), "source.txt");
        FileUtils.fileWrite(sourceFile.getAbsolutePath(), "UTF-8", content);

        File destFile = new File(getTestDirectory(), "target.txt");
        if (destFile.exists()) {
            destFile.delete();
        }
        FileUtils.copyFile(sourceFile, destFile, null, null);
        assertEqualContent(content.getBytes(StandardCharsets.UTF_8), destFile);

        String newercontent = "oldercontent";
        File olderFile = new File(getTestDirectory(), "oldersource.txt");

        FileUtils.fileWrite(olderFile.getAbsolutePath(), "UTF-8", newercontent);

        // very old file ;-)
        olderFile.setLastModified(1);
        destFile = new File(getTestDirectory(), "target.txt");
        FileUtils.copyFile(olderFile, destFile, null, null, true);
        String destFileContent = FileUtils.fileRead(destFile, "UTF-8");
        assertEquals(newercontent, destFileContent);
    }

    @Test
    void fileRead() throws Exception {
        File testFile = new File(getTestDirectory(), "testFileRead.txt");
        String testFileName = testFile.getAbsolutePath();
        /*
         * NOTE: The method under test uses the JVM's default encoding which by its nature varies from machine to
         * machine. As a consequence, we cannot know in advance which characters are supported by the effective encoding
         * of the test runner. Therefore this test must be restricted to ASCII characters which are reasonably safe to
         * survive the roundtrip test.
         */
        String testString = "Only US-ASCII characters here, see comment above!";
        try (Writer writer = new OutputStreamWriter(Files.newOutputStream(testFile.toPath()))) {
            writer.write(testString);
            writer.flush();
        }
        assertEquals(testString, FileUtils.fileRead(testFile), "testString should be equal");
        assertEquals(testString, FileUtils.fileRead(testFileName), "testString should be equal");
        testFile.delete();
    }

    @Test
    void fileReadWithEncoding() throws Exception {
        String encoding = "UTF-8";
        File testFile = new File(getTestDirectory(), "testFileRead.txt");
        String testFileName = testFile.getAbsolutePath();
        // unicode escaped Japanese hiragana, "aiueo" + Umlaut a
        String testString = "あいうえおä";
        try (Writer writer = new OutputStreamWriter(Files.newOutputStream(testFile.toPath()), encoding)) {
            writer.write(testString);
            writer.flush();
        }
        assertEquals(testString, FileUtils.fileRead(testFile, "UTF-8"), "testString should be equal");
        assertEquals(testString, FileUtils.fileRead(testFileName, "UTF-8"), "testString should be equal");
        testFile.delete();
    }

    @SuppressWarnings("deprecation")
    @Test
    void fileAppend() throws Exception {
        String baseString = "abc";
        File testFile = new File(getTestDirectory(), "testFileAppend.txt");
        String testFileName = testFile.getAbsolutePath();
        try (Writer writer = new OutputStreamWriter(Files.newOutputStream(testFile.toPath()))) {
            writer.write(baseString);
            writer.flush();
        }
        // unicode escaped Japanese hiragana, "aiueo" + Umlaut a
        String testString = "あいうえおä";
        FileUtils.fileAppend(testFileName, testString);
        assertEqualContent((baseString + testString).getBytes(), testFile);
        testFile.delete();
    }

    @SuppressWarnings("deprecation")
    @Test
    void fileAppendWithEncoding() throws Exception {
        String baseString = "abc";
        String encoding = "UTF-8";
        File testFile = new File(getTestDirectory(), "testFileAppend.txt");
        String testFileName = testFile.getAbsolutePath();
        try (Writer writer = new OutputStreamWriter(Files.newOutputStream(testFile.toPath()), encoding)) {
            writer.write(baseString);
            writer.flush();
        }
        // unicode escaped Japanese hiragana, "aiueo" + Umlaut a
        String testString = "あいうえおä";
        FileUtils.fileAppend(testFileName, encoding, testString);
        assertEqualContent((baseString + testString).getBytes(encoding), testFile);
        testFile.delete();
    }

    @Test
    void fileWrite() throws Exception {
        File testFile = new File(getTestDirectory(), "testFileWrite.txt");
        String testFileName = testFile.getAbsolutePath();
        // unicode escaped Japanese hiragana, "aiueo" + Umlaut a
        String testString = "あいうえおä";
        FileUtils.fileWrite(testFileName, testString);
        assertEqualContent(testString.getBytes(), testFile);
        testFile.delete();
    }

    @Test
    void fileWriteWithEncoding() throws Exception {
        String encoding = "UTF-8";
        File testFile = new File(getTestDirectory(), "testFileWrite.txt");
        String testFileName = testFile.getAbsolutePath();
        // unicode escaped Japanese hiragana, "aiueo" + Umlaut a
        String testString = "あいうえおä";
        FileUtils.fileWrite(testFileName, encoding, testString);
        assertEqualContent(testString.getBytes(encoding), testFile);
        testFile.delete();
    }

    /**
     * Workaround for the following Sun bugs. They are fixed in JDK 6u1 and JDK 5u11.
     *
     * @see <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4403166">Sun bug id=4403166</a>
     * @see <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6182812">Sun bug id=6182812</a>
     * @see <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6481955">Sun bug id=6481955</a>
     */
    @Test
    @EnabledOnOs(OS.WINDOWS)
    void deleteLongPathOnWindows() throws Exception {
        File a = new File(getTestDirectory(), "longpath");
        a.mkdir();
        File a1 = new File(a, "a");
        a1.mkdir();

        StringBuilder path = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            path.append("../a/");
        }

        File f = new File(a1, path + "test.txt");

        try (InputStream is = new ByteArrayInputStream("Blabla".getBytes(StandardCharsets.UTF_8));
                OutputStream os = Files.newOutputStream(f.getCanonicalFile().toPath())) {
            IOUtil.copy(is, os);
        }

        FileUtils.forceDelete(f);

        File f1 = new File(a1, "test.txt");
        if (f1.exists()) {
            throw new Exception("Unable to delete the file :" + f1.getAbsolutePath());
        }
    }

    @SuppressWarnings("deprecation")
    @Test
    void copyFileOnSameFile() throws Exception {
        String content = "ggrgreeeeeeeeeeeeeeeeeeeeeeeoierjgioejrgiojregioejrgufcdxivbsdibgfizgerfyaezgv!zeez";
        final File theFile = File.createTempFile("test", ".txt");
        theFile.deleteOnExit();
        FileUtils.fileAppend(theFile.getAbsolutePath(), content);

        assertTrue(theFile.length() > 0);
        // Now copy file over itself
        FileUtils.copyFile(theFile, theFile);

        // This should not fail
        assertTrue(theFile.length() > 0);
    }

    @Test
    void extensions() {

        String[][] values = {
            {"fry.frozen", "frozen"},
            {"fry", ""},
            {"fry.", ""},
            {"/turanga/leela/meets.fry", "fry"},
            {"/3000/turanga.leela.fry/zoidberg.helps", "helps"},
            {"/3000/turanga.leela.fry/zoidberg.", ""},
            {"/3000/turanga.leela.fry/zoidberg", ""},
            {"/3000/leela.fry.bender/", ""},
            {"/3000/leela.fry.bdner/.", ""},
            {"/3000/leela.fry.bdner/foo.bar.txt", "txt"}
        };

        for (int i = 0; i < values.length; i++) {
            String fileName = values[i][0].replace('/', File.separatorChar);
            String ext = values[i][1];
            String computed = FileUtils.extension(fileName);
            assertEquals(ext, computed, "case [" + i + "]:" + fileName + " -> " + ext + ", computed : " + computed);
        }
    }

    @Test
    void isValidWindowsFileName() {
        File f = new File("c:\test");
        assertTrue(FileUtils.isValidWindowsFileName(f));

        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            f = new File("c:\test\bla:bla");
            assertFalse(FileUtils.isValidWindowsFileName(f));
            f = new File("c:\test\bla*bla");
            assertFalse(FileUtils.isValidWindowsFileName(f));
            f = new File("c:\test\bla\"bla");
            assertFalse(FileUtils.isValidWindowsFileName(f));
            f = new File("c:\test\bla<bla");
            assertFalse(FileUtils.isValidWindowsFileName(f));
            f = new File("c:\test\bla>bla");
            assertFalse(FileUtils.isValidWindowsFileName(f));
            f = new File("c:\test\bla|bla");
            assertFalse(FileUtils.isValidWindowsFileName(f));
            f = new File("c:\test\bla*bla");
            assertFalse(FileUtils.isValidWindowsFileName(f));
        }
    }

    @Test
    void deleteDirectoryWithValidFileSymlink() throws Exception {
        File symlinkTarget = new File(getTestDirectory(), "fileSymlinkTarget");
        createFile(symlinkTarget, 1);
        File symlink = new File(getTestDirectory(), "fileSymlink");
        createSymlink(symlink, symlinkTarget);
        try {
            FileUtils.deleteDirectory(getTestDirectory());
        } finally {
            /*
             * Ensure to cleanup problematic symlink or "mvn clean" will fail
             */
            symlink.delete();
        }
        assertFalse(getTestDirectory().exists(), "Failed to delete test directory");
    }

    @Test
    void deleteDirectoryWithValidDirSymlink() throws Exception {
        File symlinkTarget = new File(getTestDirectory(), "dirSymlinkTarget");
        symlinkTarget.mkdir();
        File symlink = new File(getTestDirectory(), "dirSymlink");
        createSymlink(symlink, symlinkTarget);
        try {
            FileUtils.deleteDirectory(getTestDirectory());
        } finally {
            /*
             * Ensure to cleanup problematic symlink or "mvn clean" will fail
             */
            symlink.delete();
        }
        assertFalse(getTestDirectory().exists(), "Failed to delete test directory");
    }

    @Test
    void deleteDirectoryWithDanglingSymlink() throws Exception {
        File symlinkTarget = new File(getTestDirectory(), "missingSymlinkTarget");
        File symlink = new File(getTestDirectory(), "danglingSymlink");
        createSymlink(symlink, symlinkTarget);
        try {
            FileUtils.deleteDirectory(getTestDirectory());
        } finally {
            /*
             * Ensure to cleanup problematic symlink or "mvn clean" will fail
             */
            symlink.delete();
        }
        assertFalse(getTestDirectory().exists(), "Failed to delete test directory");
    }

    @Test
    void testcopyDirectoryLayoutWithExcludesIncludes() throws Exception {
        File destination = new File("target", "copyDirectoryStructureWithExcludesIncludes");
        if (!destination.exists()) {
            destination.mkdirs();
        }
        FileUtils.cleanDirectory(destination);

        File source = new File("src/test/resources/dir-layout-copy");

        FileUtils.copyDirectoryLayout(source, destination, null, null);

        assertTrue(destination.exists());

        File[] childs = destination.listFiles();
        assertNotNull(childs);
        assertEquals(2, childs.length);

        for (File current : childs) {
            if (current.getName().endsWith("empty-dir") || current.getName().endsWith("dir1")) {
                if (current.getName().endsWith("dir1")) {
                    File[] listFiles = current.listFiles();
                    assertNotNull(listFiles);
                    assertEquals(1, listFiles.length);
                    assertTrue(listFiles[0].getName().endsWith("dir2"));
                }
            } else {
                fail("not empty-dir or dir1");
            }
        }
    }

    /**
     * Be sure that {@link org.codehaus.plexus.util.FileUtils#createTempFile(String, String, File)} is always unique.
     */
    @Test
    void createTempFile() {
        File last = FileUtils.createTempFile("unique", ".tmp", null);
        for (int i = 0; i < 10; i++) {
            File current = FileUtils.createTempFile("unique", ".tmp", null);
            assertNotEquals(current.getName(), last.getName(), "No unique name: " + current.getName());
            last = current;
        }
    }

    /**
     * Because windows(tm) quite frequently sleeps less than the advertised time
     *
     * @param time The amount of time to sleep
     */
    private void reallySleep(int time) throws InterruptedException {
        long until = System.currentTimeMillis() + time;
        Thread.sleep(time);
        while (System.currentTimeMillis() < until) {
            Thread.sleep(time / 10);
            Thread.yield();
        }
    }
}
