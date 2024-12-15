package org.codehaus.plexus.util;

import org.junit.jupiter.api.Test;

/**
 * <p>PerfTest class.</p>
 *
 * @author herve
 * @version $Id: $Id
 * @since 3.4.0
 */
class PerfTest {
    String src = "012345578901234556789012345678901234456789012345678901234567890";

    private final int oops = 100;

    /**
     * <p>testSubString.</p>
     */
    @Test
    void subString() {
        StringBuilder res = new StringBuilder();
        int len = src.length();
        for (int cnt = 0; cnt < oops; cnt++) {
            for (int i = 0; i < len - 5; i++) {
                res.append(src, i, i + 4);
            }
        }
        int i = res.length();
        System.out.println("i = " + i);
    }

    /**
     * <p>testResDir.</p>
     */
    @Test
    void resDir() {
        StringBuilder res = new StringBuilder();
        int len = src.length();
        for (int cnt = 0; cnt < oops; cnt++) {
            for (int i = 0; i < len - 5; i++) {
                res.append(src, i, i + 4);
            }
        }
        int i = res.length();
        System.out.println("i = " + i);
    }
}
