package com.wix.mysql.io;

public class ProgressStepper {

    int lastReported = -1;

    public boolean hasNext(int value) {
        return (nextCandidate(value) != lastReported);
    }

    public int setAndGet(int value) {
        lastReported = nextCandidate(value);
        return lastReported;
    }

    private int nextCandidate(int value) {
        return (int)(5 * (Math.floor(Math.abs(value / 5))));
    }

}
