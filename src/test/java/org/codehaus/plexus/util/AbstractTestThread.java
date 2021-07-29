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

/**
 * A thread which is registered with a ThreadRegistry and notifies it when it has completed running. Collects any errors
 * and makes it available for analysis.
 * <p>
 * Created on 1/07/2003
 * </p>
 *
 * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
 * @version $Id: $Id
 * @since 3.4.0
 */
public abstract class AbstractTestThread
    implements Runnable
{
    // ~ Instance fields ----------------------------------------------------------------------------
    private String name;

    /** Constant <code>DEBUG=true</code> */
    public static final boolean DEBUG = true;

    private boolean isRunning = false;

    /** Error msg provided by implementing class (of why the test failed) */
    private String errorMsg = null;

    /** The registry to notify on completion */
    private TestThreadManager registry;

    /**
     * The error thrown when running the test. Not necessarily a test failure as some tests may test for an exception
     */
    private Throwable error;

    /** If the thread has been run */
    private boolean hasRun = false;

    /**
     * Flag indicating if the test has passed. Some test might require an exception so using the error to determine if
     * the test has passed is not sufficient.
     */
    private boolean passed = false;

    // ~ Constructors -------------------------------------------------------------------------------

    /**
     * Constructor
     * <p>
     * Remember to call <code>setThreadRegistry(ThreadRegistry)</code>
     */
    public AbstractTestThread()
    {
        super();
    }

    /**
     * <p>Constructor for AbstractTestThread.</p>
     *
     * @param registry a {@link org.codehaus.plexus.util.TestThreadManager} object.
     */
    public AbstractTestThread( TestThreadManager registry )
    {
        super();
        setThreadRegistry( registry );
    }

    // ~ Methods ------------------------------------------------------------------------------------

    /**
     * <p>Getter for the field <code>error</code>.</p>
     *
     * @return a {@link java.lang.Throwable} object.
     */
    public Throwable getError()
    {
        return error;
    }

    /**
     * Resets the test back to it's state before starting. If the test is currently running this method will block until
     * the test has finished running. Subclasses should call this method if overriding it.
     */
    public void reset()
    {
        // shouldn't reset until the test has finished running
        synchronized ( this )
        {
            while ( isRunning )
            {
                try
                {
                    wait();
                }
                catch ( InterruptedException e )
                {

                }
            }
            errorMsg = null;
            error = null;
            hasRun = false;
            passed = false;
        }
    }

    /**
     * Start this TestThread running. If the test is currently running then this method does nothing.
     */
    public final void start()
    {
        // shouldn't have multiple threads running this test at the same time
        synchronized ( this )
        {
            if ( isRunning == false )
            {
                isRunning = true;
                Thread t = new Thread( this );
                t.start();
            }
        }
    }

    /**
     * <p>Getter for the field <code>errorMsg</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getErrorMsg()
    {
        return errorMsg;
    }

    /**
     * <p>hasFailed.</p>
     *
     * @return a boolean.
     */
    public boolean hasFailed()
    {
        return !passed;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean hasPassed()
    {
        return passed;
    }

    /**
     * Don't override this. Calls <code>doRun()</code>
     *
     * @see java.lang.Runnable#run()
     */
    public final void run()
    {
        if ( registry == null )
        {
            throw new IllegalArgumentException( "The ThreadRegistry is null. Ensure this is set before running this thread" );
        }
        passed = false;
        try
        {
            doRun();
        }
        catch ( Throwable t )
        {
            error = t;
        }

        registry.completed( this );
        hasRun = true;
        isRunning = false;
        // notify objects with blocked methods which are waiting
        // on this test to complete running
        synchronized ( this )
        {
            notifyAll();
        }
    }

    /**
     * Override this to run your custom test
     *
     * @throws java.lang.Throwable
     */
    public abstract void doRun()
        throws Throwable;

    /**
     * Set the registry this thread should notify when it has completed running
     *
     * @param registry a {@link org.codehaus.plexus.util.TestThreadManager} object.
     */
    public void setThreadRegistry( TestThreadManager registry )

    {
        this.registry = registry;
    }

    /**
     * Test if the test has run
     *
     * @return a boolean.
     */
    public boolean hasRun()
    {
        return hasRun;
    }

    /**
     * <p>Setter for the field <code>error</code>.</p>
     *
     * @param throwable a {@link java.lang.Throwable} object.
     */
    public void setError( Throwable throwable )
    {
        error = throwable;
    }

    /**
     * <p>Setter for the field <code>errorMsg</code>.</p>
     *
     * @param string a {@link java.lang.String} object.
     */
    public void setErrorMsg( String string )
    {
        errorMsg = string;
    }

    /**
     * <p>Setter for the field <code>passed</code>.</p>
     *
     * @param b a boolean.
     */
    public void setPassed( boolean b )
    {
        passed = b;
    }

    /**
     * <p>Getter for the field <code>name</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getName()
    {
        return name;
    }

    /**
     * <p>Setter for the field <code>name</code>.</p>
     *
     * @param string a {@link java.lang.String} object.
     */
    public void setName( String string )
    {
        name = string;
    }

    private final void debug( String msg )
    {
        if ( DEBUG )
        {
            System.out.println( this + ":" + msg );
        }
    }
}
