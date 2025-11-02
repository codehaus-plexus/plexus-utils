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
 * @see org.codehaus.plexus.util.ReflectionUtils
 * @since 3.4.0
 */
final class ReflectionUtilsTest {
    private final ReflectionUtilsTestClass testClass = new ReflectionUtilsTestClass();

    @Test
    void simpleVariableAccess() throws Exception {
        assertEquals("woohoo", ReflectionUtils.getValueIncludingSuperclasses("myString", testClass));
    }

    @Test
    void complexVariableAccess() throws Exception {
        Map<String, Object> map = ReflectionUtils.getVariablesAndValuesIncludingSuperclasses(testClass);

        Map myMap = (Map) map.get("myMap");

        assertEquals("myValue", myMap.get("myKey"));
        assertEquals("myOtherValue", myMap.get("myOtherKey"));
    }

    @Test
    void superClassVariableAccess() throws Exception {
        assertEquals("super-duper", ReflectionUtils.getValueIncludingSuperclasses("mySuperString", testClass));
    }

    @Test
    void settingVariableValue() throws Exception {
        ReflectionUtils.setVariableValueInObject(testClass, "mySettableString", "mySetString");

        assertEquals("mySetString", ReflectionUtils.getValueIncludingSuperclasses("mySettableString", testClass));

        ReflectionUtils.setVariableValueInObject(testClass, "myParentsSettableString", "myParentsSetString");

        assertEquals(
                "myParentsSetString",
                ReflectionUtils.getValueIncludingSuperclasses("myParentsSettableString", testClass));
    }

    @SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
    private static class ReflectionUtilsTestClass extends AbstractReflectionUtilsTestClass {

        private String myString = "woohoo";

        private String mySettableString;

        @SuppressWarnings("CanBeFinal")
        private Map<String, String> myMap = new HashMap<>();

        public ReflectionUtilsTestClass() {
            myMap.put("myKey", "myValue");
            myMap.put("myOtherKey", "myOtherValue");
        }
    }

    @SuppressWarnings("FieldMayBeFinal")
    private static class AbstractReflectionUtilsTestClass {
        private String mySuperString = "super-duper";

        private String myParentsSettableString;
    }
}
