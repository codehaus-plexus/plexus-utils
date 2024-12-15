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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * This is used to test IOUtil for correctness. The following checks are performed:
 * <ul>
 * <li>The return must not be null, must be the same type and equals() to the method's second arg</li>
 * <li>All bytes must have been read from the source (available() == 0)</li>
 * <li>The source and destination content must be identical (byte-wise comparison check)</li>
 * <li>The output stream must not have been closed (a byte/char is written to test this, and subsequent size
 * checked)</li>
 * </ul>
 * Due to interdependencies in IOUtils and IOUtilsTestlet, one bug may cause multiple tests to fail.
 *
 * @author <a href="mailto:jefft@apache.org">Jeff Turner</a>
 * @version $Id: $Id
 * @since 3.4.0
 */
public final class IOUtilTest {
    /*
     * Note: this is not particularly beautiful code. A better way to check for flush and close status would be to
     * implement "trojan horse" wrapper implementations of the various stream classes, which set a flag when relevant
     * methods are called. (JT)
     */

    private final int FILE_SIZE = 1024 * 4 + 1;

    private File testDirectory;

    private File testFile;

    /**
     * <p>setUp.</p>
     */
    @BeforeEach
    void setUp() {
        try {
            testDirectory = (new File("target/test/io/")).getAbsoluteFile();
            if (!testDirectory.exists()) {
                testDirectory.mkdirs();
            }

            testFile = new File(testDirectory, "file2-test.txt");

            createFile(testFile, FILE_SIZE);
        } catch (IOException ioe) {
            throw new RuntimeException("Can't run this test because environment could not be built");
        }
    }

    /**
     * <p>tearDown.</p>
     */
    public void tearDown() {
        testFile.delete();
        testDirectory.delete();
    }

    private void createFile(File file, long size) throws IOException {
        BufferedOutputStream output = new BufferedOutputStream(Files.newOutputStream(file.toPath()));

        for (int i = 0; i < size; i++) {
            output.write((byte) (i % 128)); // nice varied byte pattern compatible with Readers and Writers
        }

        output.close();
    }

    /** Assert that the contents of two byte arrays are the same. */
    private void assertEqualContent(byte[] b0, byte[] b1) {
        assertArrayEquals(b0, b1, "Content not equal according to java.util.Arrays#equals()");
    }

    /** Assert that the content of two files is the same. */
    private void assertEqualContent(File f0, File f1) throws IOException {
        byte[] buf0 = Files.readAllBytes(f0.toPath());
        byte[] buf1 = Files.readAllBytes(f1.toPath());
        assertArrayEquals(buf0, buf1, "The files " + f0 + " and " + f1 + " have different content");
    }

    /** Assert that the content of a file is equal to that in a byte[]. */
    private void assertEqualContent(byte[] b0, File file) throws IOException {
        byte[] b1 = Files.readAllBytes(file.toPath());
        assertArrayEquals(b0, b1, "Content differs");
    }

    /**
     * <p>testInputStreamToOutputStream.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void inputStreamToOutputStream() throws Exception {
        File destination = newFile("copy1.txt");
        InputStream fin = Files.newInputStream(testFile.toPath());
        OutputStream fout = Files.newOutputStream(destination.toPath());

        IOUtil.copy(fin, fout);
        assertEquals(0, fin.available(), "Not all bytes were read");
        fout.flush();

        checkFile(destination);
        checkWrite(fout);
        fout.close();
        fin.close();
        deleteFile(destination);
    }

    /**
     * <p>testInputStreamToWriter.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void inputStreamToWriter() throws Exception {
        File destination = newFile("copy2.txt");
        InputStream fin = Files.newInputStream(testFile.toPath());
        Writer fout = Files.newBufferedWriter(destination.toPath());

        IOUtil.copy(fin, fout);

        assertEquals(0, fin.available(), "Not all bytes were read");
        fout.flush();

        checkFile(destination);
        checkWrite(fout);
        fout.close();
        fin.close();
        deleteFile(destination);
    }

    /**
     * <p>testInputStreamToString.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void inputStreamToString() throws Exception {
        InputStream fin = Files.newInputStream(testFile.toPath());
        String out = IOUtil.toString(fin);
        assertNotNull(out);
        assertEquals(0, fin.available(), "Not all bytes were read");
        assertEquals(out.length(), FILE_SIZE, "Wrong output size: out.length()=" + out.length() + "!=" + FILE_SIZE);
        fin.close();
    }

    /**
     * <p>testReaderToOutputStream.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void readerToOutputStream() throws Exception {
        File destination = newFile("copy3.txt");
        Reader fin = Files.newBufferedReader(testFile.toPath());
        OutputStream fout = Files.newOutputStream(destination.toPath());
        IOUtil.copy(fin, fout);
        // Note: this method *does* flush. It is equivalent to:
        // OutputStreamWriter _out = new OutputStreamWriter(fout);
        // IOUtil.copy( fin, _out, 4096 ); // copy( Reader, Writer, int );
        // _out.flush();
        // out = fout;

        // Note: rely on the method to flush
        checkFile(destination);
        checkWrite(fout);
        fout.close();
        fin.close();
        deleteFile(destination);
    }

    /**
     * <p>testReaderToWriter.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void readerToWriter() throws Exception {
        File destination = newFile("copy4.txt");
        Reader fin = Files.newBufferedReader(testFile.toPath());
        Writer fout = Files.newBufferedWriter(destination.toPath());
        IOUtil.copy(fin, fout);

        fout.flush();
        checkFile(destination);
        checkWrite(fout);
        fout.close();
        fin.close();
        deleteFile(destination);
    }

    /**
     * <p>testReaderToString.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void readerToString() throws Exception {
        Reader fin = Files.newBufferedReader(testFile.toPath());
        String out = IOUtil.toString(fin);
        assertNotNull(out);
        assertEquals(out.length(), FILE_SIZE, "Wrong output size: out.length()=" + out.length() + "!=" + FILE_SIZE);
        fin.close();
    }

    /**
     * <p>testStringToOutputStream.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void stringToOutputStream() throws Exception {
        File destination = newFile("copy5.txt");
        Reader fin = Files.newBufferedReader(testFile.toPath());
        // Create our String. Rely on testReaderToString() to make sure this is valid.
        String str = IOUtil.toString(fin);
        OutputStream fout = Files.newOutputStream(destination.toPath());
        IOUtil.copy(str, fout);
        // Note: this method *does* flush. It is equivalent to:
        // OutputStreamWriter _out = new OutputStreamWriter(fout);
        // IOUtil.copy( str, _out, 4096 ); // copy( Reader, Writer, int );
        // _out.flush();
        // out = fout;
        // note: we don't flush here; this IOUtils method does it for us

        checkFile(destination);
        checkWrite(fout);
        fout.close();
        fin.close();
        deleteFile(destination);
    }

    /**
     * <p>testStringToWriter.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void stringToWriter() throws Exception {
        File destination = newFile("copy6.txt");
        Reader fin = Files.newBufferedReader(testFile.toPath());
        // Create our String. Rely on testReaderToString() to make sure this is valid.
        String str = IOUtil.toString(fin);
        Writer fout = Files.newBufferedWriter(destination.toPath());
        IOUtil.copy(str, fout);
        fout.flush();

        checkFile(destination);
        checkWrite(fout);
        fout.close();
        fin.close();

        deleteFile(destination);
    }

    /**
     * <p>testInputStreamToByteArray.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void inputStreamToByteArray() throws Exception {
        InputStream fin = Files.newInputStream(testFile.toPath());
        byte[] out = IOUtil.toByteArray(fin);
        assertNotNull(out);
        assertEquals(0, fin.available(), "Not all bytes were read");
        assertEquals(out.length, FILE_SIZE, "Wrong output size: out.length=" + out.length + "!=" + FILE_SIZE);
        assertEqualContent(out, testFile);
        fin.close();
    }

    /**
     * <p>testStringToByteArray.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void stringToByteArray() throws Exception {
        Reader fin = Files.newBufferedReader(testFile.toPath());

        // Create our String. Rely on testReaderToString() to make sure this is valid.
        String str = IOUtil.toString(fin);

        byte[] out = IOUtil.toByteArray(str);
        assertEqualContent(str.getBytes(), out);
        fin.close();
    }

    /**
     * <p>testByteArrayToWriter.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void byteArrayToWriter() throws Exception {
        File destination = newFile("copy7.txt");
        Writer fout = Files.newBufferedWriter(destination.toPath());
        InputStream fin = Files.newInputStream(testFile.toPath());

        // Create our byte[]. Rely on testInputStreamToByteArray() to make sure this is valid.
        byte[] in = IOUtil.toByteArray(fin);
        IOUtil.copy(in, fout);
        fout.flush();
        checkFile(destination);
        checkWrite(fout);
        fout.close();
        fin.close();
        deleteFile(destination);
    }

    /**
     * <p>testByteArrayToString.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void byteArrayToString() throws Exception {
        InputStream fin = Files.newInputStream(testFile.toPath());
        byte[] in = IOUtil.toByteArray(fin);
        // Create our byte[]. Rely on testInputStreamToByteArray() to make sure this is valid.
        String str = IOUtil.toString(in);
        assertEqualContent(in, str.getBytes());
        fin.close();
    }

    /**
     * <p>testByteArrayToOutputStream.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void byteArrayToOutputStream() throws Exception {
        File destination = newFile("copy8.txt");
        OutputStream fout = Files.newOutputStream(destination.toPath());
        InputStream fin = Files.newInputStream(testFile.toPath());

        // Create our byte[]. Rely on testInputStreamToByteArray() to make sure this is valid.
        byte[] in = IOUtil.toByteArray(fin);

        IOUtil.copy(in, fout);

        fout.flush();

        checkFile(destination);
        checkWrite(fout);
        fout.close();
        fin.close();
        deleteFile(destination);
    }

    // ----------------------------------------------------------------------
    // Test closeXXX()
    // ----------------------------------------------------------------------

    /**
     * <p>testCloseInputStream.</p>
     *
     */
    @Test
    void closeInputStream() {
        IOUtil.close((InputStream) null);

        TestInputStream inputStream = new TestInputStream();

        IOUtil.close(inputStream);

        assertTrue(inputStream.closed);
    }

    /**
     * <p>testCloseOutputStream.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void closeOutputStream() throws Exception {
        IOUtil.close((OutputStream) null);

        TestOutputStream outputStream = new TestOutputStream();

        IOUtil.close(outputStream);

        assertTrue(outputStream.closed);
    }

    /**
     * <p>testCloseReader.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void closeReader() throws Exception {
        IOUtil.close((Reader) null);

        TestReader reader = new TestReader();

        IOUtil.close(reader);

        assertTrue(reader.closed);
    }

    /**
     * <p>testCloseWriter.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void closeWriter() throws Exception {
        IOUtil.close((Writer) null);

        TestWriter writer = new TestWriter();

        IOUtil.close(writer);

        assertTrue(writer.closed);
    }

    private class TestInputStream extends InputStream {
        boolean closed;

        public void close() {
            closed = true;
        }

        public int read() {
            fail("This method shouldn't be called");

            return 0;
        }
    }

    private class TestOutputStream extends OutputStream {
        boolean closed;

        public void close() {
            closed = true;
        }

        public void write(int value) {
            fail("This method shouldn't be called");
        }
    }

    private class TestReader extends Reader {
        boolean closed;

        public void close() {
            closed = true;
        }

        public int read(char[] cbuf, int off, int len) {
            fail("This method shouldn't be called");

            return 0;
        }
    }

    private static class TestWriter extends Writer {
        boolean closed;

        public void close() {
            closed = true;
        }

        public void write(char[] cbuf, int off, int len) {
            fail("This method shouldn't be called");
        }

        public void flush() {
            fail("This method shouldn't be called");
        }
    }

    // ----------------------------------------------------------------------
    // Utility methods
    // ----------------------------------------------------------------------

    private File newFile(String filename) throws Exception {
        File destination = new File(testDirectory, filename);
        assertFalse(destination.exists(), filename + "Test output data file shouldn't previously exist");

        return destination;
    }

    private void checkFile(File file) throws Exception {
        assertTrue(file.exists(), "Check existence of output file");
        assertEqualContent(testFile, file);
    }

    private void checkWrite(OutputStream output) throws Exception {
        try {
            new PrintStream(output).write(0);
        } catch (Throwable t) {
            throw new Exception("The copy() method closed the stream " + "when it shouldn't have. " + t.getMessage());
        }
    }

    private void checkWrite(Writer output) throws Exception {
        try {
            new PrintWriter(output).write('a');
        } catch (Throwable t) {
            throw new Exception("The copy() method closed the stream " + "when it shouldn't have. " + t.getMessage());
        }
    }

    private void deleteFile(File file) throws Exception {
        assertEquals(
                file.length(),
                FILE_SIZE + 1,
                "Wrong output size: file.length()=" + file.length() + "!=" + FILE_SIZE + 1);

        assertTrue((file.delete() || (!file.exists())), "File would not delete");
    }
}
