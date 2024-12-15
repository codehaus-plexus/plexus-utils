package org.codehaus.plexus.util.cli;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * <p>EnhancedStringTokenizerTest class.</p>
 *
 * @author herve
 * @version $Id: $Id
 * @since 3.4.0
 */
class EnhancedStringTokenizerTest {
    /**
     * <p>test1.</p>
     */
    @Test
    void test1() {
        EnhancedStringTokenizer est = new EnhancedStringTokenizer("this is a test string");
        StringBuilder sb = new StringBuilder();
        while (est.hasMoreTokens()) {
            sb.append(est.nextToken());
            sb.append(" ");
        }
        assertEquals("this is a test string ", sb.toString());
    }

    /**
     * <p>test2.</p>
     */
    @Test
    void test2() {
        EnhancedStringTokenizer est = new EnhancedStringTokenizer("1,,,3,,4", ",");
        assertEquals("1", est.nextToken(), "Token 1");
        assertEquals("", est.nextToken(), "Token 2");
        assertEquals("", est.nextToken(), "Token 3");
        assertEquals("3", est.nextToken(), "Token 4");
        assertEquals("", est.nextToken(), "Token 5");
        assertEquals("4", est.nextToken(), "Token 6");
    }

    /**
     * <p>test3.</p>
     */
    @Test
    void test3() {
        EnhancedStringTokenizer est = new EnhancedStringTokenizer("1,,,3,,4", ",", true);
        assertEquals("1", est.nextToken(), "Token 1");
        assertEquals(",", est.nextToken(), "Token 2");
        assertEquals("", est.nextToken(), "Token 3");
        assertEquals(",", est.nextToken(), "Token 4");
        assertEquals("", est.nextToken(), "Token 5");
        assertEquals(",", est.nextToken(), "Token 6");
        assertEquals("3", est.nextToken(), "Token 7");
        assertEquals(",", est.nextToken(), "Token 8");
        assertEquals("", est.nextToken(), "Token 9");
        assertEquals(",", est.nextToken(), "Token 10");
        assertEquals("4", est.nextToken(), "Token 11");
    }

    /**
     * <p>testMultipleDelim.</p>
     */
    @Test
    void multipleDelim() {
        EnhancedStringTokenizer est = new EnhancedStringTokenizer("1 2|3|4", " |", true);
        assertEquals("1", est.nextToken(), "Token 1");
        assertEquals(" ", est.nextToken(), "Token 2");
        assertEquals("2", est.nextToken(), "Token 3");
        assertEquals("|", est.nextToken(), "Token 4");
        assertEquals("3", est.nextToken(), "Token 5");
        assertEquals("|", est.nextToken(), "Token 6");
        assertEquals("4", est.nextToken(), "Token 7");
        assertFalse(est.hasMoreTokens(), "est.hasMoreTokens()");
    }

    /**
     * <p>testEmptyString.</p>
     */
    @Test
    void emptyString() {
        EnhancedStringTokenizer est = new EnhancedStringTokenizer("");
        assertFalse(est.hasMoreTokens(), "est.hasMoreTokens()");
        try {
            est.nextToken();
            fail();
        } catch (Exception e) {
        }
    }

    /**
     * <p>testSimpleString.</p>
     */
    @Test
    void simpleString() {
        EnhancedStringTokenizer est = new EnhancedStringTokenizer("a ");
        assertEquals("a", est.nextToken(), "Token 1");
        assertEquals("", est.nextToken(), "Token 2");
        assertFalse(est.hasMoreTokens(), "est.hasMoreTokens()");
    }
}
