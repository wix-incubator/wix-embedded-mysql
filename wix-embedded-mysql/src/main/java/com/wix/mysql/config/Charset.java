package com.wix.mysql.config;

/**
 * @author viliusl
 * @since 06/06/15
 */
public class Charset {
    public static final Charset UTF8MB4 = new Charset("UTF8mb4", "some-collate");
    public static final Charset UTF8 = new Charset("UTF8", "some-collate");
    public static final Charset LATIN1 = new Charset("latin1", "some-latin-1");

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
