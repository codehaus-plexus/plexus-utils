Plexus-Utils
============

[![Build Status](https://github.com/codehaus-plexus/plexus-utils/actions/workflows/maven.yml/badge.svg)](https://github.com/codehaus-plexus/plexus-utils/actions)
[![Maven Central](https://img.shields.io/maven-central/v/org.codehaus.plexus/plexus-utils.svg?label=Maven%20Central)](https://search.maven.org/artifact/org.codehaus.plexus/plexus-utils)
[![Reproducible Builds](https://img.shields.io/endpoint?url=https://raw.githubusercontent.com/jvm-repo-rebuild/reproducible-central/master/content/org/codehaus/plexus/plexus-utils/badge.json)](https://github.com/jvm-repo-rebuild/reproducible-central/blob/master/content/org/codehaus/plexus/plexus-utils/README.md)

This library is historically used by the Apache Maven project so it's developed and maintained by the same [`bad guys`](http://maven.apache.org/team.html)

The current master is now at https://github.com/codehaus-plexus/plexus-utils

For publishing [the site](https://codehaus-plexus.github.io/plexus-utils/) do the following:

```
mvn -Preporting verify site site:stage scm-publish:publish-scm
```

Starting with version 4, XML classes (in `org.codehaus.plexus.util.xml` and `org.codehaus.plexus.util.xml.pull`) have been extracted to a separate [`plexus-xml`](https://github.com/codehaus-plexus/plexus-xml/) library: if you need them, just use this new artifact.

`plexus-utils` 4 keeps an optional dependency on `plexus-xml` 3 to keep compatibility with the few XML-oriented methods of `ReaderFactory` and `WriterFactory`: these classes are deprecated, you should migrate as explained in javadoc. And keep `plexus-xml` to 3 if you want Maven 3 compatibility, as `plexus-xml` 4 works only in Maven 4.
