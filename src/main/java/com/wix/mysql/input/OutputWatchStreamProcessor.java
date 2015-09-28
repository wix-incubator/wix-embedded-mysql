package com.wix.mysql.input;

import com.google.common.base.Joiner;
import de.flapdoodle.embed.process.io.IStreamProcessor;

import java.util.Set;

import static java.lang.String.format;

/**
 * @author viliusl
 * @since 13/02/15
 */
public class OutputWatchStreamProcessor implements IStreamProcessor {

    private final StringBuilder output = new StringBuilder();
    private final Set<String> successSignals;
    private final String failureSignal;

    private boolean initWithSuccess = false;
    private String failureFound = null;

    private final IStreamProcessor destination;

    public OutputWatchStreamProcessor(Set<String> success, String failure, IStreamProcessor destination) {
        this.successSignals = success;
        this.failureSignal = failure;
        this.destination = destination;
    }

    public boolean isSuccess(String output) {
        for (String success : successSignals) {
            if (output.contains(success))
                return true;
        }

        return false;
    }

    @Override
    public void process(String block) {
        destination.process(block);

        output.append(block);

        if (isSuccess(block)) {
            gotResult(true, null);
        } else {
            int failureIndex = output.indexOf(failureSignal);
            if (failureIndex != -1) {
                gotResult(false, output.substring(failureIndex));
            }
        }
    }

    @Override
    public void onProcessed() {
        gotResult(false, format(
                "Success startup signal matching [%s] not found withing defined timeout.",
                Joiner.on(", ").join(successSignals)));
    }

    private synchronized void gotResult(boolean success, String message) {
        this.initWithSuccess = success;
        failureFound = message;
        notify();
    }

    public synchronized void waitForResult(long timeout) {
        try {
            wait(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean isInitWithSuccess() {
        return initWithSuccess;
    }

    public String getFailureFound() {
        return failureFound;
    }

    public String collectedLog() {
        return output.toString();
    }
}
