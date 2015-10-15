package com.wix.mysql.config;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * @author viliusl
 * @since 06/06/15
 */
public class SchemaConfig {

    private final String name;
    private final Optional<Charset> charset;
    private final List<File> scripts;

    private SchemaConfig(
            final String name,
            final Optional<Charset> charset,
            final List<File> scripts) {
        this.name = name;
        this.charset = charset;
        this.scripts = scripts;
    }

    public static Builder aSchemaConfig(final String name) {
        return new Builder(name);
    }

    public String getName() { return name; }
    public Optional<Charset> getCharset() { return charset; }
    public List<File> getScripts() { return scripts; }

    public static class Builder {

        private final String name;
        private Optional<Charset> charset = Optional.absent();
        private List<File> scripts = Lists.newArrayList();

        public Builder(final String name) {
            this.name = name;
        }

        public Builder withCharset(final Charset charset) {
            this.charset = Optional.of(charset);
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
