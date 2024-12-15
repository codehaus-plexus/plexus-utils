package org.codehaus.plexus.util.reflection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * <p>ReflectorTest class.</p>
 *
 * @author J&ouml;rg Schaible
 * @version $Id: $Id
 * @since 3.4.0
 */
class ReflectorTest {
    private Project project;

    private Reflector reflector;

    /**
     * <p>setUp.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @BeforeEach
    void setUp() throws Exception {
        project = new Project();
        project.setModelVersion("1.0.0");
        project.setVersion("42");

        reflector = new Reflector();
    }

    /**
     * <p>testObjectPropertyFromName.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void objectPropertyFromName() throws Exception {
        assertEquals("1.0.0", reflector.getObjectProperty(project, "modelVersion"));
    }

    /**
     * <p>testObjectPropertyFromBean.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void objectPropertyFromBean() throws Exception {
        assertEquals("Foo", reflector.getObjectProperty(project, "name"));
    }

    /**
     * <p>testObjectPropertyFromField.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @Test
    void objectPropertyFromField() throws Exception {
        assertEquals("42", reflector.getObjectProperty(project, "version"));
    }

    public static class Project {
        private String model;

        private String name;

        private String version;

        public void setModelVersion(String modelVersion) {
            this.model = modelVersion;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String modelVersion() {
            return model;
        }

        public String getName() {
            return "Foo";
        }
    }
}
