## Documentation updates

A change that users or contributors can notice ships with the documentation it affects, in the same
pull request. A refactor with no visible effect needs none.

- Behaviour of a public method (defaults, encodings, symbolic links, error handling): its Javadoc;
  the Javadoc is the reference for the utility classes.
- `src/site/markdown/index.md` is the only site page, listed as Introduction in `src/site/site.xml`
  together with the Javadoc. It covers the split from `plexus-xml`; update it when that changes.
- The release lines, the Java requirement, or the 3.x to 4.x upgrade notes: `README.md`, including
  the status table (check it against the latest releases).
- A new convention, build gate or test fixture: this file.

