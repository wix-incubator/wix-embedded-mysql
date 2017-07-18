package com.wix.mysql.config;

import com.wix.mysql.Sources;
import com.wix.mysql.SqlScriptSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SchemaConfig {

    private final String name;
    private final Charset charset;
    private final List<SqlScriptSource> scripts;

    private SchemaConfig(String name, Charset charset, List<SqlScriptSource> scripts) {
        this.name = name;
        this.charset = charset;
        this.scripts = scripts;
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

    public List<SqlScriptSource> getScripts() {
        return scripts;
    }

    public static class Builder {

        private final String name;
        private Charset charset;
        private List<SqlScriptSource> scripts = new ArrayList<>();

        public Builder(final String name) {
            this.name = name;
        }

        public Builder withCharset(final Charset charset) {
            this.charset = charset;
            return this;
        }

        public Builder withScripts(final SqlScriptSource... scripts) {
            return withScripts(Arrays.asList(scripts));
        }

        public Builder withScripts(final List<SqlScriptSource> scripts) {
            this.scripts.addAll(scripts);
            return this;
        }

        public Builder withCommands(final String... commands) {
            return withCommands(Arrays.asList(commands));
        }

        public Builder withCommands(final List<String> commands) {
            for (String cmd: commands) {
                this.scripts.add(Sources.fromString(cmd));
            }
            return this;
        }

        public SchemaConfig build() {
            return new SchemaConfig(name, charset, scripts);
        }
    }
}
