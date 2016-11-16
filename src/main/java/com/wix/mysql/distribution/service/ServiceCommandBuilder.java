package com.wix.mysql.distribution.service;

import de.flapdoodle.embed.process.collections.Collections;

import java.util.Collection;
import java.util.List;

public class ServiceCommandBuilder {
    private final String version;
    private final List<String> args = Collections.newArrayList();

    public ServiceCommandBuilder(final String version) {
        this.version = version;
    }

    public void addAll(Collection<String> args) {
        this.args.addAll(args);
    }

    public List<String> emit() {
        if (args.isEmpty()) {
            throw new RuntimeException("mysqld startup command was not populated for version: " + version);
        }

        return args;
    }
}
