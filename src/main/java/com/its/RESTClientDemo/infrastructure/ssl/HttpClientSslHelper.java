package com.its.RESTClientDemo.infrastructure.ssl;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;

import javax.net.ssl.SSLContext;

public interface HttpClientSslHelper {

    SSLConnectionSocketFactory getSslConnectionSocketFactory();
}
