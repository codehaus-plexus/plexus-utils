package org.codehaus.plexus.util.cli;

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
