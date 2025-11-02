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

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * <p>InterpolationFilterReaderTest class.</p>
 *
 * @author herve
 * @since 3.4.0
 */
class InterpolationFilterReaderTest {
    /*
     * Added and commented by jdcasey@03-Feb-2005 because it is a bug in the InterpolationFilterReader.
     * kenneyw@15-04-2005 fixed the bug.
     */
    @Test
    void shouldNotInterpolateExpressionAtEndOfDataWithInvalidEndToken() throws Exception {
        Map<String, String> m = new HashMap<>();
        m.put("test", "TestValue");

        String testStr = "This is a ${test";

        assertEquals("This is a ${test", interpolate(testStr, m));
    }

    /*
     * kenneyw@14-04-2005 Added test to check above fix.
     */
    @Test
    void shouldNotInterpolateExpressionWithMissingEndToken() throws Exception {
        Map<String, String> m = new HashMap<>();
        m.put("test", "TestValue");

        String testStr = "This is a ${test, really";

        assertEquals("This is a ${test, really", interpolate(testStr, m));
    }

    @Test
    void shouldNotInterpolateWithMalformedStartToken() throws Exception {
        Map<String, String> m = new HashMap<>();
        m.put("test", "testValue");

        String foo = "This is a $!test} again";

        assertEquals("This is a $!test} again", interpolate(foo, m));
    }

    @Test
    void shouldNotInterpolateWithMalformedEndToken() throws Exception {
        Map<String, String> m = new HashMap<>();
        m.put("test", "testValue");

        String foo = "This is a ${test!} again";

        assertEquals("This is a ${test!} again", interpolate(foo, m, "${", "$}"));
    }

    @Test
    void interpolationWithMulticharDelimiters() throws Exception {
        Map<String, String> m = new HashMap<>();
        m.put("test", "testValue");

        String foo = "This is a ${test$} again";

        assertEquals("This is a testValue again", interpolate(foo, m, "${", "$}"));
    }

    @Test
    void defaultInterpolationWithNonInterpolatedValueAtEnd() throws Exception {
        Map<String, String> m = new HashMap<>();
        m.put("name", "jason");
        m.put("noun", "asshole");

        String foo = "${name} is an ${noun}. ${not.interpolated}";

        assertEquals("jason is an asshole. ${not.interpolated}", interpolate(foo, m));
    }

    @Test
    void defaultInterpolationWithInterpolatedValueAtEnd() throws Exception {
        Map<String, String> m = new HashMap<>();
        m.put("name", "jason");
        m.put("noun", "asshole");

        String foo = "${name} is an ${noun}";

        assertEquals("jason is an asshole", interpolate(foo, m));
    }

    @Test
    void interpolationWithSpecifiedBoundaryTokens() throws Exception {
        Map<String, String> m = new HashMap<>();
        m.put("name", "jason");
        m.put("noun", "asshole");

        String foo = "@name@ is an @noun@. @not.interpolated@ baby @foo@. @bar@";

        assertEquals("jason is an asshole. @not.interpolated@ baby @foo@. @bar@", interpolate(foo, m, "@", "@"));
    }

    @Test
    void interpolationWithSpecifiedBoundaryTokensWithNonInterpolatedValueAtEnd() throws Exception {
        Map<String, String> m = new HashMap<>();
        m.put("name", "jason");
        m.put("noun", "asshole");

        String foo = "@name@ is an @foobarred@";

        assertEquals("jason is an @foobarred@", interpolate(foo, m, "@", "@"));
    }

    @Test
    void interpolationWithSpecifiedBoundaryTokensWithInterpolatedValueAtEnd() throws Exception {
        Map<String, String> m = new HashMap<>();
        m.put("name", "jason");
        m.put("noun", "asshole");

        String foo = "@name@ is an @noun@";

        assertEquals("jason is an asshole", interpolate(foo, m, "@", "@"));
    }

    @Test
    void interpolationWithSpecifiedBoundaryTokensAndAdditionalTokenCharacter() throws Exception {
        Map<String, String> m = new HashMap<>();
        m.put("name", "jason");
        m.put("noun", "asshole");

        String foo = "@name@ (known as jason@somewhere) is an @noun@";

        assertEquals("jason (known as jason@somewhere) is an asshole", interpolate(foo, m, "@", "@"));
    }

    private String interpolate(String input, Map context) throws Exception {
        return IOUtil.toString(new InterpolationFilterReader(new StringReader(input), context));
    }

    private String interpolate(String input, Map context, String startToken, String endToken) throws Exception {
        return IOUtil.toString(new InterpolationFilterReader(new StringReader(input), context, startToken, endToken));
    }
}
