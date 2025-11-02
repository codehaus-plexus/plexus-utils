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

import java.util.Collection;
import java.util.Vector;

/**
 * Manages a number of test threads, which notify this manager when they have completed. Allows TestCases to easily
 * start and manage multiple test threads.
 * <p>
 * Created on 9/06/2003
 * </p>
 *
 * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
 * @since 3.4.0
 */
public class TestThreadManager {

    /** Test threads which have completed running */
    private Collection<AbstractTestThread> runThreads = new Vector<AbstractTestThread>();

    /** Test threads still needing to be run, or are currently running */
    private Collection<AbstractTestThread> toRunThreads = new Vector<AbstractTestThread>();

    /** Any test threads which failed */
    private Vector<AbstractTestThread> failedThreads = new Vector<AbstractTestThread>();

    /**
     * The object to notify when all the test threads have completed. Clients use this to lock on (wait) while waiting
     * for the tests to complete
     */
    private Object notify = null;

    public TestThreadManager(Object notify) {
        super();
        this.notify = notify;
    }

    public Collection<AbstractTestThread> getRunThreads() {
        return runThreads;
    }

    public void runTestThreads() {
        failedThreads.clear();
        // use an array as the tests may run very quickly
        // and modify the toRunThreads vector and hence
        // cause a Concurrent ModificationException on an
        // iterator
        for (AbstractTestThread toRunThread : toRunThreads) {
            toRunThread.start();
        }
    }

    public Collection<AbstractTestThread> getFailedTests() {
        return failedThreads;
    }

    /**
     * Return the object which threads can wait on to be notified when all the test threads have completed running
     *
     * @return a {@link java.lang.Object} object.
     */
    public Object getNotifyObject() {
        return notify;
    }

    public boolean hasFailedThreads() {
        return !failedThreads.isEmpty();
    }

    public boolean isStillRunningThreads() {
        return !toRunThreads.isEmpty();
    }

    public Collection<AbstractTestThread> getToRunThreads() {
        return toRunThreads;
    }

    public void clear() {
        toRunThreads.clear();
        runThreads.clear();
        failedThreads.clear();
    }

    public synchronized void completed(AbstractTestThread thread) {
        toRunThreads.remove(thread);
        runThreads.add(thread);
        if (thread.hasFailed()) {
            failedThreads.add(thread);
        }
        // wakeup thread which is waiting for the threads to complete
        // execution
        if (toRunThreads.isEmpty()) {
            synchronized (notify) {
                notify.notify();
            }
        }
    }

    /**
     * Override this to add your own stuff. Called after <code>registerThread(Object)</code>
     *
     * @param thread DOCUMENT ME!
     */
    public void doRegisterThread(AbstractTestThread thread) {}

    public final void registerThread(AbstractTestThread thread) {
        thread.setThreadRegistry(this);
        if (toRunThreads.contains(thread) == false) {
            toRunThreads.add(thread);
            doRegisterThread(thread);
        }
    }

    /**
     * Put all the runThreads back in the que to be run again and clear the failedTest collection
     */
    public void reset() {
        toRunThreads.clear();
        for (AbstractTestThread runThread : runThreads) {
            runThread.reset();
            registerThread(runThread);
        }

        runThreads.clear();
        failedThreads.clear();
    }
}
