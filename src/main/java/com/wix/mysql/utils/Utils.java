package com.wix.mysql.utils;

import java.io.IOException;
import java.io.Reader;

/**
 * @author viliusl
 * @since 13/02/15
 */
public class Utils {

    //Cannot use higher version of guava than 16 right now due to framework (wix-embedded-mysql) uses this
    //TODO: make sure 18 version is in FW
    public static void closeCloseables(Reader... readers) {

        for (Reader reader: readers) {
            try {
                if (reader != null) reader.close();
            } catch (IOException ignored) {}
        }
    }
}
