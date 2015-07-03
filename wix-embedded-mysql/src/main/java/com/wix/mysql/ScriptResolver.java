package com.wix.mysql;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import java.io.File;
import java.io.FileFilter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import static java.lang.String.format;

/**
 * Helper for locating schema init scripts in a classpath.
 *
 * @author viliusl
 * @since 06/06/15
 */
public class ScriptResolver {

    /**
     * Locates a single classPathFile in a classpath, ex. 'db/init_schema.sql'
     */
    public static File classPathFile(final String path) {
        String normalizedPath = path.startsWith("/") ? path : format("/%s", path);
        URL resource = ScriptResolver.class.getResource(normalizedPath);

        if (resource == null)
            throw new ScriptResolutionException(normalizedPath);

        return asFile(resource);
    }

    /**
     * Locates classPathFiles matching pattern, ordered using natural alphanumeric order, ex. 'db/*.sql'.
     *
     * Note: Supports only wildcards ('*') and only in file names for matching.
     */
    public static List<File> classPathFiles(final String pattern) throws URISyntaxException {
        List<File> results;

        String[] parts = pattern.split("/");
        String path = Joiner.on("/").join(Arrays.copyOfRange(parts, 0, parts.length - 1));

        URL baseFolder = ScriptResolver.class.getResource(path.startsWith("/") ? path : format("/%s", path));
        FileFilter filter = new WildcardFileFilter(parts[parts.length - 1]);

        if (baseFolder == null)
            throw new ScriptResolutionException(path);

        results = Arrays.asList(asFile(baseFolder).listFiles(filter));

        Collections.sort(results);

        return results;
    }

    private static File asFile(final URL resource) {
        try {
            return new File(resource.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static class ScriptResolutionException extends RuntimeException {
        public ScriptResolutionException(final String path) {
            super(format("No script(s) found for path '%s'", path));
        }
    }
}
