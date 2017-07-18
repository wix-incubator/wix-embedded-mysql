package com.wix.mysql.io;

import de.flapdoodle.embed.process.io.IStreamProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NotifyingStreamProcessor implements IStreamProcessor {

    private final List<ResultMatchingListener> listeners = new ArrayList<>();
    private final IStreamProcessor delegate;

    public NotifyingStreamProcessor(IStreamProcessor processor) {
        this.delegate = processor;
    }

    public ResultMatchingListener addListener(ResultMatchingListener listener) {
        listeners.add(listener);
        return listener;
    }

    @Override
    public void process(String block) {
        for (ResultMatchingListener listener : listeners) {
            listener.onMessage(block);
        }
        delegate.process(block);
    }

    @Override
    public void onProcessed() {

    }

    public static class ResultMatchingListener {

        private final String successPattern;
        private final String failurePattern = "[ERROR]";
        private final StringBuilder output = new StringBuilder();
        private boolean initWithSuccess = false;
        private String failureFound = null;
        private boolean completed = false;

        public ResultMatchingListener(String successPattern) {
            this.successPattern = successPattern;
        }

        public void onMessage(final String message) {
            output.append(message);
            if (containsPattern()) {
                gotResult(true, null);
            } else {
                int failureIndex = output.indexOf(failurePattern);
                if (failureIndex != -1) {
                    gotResult(false, output.substring(failureIndex));
                }
            }
        }

        private boolean containsPattern() {
            return output.indexOf(this.successPattern) != -1;
        }

        private synchronized void gotResult(boolean success, String message) {
            initWithSuccess = success;
            failureFound = message;
            completed = true;
            notify();
        }

        public synchronized void waitForResult(long timeoutMs) {
            try {
                wait(timeoutMs);
                if (!completed) {
                    throw new RuntimeException(String.format("Timeout of %s sec exceeded while waiting for process to complete", TimeUnit.MILLISECONDS.toSeconds(timeoutMs)));
                }
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
    }
}
