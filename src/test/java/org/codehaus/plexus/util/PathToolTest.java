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

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * <p>PathToolTest class.</p>
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id: $Id
 * @since 3.4.0
 */
class PathToolTest {
    /**
     * <p>testGetRelativePath.</p>
     *
     * @throws java.lang.Exception
     */
    @Test
    void getRelativePath() throws Exception {
        assertEquals("", PathTool.getRelativePath(null, null));
        assertEquals("", PathTool.getRelativePath(null, "/usr/local/java/bin"));
        assertEquals("", PathTool.getRelativePath("/usr/local/", null));
        assertEquals("..", PathTool.getRelativePath("/usr/local/", "/usr/local/java/bin"));
        assertEquals("../..", PathTool.getRelativePath("/usr/local/", "/usr/local/java/bin/java.sh"));
        assertEquals("", PathTool.getRelativePath("/usr/local/java/bin/java.sh", "/usr/local/"));
    }

    /**
     * <p>testGetDirectoryComponent.</p>
     *
     * @throws java.lang.Exception
     */
    @Test
    void getDirectoryComponent() throws Exception {
        assertEquals("", PathTool.getDirectoryComponent(null));
        assertEquals("/usr/local/java", PathTool.getDirectoryComponent("/usr/local/java/bin"));
        assertEquals("/usr/local/java/bin", PathTool.getDirectoryComponent("/usr/local/java/bin/"));
        assertEquals("/usr/local/java/bin", PathTool.getDirectoryComponent("/usr/local/java/bin/java.sh"));
    }

    /**
     * <p>testCalculateLink.</p>
     *
     */
    @Test
    void calculateLink() {
        assertEquals("../../index.html", PathTool.calculateLink("/index.html", "../.."));
        assertEquals(
                "http://plexus.codehaus.org/plexus-utils/index.html",
                PathTool.calculateLink("http://plexus.codehaus.org/plexus-utils/index.html", "../.."));
        assertEquals(
                "../../usr/local/java/bin/java.sh", PathTool.calculateLink("/usr/local/java/bin/java.sh", "../.."));
        assertEquals(
                "/usr/local/java/bin/../index.html", PathTool.calculateLink("../index.html", "/usr/local/java/bin"));
        assertEquals(
                "http://plexus.codehaus.org/plexus-utils/../index.html",
                PathTool.calculateLink("../index.html", "http://plexus.codehaus.org/plexus-utils"));
    }

    /**
     * <p>testGetRelativeWebPath.</p>
     *
     * @throws java.lang.Exception
     */
    @Test
    void getRelativeWebPath() throws Exception {
        assertEquals("", PathTool.getRelativeWebPath(null, null));
        assertEquals("", PathTool.getRelativeWebPath(null, "http://plexus.codehaus.org/"));
        assertEquals("", PathTool.getRelativeWebPath("http://plexus.codehaus.org/", null));
        assertEquals(
                "plexus-utils/index.html",
                PathTool.getRelativeWebPath(
                        "http://plexus.codehaus.org/", "http://plexus.codehaus.org/plexus-utils/index.html"));
        assertEquals(
                "../../",
                PathTool.getRelativeWebPath(
                        "http://plexus.codehaus.org/plexus-utils/index.html", "http://plexus.codehaus.org/"));
    }

    /**
     * <p>testGetRelativeFilePath.</p>
     *
     * @throws java.lang.Exception
     */
    @Test
    void getRelativeFilePath() throws Exception {
        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            assertEquals("", PathTool.getRelativeFilePath(null, null));
            assertEquals("", PathTool.getRelativeFilePath(null, "c:\\tools\\java\\bin"));
            assertEquals("", PathTool.getRelativeFilePath("c:\\tools\\java", null));
            assertEquals("java\\bin", PathTool.getRelativeFilePath("c:\\tools", "c:\\tools\\java\\bin"));
            assertEquals("java\\bin\\", PathTool.getRelativeFilePath("c:\\tools", "c:\\tools\\java\\bin\\"));
            assertEquals("..\\..", PathTool.getRelativeFilePath("c:\\tools\\java\\bin", "c:\\tools"));
            assertEquals(
                    "java\\bin\\java.exe",
                    PathTool.getRelativeFilePath("c:\\tools\\", "c:\\tools\\java\\bin\\java.exe"));
            assertEquals("..\\..\\..", PathTool.getRelativeFilePath("c:\\tools\\java\\bin\\java.sh", "c:\\tools"));
            assertEquals("..\\bin", PathTool.getRelativeFilePath("c:\\tools", "c:\\bin"));
            assertEquals("..\\tools", PathTool.getRelativeFilePath("c:\\bin", "c:\\tools"));
            assertEquals("", PathTool.getRelativeFilePath("c:\\bin", "c:\\bin"));
        } else {
            assertEquals("", PathTool.getRelativeFilePath(null, null));
            assertEquals("", PathTool.getRelativeFilePath(null, "/usr/local/java/bin"));
            assertEquals("", PathTool.getRelativeFilePath("/usr/local", null));
            assertEquals("java/bin", PathTool.getRelativeFilePath("/usr/local", "/usr/local/java/bin"));
            assertEquals("java/bin/", PathTool.getRelativeFilePath("/usr/local", "/usr/local/java/bin/"));
            assertEquals("../../", PathTool.getRelativeFilePath("/usr/local/java/bin", "/usr/local/"));
            assertEquals(
                    "java/bin/java.sh", PathTool.getRelativeFilePath("/usr/local/", "/usr/local/java/bin/java.sh"));
            assertEquals("../../../", PathTool.getRelativeFilePath("/usr/local/java/bin/java.sh", "/usr/local/"));
            assertEquals("../../bin", PathTool.getRelativeFilePath("/usr/local/", "/bin"));
            assertEquals("../usr/local", PathTool.getRelativeFilePath("/bin", "/usr/local"));
            assertEquals("", PathTool.getRelativeFilePath("/bin", "/bin"));
        }
    }
}
