package org.codehaus.plexus.util;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;
import java.io.IOException;

/**
 * DirectoryScanner Multi-Version code: this class is meant to have different implementations based on JRE version,
 * using JEP 238
 * @author hboutemy@apache.org
 * @see http://openjdk.java.net/jeps/238
 */
abstract class AbstractDirectoryScannerMv
    extends AbstractScanner
{

    /**
     * Checks whether a given file is a symbolic link.
     * <p/>
     * <p>It doesn't really test for symbolic links but whether the
     * canonical and absolute paths of the file are identical - this
     * may lead to false positives on some platforms.</p>
     *
     * @param parent the parent directory of the file to test
     * @param name   the name of the file to test.
     * @return true if it's a symbolic link
     * @throws java.io.IOException .
     * @since Ant 1.5
     */
    public boolean isSymbolicLink( File parent, String name )
        throws IOException
    {
        return NioFiles.isSymbolicLink( parent );
    }

    /**
     * Checks whether the parent of this file is a symbolic link.
     * <p/>
     *
     * <p> For java versions prior to 7 It doesn't really test for
     * symbolic links but whether the
     * canonical and absolute paths of the file are identical - this
     * may lead to false positives on some platforms.</p>
     *
     * @param parent the parent directory of the file to test
     * @param name   the name of the file to test.
     * @return true if it's a symbolic link
     * @throws java.io.IOException .
     * @since Ant 1.5
     */
    public boolean isParentSymbolicLink( File parent, String name )
        throws IOException
    {
        return NioFiles.isSymbolicLink( parent );
    }
}
