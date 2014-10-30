package com.wix.mysql.input;

import de.flapdoodle.embed.process.io.IStreamProcessor;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author viliusl
 * @since 30/10/14
 */
public class CollectingLogOutputProcessor implements IStreamProcessor {

    private final Logger _logger;
    private final Level _level;
    private StringBuilder output = new StringBuilder();

    public CollectingLogOutputProcessor(Logger logger, Level level) {
        _logger = logger;
        _level = level;
    }

    @Override
    public void process(String block) {
        _logger.log(_level, block);
        output.append(block);
    }

    @Override
    public void onProcessed() {
    }

    public String getOuptut() {
        return output.toString();
    }

}