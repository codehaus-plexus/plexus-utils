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

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.file.Files;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Base class for testcases doing tests with files.
 *
 * @author Jeremias Maerki
 * @since 3.4.0
 */
public abstract class FileBasedTestCase {
    private static File testDir;

    private TestInfo testInfo;

    public static File getTestDirectory() {
        if (testDir == null) {
            testDir = (new File("target/test/io/")).getAbsoluteFile();
        }
        return testDir;
    }

    /**
     * <p>createFile.</p>
     *
     * @param file a {@link java.io.File} object.
     * @param size a long.
     * @throws java.io.IOException if any.
     */
    protected void createFile(final File file, final long size) throws IOException {
        if (!file.getParentFile().exists()) {
            throw new IOException("Cannot create file " + file + " as the parent directory does not exist");
        }

        byte[] data = generateTestData(size);
        try (BufferedOutputStream output = new BufferedOutputStream(Files.newOutputStream(file.toPath()))) {
            output.write(data);
        }
    }

    /**
     * <p>createSymlink.</p>
     *
     * @param link   a {@link java.io.File} object.
     * @param target a {@link java.io.File} object.
     */
    protected void createSymlink(final File link, final File target) {
        try {
            String[] args = {"ln", "-s", target.getAbsolutePath(), link.getAbsolutePath()};
            Process process = Runtime.getRuntime().exec(args);
            process.waitFor();
        } catch (Exception e) {
            // assume platform does not support "ln" command, tests should be skipped
        }
    }

    protected byte[] generateTestData(final long size) {
        try {
            ByteArrayOutputStream baout = new ByteArrayOutputStream();
            generateTestData(baout, size);
            return baout.toByteArray();
        } catch (IOException ioe) {
            throw new RuntimeException("This should never happen: " + ioe.getMessage());
        }
    }

    protected void generateTestData(final OutputStream out, final long size) throws IOException {
        for (int i = 0; i < size; i++) {
            // output.write((byte)'X');

            // nice varied byte pattern compatible with Readers and Writers
            out.write((byte) ((i % 127) + 1));
        }
    }

    protected void checkFile(final File file, final File referenceFile) throws Exception {
        assertTrue(file.exists(), "Check existence of output file");
        assertEqualContent(referenceFile, file);
    }

    /** Assert that the content of two files is the same. */
    private void assertEqualContent(final File f0, final File f1) throws IOException {
        /*
         * This doesn't work because the filesize isn't updated until the file is closed. assertTrue( "The files " + f0
         * + " and " + f1 + " have differing file sizes (" + f0.length() + " vs " + f1.length() + ")", ( f0.length() ==
         * f1.length() ) );
         */
        byte[] buf0 = Files.readAllBytes(f0.toPath());
        byte[] buf1 = Files.readAllBytes(f0.toPath());
        assertArrayEquals(buf0, buf1, "The files " + f0 + " and " + f1 + " have different content");
    }

    /**
     * Assert that the content of a file is equal to that in a byte[].
     *
     * @param b0 an array of {@link byte} objects.
     * @param file a {@link java.io.File} object.
     * @throws java.io.IOException if any.
     */
    protected void assertEqualContent(final byte[] b0, final File file) throws IOException {
        byte[] b1 = Files.readAllBytes(file.toPath());
        assertArrayEquals(b0, b1, "Content differs");
    }

    protected void assertIsDirectory(File file) {
        assertTrue(file.exists(), "The File doesn't exists: " + file.getAbsolutePath());

        assertTrue(file.isDirectory(), "The File isn't a directory: " + file.getAbsolutePath());
    }

    @BeforeEach
    void init(TestInfo testInfo) {
        this.testInfo = testInfo;
    }

    protected String getTestMethodName() {
        return testInfo.getTestMethod().map(Method::getName).orElse(null);
    }
}
