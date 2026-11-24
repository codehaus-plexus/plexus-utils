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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The environment a {@link Commandline} hands to the process. Windows keeps one variable per case-insensitive name,
 * and the inherited names reach the command line upper-cased, see {@link CommandLineUtils#getSystemEnvVars()}.
 */
class CommandlineEnvironmentTest {

    @Test
    @EnabledOnOs(OS.WINDOWS)
    void variableAddedInAnotherSpellingReplacesTheInheritedOne() throws Exception {
        Commandline cl = new Commandline();
        cl.addEnvironment("Path", "C:\\own");

        assertEquals(Collections.singletonList("Path=C:\\own"), paths(cl));
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    void variableAddedAfterTheInheritedOnesReplacesTheInheritedOne() throws Exception {
        Commandline cl = new Commandline();
        // as a first execute() does
        cl.addSystemEnvironment();
        cl.addEnvironment("Path", "C:\\own");

        assertEquals(Collections.singletonList("Path=C:\\own"), paths(cl));
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    void lastSpellingAddedWins() throws Exception {
        Commandline cl = new Commandline();
        cl.addEnvironment("Path", "C:\\first");
        cl.addEnvironment("PATH", "C:\\second");

        assertEquals(Collections.singletonList("PATH=C:\\second"), paths(cl));
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    void spellingsAreDifferentVariablesElsewhere() throws Exception {
        Commandline cl = new Commandline();
        cl.addEnvironment("Path", "/own");

        List<String> paths = paths(cl);
        assertEquals(2, paths.size(), paths.toString());
    }

    @Test
    void nullNameIsStillAccepted() throws Exception {
        Commandline cl = new Commandline();
        cl.addEnvironment(null, "value");

        assertTrue(Arrays.asList(cl.getEnvironmentVariables()).contains("null=value"));
    }

    private static List<String> paths(Commandline cl) throws Exception {
        List<String> paths = new ArrayList<>();
        for (String variable : cl.getEnvironmentVariables()) {
            if (variable.regionMatches(true, 0, "PATH=", 0, 5)) {
                paths.add(variable);
            }
        }
        return paths;
    }
}
