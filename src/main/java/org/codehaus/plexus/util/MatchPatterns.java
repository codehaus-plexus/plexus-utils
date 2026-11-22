package org.codehaus.plexus.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A list of patterns to be matched
 *
 * @author Kristian Rosenvold
 */
public final class MatchPatterns {
    private final MatchPattern[] patterns;

    private final String separator;

    private MatchPatterns(MatchPattern[] patterns, String separator) {
        this.patterns = patterns;
        this.separator = separator;
    }

    /**
     * Gets the enclosed patterns.
     *
     * @return an unmodifiable list of the enclosed patterns, in the order they were supplied
     * @since 3.6.5
     */
    public List<MatchPattern> getPatterns() {
        return Collections.unmodifiableList(Arrays.asList(patterns.clone()));
    }

    /**
     * Gets a list of enclosed MatchPattern sources.
     * @return A list of enclosed MatchPattern sources.
     * @since 3.6.0
     */
    public List<String> getSources() {
        List<String> sources = new ArrayList<>();
        for (MatchPattern pattern : patterns) {
            sources.add(pattern.getSource());
        }
        return sources;
    }

    /**
     * <p>Checks these MatchPatterns against a specified string.</p>
     *
     * <p>Uses far less string tokenization than any of the alternatives.</p>
     *
     * <p>The name is split with the separator these patterns were created with:
     * {@link File#separator} for {@link #from(String...)} and {@link #from(Iterable)}, or the one passed
     * to {@link #withSeparator(String, String...)} and {@link #withSeparator(String, Iterable)}.</p>
     *
     * @param name The name to look for
     * @param isCaseSensitive If the comparison is case sensitive
     * @return true if any of the supplied patterns match
     */
    public boolean matches(String name, boolean isCaseSensitive) {
        String[] tokenized = MatchPattern.tokenizePathToString(name, separator);
        return matches(name, tokenized, isCaseSensitive);
    }

    public boolean matches(String name, String[] tokenizedName, boolean isCaseSensitive) {
        char[][] tokenizedNameChar = new char[tokenizedName.length][];
        for (int i = 0; i < tokenizedName.length; i++) {
            tokenizedNameChar[i] = tokenizedName[i].toCharArray();
        }
        return matches(name, tokenizedNameChar, isCaseSensitive);
    }

    public boolean matches(String name, char[][] tokenizedNameChar, boolean isCaseSensitive) {
        for (MatchPattern pattern : patterns) {
            if (pattern.matchPath(name, tokenizedNameChar, isCaseSensitive)) {
                return true;
            }
        }
        return false;
    }

    public boolean matchesPatternStart(String name, boolean isCaseSensitive) {
        for (MatchPattern includesPattern : patterns) {
            if (includesPattern.matchPatternStart(name, isCaseSensitive)) {
                return true;
            }
        }
        return false;
    }

    public static MatchPatterns from(String... sources) {
        return withSeparator(File.separator, sources);
    }

    public static MatchPatterns from(Iterable<String> strings) {
        return withSeparator(File.separator, strings);
    }

    /**
     * Creates patterns whose path segments are separated by {@code separator} instead of
     * {@link File#separator}.
     *
     * @param separator the separator used by the patterns and by the names they are matched against
     * @param sources the patterns, each optionally wrapped in {@code %ant[...]} or {@code %regex[...]}
     * @return the patterns; with no sources, nothing matches
     * @since 3.6.5
     */
    public static MatchPatterns withSeparator(String separator, String... sources) {
        return withSeparator(separator, Arrays.asList(sources));
    }

    /**
     * Creates patterns whose path segments are separated by {@code separator} instead of
     * {@link File#separator}.
     *
     * @param separator the separator used by the patterns and by the names they are matched against
     * @param sources the patterns, each optionally wrapped in {@code %ant[...]} or {@code %regex[...]}
     * @return the patterns; with no sources, nothing matches
     * @since 3.6.5
     */
    public static MatchPatterns withSeparator(String separator, Iterable<String> sources) {
        List<MatchPattern> result = new ArrayList<>();
        for (String source : sources) {
            result.add(MatchPattern.fromString(source, separator));
        }
        return new MatchPatterns(result.toArray(new MatchPattern[0]), separator);
    }

    /**
     * Returns the patterns as they were supplied, in list form.
     *
     * @return the patterns as supplied
     */
    @Override
    public String toString() {
        return Arrays.toString(patterns);
    }
}
