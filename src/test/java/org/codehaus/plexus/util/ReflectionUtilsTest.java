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

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

import java.util.HashMap;

/**
 * This is used to test ReflectionUtils for correctness.
 *
 * @author Jesse McConnell
 * @version $Id:$
 * @see org.codehaus.plexus.util.ReflectionUtils
 * @since 3.4.0
 */
public final class ReflectionUtilsTest
{
    public ReflectionUtilsTestClass testClass = new ReflectionUtilsTestClass();

    /**
     * <p>testSimpleVariableAccess.</p>
     *
     * @throws java.lang.IllegalAccessException if any.
     */
    @Test
    public void testSimpleVariableAccess()
        throws IllegalAccessException
    {
        assertEquals( "woohoo", (String) ReflectionUtils.getValueIncludingSuperclasses( "myString", testClass ) );
    }

    /**
     * <p>testComplexVariableAccess.</p>
     *
     * @throws java.lang.IllegalAccessException if any.
     */
    @Test
    public void testComplexVariableAccess()
        throws IllegalAccessException
    {
        Map<String, Object> map = ReflectionUtils.getVariablesAndValuesIncludingSuperclasses( testClass );

        Map myMap = (Map) map.get( "myMap" );

        assertEquals( "myValue", (String) myMap.get( "myKey" ) );
        assertEquals( "myOtherValue", (String) myMap.get( "myOtherKey" ) );

    }

    /**
     * <p>testSuperClassVariableAccess.</p>
     *
     * @throws java.lang.IllegalAccessException if any.
     */
    @Test
    public void testSuperClassVariableAccess()
        throws IllegalAccessException
    {
        assertEquals( "super-duper",
                      (String) ReflectionUtils.getValueIncludingSuperclasses( "mySuperString", testClass ) );
    }

    /**
     * <p>testSettingVariableValue.</p>
     *
     * @throws java.lang.IllegalAccessException if any.
     */
    @Test
    public void testSettingVariableValue()
        throws IllegalAccessException
    {
        ReflectionUtils.setVariableValueInObject( testClass, "mySettableString", "mySetString" );

        assertEquals( "mySetString",
                      (String) ReflectionUtils.getValueIncludingSuperclasses( "mySettableString", testClass ) );

        ReflectionUtils.setVariableValueInObject( testClass, "myParentsSettableString", "myParentsSetString" );

        assertEquals( "myParentsSetString",
                      (String) ReflectionUtils.getValueIncludingSuperclasses( "myParentsSettableString", testClass ) );
    }

    private class ReflectionUtilsTestClass
        extends AbstractReflectionUtilsTestClass

    {
        private String myString = "woohoo";

        private String mySettableString;

        private Map<String, String> myMap = new HashMap<String, String>();

        public ReflectionUtilsTestClass()
        {
            myMap.put( "myKey", "myValue" );
            myMap.put( "myOtherKey", "myOtherValue" );
        }
    }

    private class AbstractReflectionUtilsTestClass
    {
        private String mySuperString = "super-duper";

        private String myParentsSettableString;
    }
}
