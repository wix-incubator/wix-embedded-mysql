package com.wix.mysql;

import com.wix.mysql.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Sources {

    public static SqlScriptSource fromString(final String str) {
        return new StringSource(str);
    }

    public static SqlScriptSource fromFile(final File str) {
        return new FileSource(str);
    }

    public static SqlScriptSource fromURL(final URL str) {
        return new URLSource(str);
    }

    private static class StringSource implements SqlScriptSource {
        final String str;

        StringSource(final String str) {
            this.str = str;
        }

        @Override
        public String read() {
            return str;
        }
    }

    private static class FileSource implements SqlScriptSource {
        final File str;

        FileSource(final File str) {
            this.str = str;
        }

        @Override
        public String read() throws IOException {
            return new String(java.nio.file.Files.readAllBytes(str.toPath()));
        }
    }

    private static class URLSource implements SqlScriptSource {
        final URL url;

        URLSource(final URL str) {
            this.url = str;
        }

        @Override
        public String read() throws IOException {
            try (InputStream in = url.openStream()) {
                return Utils.streamToString(in);
            }
        }
    }


}
