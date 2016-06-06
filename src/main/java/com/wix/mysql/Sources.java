package com.wix.mysql;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Sources {

    public static SqlCommandSource fromString(final String str) {
        return new StringSource(str);
    }

    public static SqlCommandSource fromFile(final File str) {
        return new FileSource(str);
    }

    public static SqlCommandSource fromURL(final URL str) {
        return new URLSource(str);
    }

    private static class StringSource implements SqlCommandSource {
        final String str;

        StringSource(final String str) {
            this.str = str;
        }

        @Override
        public String read() {
            return str;
        }
    }

    private static class FileSource implements SqlCommandSource {
        final File str;

        FileSource(final File str) {
            this.str = str;
        }

        @Override
        public String read() throws IOException {
            return FileUtils.readFileToString(str);
        }
    }

    private static class URLSource implements SqlCommandSource {
        final URL url;

        URLSource(final URL str) {
            this.url = str;
        }

        @Override
        public String read() throws IOException {
            InputStream in = url.openStream();

            try {
                return IOUtils.toString(in);
            } finally {
                IOUtils.closeQuietly(in);
            }
        }
    }


}
