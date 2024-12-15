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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * This is used to test FileUtils for correctness.
 *
 * @author Peter Donald
 * @author Matthew Hawthorne
 * @see FileUtils
 * @version $Id: $Id
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

    /**
     * <p>Constructor for FileUtilsTest.</p>
     *
     * @throws java.lang.Exception if any.
     */
    public FileUtilsTest() throws Exception {
        testFile1 = new File(getTestDirectory(), "file1-test.txt");
        testFile2 = new File(getTestDirectory(), "file1a-test.txt");

        testFile1Size = (int) testFile1.length();
        testFile2Size = (int) testFile2.length();
    }

    /**
     * <p>setUp.</p>
     *
     * @throws java.lang.Exception if any.
     */
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

    // byteCountToDisplaySize

    /**
     * <p>testByteCountToDisplaySize.</p>
     */
    @Test
    void byteCountToDisplaySize() {
        assertEquals("0 bytes", FileUtils.byteCountToDisplaySize(0));
        assertEquals("1 KB", FileUtils.byteCountToDisplaySize(1024));
        assertEquals("1 MB", FileUtils.byteCountToDisplaySize(1024 * 1024));
        assertEquals("1 GB", FileUtils.byteCountToDisplaySize(1024 * 1024 * 1024));
    }

    // waitFor

    /**
     * <p>testWaitFor.</p>
     */
    @Test
    void waitFor() {
        FileUtils.waitFor("", -1);

        FileUtils.waitFor("", 2);
    }

    /**
     * <p>testToFile.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void toFile() throws Exception {
        URL url = getClass().getResource("/test.txt");
        url = new URL(url.toString() + "/name%20%23%2520%3F%7B%7D%5B%5D%3C%3E.txt");
        File file = FileUtils.toFile(url);
        assertEquals("name #%20?{}[]<>.txt", file.getName());
    }

    /**
     * <p>testToFileBadProtocol.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void toFileBadProtocol() throws Exception {
        URL url = new URL("http://maven.apache.org/");
        File file = FileUtils.toFile(url);
        assertNull(file);
    }

    /**
     * <p>testToFileNull.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void toFileNull() throws Exception {
        File file = FileUtils.toFile(null);
        assertNull(file);
    }

    // Hacked to sanity by Trygve
    /**
     * <p>testToURLs.</p>
     *
     * @throws java.lang.Exception if any.
     */
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

    /**
     * <p>testGetFilesFromExtension.</p>
     */
    @Test
    void getFilesFromExtension() {
        // TODO I'm not sure what is supposed to happen here
        FileUtils.getFilesFromExtension("dir", null);

        // Non-existent files
        final String[] emptyFileNames =
                FileUtils.getFilesFromExtension(getTestDirectory().getAbsolutePath(), new String[] {"java"});
        assertEquals(0, emptyFileNames.length);

        // Existing files
        // TODO Figure out how to test this
        /*
         * final String[] fileNames = FileUtils.getFilesFromExtension( getClass().getResource("/java/util/").getFile(),
         * new String[] { "class" }); assertTrue(fileNames.length > 0);
         */
    }

    // mkdir

    /**
     * <p>testMkdir.</p>
     */
    @Test
    void mkdir() {
        final File dir = new File(getTestDirectory(), "testdir");
        FileUtils.mkdir(dir.getAbsolutePath());
        dir.deleteOnExit();

        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            try {
                File winFile = new File(getTestDirectory(), "bla*bla");
                winFile.deleteOnExit();
                FileUtils.mkdir(winFile.getAbsolutePath());
                fail();
            } catch (IllegalArgumentException e) {
                assertTrue(true);
            }
        }
    }

    // contentEquals

    /**
     * <p>testContentEquals.</p>
     *
     * @throws java.lang.Exception if any.
     */
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

    // removePath

    /**
     * <p>testRemovePath.</p>
     */
    @Test
    void removePath() {
        final String fileName =
                FileUtils.removePath(new File(getTestDirectory(), getTestMethodName()).getAbsolutePath());
        assertEquals(getTestMethodName(), fileName);
    }

    // getPath

    /**
     * <p>testGetPath.</p>
     */
    @Test
    void getPath() {
        final String fileName = FileUtils.getPath(new File(getTestDirectory(), getTestMethodName()).getAbsolutePath());
        assertEquals(getTestDirectory().getAbsolutePath(), fileName);
    }

    // copyURLToFile

    /**
     * <p>testCopyURLToFile.</p>
     *
     * @throws java.lang.Exception if any.
     */
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

    // catPath

    /**
     * <p>testCatPath.</p>
     */
    @Test
    void catPath() {
        // TODO StringIndexOutOfBoundsException thrown if file doesn't contain slash.
        // Is this acceptable?
        // assertEquals("", FileUtils.catPath("a", "b"));

        assertEquals("/a/c", FileUtils.catPath("/a/b", "c"));
        assertEquals("/a/d", FileUtils.catPath("/a/b/c", "../d"));
    }

    // forceMkdir

    /**
     * <p>testForceMkdir.</p>
     *
     * @throws java.lang.Exception if any.
     */
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
        try {
            FileUtils.forceMkdir(testFile);
            fail("Exception expected.");
        } catch (IOException ignored) {
        }

        testFile.delete();

        // Tests with non-existent directory
        FileUtils.forceMkdir(testFile);
        assertTrue(testFile.exists(), "Directory was not created.");

        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            try {
                File winFile = new File(getTestDirectory(), "bla*bla");
                winFile.deleteOnExit();
                FileUtils.forceMkdir(winFile);
                fail();
            } catch (IllegalArgumentException e) {
                assertTrue(true);
            }
        }
    }

    // sizeOfDirectory

    /**
     * <p>testSizeOfDirectory.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void sizeOfDirectory() throws Exception {
        final File file = new File(getTestDirectory(), getTestMethodName());

        // Non-existent file
        try {
            FileUtils.sizeOfDirectory(file);
            fail("Exception expected.");
        } catch (IllegalArgumentException ignored) {
        }

        // Creates file
        file.createNewFile();
        file.deleteOnExit();

        // Existing file
        try {
            FileUtils.sizeOfDirectory(file);
            fail("Exception expected.");
        } catch (IllegalArgumentException ignored) {
        }

        // Existing directory
        file.delete();
        file.mkdir();

        assertEquals(TEST_DIRECTORY_SIZE, FileUtils.sizeOfDirectory(file), "Unexpected directory size");
    }

    // isFileNewer

    // TODO Finish test

    // copyFile
    /**
     * <p>testCopyFile1.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void copyFile1() throws Exception {
        final File destination = new File(getTestDirectory(), "copy1.txt");
        FileUtils.copyFile(testFile1, destination);
        assertTrue(destination.exists(), "Check Exist");
        assertEquals(destination.length(), testFile1Size, "Check Full copy");
    }

    /**
     * <p>testCopyFile2.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void copyFile2() throws Exception {
        final File destination = new File(getTestDirectory(), "copy2.txt");
        FileUtils.copyFile(testFile1, destination);
        assertTrue(destination.exists(), "Check Exist");
        assertEquals(destination.length(), testFile2Size, "Check Full copy");
    }

    /**
     * ensure we create directory tree for destination
     *
     * @throws java.lang.Exception
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

    // linkFile
    /**
     * <p>testLinkFile1.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void linkFile1() throws Exception {
        final File destination = new File(getTestDirectory(), "link1.txt");
        FileUtils.linkFile(testFile1, destination);
        assertTrue(destination.exists(), "Check Exist");
        assertEquals(destination.length(), testFile1Size, "Check File length");
        assertTrue(Files.isSymbolicLink(destination.toPath()), "Check is link");
    }

    /**
     * <p>testLinkFile2.</p>
     *
     * @throws java.lang.Exception if any.
     */
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
     *
     * @throws java.lang.Exception
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

    // copyFileIfModified

    /**
     * <p>testCopyIfModifiedWhenSourceIsNewer.</p>
     *
     * @throws java.lang.Exception if any.
     */
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

    /**
     * <p>testCopyIfModifiedWhenSourceIsOlder.</p>
     *
     * @throws java.lang.Exception if any.
     */
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

    /**
     * <p>testCopyIfModifiedWhenSourceHasZeroDate.</p>
     *
     * @throws java.lang.Exception if any.
     */
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

    // forceDelete

    /**
     * <p>testForceDeleteAFile1.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void forceDeleteAFile1() throws Exception {
        final File destination = new File(getTestDirectory(), "copy1.txt");
        destination.createNewFile();
        assertTrue(destination.exists(), "Copy1.txt doesn't exist to delete");
        FileUtils.forceDelete(destination);
        assertFalse(destination.exists(), "Check No Exist");
    }

    /**
     * <p>testForceDeleteAFile2.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void forceDeleteAFile2() throws Exception {
        final File destination = new File(getTestDirectory(), "copy2.txt");
        destination.createNewFile();
        assertTrue(destination.exists(), "Copy2.txt doesn't exist to delete");
        FileUtils.forceDelete(destination);
        assertFalse(destination.exists(), "Check No Exist");
    }

    // copyFileToDirectory

    /**
     * <p>testCopyFile1ToDir.</p>
     *
     * @throws java.lang.Exception if any.
     */
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

    /**
     * <p>testCopyFile2ToDir.</p>
     *
     * @throws java.lang.Exception if any.
     */
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

    // copyFileToDirectoryIfModified

    /**
     * <p>testCopyFile1ToDirIfModified.</p>
     *
     * @throws java.lang.Exception if any.
     */
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

    /**
     * <p>testCopyFile2ToDirIfModified.</p>
     *
     * @throws java.lang.Exception if any.
     */
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

    // forceDelete

    /**
     * <p>testForceDeleteDir.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void forceDeleteDir() throws Exception {
        FileUtils.forceDelete(getTestDirectory().getParentFile());
        assertFalse(getTestDirectory().getParentFile().exists(), "Check No Exist");
    }

    // resolveFile

    /**
     * <p>testResolveFileDotDot.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void resolveFileDotDot() throws Exception {
        final File file = FileUtils.resolveFile(getTestDirectory(), "..");
        assertEquals(file, getTestDirectory().getParentFile(), "Check .. operator");
    }

    /**
     * <p>testResolveFileDot.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void resolveFileDot() throws Exception {
        final File file = FileUtils.resolveFile(getTestDirectory(), ".");
        assertEquals(file, getTestDirectory(), "Check . operator");
    }

    // normalize

    /**
     * <p>testNormalize.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void normalize() throws Exception {
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

    private String replaceAll(String text, String lookFor, String replaceWith) {
        StringBuilder sb = new StringBuilder(text);
        while (true) {
            int idx = sb.indexOf(lookFor);
            if (idx < 0) {
                break;
            }
            sb.replace(idx, idx + lookFor.length(), replaceWith);
        }
        return sb.toString();
    }

    /**
     * Test the FileUtils implementation.
     *
     * @throws java.lang.Exception if any.
     */
    // Used to exist as IOTestCase class
    @Test
    void fileUtils() throws Exception {
        // Loads file from classpath
        final String path = "/test.txt";
        final URL url = this.getClass().getResource(path);
        assertNotNull(url, path + " was not found.");

        final String filename = Paths.get(url.toURI()).toString();
        final String filename2 = "test2.txt";

        assertEquals("txt", FileUtils.getExtension(filename), "test.txt extension == \"txt\"");

        assertTrue(FileUtils.fileExists(filename), "Test file does exist: " + filename);

        assertFalse(FileUtils.fileExists(filename2), "Second test file does not exist");

        FileUtils.fileWrite(filename2, filename);
        assertTrue(FileUtils.fileExists(filename2), "Second file was written");

        final String file2contents = FileUtils.fileRead(filename2);
        assertEquals(FileUtils.fileRead(filename2), file2contents, "Second file's contents correct");

        FileUtils.fileAppend(filename2, filename);
        assertEquals(FileUtils.fileRead(filename2), file2contents + file2contents, "Second file's contents correct");

        FileUtils.fileDelete(filename2);
        assertFalse(FileUtils.fileExists(filename2), "Second test file does not exist");

        final String contents = FileUtils.fileRead(filename);
        assertEquals("This is a test", contents, "FileUtils.fileRead()");
    }

    /**
     * <p>testGetExtension.</p>
     */
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

    /**
     * <p>testGetExtensionWithPaths.</p>
     */
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

    /**
     * <p>testRemoveExtension.</p>
     */
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

    /* TODO: Reenable this test */
    /**
     * <p>testRemoveExtensionWithPaths.</p>
     */
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

    /**
     * <p>testCopyDirectoryStructureWithAEmptyDirectoryStructure.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void copyDirectoryStructureWithAEmptyDirectoryStructure() throws Exception {
        File from = new File(getTestDirectory(), "from");

        FileUtils.deleteDirectory(from);

        assertTrue(from.mkdirs());

        File to = new File(getTestDirectory(), "to");

        assertTrue(to.mkdirs());

        FileUtils.copyDirectoryStructure(from, to);
    }

    /**
     * <p>testCopyDirectoryStructureWithAPopulatedStructure.</p>
     *
     * @throws java.lang.Exception if any.
     */
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

    /**
     * <p>testCopyDirectoryStructureIfModified.</p>
     *
     * @throws java.lang.Exception if any.
     */
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

    /**
     * <p>testCopyDirectoryStructureToSelf.</p>
     *
     * @throws java.lang.Exception if any.
     */
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

        try {
            FileUtils.copyDirectoryStructure(toFrom, toFrom);
            fail("An exception must be thrown.");
        } catch (IOException e) {
            // expected
        }
    }

    /**
     * <p>testFilteredFileCopy.</p>
     *
     * @throws java.lang.Exception if any.
     */
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

    /**
     * <p>testFilteredWithoutFilterAndOlderFile.</p>
     *
     * @throws java.lang.Exception if any.
     */
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

    /**
     * <p>testFilteredWithoutFilterAndOlderFileAndOverwrite.</p>
     *
     * @throws java.lang.Exception if any.
     */
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

    /**
     * <p>testFileRead.</p>
     *
     * @throws java.io.IOException if any.
     */
    @Test
    void fileRead() throws IOException {
        File testFile = new File(getTestDirectory(), "testFileRead.txt");
        String testFileName = testFile.getAbsolutePath();
        /*
         * NOTE: The method under test uses the JVM's default encoding which by its nature varies from machine to
         * machine. As a consequence, we cannot know in advance which characters are supported by the effective encoding
         * of the test runner. Therefore this test must be restricted to ASCII characters which are reasonably safe to
         * survive the roundtrip test.
         */
        String testString = "Only US-ASCII characters here, see comment above!";
        Writer writer = null;
        try {
            writer = new OutputStreamWriter(Files.newOutputStream(testFile.toPath()));
            writer.write(testString);
            writer.flush();
        } finally {
            IOUtil.close(writer);
        }
        assertEquals(testString, FileUtils.fileRead(testFile), "testString should be equal");
        assertEquals(testString, FileUtils.fileRead(testFileName), "testString should be equal");
        testFile.delete();
    }

    /**
     * <p>testFileReadWithEncoding.</p>
     *
     * @throws java.io.IOException if any.
     */
    @Test
    void fileReadWithEncoding() throws IOException {
        String encoding = "UTF-8";
        File testFile = new File(getTestDirectory(), "testFileRead.txt");
        String testFileName = testFile.getAbsolutePath();
        // unicode escaped Japanese hiragana, "aiueo" + Umlaut a
        String testString = "\u3042\u3044\u3046\u3048\u304a\u00e4";
        Writer writer = null;
        try {
            writer = new OutputStreamWriter(Files.newOutputStream(testFile.toPath()), encoding);
            writer.write(testString);
            writer.flush();
        } finally {
            IOUtil.close(writer);
        }
        assertEquals(testString, FileUtils.fileRead(testFile, "UTF-8"), "testString should be equal");
        assertEquals(testString, FileUtils.fileRead(testFileName, "UTF-8"), "testString should be equal");
        testFile.delete();
    }

    /**
     * <p>testFileAppend.</p>
     *
     * @throws java.io.IOException if any.
     */
    @Test
    void fileAppend() throws IOException {
        String baseString = "abc";
        File testFile = new File(getTestDirectory(), "testFileAppend.txt");
        String testFileName = testFile.getAbsolutePath();
        Writer writer = null;
        try {
            writer = new OutputStreamWriter(Files.newOutputStream(testFile.toPath()));
            writer.write(baseString);
            writer.flush();
        } finally {
            IOUtil.close(writer);
        }
        // unicode escaped Japanese hiragana, "aiueo" + Umlaut a
        String testString = "\u3042\u3044\u3046\u3048\u304a\u00e4";
        FileUtils.fileAppend(testFileName, testString);
        assertEqualContent((baseString + testString).getBytes(), testFile);
        testFile.delete();
    }

    /**
     * <p>testFileAppendWithEncoding.</p>
     *
     * @throws java.io.IOException if any.
     */
    @Test
    void fileAppendWithEncoding() throws IOException {
        String baseString = "abc";
        String encoding = "UTF-8";
        File testFile = new File(getTestDirectory(), "testFileAppend.txt");
        String testFileName = testFile.getAbsolutePath();
        Writer writer = null;
        try {
            writer = new OutputStreamWriter(Files.newOutputStream(testFile.toPath()), encoding);
            writer.write(baseString);
            writer.flush();
        } finally {
            IOUtil.close(writer);
        }
        // unicode escaped Japanese hiragana, "aiueo" + Umlaut a
        String testString = "\u3042\u3044\u3046\u3048\u304a\u00e4";
        FileUtils.fileAppend(testFileName, encoding, testString);
        assertEqualContent((baseString + testString).getBytes(encoding), testFile);
        testFile.delete();
    }

    /**
     * <p>testFileWrite.</p>
     *
     * @throws java.io.IOException if any.
     */
    @Test
    void fileWrite() throws IOException {
        File testFile = new File(getTestDirectory(), "testFileWrite.txt");
        String testFileName = testFile.getAbsolutePath();
        // unicode escaped Japanese hiragana, "aiueo" + Umlaut a
        String testString = "\u3042\u3044\u3046\u3048\u304a\u00e4";
        FileUtils.fileWrite(testFileName, testString);
        assertEqualContent(testString.getBytes(), testFile);
        testFile.delete();
    }

    /**
     * <p>testFileWriteWithEncoding.</p>
     *
     * @throws java.io.IOException if any.
     */
    @Test
    void fileWriteWithEncoding() throws IOException {
        String encoding = "UTF-8";
        File testFile = new File(getTestDirectory(), "testFileWrite.txt");
        String testFileName = testFile.getAbsolutePath();
        // unicode escaped Japanese hiragana, "aiueo" + Umlaut a
        String testString = "\u3042\u3044\u3046\u3048\u304a\u00e4";
        FileUtils.fileWrite(testFileName, encoding, testString);
        assertEqualContent(testString.getBytes(encoding), testFile);
        testFile.delete();
    }

    /**
     * Workaround for the following Sun bugs. They are fixed in JDK 6u1 and JDK 5u11.
     *
     * @throws java.lang.Exception
     * @see <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4403166">Sun bug id=4403166</a>
     * @see <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6182812">Sun bug id=6182812</a>
     * @see <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6481955">Sun bug id=6481955</a>
     */
    @Test
    void deleteLongPathOnWindows() throws Exception {
        if (!Os.isFamily(Os.FAMILY_WINDOWS)) {
            return;
        }

        File a = new File(getTestDirectory(), "longpath");
        a.mkdir();
        File a1 = new File(a, "a");
        a1.mkdir();

        StringBuilder path = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            path.append("../a/");
        }

        File f = new File(a1, path + "test.txt");

        InputStream is = new ByteArrayInputStream("Blabla".getBytes(StandardCharsets.UTF_8));
        OutputStream os = Files.newOutputStream(f.getCanonicalFile().toPath());
        IOUtil.copy(is, os);
        IOUtil.close(is);
        IOUtil.close(os);

        FileUtils.forceDelete(f);

        File f1 = new File(a1, "test.txt");
        if (f1.exists()) {
            throw new Exception("Unable to delete the file :" + f1.getAbsolutePath());
        }
    }

    // Test for bug PLXUTILS-10
    /**
     * <p>testCopyFileOnSameFile.</p>
     *
     * @throws java.io.IOException if any.
     */
    @Test
    void copyFileOnSameFile() throws IOException {
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

    /**
     * <p>testExtensions.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void extensions() throws Exception {

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

    /**
     * <p>testIsValidWindowsFileName.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void isValidWindowsFileName() throws Exception {
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

    /**
     * <p>testDeleteDirectoryWithValidFileSymlink.</p>
     *
     * @throws java.lang.Exception if any.
     */
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

    /**
     * <p>testDeleteDirectoryWithValidDirSymlink.</p>
     *
     * @throws java.lang.Exception if any.
     */
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

    /**
     * <p>testDeleteDirectoryWithDanglingSymlink.</p>
     *
     * @throws java.lang.Exception if any.
     */
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

    /**
     * <p>testcopyDirectoryLayoutWithExcludesIncludes.</p>
     *
     * @throws java.lang.Exception if any.
     */
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
        assertEquals(2, childs.length);

        for (File current : childs) {
            if (current.getName().endsWith("empty-dir") || current.getName().endsWith("dir1")) {
                if (current.getName().endsWith("dir1")) {
                    assertEquals(1, current.listFiles().length);
                    assertTrue(current.listFiles()[0].getName().endsWith("dir2"));
                }
            } else {
                fail("not empty-dir or dir1");
            }
        }
    }

    /**
     * Be sure that {@link org.codehaus.plexus.util.FileUtils#createTempFile(String, String, File)} is always unique.
     *
     * @throws java.lang.Exception if any
     */
    @Test
    void createTempFile() throws Exception {
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
     * @throws InterruptedException
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
