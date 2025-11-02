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
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * <p>XmlStreamWriterTest class.</p>
 *
 * @author herve
 * @since 3.4.0
 */
class XmlStreamWriterTest {
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

    private static String createXmlContent(String text, String encoding) {
        String xmlDecl = "<?xml version=\"1.0\"?>";
        if (encoding != null) {
            xmlDecl = "<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>";
        }
        String xml = xmlDecl + "\n<text>" + text + "</text>";
        return xml;
    }

    private static void checkXmlContent(String xml, String encoding) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XmlStreamWriter writer = new XmlStreamWriter(out);
        writer.write(xml);
        writer.close();
        byte[] xmlContent = out.toByteArray();
        String result = new String(xmlContent, encoding);
        assertEquals(xml, result);
    }

    private static void checkXmlWriter(String text, String encoding) throws IOException {
        String xml = createXmlContent(text, encoding);
        String effectiveEncoding = (encoding == null) ? "UTF-8" : encoding;
        checkXmlContent(xml, effectiveEncoding);
    }

    @Test
    void noXmlHeader() throws IOException {
        String xml = "<text>text with no XML header</text>";
        checkXmlContent(xml, "UTF-8");
    }

    @Test
    void empty() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XmlStreamWriter writer = new XmlStreamWriter(out);
        writer.flush();
        writer.write("");
        writer.flush();
        writer.write(".");
        writer.flush();
        writer.close();
    }

    @Test
    void defaultEncoding() throws IOException {
        checkXmlWriter(TEXT_UNICODE, null);
    }

    @Test
    void utf8Encoding() throws IOException {
        checkXmlWriter(TEXT_UNICODE, "UTF-8");
    }

    @Test
    void utf16Encoding() throws IOException {
        checkXmlWriter(TEXT_UNICODE, "UTF-16");
    }

    @Test
    void utf16beEncoding() throws IOException {
        checkXmlWriter(TEXT_UNICODE, "UTF-16BE");
    }

    @Test
    void utf16leEncoding() throws IOException {
        checkXmlWriter(TEXT_UNICODE, "UTF-16LE");
    }

    @Test
    void latin1Encoding() throws IOException {
        checkXmlWriter(TEXT_LATIN1, "ISO-8859-1");
    }

    @Test
    void latin7Encoding() throws IOException {
        checkXmlWriter(TEXT_LATIN7, "ISO-8859-7");
    }

    @Test
    void latin15Encoding() throws IOException {
        checkXmlWriter(TEXT_LATIN15, "ISO-8859-15");
    }

    @Test
    void euc_jpEncoding() throws IOException {
        checkXmlWriter(TEXT_EUC_JP, "EUC-JP");
    }

    @Test
    void ebcdicEncoding() throws IOException {
        checkXmlWriter("simple text in EBCDIC", "CP1047");
    }
}
