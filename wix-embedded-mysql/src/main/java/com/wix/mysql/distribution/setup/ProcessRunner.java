package com.wix.mysql.distribution.setup;

import com.wix.mysql.exceptions.MissingDependencyException;
import com.wix.mysql.io.TimingOutProcessExecutor;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.io.IStreamProcessor;
import de.flapdoodle.embed.process.io.Processors;

import java.io.IOException;
import java.io.InputStreamReader;

import static java.lang.String.format;

final class ProcessRunner {

    private final TimingOutProcessExecutor tope;

    ProcessRunner(String cmd) {
        this.tope = new TimingOutProcessExecutor(cmd);
    }

    void run(Process p, IRuntimeConfig runtimeConfig, long timeoutNanos) throws IOException {
        CollectingAndForwardingStreamProcessor wrapped =
                new CollectingAndForwardingStreamProcessor(runtimeConfig.getProcessOutput().getOutput());
        IStreamProcessor loggingWatch = runtimeConfig.getProcessOutput().getOutput();

        try {
            Processors.connect(new InputStreamReader(p.getInputStream()), loggingWatch);
            Processors.connect(new InputStreamReader(p.getErrorStream()), loggingWatch);

            int retCode = tope.waitFor(p, timeoutNanos);

            if (retCode != 0) {
                System.out.println("retCode " + retCode);
                while (!wrapped.isProcessed()) {
                  Thread.sleep(100);
                }
                resolveException(retCode, wrapped.getOutput());
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

    public static class CollectingAndForwardingStreamProcessor implements IStreamProcessor {
        volatile String output = "";
        volatile boolean processed = false;
        final IStreamProcessor forwardTo;

        CollectingAndForwardingStreamProcessor(IStreamProcessor forwardTo) {
            this.forwardTo = forwardTo;
        }

        public void process(String block) {
            output = output + block;
            forwardTo.process(block);
        }

        public void onProcessed() {
            System.out.println("processed");
            this.processed = true;
            forwardTo.onProcessed();
        }

        boolean isProcessed() {
            return processed;
        }


        String getOutput() {
            return output;
        }


    }
}
