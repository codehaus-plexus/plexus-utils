package org.codehaus.plexus.util.introspection;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class ReflectionValueExtractorTest
    extends TestCase
{
    private Project project;

    protected void setUp()
        throws Exception
    {
        super.setUp();

        Dependency dependency1 = new Dependency();
        dependency1.setArtifactId( "dep1" );
        Dependency dependency2 = new Dependency();
        dependency2.setArtifactId( "dep2" );

        project = new Project();
        project.setModelVersion( "4.0.0" );
        project.setGroupId( "org.apache.maven" );
        project.setArtifactId( "maven-core" );
        project.setName( "Maven" );
        project.setVersion( "2.0-SNAPSHOT" );
        project.setScm( new Scm() );
        project.getScm().setConnection( "scm-connection" );
        project.addDependency( dependency1 );
        project.addDependency( dependency2 );
        project.setBuild( new Build() );

        // Build up an artifactMap
        project.addArtifact( new Artifact( "g0", "a0", "v0", "e0", "c0" ) );
        project.addArtifact( new Artifact( "g1", "a1", "v1", "e1", "c1" ) );
        project.addArtifact( new Artifact( "g2", "a2", "v2", "e2", "c2" ) );
    }

    public void testValueExtraction()
        throws Exception
    {
        // ----------------------------------------------------------------------
        // Top level values
        // ----------------------------------------------------------------------

        assertEquals( "4.0.0", ReflectionValueExtractor.evaluate( "project.modelVersion", project ) );

        assertEquals( "org.apache.maven", ReflectionValueExtractor.evaluate( "project.groupId", project ) );

        assertEquals( "maven-core", ReflectionValueExtractor.evaluate( "project.artifactId", project ) );

        assertEquals( "Maven", ReflectionValueExtractor.evaluate( "project.name", project ) );

        assertEquals( "2.0-SNAPSHOT", ReflectionValueExtractor.evaluate( "project.version", project ) );

        // ----------------------------------------------------------------------
        // SCM
        // ----------------------------------------------------------------------

        assertEquals( "scm-connection", ReflectionValueExtractor.evaluate( "project.scm.connection", project ) );

        // ----------------------------------------------------------------------
        // Dependencies
        // ----------------------------------------------------------------------

        List dependencies = (List) ReflectionValueExtractor.evaluate( "project.dependencies", project );

        Assert.assertNotNull( dependencies );

        Assert.assertEquals( 2, dependencies.size() );

        // ----------------------------------------------------------------------
        // Dependencies - using index notation
        // ----------------------------------------------------------------------

        // List
        Dependency dependency = (Dependency) ReflectionValueExtractor.evaluate( "project.dependencies[0]", project );

        Assert.assertNotNull( dependency );

        Assert.assertTrue( "dep1".equals( dependency.getArtifactId() ) );

        String artifactId = (String) ReflectionValueExtractor.evaluate( "project.dependencies[1].artifactId", project );

        Assert.assertTrue( "dep2".equals( artifactId ) );

        // Array

        dependency = (Dependency) ReflectionValueExtractor.evaluate( "project.dependenciesAsArray[0]", project );

        Assert.assertNotNull( dependency );

        Assert.assertTrue( "dep1".equals( dependency.getArtifactId() ) );

        artifactId = (String) ReflectionValueExtractor.evaluate( "project.dependenciesAsArray[1].artifactId", project );

        Assert.assertTrue( "dep2".equals( artifactId ) );

        // Map

        dependency = (Dependency) ReflectionValueExtractor.evaluate( "project.dependenciesAsMap(dep1)", project );

        Assert.assertNotNull( dependency );

        Assert.assertTrue( "dep1".equals( dependency.getArtifactId() ) );

        artifactId =
            (String) ReflectionValueExtractor.evaluate( "project.dependenciesAsMap(dep2).artifactId", project );

        Assert.assertTrue( "dep2".equals( artifactId ) );

        // ----------------------------------------------------------------------
        // Build
        // ----------------------------------------------------------------------

        Build build = (Build) ReflectionValueExtractor.evaluate( "project.build", project );

        Assert.assertNotNull( build );
    }

    public void testValueExtractorWithAInvalidExpression()
        throws Exception
    {
        Assert.assertNull( ReflectionValueExtractor.evaluate( "project.foo", project ) );
        Assert.assertNull( ReflectionValueExtractor.evaluate( "project.dependencies[10]", project ) );
        Assert.assertNull( ReflectionValueExtractor.evaluate( "project.dependencies[0].foo", project ) );
    }

    public void testMappedDottedKey()
        throws Exception
    {
        Map<String, String> map = new HashMap<String, String>();
        map.put( "a.b", "a.b-value" );

        Assert.assertEquals( "a.b-value", ReflectionValueExtractor.evaluate( "h.value(a.b)", new ValueHolder( map ) ) );
    }

    public void testIndexedMapped()
        throws Exception
    {
        Map<Object, Object> map = new HashMap<Object, Object>();
        map.put( "a", "a-value" );
        List<Object> list = new ArrayList<Object>();
        list.add( map );

        Assert.assertEquals( "a-value", ReflectionValueExtractor.evaluate( "h.value[0](a)", new ValueHolder( list ) ) );
    }

    public void testMappedIndexed()
        throws Exception
    {
        List<Object> list = new ArrayList<Object>();
        list.add( "a-value" );
        Map<Object, Object> map = new HashMap<Object, Object>();
        map.put( "a", list );
        Assert.assertEquals( "a-value", ReflectionValueExtractor.evaluate( "h.value(a)[0]", new ValueHolder( map ) ) );
    }

    public void testMappedMissingDot()
        throws Exception
    {
        Map<Object, Object> map = new HashMap<Object, Object>();
        map.put( "a", new ValueHolder( "a-value" ) );
        Assert.assertNull( ReflectionValueExtractor.evaluate( "h.value(a)value", new ValueHolder( map ) ) );
    }

    public void testIndexedMissingDot()
        throws Exception
    {
        List<Object> list = new ArrayList<Object>();
        list.add( new ValueHolder( "a-value" ) );
        Assert.assertNull( ReflectionValueExtractor.evaluate( "h.value[0]value", new ValueHolder( list ) ) );
    }

    public void testDotDot()
        throws Exception
    {
        Assert.assertNull( ReflectionValueExtractor.evaluate( "h..value", new ValueHolder( "value" ) ) );
    }

    public void testBadIndexedSyntax()
        throws Exception
    {
        List<Object> list = new ArrayList<Object>();
        list.add( "a-value" );
        Object value = new ValueHolder( list );

        Assert.assertNull( ReflectionValueExtractor.evaluate( "h.value[", value ) );
        Assert.assertNull( ReflectionValueExtractor.evaluate( "h.value[]", value ) );
        Assert.assertNull( ReflectionValueExtractor.evaluate( "h.value[a]", value ) );
        Assert.assertNull( ReflectionValueExtractor.evaluate( "h.value[0", value ) );
        Assert.assertNull( ReflectionValueExtractor.evaluate( "h.value[0)", value ) );
        Assert.assertNull( ReflectionValueExtractor.evaluate( "h.value[-1]", value ) );
    }

    public void testBadMappedSyntax()
        throws Exception
    {
        Map<Object, Object> map = new HashMap<Object, Object>();
        map.put( "a", "a-value" );
        Object value = new ValueHolder( map );

        Assert.assertNull( ReflectionValueExtractor.evaluate( "h.value(", value ) );
        Assert.assertNull( ReflectionValueExtractor.evaluate( "h.value()", value ) );
        Assert.assertNull( ReflectionValueExtractor.evaluate( "h.value(a", value ) );
        Assert.assertNull( ReflectionValueExtractor.evaluate( "h.value(a]", value ) );
    }

    public void testIllegalIndexedType()
        throws Exception
    {
        try
        {
            ReflectionValueExtractor.evaluate( "h.value[1]", new ValueHolder( "string" ) );
        }
        catch ( Exception e )
        {
            // TODO assert exception message
        }
    }

    public void testIllegalMappedType()
        throws Exception
    {
        try
        {
            ReflectionValueExtractor.evaluate( "h.value(key)", new ValueHolder( "string" ) );
        }
        catch ( Exception e )
        {
            // TODO assert exception message
        }
    }

    public void testTrimRootToken()
        throws Exception
    {
        Assert.assertNull( ReflectionValueExtractor.evaluate( "project", project, true ) );
    }

    public void testArtifactMap()
        throws Exception
    {
        assertEquals( "g0", ( (Artifact) ReflectionValueExtractor.evaluate( "project.artifactMap(g0:a0:c0)",
                                                                            project ) ).getGroupId() );
        assertEquals( "a1", ( (Artifact) ReflectionValueExtractor.evaluate( "project.artifactMap(g1:a1:c1)",
                                                                            project ) ).getArtifactId() );
        assertEquals( "c2", ( (Artifact) ReflectionValueExtractor.evaluate( "project.artifactMap(g2:a2:c2)",
                                                                            project ) ).getClassifier() );
    }

    public static class Artifact
    {
        private String groupId;

        private String artifactId;

        private String version;

        private String extension;

        private String classifier;

        public Artifact( String groupId, String artifactId, String version, String extension, String classifier )
        {
            this.groupId = groupId;
            this.artifactId = artifactId;
            this.version = version;
            this.extension = extension;
            this.classifier = classifier;
        }

        public String getGroupId()
        {
            return groupId;
        }

        public void setGroupId( String groupId )
        {
            this.groupId = groupId;
        }

        public String getArtifactId()
        {
            return artifactId;
        }

        public void setArtifactId( String artifactId )
        {
            this.artifactId = artifactId;
        }

        public String getVersion()
        {
            return version;
        }

        public void setVersion( String version )
        {
            this.version = version;
        }

        public String getExtension()
        {
            return extension;
        }

        public void setExtension( String extension )
        {
            this.extension = extension;
        }

        public String getClassifier()
        {
            return classifier;
        }

        public void setClassifier( String classifier )
        {
            this.classifier = classifier;
        }
    }

    public static class Project
    {
        private String modelVersion;

        private String groupId;

        private Scm scm;

        private List dependencies = new ArrayList();

        private Build build;

        private String artifactId;

        private String name;

        private String version;

        private Map<String, Artifact> artifactMap = new HashMap<String, Artifact>();

        public void setModelVersion( String modelVersion )
        {
            this.modelVersion = modelVersion;
        }

        public void setGroupId( String groupId )
        {
            this.groupId = groupId;
        }

        public void setScm( Scm scm )
        {
            this.scm = scm;
        }

        public void addDependency( Dependency dependency )
        {
            this.dependencies.add( dependency );
        }

        public void setBuild( Build build )
        {
            this.build = build;
        }

        public void setArtifactId( String artifactId )
        {
            this.artifactId = artifactId;
        }

        public void setName( String name )
        {
            this.name = name;
        }

        public void setVersion( String version )
        {
            this.version = version;
        }

        public Scm getScm()
        {
            return scm;
        }

        public String getModelVersion()
        {
            return modelVersion;
        }

        public String getGroupId()
        {
            return groupId;
        }

        public List getDependencies()
        {
            return dependencies;
        }

        public Build getBuild()
        {
            return build;
        }

        public String getArtifactId()
        {
            return artifactId;
        }

        public String getName()
        {
            return name;
        }

        public String getVersion()
        {
            return version;
        }

        public Dependency[] getDependenciesAsArray()
        {
            return (Dependency[]) getDependencies().toArray( new Dependency[0] );
        }

        public Map getDependenciesAsMap()
        {
            Map ret = new HashMap();
            for ( Object o : getDependencies() )
            {
                Dependency dep = (Dependency) o;
                ret.put( dep.getArtifactId(), dep );
            }
            return ret;
        }

        // ${project.artifactMap(g:a:v)}
        public void addArtifact( Artifact a )
        {
            artifactMap.put( a.getGroupId() + ":" + a.getArtifactId() + ":" + a.getClassifier(), a );
        }

        public Map<String, Artifact> getArtifactMap()
        {
            return artifactMap;
        }
    }

    public static class Build
    {

    }

    public static class Dependency
    {
        private String artifactId;

        public String getArtifactId()
        {
            return artifactId;
        }

        public void setArtifactId( String id )
        {
            artifactId = id;
        }
    }

    public static class Scm
    {
        private String connection;

        public void setConnection( String connection )
        {
            this.connection = connection;
        }

        public String getConnection()
        {
            return connection;
        }
    }

    public static class ValueHolder
    {
        private final Object value;

        public ValueHolder( Object value )
        {
            this.value = value;
        }

        public Object getValue()
        {
            return value;
        }
    }

    public void testRootPropertyRegression()
        throws Exception
    {
        MavenProjectStub project = new MavenProjectStub();
        project.setDescription( "c:\\\\org\\apache\\test" );
        Object evalued = ReflectionValueExtractor.evaluate( "description", project );
        assertNotNull( evalued );
    }
}