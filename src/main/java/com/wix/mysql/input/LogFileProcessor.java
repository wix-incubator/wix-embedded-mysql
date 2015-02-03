package com.wix.mysql.input;

import de.flapdoodle.embed.process.io.IStreamProcessor;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * @author viliusl
 * @since 24/10/14
 */
public class LogFileProcessor extends Thread {

    private final Logger log = LoggerFactory.getLogger(getClass().getName());

    private final static long delaySec = 3000;

    private final BlockingQueue<String> lines = new LinkedBlockingDeque<String>();
    private final Tailer tailer;
    private final IStreamProcessor processor;

    public LogFileProcessor(final File logFile, final IStreamProcessor processor) {
        tailer = Tailer.create(logFile, new LogTailerListener(lines), delaySec);
        this.processor = processor;

        setDaemon(true);
        start();
    }

    @Override
    public void run() {
        try {
            String res;
            while ((res = lines.poll(5, TimeUnit.SECONDS)) != null) {
                log.info(res);
                processor.process(res);
            }
        } catch (InterruptedException iox) {
            log.warn(iox.getMessage());
        } finally {
            processor.onProcessed();
            tailer.stop();
        }
    }

    public static class LogTailerListener extends TailerListenerAdapter {

        private final BlockingQueue<String> lines;

        public LogTailerListener(BlockingQueue<String> queue) {
            lines = queue;
        }

        public void handle(String line) {
            lines.offer(line);
        }
    }
}
