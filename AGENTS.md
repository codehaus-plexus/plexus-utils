# Agent guide for plexus-utils 3.x

`org.codehaus.plexus:plexus-utils` 3.x is the maintenance line on the `plexus-utils-3.x` branch; `master` builds
`4.x`. Unlike `4.x`, this line still bundles the XML classes (`org.codehaus.plexus.util.xml` and
`...xml.pull`, including `MXParser` and `Xpp3Dom`), which `4.x` moved to `plexus-xml`. The JAR is a multi-release
JAR: `src/main/java` targets Java 8 and `src/main/java9`, `java10` and `java11` hold the newer variants, compiled by
the `jdk9+`, `jdk10+` and `jdk11+` profiles of the POM.

## Build and test

- Build with `mvn verify` on JDK 17 or later; the parent POM sets the Java release to 8 and the minimum Maven version
  to 3.9.0.
- CI runs `.github/workflows/maven.yml` on every push and pull request. It calls the shared Plexus build workflow,
  which runs `mvn verify javadoc:javadoc` on Linux, Windows and macOS with several JDKs, including 8. The deploy job
  it also calls runs only for `master`, so this branch publishes nothing from CI.
- `.github/workflows/release-drafter.yml` drafts the release notes of this line from the labels of merged pull
  requests; label every pull request.
- Unit tests use JUnit 5 and run through Surefire with `childDelegation` on, so the test classes are used, not
  Surefire's own `plexus-utils`.
- The JAR sets `Automatic-Module-Name: org.codehaus.plexus.util`; keep that name stable.
- Run `mvn spotless:apply` on JDK 17 to 21 before pushing; it also formats Markdown, except `src/site/markdown`.
- Keep public API compatible: this line exists so that Maven 3 consumers can stay where they are. No new
  dependencies.

## Fixes that belong to both lines

Most fixes on this line have a twin on `master`, and the XML classes also exist in `plexus-xml`:

- A fix for a class that exists in both `3.x` and `4.x` goes to `master` first and is ported here, or the reverse;
  say in the pull request which one it is the twin of.
- A fix to `MXParser`, `Xpp3Dom` or another bundled XML class usually also belongs on the `3.x` branch of
  [plexus-xml](https://github.com/codehaus-plexus/plexus-xml) (and on its `master` when it applies there). The
  plexus-utils 3.6.4 fix for a surrogate pair at a buffer boundary is such a port.
- A change that only makes sense for `4.x`, such as removing the XML classes, does not come here.

## Documentation updates

A change that users or contributors can notice ships with the documentation it affects, in the same pull request. A
refactor with no visible effect needs none.

- Behaviour of a public method (defaults, encodings, symbolic links, error handling): its Javadoc; the Javadoc is the
  reference for the utility classes.
- The release line, the Java or Maven requirement, or the coordinates: `README.md`.
- `src/site/markdown/index.md` is the only site page, listed as Introduction in `src/site/site.xml` together with the
  Javadoc. The site is published from `master` only, so keep this page consistent with the `master` one.
- A fix that also lands on `master` (or the other way round) updates the documentation of both lines in the same
  step, so the `README.md` and Javadoc of the two lines never contradict each other.
- A new convention, build gate or test fixture: this file.

