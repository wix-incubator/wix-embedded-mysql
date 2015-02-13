package com.wix.mysql.input;

import de.flapdoodle.embed.process.io.IStreamProcessor;

import java.util.HashSet;
import java.util.Set;

/**
 * @author viliusl
 * @since 13/02/15
 */
public class OutputWatchStreamProcessor implements IStreamProcessor {

    //private final Reader _reader;
    private final StringBuilder output = new StringBuilder();
    private final Set<String> successes;
    private final Set<String> failures;

    private boolean initWithSuccess = false;
    private String failureFound = null;

    private final IStreamProcessor destination;

    public OutputWatchStreamProcessor(Set<String> successes, Set<String> failures, IStreamProcessor destination) {
        this.successes = successes;
        this.failures = new HashSet<String>(failures);
        this.destination = destination;
    }

    public boolean isSuccess(String output) {
        for (String success: successes) {
            if (output.indexOf(success) != -1)
                return true;
        }

        return false;
    }

    @Override
    public void process(String block) {
        destination.process(block);

        CharSequence line = block;
        output.append(line);

        if (isSuccess(line.toString())) {
            gotResult(true,null);
        } else {
            for (String failure : failures) {
                int failureIndex = output.indexOf(failure);
                if (failureIndex != -1) {
                    gotResult(false,output.substring(failureIndex));
                }
            }
        }
    }

    @Override
    public void onProcessed() {
        gotResult(false,"<EOF>");
    }

    private synchronized void gotResult(boolean success, String message) {
        this.initWithSuccess=success;
        failureFound=message;
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

    public String getOutput() {
        return output.toString();
    }


}
