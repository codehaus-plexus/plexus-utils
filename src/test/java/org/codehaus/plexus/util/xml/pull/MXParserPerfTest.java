package org.codehaus.plexus.util.xml.pull;

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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

/**
 * <p>MXParserPerfTest class.</p>
 *
 * @author herve
 * @version $Id: $Id
 * @since 3.4.0
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 3, timeUnit = TimeUnit.SECONDS)
public class MXParserPerfTest {

    @State(Scope.Benchmark)
    static public class AdditionState {

        byte[] data;

        @Setup(Level.Iteration)
        public void setUp() throws IOException, XmlPullParserException {
            try (InputStream buf = getClass().getResourceAsStream( "/xml/pom.xml" ) )
            {
                data = new byte[ buf.available() ];
                buf.read( data, 0, data.length );
            }
        }
    }


    /**
     * <p>benchmarkBuild.</p>
     *
     * @param state a {@link org.codehaus.plexus.util.xml.pull.MXParserPerfTest.AdditionState} object.
     * @return a {@link org.codehaus.plexus.util.xml.Xpp3Dom} object.
     * @throws java.io.IOException if any.
     * @throws org.codehaus.plexus.util.xml.pull.XmlPullParserException if any.
     */
    @Benchmark
    public Xpp3Dom benchmarkBuild( AdditionState state ) throws IOException, XmlPullParserException
    {
        return Xpp3DomBuilder.build( new ByteArrayInputStream( state.data ), null );
    }

    /**
     * <p>main.</p>
     *
     * @param args a {@link java.lang.String} object.
     * @throws org.openjdk.jmh.runner.RunnerException if any.
     */
    public static void main( String... args )
            throws RunnerException
    {
        Options opts = new OptionsBuilder()
                .measurementIterations( 3 )
                .measurementTime( TimeValue.milliseconds( 3000 ) )
                .forks( 1 )
                .include( "org.codehaus.plexus.util.xml.pull.MXParserPerfTest" )
                .build();
        new Runner( opts ).run();
    }
}
