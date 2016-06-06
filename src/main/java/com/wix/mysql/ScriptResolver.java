package com.wix.mysql;

import org.apache.commons.io.filefilter.WildcardFileFilter;

import java.io.File;
import java.io.FileFilter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.wix.mysql.utils.Utils.join;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Arrays.copyOfRange;

/**
 * Helper for locating schema init scripts in a classpath.
 */
public class ScriptResolver {

    /**
     * /**
     * Locates a single classPathFile in a classpath, ex. 'db/init_schema.sql'
     *
     * @param path path to file
     * @return resolved SqlCommandSource
     */
    public static SqlCommandSource classPathFile(final String path) {
        String normalizedPath = path.startsWith("/") ? path : format("/%s", path);
        URL resource = ScriptResolver.class.getResource(normalizedPath);

        if (resource == null)
            throw new ScriptResolutionException(normalizedPath);

        return Sources.fromURL(resource);
    }

    /**
     * Locates classPathFiles matching pattern, ordered using natural alphanumeric order
     * Note: Supports only wildcards ('*') and only in file names for matching
     *
     * @param pattern ex. 'db/*.sql'
     * @return list of resolved SqlCommandSource objects
     */
    public static List<SqlCommandSource> classPathFiles(final String pattern) {
        List<File> results;

        String[] parts = pattern.split("/");
        String path = join(asList(copyOfRange(parts, 0, parts.length - 1)), "/");

        URL baseFolder = ScriptResolver.class.getResource(path.startsWith("/") ? path : format("/%s", path));
        FileFilter filter = new WildcardFileFilter(parts[parts.length - 1]);

        if (baseFolder == null)
            throw new ScriptResolutionException(path);

        results = asList(asFile(baseFolder).listFiles(filter));

        Collections.sort(results);

        return fromFiles(results);
    }

    private static File asFile(final URL resource) {
        try {
            return new File(resource.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<SqlCommandSource> fromFiles(List<File> files) {
        List<SqlCommandSource> res = new ArrayList<>();

        for (File f: files) {
            res.add(Sources.fromFile(f));
        }
        return res;
    }

    public static class ScriptResolutionException extends RuntimeException {
        ScriptResolutionException(final String path) {
            super(format("No script(s) found for path '%s'", path));
        }
    }
}
