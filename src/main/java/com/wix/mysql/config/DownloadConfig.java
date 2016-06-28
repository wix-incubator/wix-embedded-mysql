package com.wix.mysql.config;

import de.flapdoodle.embed.process.config.store.HttpProxyFactory;
import de.flapdoodle.embed.process.config.store.IProxyFactory;
import de.flapdoodle.embed.process.config.store.NoProxyFactory;

public class DownloadConfig {
    private final IProxyFactory proxyFactory;

    private DownloadConfig(IProxyFactory proxy) {
        this.proxyFactory = proxy;
    }

    public IProxyFactory getProxyFactory() {
        return proxyFactory;
    }

    public static Builder aDownloadConfig() {
        return new Builder();
    }

    public static class Builder {
        IProxyFactory ProxyFactory = new NoProxyFactory();

        public Builder withHttpProxy(final String host, final int port) {
            this.ProxyFactory = new HttpProxyFactory(host, port);
            return this;
        }

        public DownloadConfig build() {
            return new DownloadConfig(this.ProxyFactory);
        }
    }
}
