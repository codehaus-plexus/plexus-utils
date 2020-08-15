package org.codehaus.plexus.util.cli;

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
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Read from an InputStream and write the output to an OutputStream.
 *
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 *
 */
public class StreamFeeder
    extends AbstractStreamHandler
{

    private InputStream input;

    private OutputStream output;

    private volatile Throwable exception = null;

    /**
     * Create a new StreamFeeder
     *
     * @param input Stream to read from
     * @param output Stream to write to
     */
    public StreamFeeder( InputStream input, OutputStream output )
    {
        super();
        this.input = input;
        this.output = output;
    }

    @Override
    public void run()
    {
        try
        {
            feed();
        }
        catch ( Throwable ex )
        {
            if ( exception == null )
            {
                exception = ex;
            }
        }
        finally
        {
            close();

            synchronized ( this )
            {
                setDone();

                this.notifyAll();
            }
        }
    }

    public void close()
    {
        if ( input != null )
        {
            synchronized ( input )
            {
                try
                {
                    input.close();
                }
                catch ( IOException ex )
                {
                    if ( exception == null )
                    {
                        exception = ex;
                    }
                }

                input = null;
            }
        }

        if ( output != null )
        {
            synchronized ( output )
            {
                try
                {
                    output.close();
                }
                catch ( IOException ex )
                {
                    if ( exception == null )
                    {
                        exception = ex;
                    }
                }

                output = null;
            }
        }
    }

    /**
     * @since 3.1.0
     * @return the Exception
     */
    public Throwable getException()
    {
        return exception;
    }

    private void feed()
        throws IOException
    {
        boolean flush = false;
        int data = input.read();

        while ( !isDone() && data != -1 )
        {
            synchronized ( output )
            {
                if ( !isDisabled() )
                {
                    output.write( data );
                    flush = true;
                }

                data = input.read();
            }
        }

        if ( flush )
        {
            output.flush();
        }
    }

}
