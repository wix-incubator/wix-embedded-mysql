package com.wix.mysql.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author viliusl
 * @since 06/06/15
 */
public class SchemaConfig {

    private final String name;
    private final Charset charset;
    private final List<File> scripts;

    private SchemaConfig(
            final String name,
            final Charset charset,
            final List<File> scripts) {
        this.name = name;
        this.charset = charset;
        this.scripts = scripts;
    }

    public static Builder aSchemaConfig(final String name) {
        return new Builder(name);
    }

    public String getName() { return name; }
    public Charset getCharset() { return charset; }
    public List<File> getScripts() { return scripts; }

    public static class Builder {

        private final String name;
        private Charset charset;
        private List<File> scripts = new ArrayList<>();

        public Builder(final String name) {
            this.name = name;
        }

        public Builder withCharset(final Charset charset) {
            this.charset = charset;
            return this;
        }

        public Builder withScripts(final File... scripts) {
            return withScripts(Arrays.asList(scripts));
        }

        public Builder withScripts(final List<File> scripts) {
            this.scripts = scripts;
            return this;
        }

        public SchemaConfig build() {
            return new SchemaConfig(name, charset, scripts);
        }
    }
}
