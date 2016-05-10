package com.wix.mysql.distribution.initializers;

import com.wix.mysql.exceptions.MissingDependencyException;
import org.apache.commons.io.IOUtils;

import java.io.IOException;

import static java.lang.String.format;

final class ProcessRunner {

    static void run(Process p) throws IOException {
        try {
            int retCode = p.waitFor();

            if (retCode != 0) {
                resolveException(retCode, IOUtils.toString(p.getInputStream()) + IOUtils.toString(p.getErrorStream()));
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void resolveException(int retCode, String output) {
        if (output.contains("error while loading shared libraries: libaio.so")) {
            throw new MissingDependencyException(
                    "System library 'libaio.so.1' missing. " +
                            "Please install it via system package manager, ex. 'sudo apt-get install libaio1'.\n" +
                            "For details see: http://bugs.mysql.com/bug.php?id=60544");
        } else {
            throw new RuntimeException(format("Command exited with error code: '%s' and output: '%s'", retCode, output));
        }
    }
}
