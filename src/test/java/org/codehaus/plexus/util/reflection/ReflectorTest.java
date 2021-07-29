package org.codehaus.plexus.util.reflection;

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

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * <p>ReflectorTest class.</p>
 *
 * @author J&ouml;rg Schaible
 * @version $Id: $Id
 * @since 3.4.0
 */
public class ReflectorTest
{
    private Project project;

    private Reflector reflector;

    /**
     * <p>setUp.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Before
    public void setUp()
        throws Exception
    {
        project = new Project();
        project.setModelVersion( "1.0.0" );
        project.setVersion( "42" );

        reflector = new Reflector();
    }

    /**
     * <p>testObjectPropertyFromName.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    public void testObjectPropertyFromName()
        throws Exception
    {
        assertEquals( "1.0.0", reflector.getObjectProperty( project, "modelVersion" ) );
    }

    /**
     * <p>testObjectPropertyFromBean.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    public void testObjectPropertyFromBean()
        throws Exception
    {
        assertEquals( "Foo", reflector.getObjectProperty( project, "name" ) );
    }

    /**
     * <p>testObjectPropertyFromField.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    public void testObjectPropertyFromField()
        throws Exception
    {
        assertEquals( "42", reflector.getObjectProperty( project, "version" ) );
    }

    public static class Project
    {
        private String model;

        private String name;

        private String version;

        public void setModelVersion( String modelVersion )
        {
            this.model = modelVersion;
        }

        public void setVersion( String version )
        {
            this.version = version;
        }

        public String modelVersion()
        {
            return model;
        }

        public String getName()
        {
            return "Foo";
        }
    }
}
