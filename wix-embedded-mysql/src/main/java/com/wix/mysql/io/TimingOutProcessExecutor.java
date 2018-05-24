package com.wix.mysql.io;

import com.wix.mysql.utils.Utils;

import java.io.IOException;

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

public class TimingOutProcessExecutor {

    private final String cmd;

    public TimingOutProcessExecutor(String cmd) {
        this.cmd = cmd;
    }

    public int waitFor(Process p, long timeoutNanos) throws InterruptedException, IOException {
        long startTime = System.nanoTime();
        long rem = timeoutNanos;

        do {
            try {
                return p.exitValue();
            } catch (IllegalThreadStateException ex) {
                if (rem > 0) {
                    Thread.sleep(Math.min(NANOSECONDS.toMillis(rem) + 1, 100));
                }
            }
            rem = timeoutNanos - (System.nanoTime() - startTime);
        } while (rem > 0);

        String collectedOutput = Utils.streamToString(p.getInputStream()) + Utils.streamToString(p.getErrorStream());

        p.destroy();

        throw new InterruptedException(format("Timeout of %s sec exceeded while waiting for '%s' to complete. Collected output: %s",
                NANOSECONDS.toSeconds(timeoutNanos),
                this.cmd,
                collectedOutput));
    }
}