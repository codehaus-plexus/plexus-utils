package org.codehaus.plexus.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * <p>MatchPatternTest class.</p>
 *
 * @author Kristian Rosenvold
 * @version $Id: $Id
 * @since 3.4.0
 */
class MatchPatternTest {
    /**
     * <p>testGetSource</p>
     */
    @Test
    void getSource() {
        MatchPattern mp = MatchPattern.fromString("ABC*");
        assertEquals("ABC*", mp.getSource());
        mp = MatchPattern.fromString("%ant[some/ABC*]");
        assertEquals("some/ABC*", mp.getSource());
        mp = MatchPattern.fromString("%regex[[ABC].*]");
        assertEquals("[ABC].*", mp.getSource());
    }

    /**
     * <p>testMatchPath.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void matchPath() throws Exception {
        MatchPattern mp = MatchPattern.fromString("ABC*");
        assertTrue(mp.matchPath("ABCD", true));
    }

    /**
     * <p>testMatchPatternStart.</p>
     *
     * @see <a href="https://github.com/codehaus-plexus/plexus-utils/issues/63">Issue #63</a>
     */
    @Test
    void matchPatternStart() {
        MatchPattern mp = MatchPattern.fromString("ABC*");

        assertTrue(mp.matchPatternStart("ABCD", true));
        assertFalse(mp.matchPatternStart("AbCD", true));

        assertTrue(mp.matchPatternStart("ABCD", false));
        assertTrue(mp.matchPatternStart("AbCD", false));

        assertFalse(mp.matchPatternStart("XXXX", true));
        assertFalse(mp.matchPatternStart("XXXX", false));
    }

    /**
     * <p>testTokenizePathToString.</p>
     */
    @Test
    void tokenizePathToString() {
        String[] expected = {"hello", "world"};
        String[] actual = MatchPattern.tokenizePathToString("hello/world", "/");
        assertArrayEquals(expected, actual);

        actual = MatchPattern.tokenizePathToString("/hello/world", "/");
        assertArrayEquals(expected, actual);

        actual = MatchPattern.tokenizePathToString("/hello/world/", "/");
        assertArrayEquals(expected, actual);
    }
}
