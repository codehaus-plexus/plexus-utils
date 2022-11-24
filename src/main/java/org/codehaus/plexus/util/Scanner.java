package org.codehaus.plexus.util;

import java.io.IOException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Comparator;

/**
 * Scan a directory tree for files, with specified inclusions and exclusions.
 */
public interface Scanner
{

    /**
     * Sets the list of include patterns to use. All '/' and '\' characters are replaced by
     * <code>File.separatorChar</code>, so the separator used need not match <code>File.separatorChar</code>.
     * <p>
     * When a pattern ends with a '/' or '\', "**" is appended.
     *
     * @param includes A list of include patterns. May be <code>null</code>, indicating that all files should be
     *            included. If a non-<code>null</code> list is given, all elements must be non-<code>null</code>.
     */
    void setIncludes( String[] includes );

    /**
     * Sets the list of exclude patterns to use. All '/' and '\' characters are replaced by
     * <code>File.separatorChar</code>, so the separator used need not match <code>File.separatorChar</code>.
     * <p>
     * When a pattern ends with a '/' or '\', "**" is appended.
     *
     * @param excludes A list of exclude patterns. May be <code>null</code>, indicating that no files should be
     *            excluded. If a non-<code>null</code> list is given, all elements must be non-<code>null</code>.
     */
    void setExcludes( String[] excludes );

    /**
     * Adds default exclusions to the current exclusions set.
     */
    void addDefaultExcludes();

    /**
	 * Scans the base directory for files which match at least one include pattern
	 * and don't match any exclude patterns.
	 * 
	 * @throws IOException if any IO problem occurs while scanning
	 *
	 * @exception IllegalStateException if the base directory was set incorrectly
	 *                                  (i.e. if it is <code>null</code>, doesn't
	 *                                  exist, or isn't a directory).
	 */
	void scan() throws IllegalStateException, IOException;

    /**
     * Returns the names of the files which matched at least one of the include patterns and none of the exclude
     * patterns. The names are relative to the base directory.
     *
     * @return the names of the files which matched at least one of the include patterns and none of the exclude
     *         patterns.
     */
    String[] getIncludedFiles();

    /**
     * Returns the names of the directories which matched at least one of the include patterns and none of the exclude
     * patterns. The names are relative to the base directory.
     *
     * @return the names of the directories which matched at least one of the include patterns and none of the exclude
     *         patterns.
     */
    String[] getIncludedDirectories();

    /**
     * Returns the base directory to be scanned. This is the directory which is scanned recursively.
     *
     * @return the base directory to be scanned
     */
	Path getBasedir();

	/**
	 * Return the link options used for scanning operations
	 * 
	 * @return the link options used for scanning
	 */
	LinkOption[] getLinkOptions();

	/**
	 * set the link options to be used for scanning operations
	 * 
	 * @param options the nes link options
	 */
	void setLinkOptions(LinkOption[] options);

    /**
     * Use a filename comparator in each directory when scanning.
     *
     * @param filenameComparator the Comparator instance to use
     * @since 3.3.0
     */
    void setFilenameComparator( Comparator<String> filenameComparator );
}
