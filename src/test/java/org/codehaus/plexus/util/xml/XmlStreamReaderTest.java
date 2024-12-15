package org.codehaus.plexus.util.xml;

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
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;

import junit.framework.ComparisonFailure;
import junit.framework.TestCase;
import org.codehaus.plexus.util.IOUtil;

/**
 * <p>XmlStreamReaderTest class.</p>
 *
 * @author herve
 * @version $Id: $Id
 * @since 3.4.0
 */
public class XmlStreamReaderTest extends TestCase {
    /** french */
    private static final String TEXT_LATIN1 = "eacute: \u00E9";

    /** greek */
    private static final String TEXT_LATIN7 = "alpha: \u03B1";

    /** euro support */
    private static final String TEXT_LATIN15 = "euro: \u20AC";

    /** japanese */
    private static final String TEXT_EUC_JP = "hiragana A: \u3042";

    /** Unicode: support everything */
    private static final String TEXT_UNICODE =
            TEXT_LATIN1 + ", " + TEXT_LATIN7 + ", " + TEXT_LATIN15 + ", " + TEXT_EUC_JP;

    /** see http://unicode.org/faq/utf_bom.html#BOM */
    private static final byte[] BOM_UTF8 = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};

    private static final byte[] BOM_UTF16BE = {(byte) 0xFE, (byte) 0xFF};

    private static final byte[] BOM_UTF16LE = {(byte) 0xFF, (byte) 0xFE};

    private static final byte[] BOM_UTF32BE = {(byte) 0x00, (byte) 0x00, (byte) 0xFF, (byte) 0xFE};

    private static final byte[] BOM_UTF32LE = {(byte) 0xFF, (byte) 0xFE, (byte) 0x00, (byte) 0x00};

    private static String createXmlContent(String text, String encoding) {
        String xmlDecl = "<?xml version=\"1.0\"?>";
        if (encoding != null) {
            xmlDecl = "<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>";
        }
        String xml = xmlDecl + "\n<text>" + text + "</text>";
        return xml;
    }

    private static void checkXmlContent(String xml, String encoding) throws IOException {
        checkXmlContent(xml, encoding, null);
    }

    private static void checkXmlContent(String xml, String encoding, byte... bom) throws IOException {
        byte[] xmlContent = xml.getBytes(encoding);
        InputStream in = new ByteArrayInputStream(xmlContent);

        if (bom != null) {
            in = new SequenceInputStream(new ByteArrayInputStream(bom), in);
        }

        XmlStreamReader reader = new XmlStreamReader(in);
        assertEquals(encoding, reader.getEncoding());
        String result = IOUtil.toString(reader);
        assertEquals(xml, result);
    }

    private static void checkXmlStreamReader(String text, String encoding, String effectiveEncoding)
            throws IOException {
        checkXmlStreamReader(text, encoding, effectiveEncoding, null);
    }

    private static void checkXmlStreamReader(String text, String encoding) throws IOException {
        checkXmlStreamReader(text, encoding, encoding, null);
    }

    private static void checkXmlStreamReader(String text, String encoding, byte... bom) throws IOException {
        checkXmlStreamReader(text, encoding, encoding, bom);
    }

    private static void checkXmlStreamReader(String text, String encoding, String effectiveEncoding, byte... bom)
            throws IOException {
        String xml = createXmlContent(text, encoding);
        checkXmlContent(xml, effectiveEncoding, bom);
    }

    /**
     * <p>testNoXmlHeader.</p>
     *
     * @throws java.io.IOException if any.
     */
    public void testNoXmlHeader() throws IOException {
        String xml = "<text>text with no XML header</text>";
        checkXmlContent(xml, "UTF-8");
        checkXmlContent(xml, "UTF-8", BOM_UTF8);
    }

    /**
     * <p>testDefaultEncoding.</p>
     *
     * @throws java.io.IOException if any.
     */
    public void testDefaultEncoding() throws IOException {
        checkXmlStreamReader(TEXT_UNICODE, null, "UTF-8");
        checkXmlStreamReader(TEXT_UNICODE, null, "UTF-8", BOM_UTF8);
    }

    /**
     * <p>testUTF8Encoding.</p>
     *
     * @throws java.io.IOException if any.
     */
    public void testUTF8Encoding() throws IOException {
        checkXmlStreamReader(TEXT_UNICODE, "UTF-8");
        checkXmlStreamReader(TEXT_UNICODE, "UTF-8", BOM_UTF8);
    }

    /**
     * <p>testUTF16Encoding.</p>
     *
     * @throws java.io.IOException if any.
     */
    public void testUTF16Encoding() throws IOException {
        checkXmlStreamReader(TEXT_UNICODE, "UTF-16", "UTF-16BE", null);
        checkXmlStreamReader(TEXT_UNICODE, "UTF-16", "UTF-16LE", BOM_UTF16LE);
        checkXmlStreamReader(TEXT_UNICODE, "UTF-16", "UTF-16BE", BOM_UTF16BE);
    }

    /**
     * <p>testUTF16BEEncoding.</p>
     *
     * @throws java.io.IOException if any.
     */
    public void testUTF16BEEncoding() throws IOException {
        checkXmlStreamReader(TEXT_UNICODE, "UTF-16BE");
    }

    /**
     * <p>testUTF16LEEncoding.</p>
     *
     * @throws java.io.IOException if any.
     */
    public void testUTF16LEEncoding() throws IOException {
        checkXmlStreamReader(TEXT_UNICODE, "UTF-16LE");
    }

    /**
     * <p>testLatin1Encoding.</p>
     *
     * @throws java.io.IOException if any.
     */
    public void testLatin1Encoding() throws IOException {
        checkXmlStreamReader(TEXT_LATIN1, "ISO-8859-1");
    }

    /**
     * <p>testLatin7Encoding.</p>
     *
     * @throws java.io.IOException if any.
     */
    public void testLatin7Encoding() throws IOException {
        checkXmlStreamReader(TEXT_LATIN7, "ISO-8859-7");
    }

    /**
     * <p>testLatin15Encoding.</p>
     *
     * @throws java.io.IOException if any.
     */
    public void testLatin15Encoding() throws IOException {
        checkXmlStreamReader(TEXT_LATIN15, "ISO-8859-15");
    }

    /**
     * <p>testEUC_JPEncoding.</p>
     *
     * @throws java.io.IOException if any.
     */
    public void testEUC_JPEncoding() throws IOException {
        checkXmlStreamReader(TEXT_EUC_JP, "EUC-JP");
    }

    /**
     * <p>testEBCDICEncoding.</p>
     *
     * @throws java.io.IOException if any.
     */
    public void testEBCDICEncoding() throws IOException {
        checkXmlStreamReader("simple text in EBCDIC", "CP1047");
    }

    /**
     * <p>testInappropriateEncoding.</p>
     *
     * @throws java.io.IOException if any.
     */
    public void testInappropriateEncoding() throws IOException {
        try {
            checkXmlStreamReader(TEXT_UNICODE, "ISO-8859-2");
            fail("Check should have failed, since some characters are not available in the specified encoding");
        } catch (ComparisonFailure cf) {
            // expected failure, since the encoding does not contain some characters
        }
    }

    /**
     * <p>testEncodingAttribute.</p>
     *
     * @throws java.io.IOException if any.
     */
    public void testEncodingAttribute() throws IOException {
        String xml = "<?xml version='1.0' encoding='US-ASCII'?><element encoding='attribute value'/>";
        checkXmlContent(xml, "US-ASCII");

        xml = "<?xml version='1.0' encoding  =  'US-ASCII'  ?><element encoding='attribute value'/>";
        checkXmlContent(xml, "US-ASCII");

        xml = "<?xml version='1.0'?><element encoding='attribute value'/>";
        checkXmlContent(xml, "UTF-8");

        xml = "<?xml\nversion='1.0'\nencoding\n=\n'US-ASCII'\n?>\n<element encoding='attribute value'/>";
        checkXmlContent(xml, "US-ASCII");

        xml = "<?xml\nversion='1.0'\n?>\n<element encoding='attribute value'/>";
        checkXmlContent(xml, "UTF-8");

        xml = "<element encoding='attribute value'/>";
        checkXmlContent(xml, "UTF-8");
    }
}
