package com.wix.mysql;

import org.apache.commons.io.filefilter.WildcardFileFilter;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static com.wix.mysql.utils.Utils.join;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Arrays.copyOfRange;

/**
 * Helper for locating schema init scripts in a classpath.
 */
public class ScriptResolver {

    /**
     * Locates classPathFiles matching pattern, ordered using natural alphanumeric order
     * Note: Supports only wildcards ('*') and only in file names for matching
     *
     * @param pattern ex. 'db/*.sql'
     * @return list of resolved SqlScriptSource objects
     */
    public static List<SqlScriptSource> classPathScripts(final String pattern) {
        List<SqlScriptSource> results = new ArrayList<>();

        String[] parts = pattern.split("/");
        String path = join(asList(copyOfRange(parts, 0, parts.length - 1)), "/");
        String normalizedPath = path.startsWith("/") ? path : format("/%s", path);

        URL baseFolder = ScriptResolver.class.getResource(normalizedPath);

        if (baseFolder == null)
            throw new ScriptResolutionException(normalizedPath);

        if (baseFolder.getProtocol().equals("jar")) {
            String normalizedPattern = pattern.startsWith("/") ? pattern : format("/%s", pattern);
            String regexPattern = normalizedPattern.replace("*", ".*");
            String jarPath = baseFolder.getPath().substring(5, baseFolder.getPath().indexOf("!"));
            JarFile jar;

            try {
                jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Enumeration<JarEntry> entries = jar.entries();
            List<String> names = new ArrayList<>();
            while (entries.hasMoreElements()) {
                String name = "/" + entries.nextElement().getName();
                if (name.matches(regexPattern)) {
                    names.add(name);
                }
            }
            Collections.sort(names);
            for (String found : names) {
                results.add(classPathScript(found));
            }
        } else {
            FileFilter filter = new WildcardFileFilter(parts[parts.length - 1]);
            List<File> filesInPath = asList(asFile(baseFolder).listFiles(filter));
            Collections.sort(filesInPath);
            for (File f : filesInPath) {
                results.add(Sources.fromFile(f));
            }
        }

        return results;
    }

    /**
     * Locates a single classPathFile in a classpath, ex. 'db/init_schema.sql'
     *
     * @param path path to file
     * @return resolved SqlScriptSource
     */
    public static SqlScriptSource classPathScript(final String path) {
        String normalizedPath = path.startsWith("/") ? path : format("/%s", path);
        URL resource = ScriptResolver.class.getResource(normalizedPath);

        if (resource == null)
            throw new ScriptResolutionException(normalizedPath);

        return Sources.fromURL(resource);
    }

    /**
     * Locates a single classPathFile in a classpath, ex. 'db/init_schema.sql'
     *
     * @param path path to file
     * @return resolved SqlScriptSource
     * @deprecated in favor of {@link #classPathScript(String)}
     */
    @Deprecated
    public static SqlScriptSource classPathFile(final String path) {
        return classPathScript(path);
    }

    /**
     * Locates classPathFiles matching pattern, ordered using natural alphanumeric order
     * Note: Supports only wildcards ('*') and only in file names for matching
     *
     * @param pattern ex. 'db/*.sql'
     * @return list of resolved SqlScriptSource objects
     * @deprecated in favor of {@link #classPathScripts(String)}
     */
    @Deprecated
    public static List<SqlScriptSource> classPathFiles(final String pattern) {
        return classPathScripts(pattern);
    }

    private static File asFile(final URL resource) {
        try {
            return new File(resource.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static class ScriptResolutionException extends RuntimeException {
        ScriptResolutionException(final String path) {
            super(format("No script(s) found for path '%s'", path));
        }
    }
}
