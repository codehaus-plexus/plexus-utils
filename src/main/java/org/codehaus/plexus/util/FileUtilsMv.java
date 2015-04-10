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
 * FileUtils Multi-Version code: this class is meant to have different implementations based on JRE version,
 * using JEP 238
 * @author hboutemy@apache.org
 * @see http://openjdk.java.net/jeps/238
 */
class FileUtilsMv
{
    /**
     * Deletes a file.
     *
     * @param fileName The path of the file to delete.
     */
    static void fileDelete( String fileName )
    {
        new File( fileName ).delete();
    }

    /**
     * Creates a number of directories, as delivered from DirectoryScanner
     * @param sourceBase The basedir used for the directory scan
     * @param dirs The getIncludedDirs from the dirscanner
     * @param destination The base dir of the output structure
     */
    static void mkDirs(  final File sourceBase, String[] dirs,  final File destination )
        throws IOException
    {
        for ( String dir : dirs )
        {
            new File( destination, dir ).mkdirs();
        }
    }
}
