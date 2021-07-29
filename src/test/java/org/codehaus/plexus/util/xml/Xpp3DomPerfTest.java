package org.codehaus.plexus.util.xml;

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

import java.io.IOException;
import java.io.StringReader;
import java.util.concurrent.TimeUnit;

import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
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
 * <p>Xpp3DomPerfTest class.</p>
 *
 * @author herve
 * @version $Id: $Id
 * @since 3.4.0
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 3, timeUnit = TimeUnit.SECONDS)
public class Xpp3DomPerfTest
{
    @State(Scope.Benchmark)
    static public class AdditionState {
        Xpp3Dom dom1;
        Xpp3Dom dom2;

        @Setup(Level.Iteration)
        public void setUp() throws IOException, XmlPullParserException {
            String testDom = "<configuration><items thing='blah'><item>one</item><item>two</item></items></configuration>";
            dom1 = Xpp3DomBuilder.build( new StringReader( testDom ) );
            dom2 = new Xpp3Dom( dom1 );
        }
    }


    /**
     * <p>benchmarkClone.</p>
     *
     * @param state a {@link org.codehaus.plexus.util.xml.Xpp3DomPerfTest.AdditionState} object.
     * @return a {@link org.codehaus.plexus.util.xml.Xpp3Dom} object.
     */
    @Benchmark
    public Xpp3Dom benchmarkClone(AdditionState state)
    {
        return new Xpp3Dom( state.dom1 );
    }

    /**
     * <p>benchmarkMerge.</p>
     *
     * @param state a {@link org.codehaus.plexus.util.xml.Xpp3DomPerfTest.AdditionState} object.
     */
    @Benchmark
    public void benchmarkMerge(AdditionState state)
    {
        Xpp3Dom.mergeXpp3Dom( state.dom1, state.dom2 );
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
                .build();
        new Runner( opts ).run();
    }
}
