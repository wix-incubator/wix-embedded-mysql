package com.wix.mysql.distribution;


import de.flapdoodle.embed.process.extract.IExtractedFileSet;

import java.io.IOException;

public interface Initializer {
    boolean matches(Version version);

    void apply(IExtractedFileSet files) throws IOException;
}
