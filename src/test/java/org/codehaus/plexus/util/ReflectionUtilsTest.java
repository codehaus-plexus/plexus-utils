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

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This is used to test ReflectionUtils for correctness.
 *
 * @author Jesse McConnell
 * @version $Id:$
 * @see org.codehaus.plexus.util.ReflectionUtils
 * @since 3.4.0
 */
public final class ReflectionUtilsTest {
    private final ReflectionUtilsTestClass testClass = new ReflectionUtilsTestClass();

    /**
     * <p>testSimpleVariableAccess.</p>
     *
     * @throws java.lang.IllegalAccessException if any.
     */
    @Test
    void simpleVariableAccess() throws IllegalAccessException {
        assertEquals("woohoo", ReflectionUtils.getValueIncludingSuperclasses("myString", testClass));
    }

    /**
     * <p>testComplexVariableAccess.</p>
     *
     * @throws java.lang.IllegalAccessException if any.
     */
    @Test
    void complexVariableAccess() throws IllegalAccessException {
        Map<String, Object> map = ReflectionUtils.getVariablesAndValuesIncludingSuperclasses(testClass);

        Map myMap = (Map) map.get("myMap");

        assertEquals("myValue", myMap.get("myKey"));
        assertEquals("myOtherValue", myMap.get("myOtherKey"));
    }

    /**
     * <p>testSuperClassVariableAccess.</p>
     *
     * @throws java.lang.IllegalAccessException if any.
     */
    @Test
    void superClassVariableAccess() throws IllegalAccessException {
        assertEquals("super-duper", ReflectionUtils.getValueIncludingSuperclasses("mySuperString", testClass));
    }

    /**
     * <p>testSettingVariableValue.</p>
     *
     * @throws java.lang.IllegalAccessException if any.
     */
    @Test
    void settingVariableValue() throws IllegalAccessException {
        ReflectionUtils.setVariableValueInObject(testClass, "mySettableString", "mySetString");

        assertEquals("mySetString", ReflectionUtils.getValueIncludingSuperclasses("mySettableString", testClass));

        ReflectionUtils.setVariableValueInObject(testClass, "myParentsSettableString", "myParentsSetString");

        assertEquals(
                "myParentsSetString",
                ReflectionUtils.getValueIncludingSuperclasses("myParentsSettableString", testClass));
    }

    private class ReflectionUtilsTestClass extends AbstractReflectionUtilsTestClass {

        private String myString = "woohoo";

        private String mySettableString;

        private Map<String, String> myMap = new HashMap<>();

        public ReflectionUtilsTestClass() {
            myMap.put("myKey", "myValue");
            myMap.put("myOtherKey", "myOtherValue");
        }
    }

    private class AbstractReflectionUtilsTestClass {
        private String mySuperString = "super-duper";

        private String myParentsSettableString;
    }
}
