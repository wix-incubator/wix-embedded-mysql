package com.wix.mysql.distribution.setup;

import com.wix.mysql.exceptions.MissingDependencyException;
import com.wix.mysql.io.TimingOutProcessExecutor;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.io.IStreamProcessor;
import de.flapdoodle.embed.process.io.Processors;
import de.flapdoodle.embed.process.io.StreamToLineProcessor;

import java.io.IOException;
import java.io.InputStreamReader;

import static java.lang.String.format;

final class ProcessRunner {

    private final TimingOutProcessExecutor tope;

    ProcessRunner(String cmd) {
        this.tope = new TimingOutProcessExecutor(cmd);
    }

    void run(Process p, IRuntimeConfig runtimeConfig, long timeoutNanos) throws IOException {
        CollectingAndForwardingOutputStreamProcessor wrapped =
                new CollectingAndForwardingOutputStreamProcessor(runtimeConfig.getProcessOutput().getOutput());
        IStreamProcessor loggingWatch = StreamToLineProcessor.wrap(wrapped);

        try {
            Processors.connect(new InputStreamReader(p.getInputStream()), loggingWatch);
            Processors.connect(new InputStreamReader(p.getErrorStream()), loggingWatch);

            int retCode = tope.waitFor(p, timeoutNanos);

            if (retCode != 0) {
                System.out.println("retCode " + retCode);
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

    public static class CollectingAndForwardingOutputStreamProcessor implements IStreamProcessor {
        String output = "";
        final IStreamProcessor forwardTo;

        CollectingAndForwardingOutputStreamProcessor(IStreamProcessor forwardTo) {
            this.forwardTo = forwardTo;
        }

        public void process(String block) {
            System.out.println("qwe" + block);
            output += block;
            forwardTo.process(block);
        }

        public void onProcessed() {
            System.out.println("processed");
            forwardTo.onProcessed();
        }

        String getOutput() {
            System.out.println("outout " + output);
            return output;
        }
    }
}
