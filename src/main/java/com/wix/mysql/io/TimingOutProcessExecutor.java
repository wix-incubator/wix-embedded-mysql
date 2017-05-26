package com.wix.mysql.io;

import java.util.concurrent.TimeUnit;

public class TimingOutProcessExecutor {

    public static int waitFor(Process p, long timeoutNanos) throws InterruptedException {
        long startTime = System.nanoTime();
        long rem = timeoutNanos;

        do {
            try {
                System.out.println("try");
                return p.exitValue();
            } catch (IllegalThreadStateException ex) {
                if (rem > 0)
                    Thread.sleep(
                            Math.min(TimeUnit.NANOSECONDS.toMillis(rem) + 1, 100));
            }
            rem = timeoutNanos - (System.nanoTime() - startTime);
        } while (rem > 0);
        p.destroy();
        throw new InterruptedException(String.format("Timeout of %s sec exceeded while waiting for process to complete", TimeUnit.NANOSECONDS.toSeconds(timeoutNanos)));
    }

}