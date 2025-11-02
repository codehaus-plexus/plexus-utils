package org.codehaus.plexus.util.dag;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * <p>VertexTest class.</p>
 *
 * @author <a href="michal.maczka@dimatics.com">Michal Maczka</a>
 * @since 3.4.0
 */
class VertexTest {

    @Test
    void vertex() {

        final Vertex vertex1 = new Vertex("a");

        assertEquals("a", vertex1.getLabel());

        assertEquals(0, vertex1.getChildren().size());

        assertEquals(0, vertex1.getChildLabels().size());

        final Vertex vertex2 = new Vertex("b");

        assertEquals("b", vertex2.getLabel());

        vertex1.addEdgeTo(vertex2);

        assertEquals(1, vertex1.getChildren().size());

        assertEquals(1, vertex1.getChildLabels().size());

        assertEquals(vertex2, vertex1.getChildren().get(0));

        assertEquals("b", vertex1.getChildLabels().get(0));
    }
}
