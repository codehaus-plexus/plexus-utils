package org.codehaus.plexus.util;

/**
 * Implementation specific to Java SE 8 version.
 */
final class VersionSpecifics extends CommonImplementation
{
    static final VersionSpecifics INSTANCE = new VersionSpecifics();

    private VersionSpecifics() {
        // singleton
    }
}
