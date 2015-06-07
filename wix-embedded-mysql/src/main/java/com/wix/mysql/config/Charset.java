package com.wix.mysql.config;

/**
 * @author viliusl
 * @since 06/06/15
 */
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

    public String getCharset() { return charset; }
    public String getCollate() { return collate; }

    public static Charset of(final String charset, final String collate) {
        return new Charset(charset, collate);
    }

    public static Charset defaults()  { return UTF8MB4; }
}
