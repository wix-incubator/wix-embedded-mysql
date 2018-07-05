package com.wix.mysql.utils;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class Utils {
    public static void closeCloseables(Reader... readers) {

        for (Reader reader : readers) {
            try {
                if (reader != null) reader.close();
            } catch (IOException ignored) {
            }
        }
    }

    public static String asHHmmOffset(TimeZone timeZone) {
        long offsetInMillis = timeZone.getOffset(Calendar.getInstance().getTimeInMillis());
        return format("%s%02d:%02d",
                offsetInMillis >= 0 ? "+" : "-",
                Math.abs(MILLISECONDS.toHours(offsetInMillis)),
                MILLISECONDS.toMinutes(offsetInMillis) - HOURS.toMinutes(MILLISECONDS.toHours(offsetInMillis)));
    }

    public static <T> T or(T arg1, T arg2) {
        return arg1 != null ? arg1 : arg2;
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static String join(List<?> list, String delim) {
        if (list.isEmpty()) return "";
        StringBuilder sb = new StringBuilder(list.get(0).toString());
        for (int i = 1; i < list.size(); i++) {
            sb.append(delim).append(list.get(i).toString());
        }
        return sb.toString();
    }

    public static String readToString(Reader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        CharBuffer buf = CharBuffer.allocate(1024);
        while (reader.read(buf) != -1) {
            buf.flip();
            sb.append(buf);
            buf.clear();
        }
        return sb.toString();
    }
}
