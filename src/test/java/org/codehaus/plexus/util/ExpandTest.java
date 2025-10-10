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
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test for {@link Expand}.
 */
class ExpandTest extends FileBasedTestCase {

    @Test
    void testZipSlipVulnerabilityWithParentDirectory() throws Exception {
        File tempDir = getTestDirectory();
        File zipFile = new File(tempDir, "malicious.zip");
        File targetDir = new File(tempDir, "extract");
        targetDir.mkdirs();

        // Create a malicious zip with path traversal
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipFile.toPath()))) {
            ZipEntry entry = new ZipEntry("../../evil.txt");
            zos.putNextEntry(entry);
            zos.write("malicious content".getBytes());
            zos.closeEntry();
        }

        Expand expand = new Expand();
        expand.setSrc(zipFile);
        expand.setDest(targetDir);

        // This should throw an exception, not extract the file
        assertThrows(Exception.class, () -> expand.execute());

        // Verify the file was not created outside the target directory
        File evilFile = new File(tempDir, "evil.txt");
        assertFalse(evilFile.exists(), "File should not be extracted outside target directory");
    }

    @Test
    void testZipSlipVulnerabilityWithAbsolutePath() throws Exception {
        File tempDir = getTestDirectory();
        File zipFile = new File(tempDir, "malicious-absolute.zip");
        File targetDir = new File(tempDir, "extract-abs");
        targetDir.mkdirs();

        // Create a malicious zip with absolute path
        File evilTarget = new File("/tmp/evil-absolute.txt");
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipFile.toPath()))) {
            ZipEntry entry = new ZipEntry(evilTarget.getAbsolutePath());
            zos.putNextEntry(entry);
            zos.write("malicious content".getBytes());
            zos.closeEntry();
        }

        Expand expand = new Expand();
        expand.setSrc(zipFile);
        expand.setDest(targetDir);

        // This should throw an exception, not extract the file
        assertThrows(Exception.class, () -> expand.execute());

        // Verify the file was not created at the absolute path
        assertFalse(evilTarget.exists(), "File should not be extracted to absolute path");
    }

    @Test
    void testZipSlipVulnerabilityWithSimilarDirectoryName() throws Exception {
        File tempDir = getTestDirectory();
        File zipFile = new File(tempDir, "malicious-similar.zip");
        File targetDir = new File(tempDir, "extract");
        targetDir.mkdirs();

        // Create a directory with a similar name to test prefix matching vulnerability
        File similarDir = new File(tempDir, "extract-evil");
        similarDir.mkdirs();

        // Create a malicious zip that tries to exploit prefix matching
        // If targetDir is /tmp/extract, this tries to write to /tmp/extract-evil/file.txt
        String maliciousPath = "../extract-evil/evil.txt";
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipFile.toPath()))) {
            ZipEntry entry = new ZipEntry(maliciousPath);
            zos.putNextEntry(entry);
            zos.write("malicious content".getBytes());
            zos.closeEntry();
        }

        Expand expand = new Expand();
        expand.setSrc(zipFile);
        expand.setDest(targetDir);

        // This should throw an exception, not extract the file
        assertThrows(Exception.class, () -> expand.execute());

        // Verify the file was not created in the similar directory
        File evilFile = new File(similarDir, "evil.txt");
        assertFalse(evilFile.exists(), "File should not be extracted to directory with similar name");
    }

    @Test
    void testNormalZipExtraction() throws Exception {
        File tempDir = getTestDirectory();
        File zipFile = new File(tempDir, "normal.zip");
        File targetDir = new File(tempDir, "extract-normal");
        targetDir.mkdirs();

        // Create a normal zip
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipFile.toPath()))) {
            ZipEntry entry = new ZipEntry("subdir/normal.txt");
            zos.putNextEntry(entry);
            zos.write("normal content".getBytes());
            zos.closeEntry();
        }

        Expand expand = new Expand();
        expand.setSrc(zipFile);
        expand.setDest(targetDir);

        // This should succeed
        expand.execute();

        // Verify the file was created in the correct location
        File normalFile = new File(targetDir, "subdir/normal.txt");
        assertTrue(normalFile.exists(), "File should be extracted to correct location");
    }
}
