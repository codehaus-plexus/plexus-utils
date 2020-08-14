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

import java.io.IOException;
import java.io.OutputStream;

/**
 * Wraps a String as an OutputStream.
 *
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 *
 * @deprecated As of version 1.5.2 this class should no longer be used because it does not properly handle character
 *             encoding. Instead, use {@link java.io.ByteArrayOutputStream#toString(String)}.
 */
@Deprecated
public class StringOutputStream
    extends OutputStream
{
    private StringBuffer buf = new StringBuffer();

    @Override
    public void write( byte[] b )
        throws IOException
    {
        buf.append( new String( b ) );
    }

    @Override
    public void write( byte[] b, int off, int len )
        throws IOException
    {
        buf.append( new String( b, off, len ) );
    }

    @Override
    public void write( int b )
        throws IOException
    {
        byte[] bytes = new byte[1];
        bytes[0] = (byte) b;
        buf.append( new String( bytes ) );
    }

    @Override
    public String toString()
    {
        return buf.toString();
    }
}
