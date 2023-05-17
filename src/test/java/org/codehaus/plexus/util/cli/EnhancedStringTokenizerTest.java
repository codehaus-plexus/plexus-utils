package org.codehaus.plexus.util.cli;

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
public class EnhancedStringTokenizerTest {
    /**
     * <p>test1.</p>
     */
    @org.junit.jupiter.api.Test
    public void test1() {
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
    public void test2() {
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
    @org.junit.jupiter.api.Test
    public void test3() {
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
    @org.junit.jupiter.api.Test
    public void testMultipleDelim() {
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
    @org.junit.jupiter.api.Test
    public void testEmptyString() {
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
    @org.junit.jupiter.api.Test
    public void testSimpleString() {
        EnhancedStringTokenizer est = new EnhancedStringTokenizer("a ");
        assertEquals("a", est.nextToken(), "Token 1");
        assertEquals("", est.nextToken(), "Token 2");
        assertFalse(est.hasMoreTokens(), "est.hasMoreTokens()");
    }
}
