package com.wix.mysql.distribution.service;

import de.flapdoodle.embed.process.collections.Collections;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.String.format;

public class ServiceCommandBuilder {
    private final String version;
    private final List<String> args = Collections.newArrayList();
    private final Set<String> keys = new HashSet<>();

    public ServiceCommandBuilder(final String version) {
        this.version = version;
    }

    public ServiceCommandBuilder addAll(Collection<String> args) {
        for (String arg : args) {
            String argName = argName(arg);
            if (!keys.add(argName)) {
                throw new RuntimeException(format("argument with name '%s' is already present in argument list.", argName));
            }
        }
        this.args.addAll(args);
        return this;
    }

    public List<String> emit() {
        if (args.isEmpty()) {
            throw new RuntimeException("mysqld startup command was not populated for version: " + version);
        }

        return args;
    }

    private String argName(String arg) {
        return arg.split("=")[0];
    }
}
