package com.wix.mysql;

import java.io.IOException;

public interface SqlScriptSource {
    String read() throws IOException;
}
