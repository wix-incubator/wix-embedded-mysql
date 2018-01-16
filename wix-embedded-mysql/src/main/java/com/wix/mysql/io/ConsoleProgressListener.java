package com.wix.mysql.io;

import de.flapdoodle.embed.process.io.progress.StandardConsoleProgressListener;

import static java.lang.String.format;

public class ConsoleProgressListener extends StandardConsoleProgressListener {

    private ProgressStepper progressStepper = new ProgressStepper();

    @Override
    public void progress(String label, int percent) {
        if (progressStepper.hasNext(percent)) {
            int percentageToReport = progressStepper.setAndGet(percent);
            System.out.println(format("%s %d%%", label, percentageToReport));
        }
    }
}
