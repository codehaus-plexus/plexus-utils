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

import java.util.Iterator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test Case for Os
 *
 * @author herve
 * @version $Id: $Id
 * @since 3.4.0
 */
class OsTest {
    /**
     * <p>testUndefinedFamily.</p>
     */
    @Test
    void undefinedFamily() {
        assertFalse(Os.isFamily("bogus family"));
    }

    /**
     * <p>testOs.</p>
     */
    @Test
    void os() {
        Iterator<String> iter = Os.getValidFamilies().iterator();
        String currentFamily = null;
        String notCurrentFamily = null;
        while (iter.hasNext() && (currentFamily == null || notCurrentFamily == null)) {
            String fam = iter.next();
            if (Os.isFamily(fam)) {
                currentFamily = fam;
            } else {
                notCurrentFamily = fam;
            }
        }

        // make sure the OS_FAMILY is set right.
        assertEquals(Os.OS_FAMILY, currentFamily);

        // check the current family and one of the others
        assertTrue(Os.isOs(currentFamily, null, null, null));
        assertFalse(Os.isOs(notCurrentFamily, null, null, null));

        // check for junk
        assertFalse(Os.isOs("junk", null, null, null));

        // check the current name
        assertTrue(Os.isOs(currentFamily, Os.OS_NAME, null, null));

        // check some other name
        assertFalse(Os.isOs(currentFamily, "myos", null, null));

        // check the arch
        assertTrue(Os.isOs(currentFamily, Os.OS_NAME, Os.OS_ARCH, null));
        assertFalse(Os.isOs(currentFamily, Os.OS_NAME, "myarch", null));

        // check the version
        assertTrue(Os.isOs(currentFamily, Os.OS_NAME, Os.OS_ARCH, Os.OS_VERSION));
        assertFalse(Os.isOs(currentFamily, Os.OS_NAME, Os.OS_ARCH, "myversion"));
    }

    /**
     * <p>testValidList.</p>
     */
    @Test
    void validList() {
        assertTrue(Os.isValidFamily("dos"));

        assertFalse(Os.isValidFamily(""));
        assertFalse(Os.isValidFamily(null));
        assertFalse(Os.isValidFamily("something"));
    }
}
