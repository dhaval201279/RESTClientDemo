package com.its.RESTClientDemo.infrastructure.ssl;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;

import javax.net.ssl.SSLContext;

public class UntrustedHttpClientSslHelper implements HttpClientSslHelper {
    @Override
    public SSLConnectionSocketFactory getSslConnectionSocketFactory() {
        /*SSLContext
            .getDefault()
            .init();

        return new SSLConnectionSocketFactory();*/
        return null;
    }
}
