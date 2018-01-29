package com.wix.mysql.config;

import de.flapdoodle.embed.process.config.store.HttpProxyFactory;
import de.flapdoodle.embed.process.config.store.IProxyFactory;

public class ProxyFactory {

    public static IProxyFactory aHttpProxy(String hostName, int port) {
        return new HttpProxyFactory(hostName, port);
    }
}
