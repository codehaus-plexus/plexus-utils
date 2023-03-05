Plexus-Xml
============

[![Build Status](https://github.com/codehaus-plexus/plexus-xml/actions/workflows/maven.yml/badge.svg)](https://github.com/codehaus-plexus/plexus-xml/actions)
[![Maven Central](https://img.shields.io/maven-central/v/org.codehaus.plexus/plexus-xml.svg?label=Maven%20Central)](https://search.maven.org/artifact/org.codehaus.plexus/plexus-xml)

This library consists of XML classes (`org.codehaus.plexus.util.xml`, and `ReaderFactory`/`WriterFactory` moved from `org.codehaus.plexus.util`) that have been extracted from `plexus-utils` 4.

For publishing [the site](https://codehaus-plexus.github.io/plexus-xml/) do the following:

```
mvn -Preporting verify site site:stage scm-publish:publish-scm
```
