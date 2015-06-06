package com.wix.mysql;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import static java.lang.String.format;

/**
 * Helper for locating schema init scripts in a classpath
 *
 * @author viliusl
 * @since 06/06/15
 */
public class ClassPathScriptResolver {

    /**
     * Locates a single file in a classpath, ex. 'db/init_schema.sql'
     */
    public static File file(final String path) {
        String normalizedPath = path.startsWith("/") ? path : format("/%s", path);
        URL resource = ClassPathScriptResolver.class.getResource(normalizedPath);

        if (resource == null)
            throw new ScriptResolutionException(normalizedPath);

        return asFile(resource);
    }

    /**
     * Locates files matching pattern, ordered using natural aphanumeric order, ex. 'db/*.sql'
     */
    public static List<File> files(final String pattern) {
        throw new IllegalStateException("not implemented");
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
