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
import java.io.OutputStream;
import java.io.Writer;

import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.WriterFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * <p>XmlWriterUtilTest class.</p>
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id: $Id
 * @since 3.4.0
 */
class XmlWriterUtilTest {
    private OutputStream output;

    private Writer writer;

    private XMLWriter xmlWriter;

    /**
     * <p>setUp.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @BeforeEach
    void setUp() throws Exception {
        output = new ByteArrayOutputStream();
        writer = WriterFactory.newXmlWriter(output);
        xmlWriter = new PrettyPrintXMLWriter(writer);
    }

    /**
     * <p>tearDown.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @AfterEach
    void tearDown() throws Exception {
        xmlWriter = null;
        writer = null;
        output = null;
    }

    /**
     * Test method for
     * {@link org.codehaus.plexus.util.xml.XmlWriterUtil#writeLineBreak(org.codehaus.plexus.util.xml.XMLWriter)}.
     *
     * @throws java.lang.Exception if any
     */
    @Test
    void writeLineBreakXMLWriter() throws Exception {
        XmlWriterUtil.writeLineBreak(xmlWriter);
        writer.close();
        assertEquals(1, StringUtils.countMatches(output.toString(), XmlWriterUtil.LS));
    }

    /**
     * Test method for
     * {@link org.codehaus.plexus.util.xml.XmlWriterUtil#writeLineBreak(org.codehaus.plexus.util.xml.XMLWriter, int)}.
     *
     * @throws java.lang.Exception if any
     */
    @Test
    void writeLineBreakXMLWriterInt() throws Exception {
        XmlWriterUtil.writeLineBreak(xmlWriter, 10);
        writer.close();
        assertEquals(10, StringUtils.countMatches(output.toString(), XmlWriterUtil.LS));
    }

    /**
     * Test method for
     * {@link org.codehaus.plexus.util.xml.XmlWriterUtil#writeLineBreak(org.codehaus.plexus.util.xml.XMLWriter, int, int)}.
     *
     * @throws java.lang.Exception if any
     */
    @Test
    void writeLineBreakXMLWriterIntInt() throws Exception {
        XmlWriterUtil.writeLineBreak(xmlWriter, 10, 2);
        writer.close();
        assertEquals(10, StringUtils.countMatches(output.toString(), XmlWriterUtil.LS));
        assertEquals(
                1,
                StringUtils.countMatches(
                        output.toString(), StringUtils.repeat(" ", 2 * XmlWriterUtil.DEFAULT_INDENTATION_SIZE)));
    }

    /**
     * Test method for
     * {@link org.codehaus.plexus.util.xml.XmlWriterUtil#writeLineBreak(org.codehaus.plexus.util.xml.XMLWriter, int, int, int)}.
     *
     * @throws java.lang.Exception if any
     */
    @Test
    void writeLineBreakXMLWriterIntIntInt() throws Exception {
        XmlWriterUtil.writeLineBreak(xmlWriter, 10, 2, 4);
        writer.close();
        assertEquals(10, StringUtils.countMatches(output.toString(), XmlWriterUtil.LS));
        assertEquals(1, StringUtils.countMatches(output.toString(), StringUtils.repeat(" ", 2 * 4)));
    }

    /**
     * Test method for
     * {@link org.codehaus.plexus.util.xml.XmlWriterUtil#writeCommentLineBreak(org.codehaus.plexus.util.xml.XMLWriter)}.
     *
     * @throws java.lang.Exception if any
     */
    @Test
    void writeCommentLineBreakXMLWriter() throws Exception {
        XmlWriterUtil.writeCommentLineBreak(xmlWriter);
        writer.close();
        String sb =
                "<!-- ====================================================================== -->" + XmlWriterUtil.LS;
        assertEquals(output.toString(), sb);
        assertEquals(output.toString().length(), XmlWriterUtil.DEFAULT_COLUMN_LINE - 1 + XmlWriterUtil.LS.length());
    }

    /**
     * Test method for
     * {@link org.codehaus.plexus.util.xml.XmlWriterUtil#writeCommentLineBreak(org.codehaus.plexus.util.xml.XMLWriter, int)}.
     *
     * @throws java.lang.Exception if any
     */
    @Test
    void writeCommentLineBreakXMLWriterInt() throws Exception {
        XmlWriterUtil.writeCommentLineBreak(xmlWriter, 20);
        writer.close();
        assertEquals("<!-- ========== -->" + XmlWriterUtil.LS, output.toString());

        tearDown();
        setUp();

        XmlWriterUtil.writeCommentLineBreak(xmlWriter, 10);
        writer.close();
        assertEquals("<!--  -->" + XmlWriterUtil.LS, output.toString(), output.toString());
    }

    /**
     * Test method for
     * {@link org.codehaus.plexus.util.xml.XmlWriterUtil#writeComment(org.codehaus.plexus.util.xml.XMLWriter, java.lang.String)}.
     *
     * @throws java.lang.Exception if any
     */
    @Test
    void writeCommentXMLWriterString() throws Exception {
        XmlWriterUtil.writeComment(xmlWriter, "hello");
        writer.close();
        StringBuilder sb = new StringBuilder();
        sb.append("<!-- hello                                                                  -->")
                .append(XmlWriterUtil.LS);
        assertEquals(output.toString(), sb.toString());
        assertEquals(output.toString().length(), XmlWriterUtil.DEFAULT_COLUMN_LINE - 1 + XmlWriterUtil.LS.length());

        tearDown();
        setUp();

        XmlWriterUtil.writeComment(
                xmlWriter, "hellooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo");
        writer.close();
        sb = new StringBuilder();
        sb.append("<!-- hellooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo -->")
                .append(XmlWriterUtil.LS);
        assertEquals(output.toString(), sb.toString());
        assertTrue(output.toString().length() >= XmlWriterUtil.DEFAULT_COLUMN_LINE);

        tearDown();
        setUp();

        XmlWriterUtil.writeComment(xmlWriter, "hello\nworld");
        writer.close();
        sb = new StringBuilder();
        sb.append("<!-- hello                                                                  -->")
                .append(XmlWriterUtil.LS);
        sb.append("<!-- world                                                                  -->")
                .append(XmlWriterUtil.LS);
        assertEquals(output.toString(), sb.toString());
        assertEquals(
                output.toString().length(), 2 * (XmlWriterUtil.DEFAULT_COLUMN_LINE - 1 + XmlWriterUtil.LS.length()));
    }

    /**
     * Test method for
     * {@link org.codehaus.plexus.util.xml.XmlWriterUtil#writeComment(org.codehaus.plexus.util.xml.XMLWriter, java.lang.String, int)}.
     *
     * @throws java.lang.Exception if any
     */
    @Test
    void writeCommentXMLWriterStringInt() throws Exception {
        String indent = StringUtils.repeat(" ", 2 * XmlWriterUtil.DEFAULT_INDENTATION_SIZE);

        XmlWriterUtil.writeComment(xmlWriter, "hello", 2);
        writer.close();
        StringBuilder sb = new StringBuilder();
        sb.append(indent);
        sb.append("<!-- hello                                                                  -->")
                .append(XmlWriterUtil.LS);
        assertEquals(output.toString(), sb.toString());
        assertEquals(
                output.toString().length(),
                XmlWriterUtil.DEFAULT_COLUMN_LINE
                        - 1
                        + XmlWriterUtil.LS.length()
                        + 2 * XmlWriterUtil.DEFAULT_INDENTATION_SIZE);

        tearDown();
        setUp();

        XmlWriterUtil.writeComment(xmlWriter, "hello\nworld", 2);
        writer.close();
        sb = new StringBuilder();
        sb.append(indent);
        sb.append("<!-- hello                                                                  -->")
                .append(XmlWriterUtil.LS);
        sb.append(indent);
        sb.append("<!-- world                                                                  -->")
                .append(XmlWriterUtil.LS);
        assertEquals(output.toString(), sb.toString());
        assertEquals(
                output.toString().length(),
                2 * (XmlWriterUtil.DEFAULT_COLUMN_LINE - 1 + XmlWriterUtil.LS.length()) + 2 * indent.length());
    }

    /**
     * Test method for
     * {@link org.codehaus.plexus.util.xml.XmlWriterUtil#writeComment(org.codehaus.plexus.util.xml.XMLWriter, java.lang.String, int, int)}.
     *
     * @throws java.lang.Exception if any
     */
    @Test
    void writeCommentXMLWriterStringIntInt() throws Exception {
        String repeat = StringUtils.repeat(" ", 2 * 4);

        XmlWriterUtil.writeComment(xmlWriter, "hello", 2, 4);
        writer.close();
        StringBuilder sb = new StringBuilder();
        sb.append(repeat);
        sb.append("<!-- hello                                                                  -->")
                .append(XmlWriterUtil.LS);
        assertEquals(output.toString(), sb.toString());
        assertEquals(
                output.toString().length(), XmlWriterUtil.DEFAULT_COLUMN_LINE - 1 + XmlWriterUtil.LS.length() + 2 * 4);

        tearDown();
        setUp();

        XmlWriterUtil.writeComment(xmlWriter, "hello\nworld", 2, 4);
        writer.close();
        sb = new StringBuilder();
        sb.append(repeat);
        sb.append("<!-- hello                                                                  -->")
                .append(XmlWriterUtil.LS);
        sb.append(repeat);
        sb.append("<!-- world                                                                  -->")
                .append(XmlWriterUtil.LS);
        assertEquals(output.toString(), sb.toString());
        assertEquals(
                output.toString().length(),
                2 * (XmlWriterUtil.DEFAULT_COLUMN_LINE - 1 + XmlWriterUtil.LS.length()) + 2 * repeat.length());
    }

    /**
     * Test method for
     * {@link org.codehaus.plexus.util.xml.XmlWriterUtil#writeComment(org.codehaus.plexus.util.xml.XMLWriter, java.lang.String, int, int, int)}.
     *
     * @throws java.lang.Exception if any
     */
    @Test
    void writeCommentXMLWriterStringIntIntInt() throws Exception {
        String indent = StringUtils.repeat(" ", 2 * 4);

        XmlWriterUtil.writeComment(xmlWriter, "hello", 2, 4, 50);
        writer.close();
        StringBuilder sb = new StringBuilder();
        sb.append(indent);
        sb.append("<!-- hello                                    -->").append(XmlWriterUtil.LS);
        assertEquals(output.toString(), sb.toString());
        assertEquals(output.toString().length(), 50 - 1 + XmlWriterUtil.LS.length() + 2 * 4);

        tearDown();
        setUp();

        XmlWriterUtil.writeComment(xmlWriter, "hello", 2, 4, 10);
        writer.close();
        sb = new StringBuilder();
        sb.append(indent);
        sb.append("<!-- hello -->").append(XmlWriterUtil.LS);
        assertEquals(output.toString(), sb.toString());
        assertTrue(output.toString().length() >= 10 + 2 * 4);
    }

    /**
     * Test method for
     * {@link org.codehaus.plexus.util.xml.XmlWriterUtil#writeCommentText(org.codehaus.plexus.util.xml.XMLWriter, java.lang.String, int)}.
     *
     * @throws java.lang.Exception if any
     */
    @Test
    void writeCommentTextXMLWriterStringInt() throws Exception {
        XmlWriterUtil.writeCommentText(xmlWriter, "hello", 0);
        writer.close();
        StringBuilder sb = new StringBuilder();
        sb.append(XmlWriterUtil.LS);
        sb.append("<!-- ====================================================================== -->")
                .append(XmlWriterUtil.LS);
        sb.append("<!-- hello                                                                  -->")
                .append(XmlWriterUtil.LS);
        sb.append("<!-- ====================================================================== -->")
                .append(XmlWriterUtil.LS);
        sb.append(XmlWriterUtil.LS);
        assertEquals(output.toString(), sb.toString());
        assertEquals(
                output.toString().length(), 3 * (80 - 1 + XmlWriterUtil.LS.length()) + 2 * XmlWriterUtil.LS.length());

        tearDown();
        setUp();

        String indent = StringUtils.repeat(" ", 2 * 2);

        XmlWriterUtil.writeCommentText(
                xmlWriter,
                "hello world with end of line\n and "
                        + "loooooooooooooooooooooooooooooooooooooooooooooooooooooonnnnnnnnnnong line",
                2);
        writer.close();
        sb = new StringBuilder();
        sb.append(XmlWriterUtil.LS);
        sb.append(indent)
                .append("<!-- ====================================================================== -->")
                .append(XmlWriterUtil.LS);
        sb.append(indent)
                .append("<!-- hello world with end of line                                           -->")
                .append(XmlWriterUtil.LS);
        sb.append(indent)
                .append("<!-- and                                                                    -->")
                .append(XmlWriterUtil.LS);
        sb.append(indent)
                .append("<!-- loooooooooooooooooooooooooooooooooooooooooooooooooooooonnnnnnnnnnong   -->")
                .append(XmlWriterUtil.LS);
        sb.append(indent)
                .append("<!-- line                                                                   -->")
                .append(XmlWriterUtil.LS);
        sb.append(indent)
                .append("<!-- ====================================================================== -->")
                .append(XmlWriterUtil.LS);
        sb.append(XmlWriterUtil.LS);
        sb.append(indent);
        assertEquals(output.toString(), sb.toString());
    }

    /**
     * Test method for
     * {@link org.codehaus.plexus.util.xml.XmlWriterUtil#writeCommentText(org.codehaus.plexus.util.xml.XMLWriter, java.lang.String, int, int)}.
     *
     * @throws java.lang.Exception if any
     */
    @Test
    void writeCommentTextXMLWriterStringIntInt() throws Exception {
        String indent = StringUtils.repeat(" ", 2 * 4);

        XmlWriterUtil.writeCommentText(xmlWriter, "hello", 2, 4);
        writer.close();
        String sb = XmlWriterUtil.LS + indent
                + "<!-- ====================================================================== -->"
                + XmlWriterUtil.LS
                + indent
                + "<!-- hello                                                                  -->"
                + XmlWriterUtil.LS
                + indent
                + "<!-- ====================================================================== -->"
                + XmlWriterUtil.LS
                + XmlWriterUtil.LS
                + indent;
        assertEquals(output.toString(), sb);
        assertEquals(
                output.toString().length(),
                3 * (80 - 1 + XmlWriterUtil.LS.length()) + 4 * 2 * 4 + 2 * XmlWriterUtil.LS.length());
    }

    /**
     * Test method for
     * {@link org.codehaus.plexus.util.xml.XmlWriterUtil#writeCommentText(org.codehaus.plexus.util.xml.XMLWriter, java.lang.String, int, int, int)}.
     *
     * @throws java.lang.Exception if any
     */
    @Test
    void writeCommentTextXMLWriterStringIntIntInt() throws Exception {
        String indent = StringUtils.repeat(" ", 2 * 4);

        XmlWriterUtil.writeCommentText(xmlWriter, "hello", 2, 4, 50);
        writer.close();
        String sb = XmlWriterUtil.LS + indent
                + "<!-- ======================================== -->"
                + XmlWriterUtil.LS
                + indent
                + "<!-- hello                                    -->"
                + XmlWriterUtil.LS
                + indent
                + "<!-- ======================================== -->"
                + XmlWriterUtil.LS
                + XmlWriterUtil.LS
                + indent;
        assertEquals(output.toString(), sb);
        assertEquals(
                output.toString().length(),
                3 * (50 - 1 + XmlWriterUtil.LS.length()) + 4 * 2 * 4 + 2 * XmlWriterUtil.LS.length());
    }

    /**
     * Test method for
     * {@link org.codehaus.plexus.util.xml.XmlWriterUtil#writeComment(org.codehaus.plexus.util.xml.XMLWriter, java.lang.String)}.
     *
     * @throws java.lang.Exception if any
     */
    @Test
    void writeCommentNull() throws Exception {
        XmlWriterUtil.writeComment(xmlWriter, null);
        writer.close();
        String sb =
                "<!-- null                                                                   -->" + XmlWriterUtil.LS;
        assertEquals(output.toString(), sb);
    }

    /**
     * Test method for
     * {@link org.codehaus.plexus.util.xml.XmlWriterUtil#writeComment(org.codehaus.plexus.util.xml.XMLWriter, java.lang.String)}.
     *
     * @throws java.lang.Exception if any
     */
    @Test
    void writeCommentShort() throws Exception {
        XmlWriterUtil.writeComment(xmlWriter, "This is a short text");
        writer.close();
        String sb =
                "<!-- This is a short text                                                   -->" + XmlWriterUtil.LS;
        assertEquals(output.toString(), sb);
    }

    /**
     * Test method for
     * {@link org.codehaus.plexus.util.xml.XmlWriterUtil#writeComment(org.codehaus.plexus.util.xml.XMLWriter, java.lang.String)}.
     *
     * @throws java.lang.Exception if any
     */
    @Test
    void writeCommentLong() throws Exception {
        XmlWriterUtil.writeComment(
                xmlWriter,
                "Maven is a software project management and comprehension tool. "
                        + "Based on the concept of a project object model (POM), Maven can manage a project's build, reporting "
                        + "and documentation from a central piece of information.");
        writer.close();
        String sb = "<!-- Maven is a software project management and comprehension tool. Based   -->" + XmlWriterUtil.LS
                + "<!-- on the concept of a project object model (POM), Maven can manage a     -->"
                + XmlWriterUtil.LS
                + "<!-- project's build, reporting and documentation from a central piece of   -->"
                + XmlWriterUtil.LS
                + "<!-- information.                                                           -->"
                + XmlWriterUtil.LS;
        assertEquals(output.toString(), sb);
    }
}
