package com.wix.mysql.config;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author viliusl
 * @since 06/06/15
 */
public class SchemaConfig {

    private final String name;
    private final Charset charset;
    private final List<File> scripts;
    private final List<String> commands;

    private SchemaConfig(String name, Charset charset, List<File> scripts, List<String> commands) {
        this.name = name;
        this.charset = charset;
        this.scripts = scripts;
        this.commands = commands;
    }

    public static Builder aSchemaConfig(final String name) {
        return new Builder(name);
    }

    public String getName() {
        return name;
    }

    public Charset getCharset() {
        return charset;
    }

    public List<File> getScripts() {
        return scripts;
    }

    public List<String> getCommands() {
        return commands;
    }

    public static class Builder {

        private final String name;
        private Charset charset;
        private List<File> scripts = Collections.emptyList();
        private List<String> commands = Collections.emptyList();

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

        public Builder withCommands(final String... commands) {
            return withCommands(Arrays.asList(commands));
        }

        public Builder withCommands(final List<String> commands) {
            this.commands = commands;
            return this;
        }

        public SchemaConfig build() {
            return new SchemaConfig(name, charset, scripts, commands);
        }
    }
}
