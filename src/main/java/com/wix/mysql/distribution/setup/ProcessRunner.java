package com.wix.mysql.distribution.setup;

import com.wix.mysql.exceptions.MissingDependencyException;
import com.wix.mysql.io.NotifyingStreamProcessor;
import com.wix.mysql.io.TimingOutProcessExecutor;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.io.IStreamProcessor;
import de.flapdoodle.embed.process.io.Processors;
import de.flapdoodle.embed.process.io.StreamToLineProcessor;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;

final class ProcessRunner {

    final TimingOutProcessExecutor tope;

    ProcessRunner(String cmd) {
        this.tope = new TimingOutProcessExecutor(cmd);
    }

    void run(Process p, IRuntimeConfig runtimeConfig, long timeoutNanos) throws IOException {
        IStreamProcessor outputWatch = StreamToLineProcessor.wrap(runtimeConfig.getProcessOutput().getOutput());
        try {
            Processors.connect(new InputStreamReader(p.getInputStream()), outputWatch);
            Processors.connect(new InputStreamReader(p.getErrorStream()), outputWatch);

            int retCode = tope.waitFor(p, timeoutNanos);

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
