# Plexus Utils 3.x

[![Maven Central 3.x](https://img.shields.io/maven-central/v/org.codehaus.plexus/plexus-utils.svg?label=Maven%20Central%203.x&versionPrefix=3.)](https://central.sonatype.com/artifact/org.codehaus.plexus/plexus-utils/versions)
[![GitHub CI](https://github.com/codehaus-plexus/plexus-utils/actions/workflows/maven.yml/badge.svg?branch=plexus-utils-3.x)](https://github.com/codehaus-plexus/plexus-utils/actions/workflows/maven.yml?query=branch%3Aplexus-utils-3.x)

This is the `3.x` maintenance line of Plexus Utils, a collection of utility classes for strings, files, command
lines and process execution, such as `FileUtils`, `StringUtils`, `DirectoryScanner` and `CommandLineUtils`. Unlike
`4.x`, it still bundles the XML classes (`Xpp3Dom`, `MXParser` and the others in `org.codehaus.plexus.util.xml` and
`org.codehaus.plexus.util.xml.pull`) in the same artifact.

## Status

This branch receives bug fixes and security fixes, not new features. New work happens on `master`, which builds
`4.x`; see the [`master` README](https://github.com/codehaus-plexus/plexus-utils/blob/master/README.md) for
the `3.x` to `4.x` upgrade notes.

Use `3.x` when your code depends on the XML classes being part of `plexus-utils`, as plugins and extensions written
for Maven 3 often do. Otherwise use `4.x`, which is the default for new work; there the XML classes come from
[`plexus-xml`](https://github.com/codehaus-plexus/plexus-xml), and its `3.x` line is the one that works under Maven 3.

A defect in the bundled XML parser or `Xpp3Dom` usually exists in `plexus-xml` too, so a fix here often needs a
matching change on its `3.x` branch, and the other way round.

## Using it

```xml
<dependency>
  <groupId>org.codehaus.plexus</groupId>
  <artifactId>plexus-utils</artifactId>
  <version>3.6.4</version>
</dependency>
```

3.6.4 is the latest `3.x` release; the badge above shows the current one.

## Requirements

Java 8 or later. The JAR is a multi-release JAR that adds classes for Java 9, 10 and 11, and sets the automatic module
name `org.codehaus.plexus.util`. Building the project needs Maven 3.9.0 or later, and a JDK 11 or later for the
release profile.

## Documentation

- [Javadoc](https://javadoc.io/doc/org.codehaus.plexus/plexus-utils/3.6.4)
- [Release notes](https://github.com/codehaus-plexus/plexus-utils/releases)

The [project site](https://codehaus-plexus.github.io/plexus-utils/) is published from `master` only and describes
`4.x`. Publishing it from this branch would replace it, so do not.

## Building

```shell
mvn verify
```

A local build formats the sources with Spotless. CI only checks the format and fails on unformatted code, so run
`mvn spotless:apply` on JDK 17 to 21 and commit the result.

## Releasing

Releases of this line are cut from the `plexus-utils-3.x` branch the same way as from `master`; the steps and the
one-time setup are in [RELEASING.md](https://github.com/codehaus-plexus/.github/blob/master/RELEASING.md):

```shell
mvn release:prepare release:perform
```

The release drafter keeps a separate draft for this branch: edit that one, and publish it without marking it the
latest release. Skip the site.

## Contributing

Conventions for contributors and agents are in [AGENTS.md](AGENTS.md); see also
[CONTRIBUTING.md](https://github.com/codehaus-plexus/.github/blob/master/CONTRIBUTING.md). Open pull requests
against `plexus-utils-3.x`, and report bugs in the [issue tracker](https://github.com/codehaus-plexus/plexus-utils/issues).

Report security vulnerabilities privately, as described in
[SECURITY.md](https://github.com/codehaus-plexus/.github/blob/master/SECURITY.md), not in a public issue.

## License

Plexus Utils is licensed under the
[Apache License, Version 2.0](https://github.com/codehaus-plexus/plexus-utils/blob/plexus-utils-3.x/LICENSE.txt).
