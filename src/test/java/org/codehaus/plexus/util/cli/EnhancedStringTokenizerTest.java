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

import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>EnhancedStringTokenizerTest class.</p>
 *
 * @author herve
 * @since 3.4.0
 */
class EnhancedStringTokenizerTest {

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

    @Test
    void emptyString() {
        EnhancedStringTokenizer est = new EnhancedStringTokenizer("");
        assertFalse(est.hasMoreTokens(), "est.hasMoreTokens()");
        assertThrows(NoSuchElementException.class, est::nextToken);
    }

    @Test
    void simpleString() {
        EnhancedStringTokenizer est = new EnhancedStringTokenizer("a ");
        assertEquals("a", est.nextToken(), "Token 1");
        assertEquals("", est.nextToken(), "Token 2");
        assertFalse(est.hasMoreTokens(), "est.hasMoreTokens()");
    }
}
