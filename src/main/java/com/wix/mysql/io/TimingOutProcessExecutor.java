package com.wix.mysql.io;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

public class TimingOutProcessExecutor {

    final String cmd;

    public TimingOutProcessExecutor(String cmd) {
        this.cmd = cmd;
    }

    public int waitFor(Process p, long timeoutNanos) throws InterruptedException {
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
        p.destroy();
        throw new InterruptedException(String.format("Timeout of %s sec exceeded while waiting for '%s' to complete", NANOSECONDS.toSeconds(timeoutNanos), this.cmd));
    }
}