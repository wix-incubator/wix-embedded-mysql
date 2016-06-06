package com.wix.mysql;

import java.io.IOException;

public interface SqlCommandSource {
    String read() throws IOException;
}
