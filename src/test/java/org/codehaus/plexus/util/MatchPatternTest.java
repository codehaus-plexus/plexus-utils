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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * <p>MatchPatternTest class.</p>
 *
 * @author Kristian Rosenvold
 * @version $Id: $Id
 * @since 3.4.0
 */
public class MatchPatternTest
{
    /**
     * <p>testMatchPath.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    public void testMatchPath()
        throws Exception
    {
        MatchPattern mp = MatchPattern.fromString( "ABC*" );
        assertTrue( mp.matchPath( "ABCD", true ) );
    }

    /**
     * <p>testMatchPatternStart.</p>
     *
     * @see <a href="https://github.com/codehaus-plexus/plexus-utils/issues/63">Issue #63</a>
     */
    @org.junit.jupiter.api.Test
    public void testMatchPatternStart()
    {
        MatchPattern mp = MatchPattern.fromString( "ABC*" );

        assertTrue( mp.matchPatternStart( "ABCD", true ) );
        assertFalse( mp.matchPatternStart( "AbCD", true ) );

        assertTrue( mp.matchPatternStart( "ABCD", false ) );
        assertTrue( mp.matchPatternStart( "AbCD", false ) );

        assertFalse( mp.matchPatternStart( "XXXX", true ) );
        assertFalse( mp.matchPatternStart( "XXXX", false ) );
    }

}
