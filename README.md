# Plexus Utils

[![Maven Central](https://img.shields.io/maven-central/v/org.codehaus.plexus/plexus-utils.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/org.codehaus.plexus/plexus-utils)
[![GitHub CI](https://github.com/codehaus-plexus/plexus-utils/actions/workflows/maven.yml/badge.svg)](https://github.com/codehaus-plexus/plexus-utils/actions)
[![Reproducible Builds](https://img.shields.io/endpoint?url=https://raw.githubusercontent.com/jvm-repo-rebuild/reproducible-central/master/content/org/codehaus/plexus/plexus-utils/badge.json)](https://github.com/jvm-repo-rebuild/reproducible-central/blob/master/content/org/codehaus/plexus/plexus-utils/README.md)

A collection of utility classes for strings, files, command lines and process execution. Historically developed alongside Apache Maven, and maintained by the same people.

## Status

Maintained, conservatively. This artifact is on the classpath of very nearly every Maven build in existence, so public API is kept compatible and new methods are added rarely. Much of it predates equivalents in the JDK and in Commons Lang — for new code, prefer the JDK where one exists.

**Two release lines are current:**

| Line | Latest | Use when |
|---|---|---|
| `4.x` | 4.0.x | Default for new work |
| `3.x` | 3.6.x | You need the XML classes bundled in this artifact rather than split out (see below) |

The `3.x` line lives on the [`plexus-utils-3.x`](https://github.com/codehaus-plexus/plexus-utils/tree/plexus-utils-3.x) branch and still receives releases.

## Upgrading from 3.x to 4.x

**The XML classes moved out.** `org.codehaus.plexus.util.xml` and `org.codehaus.plexus.util.xml.pull` — `Xpp3Dom`, `Xpp3DomBuilder`, `Xpp3DomWriter`, the pull parser — are no longer in `plexus-utils` 4. They live in [`plexus-xml`](https://github.com/codehaus-plexus/plexus-xml).

If you hit `NoClassDefFoundError` on any of those after upgrading, add:

```xml
<dependency>
  <groupId>org.codehaus.plexus</groupId>
  <artifactId>plexus-xml</artifactId>
  <version>3.0.2</version>
</dependency>
```

**Pick the `plexus-xml` line carefully:**

- **`plexus-xml` 3.x** is the straight extraction from `plexus-utils` 4 — same classes, same packages. This is what you want for Maven 3 compatibility.
- **`plexus-xml` 4.x** is rebuilt on Maven 4's `maven-xml-api`, requires **Java 17**, and works only under Maven 4.

`plexus-utils` 4 keeps an *optional* dependency on `plexus-xml` 3 so the few XML-oriented methods on [`ReaderFactory`](https://javadoc.io/doc/org.codehaus.plexus/plexus-utils/latest/org/codehaus/plexus/util/ReaderFactory.html) and [`WriterFactory`](https://javadoc.io/doc/org.codehaus.plexus/plexus-utils/latest/org/codehaus/plexus/util/WriterFactory.html) keep working. Those methods are deprecated — the Javadoc explains what to move to.

## Using it

```xml
<dependency>
  <groupId>org.codehaus.plexus</groupId>
  <artifactId>plexus-utils</artifactId>
  <version>4.0.3</version>
</dependency>
```

Check the badge above for the current version.

## Requirements

Java 8 or later. Both the `4.x` and `3.x` lines currently target Java 8.

## Documentation

- [Project site](https://codehaus-plexus.github.io/plexus-utils/)
- [Javadoc](https://javadoc.io/doc/org.codehaus.plexus/plexus-utils)
- [Release notes](https://github.com/codehaus-plexus/plexus-utils/releases)

## Contributing

See [CONTRIBUTING.md](https://github.com/codehaus-plexus/.github/blob/master/CONTRIBUTING.md). In short: `mvn verify` builds, and run `mvn spotless:apply` before pushing or CI will fail on formatting.

Please report security vulnerabilities privately — see [SECURITY.md](https://github.com/codehaus-plexus/.github/blob/master/SECURITY.md), not a public issue.
