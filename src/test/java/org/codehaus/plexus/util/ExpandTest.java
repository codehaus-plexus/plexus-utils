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
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the Expand class to ensure it properly prevents directory traversal attacks.
 */
public class ExpandTest extends FileBasedTestCase {

    /**
     * Test that path traversal using ../ is blocked
     */
    @Test
    void testPathTraversalWithParentDirectory() throws Exception {
        File testDir = new File(getTestDirectory(), "expandTest");
        testDir.mkdirs();

        File zipFile = new File(testDir, "malicious.zip");
        File extractDir = new File(testDir, "extract");
        extractDir.mkdirs();

        // Create a malicious zip with path traversal
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            ZipEntry entry = new ZipEntry("../evil.txt");
            zos.putNextEntry(entry);
            zos.write("malicious content".getBytes());
            zos.closeEntry();
        }

        Expand expand = new Expand();
        expand.setSrc(zipFile);
        expand.setDest(extractDir);

        // This should throw an IOException due to path traversal detection
        assertThrows(
                Exception.class, () -> expand.execute(), "Should have thrown exception for path traversal attempt");

        // Verify the file was not created outside the target directory
        File evilFile = new File(testDir, "evil.txt");
        assertTrue(!evilFile.exists(), "File should not have been created outside target directory");
    }

    /**
     * Test that absolute paths are blocked
     */
    @Test
    void testPathTraversalWithAbsolutePath() throws Exception {
        File testDir = new File(getTestDirectory(), "expandTest2");
        testDir.mkdirs();

        File zipFile = new File(testDir, "malicious.zip");
        File extractDir = new File(testDir, "extract");
        extractDir.mkdirs();

        // Create a malicious zip with absolute path
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            ZipEntry entry = new ZipEntry("/tmp/evil.txt");
            zos.putNextEntry(entry);
            zos.write("malicious content".getBytes());
            zos.closeEntry();
        }

        Expand expand = new Expand();
        expand.setSrc(zipFile);
        expand.setDest(extractDir);

        // This should throw an IOException due to absolute path
        assertThrows(Exception.class, () -> expand.execute(), "Should have thrown exception for absolute path");
    }

    /**
     * Test that valid files are extracted correctly
     */
    @Test
    void testValidFileExtraction() throws Exception {
        File testDir = new File(getTestDirectory(), "expandTest3");
        testDir.mkdirs();

        File zipFile = new File(testDir, "valid.zip");
        File extractDir = new File(testDir, "extract");
        extractDir.mkdirs();

        String testContent = "valid content";

        // Create a valid zip file
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            ZipEntry entry = new ZipEntry("subdir/valid.txt");
            zos.putNextEntry(entry);
            zos.write(testContent.getBytes());
            zos.closeEntry();
        }

        Expand expand = new Expand();
        expand.setSrc(zipFile);
        expand.setDest(extractDir);
        expand.execute();

        // Verify the file was created in the correct location
        File extractedFile = new File(extractDir, "subdir/valid.txt");
        assertTrue(extractedFile.exists(), "Valid file should have been extracted");
        assertTrue(extractedFile.isFile(), "Extracted path should be a file");
    }

    /**
     * Test complex path traversal attempts
     */
    @Test
    void testComplexPathTraversal() throws Exception {
        File testDir = new File(getTestDirectory(), "expandTest4");
        testDir.mkdirs();

        File zipFile = new File(testDir, "malicious.zip");
        File extractDir = new File(testDir, "extract");
        extractDir.mkdirs();

        // Create a malicious zip with complex path traversal
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            // Try various path traversal techniques
            String[] maliciousPaths = {
                "../../evil.txt", "../../../evil.txt", "subdir/../../evil.txt", "subdir/../../../evil.txt"
            };

            for (String path : maliciousPaths) {
                ZipEntry entry = new ZipEntry(path);
                zos.putNextEntry(entry);
                zos.write("malicious content".getBytes());
                zos.closeEntry();
            }
        }

        Expand expand = new Expand();
        expand.setSrc(zipFile);
        expand.setDest(extractDir);

        // This should throw an IOException due to path traversal detection
        assertThrows(
                Exception.class,
                () -> expand.execute(),
                "Should have thrown exception for complex path traversal attempt");
    }

    /**
     * Test partial path prefix match vulnerability
     * This tests the case where the target directory is /tmp/app and an attacker
     * tries to write to /tmp/app-data which would pass a naive startsWith check
     */
    @Test
    void testPartialPrefixMatchVulnerability() throws Exception {
        File testDir = new File(getTestDirectory(), "expandTest5");
        testDir.mkdirs();

        // Create directories with similar names
        File extractDir = new File(testDir, "app");
        extractDir.mkdirs();
        File siblingDir = new File(testDir, "app-data");
        siblingDir.mkdirs();

        File zipFile = new File(testDir, "malicious.zip");

        // Create a malicious zip that tries to escape to the sibling directory
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            ZipEntry entry = new ZipEntry("../app-data/evil.txt");
            zos.putNextEntry(entry);
            zos.write("malicious content".getBytes());
            zos.closeEntry();
        }

        Expand expand = new Expand();
        expand.setSrc(zipFile);
        expand.setDest(extractDir);

        // This should throw an IOException due to path traversal detection
        assertThrows(
                Exception.class,
                () -> expand.execute(),
                "Should have thrown exception for partial prefix match attack");

        // Verify the file was not created in the sibling directory
        File evilFile = new File(siblingDir, "evil.txt");
        assertTrue(!evilFile.exists(), "File should not have been created in sibling directory");
    }
}
