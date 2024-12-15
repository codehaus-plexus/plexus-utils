package org.codehaus.plexus.util.xml.pull;

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

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.ReaderFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * <p>MXParserTest class.</p>
 *
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id: $Id
 * @since 3.4.0
 */
class MXParserTest {
    /**
     * <p>testHexadecimalEntities.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void hexadecimalEntities() throws Exception {
        MXParser parser = new MXParser();

        parser.defineEntityReplacementText("test", "replacement");

        String input = "<root>&#x41;</root>";

        parser.setInput(new StringReader(input));

        assertEquals(XmlPullParser.START_TAG, parser.next());

        assertEquals(XmlPullParser.TEXT, parser.next());

        assertEquals("A", parser.getText());

        assertEquals(XmlPullParser.END_TAG, parser.next());
    }

    /**
     * <p>testDecimalEntities.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void decimalEntities() throws Exception {
        MXParser parser = new MXParser();

        parser.defineEntityReplacementText("test", "replacement");

        String input = "<root>&#65;</root>";

        parser.setInput(new StringReader(input));

        assertEquals(XmlPullParser.START_TAG, parser.next());

        assertEquals(XmlPullParser.TEXT, parser.next());

        assertEquals("A", parser.getText());

        assertEquals(XmlPullParser.END_TAG, parser.next());
    }

    /**
     * <p>testPredefinedEntities.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void predefinedEntities() throws Exception {
        MXParser parser = new MXParser();

        parser.defineEntityReplacementText("test", "replacement");

        String input = "<root>&lt;&gt;&amp;&apos;&quot;</root>";

        parser.setInput(new StringReader(input));

        assertEquals(XmlPullParser.START_TAG, parser.next());

        assertEquals(XmlPullParser.TEXT, parser.next());

        assertEquals("<>&'\"", parser.getText());

        assertEquals(XmlPullParser.END_TAG, parser.next());
    }

    /**
     * <p>testEntityReplacementMap.</p>
     *
     * @throws org.codehaus.plexus.util.xml.pull.XmlPullParserException if any.
     * @throws java.io.IOException if any.
     */
    @Test
    void entityReplacementMap() throws XmlPullParserException, IOException {
        EntityReplacementMap erm = new EntityReplacementMap(new String[][] {{"abc", "CDE"}, {"EFG", "HIJ"}});
        MXParser parser = new MXParser(erm);

        String input = "<root>&EFG;</root>";
        parser.setInput(new StringReader(input));

        assertEquals(XmlPullParser.START_TAG, parser.next());
        assertEquals(XmlPullParser.TEXT, parser.next());
        assertEquals("HIJ", parser.getText());
        assertEquals(XmlPullParser.END_TAG, parser.next());
    }

    /**
     * <p>testCustomEntities.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void customEntities() throws Exception {
        MXParser parser = new MXParser();

        String input = "<root>&myentity;</root>";
        parser.setInput(new StringReader(input));
        parser.defineEntityReplacementText("myentity", "replacement");
        assertEquals(XmlPullParser.START_TAG, parser.next());
        assertEquals(XmlPullParser.TEXT, parser.next());
        assertEquals("replacement", parser.getText());
        assertEquals(XmlPullParser.END_TAG, parser.next());

        parser = new MXParser();
        input = "<root>&myCustom;</root>";
        parser.setInput(new StringReader(input));
        parser.defineEntityReplacementText("fo", "&#65;");
        parser.defineEntityReplacementText("myCustom", "&fo;");
        assertEquals(XmlPullParser.START_TAG, parser.next());
        assertEquals(XmlPullParser.TEXT, parser.next());
        assertEquals("&#65;", parser.getText());
        assertEquals(XmlPullParser.END_TAG, parser.next());
    }

    /**
     * <p>testUnicodeEntities.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void unicodeEntities() throws Exception {
        MXParser parser = new MXParser();
        String input = "<root>&#x1d7ed;</root>";
        parser.setInput(new StringReader(input));

        assertEquals(XmlPullParser.START_TAG, parser.nextToken());
        assertEquals(XmlPullParser.ENTITY_REF, parser.nextToken());
        assertEquals("\uD835\uDFED", parser.getText());
        assertEquals(XmlPullParser.END_TAG, parser.nextToken());

        parser = new MXParser();
        input = "<root>&#x159;</root>";
        parser.setInput(new StringReader(input));

        assertEquals(XmlPullParser.START_TAG, parser.nextToken());
        assertEquals(XmlPullParser.ENTITY_REF, parser.nextToken());
        assertEquals("\u0159", parser.getText());
        assertEquals(XmlPullParser.END_TAG, parser.nextToken());
    }

    /**
     * <p>testInvalidCharacterReferenceHexa.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void invalidCharacterReferenceHexa() throws Exception {
        MXParser parser = new MXParser();
        String input = "<root>&#x110000;</root>";
        parser.setInput(new StringReader(input));

        try {
            assertEquals(XmlPullParser.START_TAG, parser.nextToken());
            assertEquals(XmlPullParser.ENTITY_REF, parser.nextToken());
            fail("Should fail since &#x110000; is an illegal character reference");
        } catch (XmlPullParserException e) {
            assertTrue(e.getMessage().contains("character reference (with hex value 110000) is invalid"));
        }
    }

    /**
     * <p>testValidCharacterReferenceHexa.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void validCharacterReferenceHexa() throws Exception {
        MXParser parser = new MXParser();
        String input =
                "<root>&#x9;&#xA;&#xD;&#x20;&#x200;&#xD7FF;&#xE000;&#xFFA2;&#xFFFD;&#x10000;&#x10FFFD;&#x10FFFF;</root>";
        parser.setInput(new StringReader(input));

        Assertions.assertDoesNotThrow(
                () -> {
                    assertEquals(XmlPullParser.START_TAG, parser.nextToken());
                    assertEquals(XmlPullParser.ENTITY_REF, parser.nextToken());
                    assertEquals(0x9, parser.getText().codePointAt(0));
                    assertEquals(XmlPullParser.ENTITY_REF, parser.nextToken());
                    assertEquals(0xA, parser.getText().codePointAt(0));
                    assertEquals(XmlPullParser.ENTITY_REF, parser.nextToken());
                    assertEquals(0xD, parser.getText().codePointAt(0));
                    assertEquals(XmlPullParser.ENTITY_REF, parser.nextToken());
                    assertEquals(0x20, parser.getText().codePointAt(0));
                    assertEquals(XmlPullParser.ENTITY_REF, parser.nextToken());
                    assertEquals(0x200, parser.getText().codePointAt(0));
                    assertEquals(XmlPullParser.ENTITY_REF, parser.nextToken());
                    assertEquals(0xD7FF, parser.getText().codePointAt(0));
                    assertEquals(XmlPullParser.ENTITY_REF, parser.nextToken());
                    assertEquals(0xE000, parser.getText().codePointAt(0));
                    assertEquals(XmlPullParser.ENTITY_REF, parser.nextToken());
                    assertEquals(0xFFA2, parser.getText().codePointAt(0));
                    assertEquals(XmlPullParser.ENTITY_REF, parser.nextToken());
                    assertEquals(0xFFFD, parser.getText().codePointAt(0));
                    assertEquals(XmlPullParser.ENTITY_REF, parser.nextToken());
                    assertEquals(0x10000, parser.getText().codePointAt(0));
                    assertEquals(XmlPullParser.ENTITY_REF, parser.nextToken());
                    assertEquals(0x10FFFD, parser.getText().codePointAt(0));
                    assertEquals(XmlPullParser.ENTITY_REF, parser.nextToken());
                    assertEquals(0x10FFFF, parser.getText().codePointAt(0));
                    assertEquals(XmlPullParser.END_TAG, parser.nextToken());
                },
                "Should success since the input represents all legal character references");
    }

    /**
     * <p>testInvalidCharacterReferenceDecimal.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void invalidCharacterReferenceDecimal() throws Exception {
        MXParser parser = new MXParser();
        String input = "<root>&#1114112;</root>";
        parser.setInput(new StringReader(input));

        try {
            assertEquals(XmlPullParser.START_TAG, parser.nextToken());
            assertEquals(XmlPullParser.ENTITY_REF, parser.nextToken());
            fail("Should fail since &#1114112; is an illegal character reference");
        } catch (XmlPullParserException e) {
            assertTrue(e.getMessage().contains("character reference (with decimal value 1114112) is invalid"));
        }
    }

    /**
     * <p>testValidCharacterReferenceDecimal.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void validCharacterReferenceDecimal() throws Exception {
        MXParser parser = new MXParser();
        String input =
                "<root>&#9;&#10;&#13;&#32;&#512;&#55295;&#57344;&#65442;&#65533;&#65536;&#1114109;&#1114111;</root>";
        parser.setInput(new StringReader(input));

        Assertions.assertDoesNotThrow(
                () -> {
                    assertEquals(XmlPullParser.START_TAG, parser.nextToken());
                    assertEquals(XmlPullParser.ENTITY_REF, parser.nextToken());
                    assertEquals(9, parser.getText().codePointAt(0));
                    assertEquals(XmlPullParser.ENTITY_REF, parser.nextToken());
                    assertEquals(10, parser.getText().codePointAt(0));
                    assertEquals(XmlPullParser.ENTITY_REF, parser.nextToken());
                    assertEquals(13, parser.getText().codePointAt(0));
                    assertEquals(XmlPullParser.ENTITY_REF, parser.nextToken());
                    assertEquals(32, parser.getText().codePointAt(0));
                    assertEquals(XmlPullParser.ENTITY_REF, parser.nextToken());
                    assertEquals(512, parser.getText().codePointAt(0));
                    assertEquals(XmlPullParser.ENTITY_REF, parser.nextToken());
                    assertEquals(55295, parser.getText().codePointAt(0));
                    assertEquals(XmlPullParser.ENTITY_REF, parser.nextToken());
                    assertEquals(57344, parser.getText().codePointAt(0));
                    assertEquals(XmlPullParser.ENTITY_REF, parser.nextToken());
                    assertEquals(65442, parser.getText().codePointAt(0));
                    assertEquals(XmlPullParser.ENTITY_REF, parser.nextToken());
                    assertEquals(65533, parser.getText().codePointAt(0));
                    assertEquals(XmlPullParser.ENTITY_REF, parser.nextToken());
                    assertEquals(65536, parser.getText().codePointAt(0));
                    assertEquals(XmlPullParser.ENTITY_REF, parser.nextToken());
                    assertEquals(1114109, parser.getText().codePointAt(0));
                    assertEquals(XmlPullParser.ENTITY_REF, parser.nextToken());
                    assertEquals(1114111, parser.getText().codePointAt(0));
                    assertEquals(XmlPullParser.END_TAG, parser.nextToken());
                },
                "Should success since the input represents all legal character references");
    }

    /**
     * <p>testProcessingInstruction.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void parserPosition() throws Exception {
        String input =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!-- A --> \n <!-- B --><test>\tnnn</test>\n<!-- C\nC -->";

        MXParser parser = new MXParser();
        parser.setInput(new StringReader(input));

        assertEquals(XmlPullParser.PROCESSING_INSTRUCTION, parser.nextToken());
        assertPosition(1, 39, parser);
        assertEquals(XmlPullParser.COMMENT, parser.nextToken());
        assertPosition(1, 49, parser);
        assertEquals(XmlPullParser.IGNORABLE_WHITESPACE, parser.nextToken());
        assertPosition(2, 3, parser); // end when next token starts
        assertEquals(XmlPullParser.COMMENT, parser.nextToken());
        assertPosition(2, 12, parser);
        assertEquals(XmlPullParser.START_TAG, parser.nextToken());
        assertPosition(2, 18, parser);
        assertEquals(XmlPullParser.TEXT, parser.nextToken());
        assertPosition(2, 23, parser); // end when next token starts
        assertEquals(XmlPullParser.END_TAG, parser.nextToken());
        assertPosition(2, 29, parser);
        assertEquals(XmlPullParser.IGNORABLE_WHITESPACE, parser.nextToken());
        assertPosition(3, 2, parser); // end when next token starts
        assertEquals(XmlPullParser.COMMENT, parser.nextToken());
        assertPosition(4, 6, parser);
    }

    @Test
    void processingInstruction() throws Exception {
        String input = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><test>nnn</test>";

        MXParser parser = new MXParser();
        parser.setInput(new StringReader(input));

        assertEquals(XmlPullParser.PROCESSING_INSTRUCTION, parser.nextToken());
        assertEquals(XmlPullParser.START_TAG, parser.nextToken());
        assertEquals(XmlPullParser.TEXT, parser.nextToken());
        assertEquals(XmlPullParser.END_TAG, parser.nextToken());
    }

    /**
     * <p>testProcessingInstructionsContainingXml.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void processingInstructionsContainingXml() throws Exception {
        StringBuffer sb = new StringBuffer();

        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<project>\n");
        sb.append(" <?pi\n");
        sb.append("   <tag>\n");
        sb.append("   </tag>\n");
        sb.append(" ?>\n");
        sb.append("</project>");

        MXParser parser = new MXParser();
        parser.setInput(new StringReader(sb.toString()));

        assertEquals(XmlPullParser.PROCESSING_INSTRUCTION, parser.nextToken());
        assertEquals(XmlPullParser.START_TAG, parser.nextToken());
        assertEquals(XmlPullParser.TEXT, parser.nextToken()); // whitespace
        assertEquals(XmlPullParser.PROCESSING_INSTRUCTION, parser.nextToken());
        assertEquals(XmlPullParser.TEXT, parser.nextToken()); // whitespace
        assertEquals(XmlPullParser.END_TAG, parser.nextToken());
    }

    /**
     * <p>testSubsequentProcessingInstructionShort.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void malformedProcessingInstructionsContainingXmlNoClosingQuestionMark() throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<project />\n");
        sb.append("<?pi\n");
        sb.append("   <tag>\n");
        sb.append("   </tag>>\n");

        MXParser parser = new MXParser();
        parser.setInput(new StringReader(sb.toString()));

        try {
            assertEquals(XmlPullParser.PROCESSING_INSTRUCTION, parser.nextToken());
            assertEquals(XmlPullParser.IGNORABLE_WHITESPACE, parser.nextToken());
            assertEquals(XmlPullParser.START_TAG, parser.nextToken());
            assertEquals(XmlPullParser.END_TAG, parser.nextToken());
            assertEquals(XmlPullParser.IGNORABLE_WHITESPACE, parser.nextToken());
            assertEquals(XmlPullParser.PROCESSING_INSTRUCTION, parser.nextToken());

            fail("Should fail since it has invalid PI");
        } catch (XmlPullParserException ex) {
            assertTrue(
                    ex.getMessage().contains("processing instruction started on line 3 and column 1 was not closed"));
        }
    }

    @Test
    void subsequentProcessingInstructionShort() throws Exception {
        StringBuffer sb = new StringBuffer();

        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<project>");
        sb.append("<!-- comment -->");
        sb.append("<?m2e ignore?>");
        sb.append("</project>");

        MXParser parser = new MXParser();
        parser.setInput(new StringReader(sb.toString()));

        assertEquals(XmlPullParser.PROCESSING_INSTRUCTION, parser.nextToken());
        assertEquals(XmlPullParser.START_TAG, parser.nextToken());
        assertEquals(XmlPullParser.COMMENT, parser.nextToken());
        assertEquals(XmlPullParser.PROCESSING_INSTRUCTION, parser.nextToken());
        assertEquals(XmlPullParser.END_TAG, parser.nextToken());
    }

    /**
     * <p>testSubsequentProcessingInstructionMoreThan8k.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void subsequentProcessingInstructionMoreThan8k() throws Exception {
        StringBuffer sb = new StringBuffer();

        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<project>");

        // add ten times 1000 chars as comment
        for (int j = 0; j < 10; j++) {

            sb.append("<!-- ");
            for (int i = 0; i < 2000; i++) {
                sb.append("ten bytes ");
            }
            sb.append(" -->");
        }

        sb.append("<?m2e ignore?>");
        sb.append("</project>");

        MXParser parser = new MXParser();
        parser.setInput(new StringReader(sb.toString()));

        assertEquals(XmlPullParser.PROCESSING_INSTRUCTION, parser.nextToken());
        assertEquals(XmlPullParser.START_TAG, parser.nextToken());
        assertEquals(XmlPullParser.COMMENT, parser.nextToken());
        assertEquals(XmlPullParser.COMMENT, parser.nextToken());
        assertEquals(XmlPullParser.COMMENT, parser.nextToken());
        assertEquals(XmlPullParser.COMMENT, parser.nextToken());
        assertEquals(XmlPullParser.COMMENT, parser.nextToken());
        assertEquals(XmlPullParser.COMMENT, parser.nextToken());
        assertEquals(XmlPullParser.COMMENT, parser.nextToken());
        assertEquals(XmlPullParser.COMMENT, parser.nextToken());
        assertEquals(XmlPullParser.COMMENT, parser.nextToken());
        assertEquals(XmlPullParser.COMMENT, parser.nextToken());
        assertEquals(XmlPullParser.PROCESSING_INSTRUCTION, parser.nextToken());
        assertEquals(XmlPullParser.END_TAG, parser.nextToken());
    }

    /**
     * <p>testLargeText_NoOverflow.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void largeTextNoOverflow() throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<largetextblock>");
        // Anything above 33,554,431 would fail without a fix for
        // https://web.archive.org/web/20070831191548/http://www.extreme.indiana.edu/bugzilla/show_bug.cgi?id=228
        // with java.io.IOException: error reading input, returned 0
        sb.append(new String(new char[33554432]));
        sb.append("</largetextblock>");

        MXParser parser = new MXParser();
        parser.setInput(new StringReader(sb.toString()));

        assertEquals(XmlPullParser.PROCESSING_INSTRUCTION, parser.nextToken());
        assertEquals(XmlPullParser.START_TAG, parser.nextToken());
        assertEquals(XmlPullParser.TEXT, parser.nextToken());
        assertEquals(XmlPullParser.END_TAG, parser.nextToken());
    }

    /**
     * <p>testMalformedProcessingInstructionAfterTag.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void malformedProcessingInstructionAfterTag() throws Exception {
        MXParser parser = new MXParser();

        String input = "<project /><?>";

        parser.setInput(new StringReader(input));

        try {
            assertEquals(XmlPullParser.START_TAG, parser.next());

            assertEquals(XmlPullParser.END_TAG, parser.next());

            assertEquals(XmlPullParser.PROCESSING_INSTRUCTION, parser.next());

            fail("Should fail since it has an invalid Processing Instruction");
        } catch (XmlPullParserException ex) {
            assertTrue(ex.getMessage().contains("processing instruction PITarget name not found"));
        }
    }

    /**
     * <p>testMalformedProcessingInstructionBeforeTag.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void malformedProcessingInstructionBeforeTag() throws Exception {
        MXParser parser = new MXParser();

        String input = "<?><project />";

        parser.setInput(new StringReader(input));

        try {
            assertEquals(XmlPullParser.PROCESSING_INSTRUCTION, parser.next());

            assertEquals(XmlPullParser.START_TAG, parser.next());

            assertEquals(XmlPullParser.END_TAG, parser.next());

            fail("Should fail since it has invalid PI");
        } catch (XmlPullParserException ex) {
            assertTrue(ex.getMessage().contains("processing instruction PITarget name not found"));
        }
    }

    /**
     * <p>testMalformedProcessingInstructionSpaceBeforeName.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void malformedProcessingInstructionSpaceBeforeName() throws Exception {
        MXParser parser = new MXParser();

        StringBuilder sb = new StringBuilder();
        sb.append("<? shouldhavenospace>");
        sb.append("<project />");

        parser.setInput(new StringReader(sb.toString()));

        try {
            assertEquals(XmlPullParser.PROCESSING_INSTRUCTION, parser.next());

            assertEquals(XmlPullParser.START_TAG, parser.next());

            assertEquals(XmlPullParser.END_TAG, parser.next());

            fail("Should fail since it has invalid PI");
        } catch (XmlPullParserException ex) {
            assertTrue(ex.getMessage()
                    .contains(
                            "processing instruction PITarget must be exactly after <? and not white space character"));
        }
    }

    /**
     * <p>testMalformedProcessingInstructionNoClosingQuestionMark.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void malformedProcessingInstructionNoClosingQuestionMark() throws Exception {
        MXParser parser = new MXParser();

        StringBuilder sb = new StringBuilder();
        sb.append("<?shouldhavenospace>");
        sb.append("<project />");

        parser.setInput(new StringReader(sb.toString()));

        try {
            assertEquals(XmlPullParser.PROCESSING_INSTRUCTION, parser.next());

            assertEquals(XmlPullParser.START_TAG, parser.next());

            assertEquals(XmlPullParser.END_TAG, parser.next());

            fail("Should fail since it has invalid PI");
        } catch (XmlPullParserException ex) {
            assertTrue(
                    ex.getMessage().contains("processing instruction started on line 1 and column 1 was not closed"));
        }
    }

    /**
     * <p>testSubsequentMalformedProcessingInstructionNoClosingQuestionMark.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void subsequentMalformedProcessingInstructionNoClosingQuestionMark() throws Exception {
        MXParser parser = new MXParser();

        StringBuilder sb = new StringBuilder();
        sb.append("<project />");
        sb.append("<?shouldhavenospace>");

        parser.setInput(new StringReader(sb.toString()));

        try {
            assertEquals(XmlPullParser.START_TAG, parser.next());

            assertEquals(XmlPullParser.END_TAG, parser.next());

            assertEquals(XmlPullParser.PROCESSING_INSTRUCTION, parser.next());

            fail("Should fail since it has invalid PI");
        } catch (XmlPullParserException ex) {
            assertTrue(
                    ex.getMessage().contains("processing instruction started on line 1 and column 12 was not closed"));
        }
    }

    /**
     * <p>testMalformedXMLRootElement.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void subsequentAbortedProcessingInstruction() throws Exception {
        MXParser parser = new MXParser();
        StringBuilder sb = new StringBuilder();
        sb.append("<project />");
        sb.append("<?aborted");

        parser.setInput(new StringReader(sb.toString()));

        try {
            assertEquals(XmlPullParser.START_TAG, parser.next());
            assertEquals(XmlPullParser.END_TAG, parser.next());
            assertEquals(XmlPullParser.PROCESSING_INSTRUCTION, parser.next());

            fail("Should fail since it has aborted PI");
        } catch (XmlPullParserException ex) {
            assertTrue(ex.getMessage().contains("@1:21"));
            assertTrue(
                    ex.getMessage().contains("processing instruction started on line 1 and column 12 was not closed"));
        }
    }

    @Test
    void subsequentAbortedComment() throws Exception {
        MXParser parser = new MXParser();
        StringBuilder sb = new StringBuilder();
        sb.append("<project />");
        sb.append("<!-- aborted");

        parser.setInput(new StringReader(sb.toString()));

        try {
            assertEquals(XmlPullParser.START_TAG, parser.next());
            assertEquals(XmlPullParser.END_TAG, parser.next());
            assertEquals(XmlPullParser.PROCESSING_INSTRUCTION, parser.next());

            fail("Should fail since it has aborted comment");
        } catch (XmlPullParserException ex) {
            assertTrue(ex.getMessage().contains("@1:24"));
            assertTrue(ex.getMessage().contains("comment started on line 1 and column 12 was not closed"));
        }
    }

    @Test
    void malformedXMLRootElement() throws Exception {
        String input = "<Y";

        MXParser parser = new MXParser();
        parser.setInput(new StringReader(input));

        try {
            assertEquals(XmlPullParser.START_TAG, parser.nextToken());

            fail("Should throw EOFException");
        } catch (EOFException e) {
            assertTrue(e.getMessage().contains("no more data available - expected the opening tag <Y...>"));
        }
    }

    /**
     * <p>testMalformedXMLRootElement2.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void malformedXMLRootElement2() throws Exception {
        String input = "<hello";

        MXParser parser = new MXParser();
        parser.setInput(new StringReader(input));

        try {
            assertEquals(XmlPullParser.START_TAG, parser.nextToken());

            fail("Should throw EOFException");
        } catch (EOFException e) {
            assertTrue(e.getMessage().contains("no more data available - expected the opening tag <hello...>"));
        }
    }

    /**
     * <p>testMalformedXMLRootElement3.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void malformedXMLRootElement3() throws Exception {
        String input = "<hello><how";

        MXParser parser = new MXParser();
        parser.setInput(new StringReader(input));

        try {
            assertEquals(XmlPullParser.START_TAG, parser.nextToken());
            assertEquals(XmlPullParser.START_TAG, parser.nextToken());

            fail("Should throw EOFException");
        } catch (EOFException e) {
            assertTrue(e.getMessage().contains("no more data available - expected the opening tag <how...>"));
        }
    }

    /**
     * <p>testMalformedXMLRootElement4.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void malformedXMLRootElement4() throws Exception {
        String input = "<hello>some text<how";

        MXParser parser = new MXParser();
        parser.setInput(new StringReader(input));

        try {
            assertEquals(XmlPullParser.START_TAG, parser.nextToken());
            assertEquals(XmlPullParser.TEXT, parser.nextToken());
            assertEquals("some text", parser.getText());
            assertEquals(XmlPullParser.START_TAG, parser.nextToken());

            fail("Should throw EOFException");
        } catch (EOFException e) {
            assertTrue(e.getMessage().contains("no more data available - expected the opening tag <how...>"));
        }
    }

    /**
     * <p>testMalformedXMLRootElement5.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void malformedXMLRootElement5() throws Exception {
        String input = "<hello>some text</hello";

        MXParser parser = new MXParser();
        parser.setInput(new StringReader(input));

        try {
            assertEquals(XmlPullParser.START_TAG, parser.nextToken());
            assertEquals(XmlPullParser.TEXT, parser.nextToken());
            assertEquals("some text", parser.getText());
            assertEquals(XmlPullParser.END_TAG, parser.nextToken());

            fail("Should throw EOFException");
        } catch (EOFException e) {
            assertTrue(e.getMessage()
                    .contains("no more data available - expected end tag </hello> to close start tag <hello>"));
        }
    }

    /**
     * <p>testXMLDeclVersionOnly.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void xmlDeclVersionOnly() throws Exception {
        String input = "<?xml version='1.0'?><hello/>";

        MXParser parser = new MXParser();
        parser.setInput(new StringReader(input));

        Assertions.assertDoesNotThrow(
                () -> {
                    assertEquals(XmlPullParser.PROCESSING_INSTRUCTION, parser.nextToken());
                    assertEquals(XmlPullParser.START_TAG, parser.nextToken());
                    assertEquals(XmlPullParser.END_TAG, parser.nextToken());
                },
                "Should not throw Exception");
    }

    /**
     * <p>testXMLDeclVersionEncodingStandaloneNoSpace.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void xmlDeclVersionEncodingStandaloneNoSpace() throws Exception {
        String input = "<?xml version='1.0' encoding='ASCII'standalone='yes'?><hello/>";

        MXParser parser = new MXParser();
        parser.setInput(new StringReader(input));

        try {
            parser.nextToken();
        } catch (XmlPullParserException e) {
            assertTrue(e.getMessage().contains("expected a space after encoding and not s"));
        }
    }

    /**
     * Issue 163: https://github.com/codehaus-plexus/plexus-utils/issues/163
     *
     * @throws IOException if IO error.
     *
     * @since 3.4.1
     */
    @Test
    void encodingISO88591setInputReader() throws IOException {
        try (Reader reader =
                ReaderFactory.newXmlReader(new File("src/test/resources/xml", "test-encoding-ISO-8859-1.xml"))) {
            MXParser parser = new MXParser();
            parser.setInput(reader);
            while (parser.nextToken() != XmlPullParser.END_DOCUMENT)
                ;
            assertTrue(true);
        } catch (XmlPullParserException e) {
            fail("should not raise exception: " + e);
        }
    }

    /**
     * Issue 163: https://github.com/codehaus-plexus/plexus-utils/issues/163
     *
     * @throws IOException if IO error.
     *
     * @since 3.4.1
     */
    @Test
    void encodingISO88591SetInputStream() throws IOException {
        try (InputStream input =
                Files.newInputStream(Paths.get("src/test/resources/xml", "test-encoding-ISO-8859-1.xml"))) {
            MXParser parser = new MXParser();
            parser.setInput(input, null);
            while (parser.nextToken() != XmlPullParser.END_DOCUMENT)
                ;
            assertTrue(true);
        } catch (XmlPullParserException e) {
            fail("should not raise exception: " + e);
        }
    }

    private static void assertPosition(int row, int col, MXParser parser) {
        assertEquals(row, parser.getLineNumber(), "Current line");
        assertEquals(col, parser.getColumnNumber(), "Current column");
    }

    /**
     * Issue 163: https://github.com/codehaus-plexus/plexus-utils/issues/163
     *
     * Another case of bug #163: File encoding information is lost after the input file is copied to a String.
     *
     * @throws IOException if IO error.
     *
     * @since 3.4.2
     */
    @Test
    void encodingISO88591setStringReader() throws IOException {
        try (Reader reader =
                ReaderFactory.newXmlReader(new File("src/test/resources/xml", "test-encoding-ISO-8859-1.xml"))) {
            MXParser parser = new MXParser();
            String xmlFileContents = IOUtil.toString(reader);
            parser.setInput(new StringReader(xmlFileContents));
            while (parser.nextToken() != XmlPullParser.END_DOCUMENT)
                ;
            assertTrue(true);
        } catch (XmlPullParserException e) {
            fail("should not raise exception: " + e);
        }
    }

    /**
     * <p>
     * Test custom Entity not found.
     * </p>
     *
     * Regression test: assure same behavior of MXParser from plexus-utils 3.3.0.
     *
     * @throws java.lang.Exception if any.
     *
     * @since 3.4.2
     */
    @Test
    void customEntityNotFoundInText() throws Exception {
        MXParser parser = new MXParser();

        String input = "<root>&otherentity;</root>";
        parser.setInput(new StringReader(input));
        parser.defineEntityReplacementText("myentity", "replacement");

        try {
            assertEquals(XmlPullParser.START_TAG, parser.next());
            assertEquals(XmlPullParser.TEXT, parser.next());
            fail("should raise exception");
        } catch (XmlPullParserException e) {
            assertTrue(
                    e.getMessage()
                            .contains(
                                    "could not resolve entity named 'otherentity' (position: START_TAG seen <root>&otherentity;... @1:20)"));
            assertEquals(XmlPullParser.START_TAG, parser.getEventType()); // not an ENTITY_REF
            assertEquals("otherentity", parser.getText());
        }
    }

    /**
     * <p>
     * Test custom Entity not found, with tokenize.
     * </p>
     *
     * Regression test: assure same behavior of MXParser from plexus-utils 3.3.0.
     *
     * @throws java.lang.Exception if any.
     *
     * @since 3.4.2
     */
    @Test
    void customEntityNotFoundInTextTokenize() throws Exception {
        MXParser parser = new MXParser();

        String input = "<root>&otherentity;</root>";
        parser.setInput(new StringReader(input));
        parser.defineEntityReplacementText("myentity", "replacement");

        Assertions.assertDoesNotThrow(
                () -> {
                    assertEquals(XmlPullParser.START_TAG, parser.nextToken());
                    assertEquals(XmlPullParser.ENTITY_REF, parser.nextToken());
                    assertNull(parser.getText());
                },
                "should not throw exception if tokenize");
    }

    /**
     * <p>
     * Test custom Entity not found in attribute.
     * </p>
     *
     * Regression test: assure same behavior of MXParser from plexus-utils 3.3.0.
     *
     * @throws java.lang.Exception if any.
     *
     * @since 3.4.2
     */
    @Test
    void customEntityNotFoundInAttr() throws Exception {
        MXParser parser = new MXParser();

        String input = "<root name=\"&otherentity;\">sometext</root>";
        parser.setInput(new StringReader(input));
        parser.defineEntityReplacementText("myentity", "replacement");

        try {
            assertEquals(XmlPullParser.START_TAG, parser.next());
            fail("should raise exception");
        } catch (XmlPullParserException e) {
            assertTrue(
                    e.getMessage()
                            .contains(
                                    "could not resolve entity named 'otherentity' (position: START_DOCUMENT seen <root name=\"&otherentity;... @1:26)"));
            assertEquals(XmlPullParser.START_DOCUMENT, parser.getEventType()); // not an ENTITY_REF
            assertNull(parser.getText());
        }
    }

    /**
     * <p>
     * Test custom Entity not found in attribute, with tokenize.
     * </p>
     *
     * Regression test: assure same behavior of MXParser from plexus-utils 3.3.0.
     * @throws XmlPullParserException
     *
     * @throws Exception if any.
     *
     * @since 3.4.2
     */
    @Test
    void customEntityNotFoundInAttrTokenize() throws Exception {
        MXParser parser = new MXParser();

        String input = "<root name=\"&otherentity;\">sometext</root>";

        try {
            parser.setInput(new StringReader(input));
            parser.defineEntityReplacementText("myentity", "replacement");

            assertEquals(XmlPullParser.START_TAG, parser.nextToken());
            fail("should raise exception");
        } catch (XmlPullParserException e) {
            assertTrue(
                    e.getMessage()
                            .contains(
                                    "could not resolve entity named 'otherentity' (position: START_DOCUMENT seen <root name=\"&otherentity;... @1:26)"));
            assertEquals(XmlPullParser.START_DOCUMENT, parser.getEventType()); // not an ENTITY_REF
            assertNull(parser.getText());
        }
    }

    /**
     * <p>Issue #194: Incorrect getText() after parsing the DOCDECL section</>
     *
     * <p>test DOCDECL text with myCustomEntity that cannot be resolved, Unix line separator.</p>
     *
     * Regression test: assure same behavior of MXParser from plexus-utils 3.3.0.
     *
     * @throws IOException if any.
     *
     * @since 3.4.2
     */
    @Test
    void docdeclTextWithEntitiesUnix() throws IOException {
        testDocdeclTextWithEntities("test-entities-UNIX.xml");
    }

    /**
     * <p>Issue #194: Incorrect getText() after parsing the DOCDECL section</>
     *
     * <p>test DOCDECL text with myCustomEntity that cannot be resolved, DOS line separator.</p>
     *
     * Regression test: assure same behavior of MXParser from plexus-utils 3.3.0.
     *
     * @throws IOException if any.
     *
     * @since 3.4.2
     */
    @Test
    void docdeclTextWithEntitiesDOS() throws IOException {
        testDocdeclTextWithEntities("test-entities-DOS.xml");
    }

    private void testDocdeclTextWithEntities(String filename) throws IOException {
        try (Reader reader = ReaderFactory.newXmlReader(new File("src/test/resources/xml", filename))) {
            MXParser parser = new MXParser();
            parser.setInput(reader);
            assertEquals(XmlPullParser.PROCESSING_INSTRUCTION, parser.nextToken());
            assertEquals(XmlPullParser.IGNORABLE_WHITESPACE, parser.nextToken());
            assertEquals(XmlPullParser.DOCDECL, parser.nextToken());
            assertEquals(
                    " document [\n" + "<!ENTITY flo \"&#x159;\">\n" + "<!ENTITY myCustomEntity \"&flo;\">\n" + "]",
                    parser.getText());
            assertEquals(XmlPullParser.IGNORABLE_WHITESPACE, parser.nextToken());
            assertEquals(XmlPullParser.START_TAG, parser.nextToken());
            assertEquals("document", parser.getName());
            assertEquals(XmlPullParser.TEXT, parser.next());

            fail("should fail to resolve 'myCustomEntity' entity");
        } catch (XmlPullParserException e) {
            assertTrue(e.getMessage().contains("could not resolve entity named 'myCustomEntity'"));
        }
    }

    /**
     * <p>Issue #194: Incorrect getText() after parsing the DOCDECL section</>
     *
     * <p>test DOCDECL text with entities appearing in attributes, Unix line separator.</p>
     *
     * Regression test: assure same behavior of MXParser from plexus-utils 3.3.0.
     *
     * @throws IOException if any.
     *
     * @since 3.4.2
     */
    @Test
    void docdeclTextWithEntitiesInAttributesUnix() throws IOException {
        testDocdeclTextWithEntitiesInAttributes("test-entities-in-attr-UNIX.xml");
    }

    /**
     * <p>Issue #194: Incorrect getText() after parsing the DOCDECL section</>
     *
     * <p>test DOCDECL text with entities appearing in attributes, DOS line separator.</p>
     *
     * Regression test: assure same behavior of MXParser from plexus-utils 3.3.0.
     *
     * @throws IOException if any.
     *
     * @since 3.4.2
     */
    @Test
    void docdeclTextWithEntitiesInAttributesDOS() throws IOException {
        testDocdeclTextWithEntitiesInAttributes("test-entities-in-attr-DOS.xml");
    }

    private void testDocdeclTextWithEntitiesInAttributes(String filename) throws IOException {
        try (Reader reader = ReaderFactory.newXmlReader(new File("src/test/resources/xml", filename))) {
            MXParser parser = new MXParser();
            parser.setInput(reader);
            parser.defineEntityReplacementText("nbsp", "&#160;");
            parser.defineEntityReplacementText("Alpha", "&#913;");
            parser.defineEntityReplacementText("tritPos", "&#x1d7ed;");
            parser.defineEntityReplacementText("flo", "&#x159;");
            parser.defineEntityReplacementText("myCustomEntity", "&flo;");
            assertEquals(XmlPullParser.PROCESSING_INSTRUCTION, parser.nextToken());
            assertEquals(XmlPullParser.IGNORABLE_WHITESPACE, parser.nextToken());
            assertEquals(XmlPullParser.DOCDECL, parser.nextToken());
            assertEquals(
                    " document [\n"
                            + "<!ENTITY nbsp   \"&#160;\"> <!-- no-break space = non-breaking space, U+00A0 ISOnum -->\n"
                            + "<!ENTITY Alpha    \"&#913;\"> <!-- greek capital letter alpha, U+0391 -->\n"
                            + "<!ENTITY tritPos  \"&#x1d7ed;\"> <!-- MATHEMATICAL SANS-SERIF BOLD DIGIT ONE -->\n"
                            + "<!ENTITY flo \"&#x159;\">\n"
                            + "<!ENTITY myCustomEntity \"&flo;\">\n"
                            + "]",
                    parser.getText());
            assertEquals(XmlPullParser.IGNORABLE_WHITESPACE, parser.nextToken());
            assertEquals(XmlPullParser.START_TAG, parser.nextToken());
            assertEquals("document", parser.getName());
            assertEquals(1, parser.getAttributeCount());
            assertEquals("name", parser.getAttributeName(0));
            assertEquals(
                    "section name with entities: '&' '&#913;' '<' '&#160;' '>' '&#x1d7ed;' ''' '&#x159;' '\"'",
                    parser.getAttributeValue(0));

            assertEquals(XmlPullParser.ENTITY_REF, parser.nextToken());
            assertEquals("myCustomEntity", parser.getName());
            assertEquals("&#x159;", parser.getText());

            assertEquals(XmlPullParser.END_TAG, parser.nextToken());
            assertEquals(XmlPullParser.END_DOCUMENT, parser.nextToken());
        } catch (XmlPullParserException e) {
            fail("should not raise exception: " + e);
        }
    }

    /**
     * <p>test entity ref with entities appearing in tags, Unix line separator.</p>
     *
     * Regression test: assure same behavior of MXParser from plexus-utils 3.3.0.
     *
     * @throws IOException if any.
     *
     * @since 3.4.2
     */
    @Test
    void entityRefTextUnix() throws IOException {
        testEntityRefText("\n");
    }

    /**
     * <p>test entity ref with entities appearing in tags, DOS line separator.</p>
     *
     * Regression test: assure same behavior of MXParser from plexus-utils 3.3.0.
     *
     * @throws IOException if any.
     *
     * @since 3.4.2
     */
    @Test
    void entityRefTextDOS() throws IOException {
        testEntityRefText("\r\n");
    }

    private void testEntityRefText(String newLine) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE test [").append(newLine);
        sb.append("<!ENTITY foo \"&#x159;\">").append(newLine);
        sb.append("<!ENTITY foo1 \"&nbsp;\">").append(newLine);
        sb.append("<!ENTITY foo2 \"&#x161;\">").append(newLine);
        sb.append("<!ENTITY tritPos \"&#x1d7ed;\">").append(newLine);
        sb.append("]>").append(newLine);
        sb.append("<b>&foo;&foo1;&foo2;&tritPos;</b>");

        Assertions.assertDoesNotThrow(
                () -> {
                    MXParser parser = new MXParser();
                    parser.setInput(new StringReader(sb.toString()));
                    parser.defineEntityReplacementText("foo", "&#x159;");
                    parser.defineEntityReplacementText("nbsp", "&#160;");
                    parser.defineEntityReplacementText("foo1", "&nbsp;");
                    parser.defineEntityReplacementText("foo2", "&#x161;");
                    parser.defineEntityReplacementText("tritPos", "&#x1d7ed;");

                    assertEquals(XmlPullParser.DOCDECL, parser.nextToken());
                    assertEquals(
                            " test [\n"
                                    + "<!ENTITY foo \"&#x159;\">\n"
                                    + "<!ENTITY foo1 \"&nbsp;\">\n"
                                    + "<!ENTITY foo2 \"&#x161;\">\n"
                                    + "<!ENTITY tritPos \"&#x1d7ed;\">\n"
                                    + "]",
                            parser.getText());
                    assertEquals(XmlPullParser.IGNORABLE_WHITESPACE, parser.nextToken());
                    assertEquals(XmlPullParser.START_TAG, parser.nextToken());
                    assertEquals("b", parser.getName());
                    assertEquals(XmlPullParser.ENTITY_REF, parser.nextToken());
                    assertEquals("&#x159;", parser.getText());
                    assertEquals("foo", parser.getName());
                    assertEquals(XmlPullParser.ENTITY_REF, parser.nextToken());
                    assertEquals("&#160;", parser.getText());
                    assertEquals("foo1", parser.getName());
                    assertEquals(XmlPullParser.ENTITY_REF, parser.nextToken());
                    assertEquals("&#x161;", parser.getText());
                    assertEquals("foo2", parser.getName());
                    assertEquals(XmlPullParser.ENTITY_REF, parser.nextToken());
                    assertEquals("&#x1d7ed;", parser.getText());
                    assertEquals("tritPos", parser.getName());
                    assertEquals(XmlPullParser.END_TAG, parser.nextToken());
                    assertEquals("b", parser.getName());
                    assertEquals(XmlPullParser.END_DOCUMENT, parser.nextToken());
                },
                "should not raise exception: ");
    }

    /**
     * <b>Ensures that entity ref getText() and getName() return the correct value.</b>
     *
     * Regression test: assure same behavior of MXParser from plexus-utils 3.3.0.
     *
     * @throws IOException if any.
     *
     * @since 3.4.2
     */
    @Test
    void entityReplacement() throws IOException {
        String input = "<p><!-- a pagebreak: --><!-- PB -->&#160;&nbsp;<unknown /></p>";

        Assertions.assertDoesNotThrow(
                () -> {
                    MXParser parser = new MXParser();
                    parser.setInput(new StringReader(input));
                    parser.defineEntityReplacementText("nbsp", "&#160;");

                    assertEquals(XmlPullParser.START_TAG, parser.nextToken());
                    assertEquals("p", parser.getName());
                    assertEquals(XmlPullParser.COMMENT, parser.nextToken());
                    assertEquals(" a pagebreak: ", parser.getText());
                    assertEquals(XmlPullParser.COMMENT, parser.nextToken());
                    assertEquals(" PB ", parser.getText());
                    assertEquals(XmlPullParser.ENTITY_REF, parser.nextToken());
                    assertEquals("\u00A0", parser.getText());
                    assertEquals("#160", parser.getName());
                    assertEquals(XmlPullParser.ENTITY_REF, parser.nextToken());
                    assertEquals("&#160;", parser.getText());
                    assertEquals("nbsp", parser.getName());
                    assertEquals(XmlPullParser.START_TAG, parser.nextToken());
                    assertEquals("unknown", parser.getName());
                    assertEquals(XmlPullParser.END_TAG, parser.nextToken());
                    assertEquals("unknown", parser.getName());
                    assertEquals(XmlPullParser.END_TAG, parser.nextToken());
                    assertEquals("p", parser.getName());
                    assertEquals(XmlPullParser.END_DOCUMENT, parser.nextToken());
                },
                "should not raise exception: ");
    }

    /**
     * <b>Ensures correct replacements inside the internal PC array when the new copied array size is shorter than
     * previous ones.</b>
     *
     * Regression test: assure same behavior of MXParser from plexus-utils 3.3.0.
     *
     * @throws IOException if any.
     *
     * @since 3.4.2
     */
    @Test
    void replacementInPCArrayWithShorterCharArray() throws IOException {
        String input = "<!DOCTYPE test [<!ENTITY foo \"&#x159;\"><!ENTITY tritPos  \"&#x1d7ed;\">]>"
                + "<section name=\"&amp;&foo;&tritPos;\"><p>&amp;&foo;&tritPos;</p></section>";

        Assertions.assertDoesNotThrow(
                () -> {
                    MXParser parser = new MXParser();
                    parser.setInput(new StringReader(new String(input.getBytes(), "ISO-8859-1")));
                    parser.defineEntityReplacementText("foo", "&#x159;");
                    parser.defineEntityReplacementText("tritPos", "&#x1d7ed;");

                    assertEquals(XmlPullParser.DOCDECL, parser.nextToken());
                    assertEquals(" test [<!ENTITY foo \"&#x159;\"><!ENTITY tritPos  \"&#x1d7ed;\">]", parser.getText());
                    assertEquals(XmlPullParser.START_TAG, parser.nextToken());
                    assertEquals("section", parser.getName());
                    assertEquals(1, parser.getAttributeCount());
                    assertEquals("name", parser.getAttributeName(0));
                    assertEquals("&&#x159;&#x1d7ed;", parser.getAttributeValue(0));
                    assertEquals(XmlPullParser.START_TAG, parser.nextToken());
                    assertEquals("<p>", parser.getText());
                    assertEquals("p", parser.getName());
                    assertEquals(XmlPullParser.ENTITY_REF, parser.nextToken());
                    assertEquals("&", parser.getText());
                    assertEquals("amp", parser.getName());
                    assertEquals(XmlPullParser.ENTITY_REF, parser.nextToken());
                    assertEquals("&#x159;", parser.getText());
                    assertEquals("foo", parser.getName());
                    assertEquals(XmlPullParser.ENTITY_REF, parser.nextToken());
                    assertEquals("&#x1d7ed;", parser.getText());
                    assertEquals("tritPos", parser.getName());
                    assertEquals(XmlPullParser.END_TAG, parser.nextToken());
                    assertEquals("p", parser.getName());
                    assertEquals(XmlPullParser.END_TAG, parser.nextToken());
                    assertEquals("section", parser.getName());
                    assertEquals(XmlPullParser.END_DOCUMENT, parser.nextToken());
                },
                "should not raise exception: ");
    }

    /**
     * Ensures emoji can be parsed correctly
     */
    @Test
    void unicode() throws IOException {
        String input = "<project><!--ALL TEH BOMS!  \uD83D\uDCA3  --></project>";

        try {
            MXParser parser = new MXParser();
            parser.setInput(new StringReader(input));

            assertEquals(XmlPullParser.START_TAG, parser.nextToken());
            assertEquals("project", parser.getName());
            assertEquals(XmlPullParser.COMMENT, parser.nextToken());
            assertEquals("ALL TEH BOMS!  \uD83D\uDCA3  ", parser.getText());
            assertEquals(XmlPullParser.END_TAG, parser.nextToken());
            assertEquals("project", parser.getName());
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            fail("should not raise exception: " + e);
        }
    }
}
