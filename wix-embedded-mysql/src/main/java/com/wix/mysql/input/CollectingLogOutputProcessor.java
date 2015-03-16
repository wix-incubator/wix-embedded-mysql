package com.wix.mysql.input;

import de.flapdoodle.embed.process.io.IStreamProcessor;
import org.slf4j.Logger;


/**
 * @author viliusl
 * @since 30/10/14
 */
public class CollectingLogOutputProcessor implements IStreamProcessor {

    private final Logger _logger;
    private StringBuilder output = new StringBuilder();

    public CollectingLogOutputProcessor(Logger logger) {
        _logger = logger;
    }

    @Override
    public void process(String block) {
        _logger.debug(block);
        output.append(block);
    }

    @Override
    public void onProcessed() {
    }

    public String getOuptut() {
        return output.toString();
    }

}