package com.its.RESTClientDemo.infrastructure.ssl;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@Component
@Slf4j
public class UntrustedHttpClientSslHelper implements HttpClientSslHelper {
    @Override
    public SSLContext getSslContext() {
        log.info("Entering UntrustedHttpClientSslHelper");
        SSLContext sslContext = null;
        SSLContextBuilder builder = SSLContextBuilder.create();
        try {
            builder = builder.loadTrustMaterial(null, new TrustAllStrategy());
            sslContext = builder.build();
            log.info("Trust material successfully loaded");
        } catch (NoSuchAlgorithmException | KeyStoreException e) {
            log.error("SSLContextBuilder could not be initialized " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("SSLContextBuilder could not be initialized " + e.getMessage(), e);
        }
        log.info("Leaving UntrustedHttpClientSslHelper");
        return sslContext;
    }
}
