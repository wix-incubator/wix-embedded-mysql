package com.wix.mysql.common;

import org.slf4j.bridge.SLF4JBridgeHandler;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public final class LoggingAdapter {

    public static void initLogging() {
        if (!initiated) {
            initiated = true;
            LogManager.getLogManager().reset();
            SLF4JBridgeHandler.install();
            Logger.getLogger("global").setLevel(Level.FINEST);
        }
    }

    private static boolean initiated = false;
}
