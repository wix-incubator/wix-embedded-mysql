package com.wix.mysql.io;

import java.util.concurrent.TimeUnit;

public class TimingOutProcessExecutor {

    public static int waitFor(Process p, long timeout, TimeUnit unit)
            throws InterruptedException
    {
        long startTime = System.nanoTime();
        long rem = unit.toNanos(timeout);

        do {
            try {
                return p.exitValue();
            } catch(IllegalThreadStateException ex) {
                if (rem > 0)
                    Thread.sleep(
                            Math.min(TimeUnit.NANOSECONDS.toMillis(rem) + 1, 100));
            }
            rem = unit.toNanos(timeout) - (System.nanoTime() - startTime);
        } while (rem > 0);
        return -9;
    }

}
