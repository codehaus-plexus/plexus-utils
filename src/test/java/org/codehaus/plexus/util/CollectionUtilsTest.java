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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.Test;

public class CollectionUtilsTest
{
    @Test
    public void testMergeMaps()
    {
        Map<String, String> dominantMap = new HashMap<String, String>();
        dominantMap.put( "a", "a" );
        dominantMap.put( "b", "b" );
        dominantMap.put( "c", "c" );
        dominantMap.put( "d", "d" );
        dominantMap.put( "e", "e" );
        dominantMap.put( "f", "f" );

        Map<String, String> recessiveMap = new HashMap<String, String>();
        recessiveMap.put( "a", "invalid" );
        recessiveMap.put( "b", "invalid" );
        recessiveMap.put( "c", "invalid" );
        recessiveMap.put( "x", "x" );
        recessiveMap.put( "y", "y" );
        recessiveMap.put( "z", "z" );

        Map<String, String> result = CollectionUtils.mergeMaps( dominantMap, recessiveMap );

        // We should have 9 elements
        assertEquals( 9, result.keySet().size() );

        // Check the elements.
        assertEquals( "a", result.get( "a" ) );
        assertEquals( "b", result.get( "b" ) );
        assertEquals( "c", result.get( "c" ) );
        assertEquals( "d", result.get( "d" ) );
        assertEquals( "e", result.get( "e" ) );
        assertEquals( "f", result.get( "f" ) );
        assertEquals( "x", result.get( "x" ) );
        assertEquals( "y", result.get( "y" ) );
        assertEquals( "z", result.get( "z" ) );
    }

    @SuppressWarnings( "unchecked" )
    @Test
    public void testMergeMapArray()
    {
        // Test empty array of Maps
        Map<String, String> result0 = CollectionUtils.mergeMaps( new Map[] {} );

        assertNull( result0 );

        // Test with an array with a single element.
        Map<String, String> map1 = new HashMap<String, String>();
        map1.put( "a", "a" );

        Map<String, String> result1 = CollectionUtils.mergeMaps( new Map[] { map1 } );

        assertEquals( "a", result1.get( "a" ) );

        // Test with an array with two elements.
        Map<String, String> map2 = new HashMap<String, String>();
        map2.put( "a", "aa" );
        map2.put( "b", "bb" );

        Map<String, String> result2 = CollectionUtils.mergeMaps( new Map[] { map1, map2 } );

        assertEquals( "a", result2.get( "a" ) );
        assertEquals( "bb", result2.get( "b" ) );

        // Now swap the dominant order.
        Map<String, String> result3 = CollectionUtils.mergeMaps( new Map[] { map2, map1 } );

        assertEquals( "aa", result3.get( "a" ) );
        assertEquals( "bb", result3.get( "b" ) );

        // Test with an array with three elements.
        Map<String, String> map3 = new HashMap<String, String>();
        map3.put( "a", "aaa" );
        map3.put( "b", "bbb" );
        map3.put( "c", "ccc" );

        Map<String, String> result4 = CollectionUtils.mergeMaps( new Map[] { map1, map2, map3 } );

        assertEquals( "a", result4.get( "a" ) );
        assertEquals( "bb", result4.get( "b" ) );
        assertEquals( "ccc", result4.get( "c" ) );

        // Now swap the dominant order.
        Map<String, String> result5 = CollectionUtils.mergeMaps( new Map[] { map3, map2, map1 } );

        assertEquals( "aaa", result5.get( "a" ) );
        assertEquals( "bbb", result5.get( "b" ) );
        assertEquals( "ccc", result5.get( "c" ) );
    }

    @Test
    public void testMavenPropertiesLoading()
    {
        // Mimic MavenSession properties loading. Properties listed
        // in dominant order.
        Properties systemProperties = new Properties();
        Properties userBuildProperties = new Properties();
        Properties projectBuildProperties = new Properties();
        Properties projectProperties = new Properties();
        Properties driverProperties = new Properties();

        // System properties
        systemProperties.setProperty( "maven.home", "/projects/maven" );

        // User build properties
        userBuildProperties.setProperty( "maven.username", "jvanzyl" );
        userBuildProperties.setProperty( "maven.repo.remote.enabled", "false" );
        userBuildProperties.setProperty( "maven.repo.local", "/opt/maven/artifact" );

        // Project build properties
        projectBuildProperties.setProperty( "maven.final.name", "maven" );

        String mavenRepoRemote = "http://www.ibiblio.org/maven,http://foo/bar";

        // Project properties
        projectProperties.setProperty( "maven.repo.remote", mavenRepoRemote );

        String basedir = "/home/jvanzyl/projects/maven";

        // Driver properties
        driverProperties.setProperty( "basedir", basedir );
        driverProperties.setProperty( "maven.build.src", "${basedir}/src" );
        driverProperties.setProperty( "maven.build.dir", "${basedir}/target" );
        driverProperties.setProperty( "maven.build.dest", "${maven.build.dir}/classes" );
        driverProperties.setProperty( "maven.repo.remote", "http://www.ibiblio.org/maven" );
        driverProperties.setProperty( "maven.final.name", "maven-1.0" );
        driverProperties.setProperty( "maven.repo.remote.enabled", "true" );
        driverProperties.setProperty( "maven.repo.local", "${maven.home}/artifact" );

        Map result = CollectionUtils.mergeMaps( new Map[] { systemProperties, userBuildProperties,
            projectBuildProperties, projectProperties, driverProperties } );

        // Values that should be taken from systemProperties.
        assertEquals( "/projects/maven", (String) result.get( "maven.home" ) );

        // Values that should be taken from userBuildProperties.
        assertEquals( "/opt/maven/artifact", (String) result.get( "maven.repo.local" ) );
        assertEquals( "false", (String) result.get( "maven.repo.remote.enabled" ) );
        assertEquals( "jvanzyl", (String) result.get( "maven.username" ) );

        // Values take from projectBuildProperties.
        assertEquals( "maven", (String) result.get( "maven.final.name" ) );

        // Values take from projectProperties.
        assertEquals( mavenRepoRemote, (String) result.get( "maven.repo.remote" ) );
    }

    @Test
    public void testIteratorToListWithAPopulatedList()
    {
        List<String> original = new ArrayList<String>();

        original.add( "en" );
        original.add( "to" );
        original.add( "tre" );

        List<String> copy = CollectionUtils.iteratorToList( original.iterator() );

        assertNotNull( copy );

        assertEquals( 3, copy.size() );

        assertEquals( "en", copy.get( 0 ) );
        assertEquals( "to", copy.get( 1 ) );
        assertEquals( "tre", copy.get( 2 ) );
    }

    @Test
    public void testIteratorToListWithAEmptyList()
    {
        List<String> original = new ArrayList<String>();

        List<String> copy = CollectionUtils.iteratorToList( original.iterator() );

        assertNotNull( copy );

        assertEquals( 0, copy.size() );
    }
}
