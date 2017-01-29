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

import junit.framework.TestCase;

import java.util.Vector;

/**
 * Created on 21/06/2003
 *
 * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
 * @version $Revision$
 */
public class SweeperPoolTest extends TestCase
{
    /** The pool under test */
    TestObjectPool pool;
    /** A bunch of object to pool */
    Object o1;
    Object o2;
    Object o3;
    Object o4;
    Object o5;
    Object o6;

    /**
     * Constructor
     *
     *
     */
    public SweeperPoolTest()
    {
        super();
    }

    /**
     * Constructor
     *
     * @param arg0
     */
    public SweeperPoolTest( String arg0 )
    {
        super( arg0 );
    }

    /**
     * Test the pool limits it's size, and disposes unneeded objects correctly
     *
     */
    public void testMaxSize()
    {
        int sweepInterval = 0;
        int initialCapacity = 5;
        int maxSize = 2;
        int minSize = 1;
        int triggerSize = 2;

        pool =
            new TestObjectPool(
                maxSize,
                minSize,
                initialCapacity,
                sweepInterval,
                triggerSize );

        Object tmp = pool.get();
        assertNull( "Expected object from pool to be null", tmp );
        pool.put( o1 );
        assertEquals( "Expected pool to contain 1 object", 1, pool.getSize() );
        tmp = pool.get();
        assertSame(
            "Expected returned pool object to be the same as the one put in",
            tmp,
            o1 );
        pool.put( o1 );
        pool.put( o2 );
        assertEquals( "Expected pool to contain 2 objects", 2, pool.getSize() );
        pool.put( o3 );
        assertEquals(
            "Expected pool to contain only a maximum of 2 objects.",
            2,
            pool.getSize() );
        assertEquals(
            "Expected 1 disposed pool object",
            1,
            pool.testGetDisposedObjects().size() );
        tmp = pool.testGetDisposedObjects().iterator().next();

        tmp = pool.get();
        assertEquals(
            "Expected pool size to be 1 after removing one object",
            1,
            pool.getSize() );
        Object tmp2 = pool.get();
        assertEquals(
            "Expected pool size to be 0 after removing 2 objects",
            0,
            pool.getSize() );
        assertNotSame( "Expected returned objects to be different", tmp, tmp2 );

    }

    public void testSweepAndTrim1()
    {
        //test trigger
        int sweepInterval = 1;
        int initialCapacity = 5;
        int maxSize = 5;
        int minSize = 1;
        int triggerSize = 2;

        pool =
            new TestObjectPool(
                maxSize,
                minSize,
                initialCapacity,
                sweepInterval,
                triggerSize );
        pool.put( o1 );
        pool.put( o2 );
        pool.put( o3 );
        pool.put( o4 );
        //give the sweeper some time to run
        synchronized ( this )
        {
            try
            {
                wait( 2 * 1000 );
            }
            catch ( InterruptedException e )
            {
                fail(
                    "Unexpected exception thrown. e="
                    + Tracer.traceToString( e ) );
            }
        }
        assertEquals(
            "Expected pool to only contain 1 object",
            1,
            pool.getSize() );
        assertEquals(
            "Expected 3 disposed objects",
            3,
            pool.testGetDisposedObjects().size() );

    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {

        o1 = new Object();
        o2 = new Object();
        o3 = new Object();
        o4 = new Object();
        o5 = new Object();
        o6 = new Object();
        super.setUp();
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        pool.dispose();
        assertTrue( pool.isDisposed() );
        pool = null;
        super.tearDown();

    }

    class TestObjectPool extends SweeperPool
    {
        private Vector<Object> disposedObjects = new Vector<Object>();

        public TestObjectPool(
            int maxSize,
            int minSize,
            int intialCapacity,
            int sweepInterval,
            int triggerSize )
        {
            super( maxSize, minSize, intialCapacity, sweepInterval, triggerSize );
        }

        public void reset()
        {
            disposedObjects.clear();
        }

        /**
         * @see nz.co.bonzo.beans.castor.pool.ObjectPool#objectDisposed(java.lang.Object)
         */
        public void objectDisposed( Object obj )
        {
            disposedObjects.add( obj );
        }

        public Vector<Object> testGetDisposedObjects()
        {
            return disposedObjects;
        }

    }

}
