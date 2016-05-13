package com.wix.mysql.config;

import java.util.Arrays;

public class Charset {
    public static final Charset UTF8MB4 = new Charset("utf8mb4", "utf8mb4_unicode_ci");
    public static final Charset UTF8 = new Charset("utf8", "utf8_general_ci");
    public static final Charset LATIN1 = new Charset("latin1", "latin1_swedish_ci");

    private final String charset;
    private final String collate;

    private Charset(final String charset, final String collate) {
        this.charset = charset;
        this.collate = collate;
    }

    public static Charset aCharset(final String charset, final String collate) {
        return new Charset(charset, collate);
    }

    public static Charset defaults() {
        return UTF8MB4;
    }

    public String getCharset() {
        return charset;
    }

    public String getCollate() {
        return collate;
    }

    @Override
    public String toString() {
        return "Charset{" +
                "charset='" + charset + '\'' +
                ", collate='" + collate + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Charset that = (Charset) o;

        return areObjectsEqual(this.charset, that.charset) &&
                areObjectsEqual(this.collate, that.collate);
    }

    private <T> boolean areObjectsEqual(T o1, T o2) {
        return o1 == o2 || (o1 != null && o1.equals(o2));
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[]{charset, collate});
    }
}
