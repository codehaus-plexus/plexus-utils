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

import org.junit.jupiter.api.Test;

/**
 * <p>DefaultConsumerTest class.</p>
 *
 * @author herve
 * @version $Id: $Id
 * @since 3.4.0
 */
class DefaultConsumerTest {
    /**
     * <p>testConsumeLine.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void consumeLine() throws Exception {
        DefaultConsumer cons = new DefaultConsumer();
        cons.consumeLine("Test DefaultConsumer consumeLine");
    }
}
