## Plexus Common Utilities

A collection of various utility classes to ease working with strings, files, command lines and more.

Starting with version 4, XML classes (in `org.codehaus.plexus.util.xml` and `org.codehaus.plexus.util.xml.pull`) have been extracted to a separate [`plexus-xml`](../plexus-xml/) library: if you need them, just use this new artifact.

`plexus-utils` 4 keeps an optional dependency on `plexus-xml` 3 to keep compatibility with the few XML-oriented methods of [`ReaderFactory`](./apidocs/org/codehaus/plexus/util/ReaderFactory.html) and [`WriterFactory`](./apidocs/org/codehaus/plexus/util/WriterFactory.html): these classes are deprecated, you should migrate as explained in javadoc. And keep `plexus-xml` to 3 if you want Maven 3 compatibility, as `plexus-xml` 4 works only in Maven 4..
