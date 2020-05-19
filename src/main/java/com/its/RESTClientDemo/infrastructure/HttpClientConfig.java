package com.its.RESTClientDemo.infrastructure;

import com.its.RESTClientDemo.infrastructure.ssl.HttpClientSslHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.pool.PoolStats;
import org.apache.http.protocol.HttpContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
@RequiredArgsConstructor
@EnableScheduling
public class HttpClientConfig {

    private final HttpClientSslHelper httpClientSslHelper;

    @Value("${http.client.connection.timeout:15000}")
    private static final Integer CONNECTION_TIMEOUT = 15000;

    @Value("${http.client.connection.request.timeout:15000}")
    private static final Integer CONNECTION_REQUEST_TIMEOUT = 15000;

    @Value("${http.client.socket.timeout:30000}")
    private static final Integer SOCKET_TIMEOUT = 30000;

    @Value("${http.client.max.connections:50}")
    private static final Integer MAX_CONNECTIONS = 50;

    @Value("${http.client.connection.timeout:50}")
    private static final Integer MAX_PER_ROUTE_CONNECTION = 50;

    @Value("${http.client.default.keep.alive.time.millis:15000}")
    private static final Integer DEFAULT_KEEP_ALIVE_TIME_MILLIS = 15 * 1000;

    @Value("${http.client.max.keep.alive.time.millis:30000}")
    private static final Integer MAX_KEEP_ALIVE_TIME_MILLIS = 30 * 1000;

    @Value("${http.client.idle.connection.wait.time.sec:50}")
    private static final Integer IDLE_CONNECTION_WAIT_TIME_SECS = 30;

    @Value("${http.client.validate.after.inactivity.millis:200}")
    private static final Integer VALIDATE_AFTER_INACTIVITY_IN_MILLIS = 200;

    /**
     * When an instance CloseableHttpClient is no longer needed and is about to go out of scope the
     * connection manager associated with it must be shut down by calling the
     * CloseableHttpClient#close() method.
     * */
    @Bean
    public CloseableHttpClient getHttpClient() {
        log.info("Instantiating CloseableHttpClient");
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            httpclient = HttpClients
                            .custom()
                            .setDefaultRequestConfig(prepareHttpClientRequestConfig())
                            .setConnectionManager(poolingConnectionManager())
                            .setKeepAliveStrategy(connectionKeepAliveStrategy())
                            // got rid of  java.lang.IllegalStateException: Connection pool shut down issue
                            .setConnectionManagerShared(Boolean.TRUE)
                            .disableConnectionState()
                            .disableCookieManagement()
                            .build();
        }
        finally {
            try {
                log.info("Closing http client");
                httpclient.close();
            } catch (IOException e) {
                log.error("Error whilst closing httpclient");
            }
        }
        return httpclient;
    }

    /**
     *  If the Keep-Alive header is not present in the response, HttpClient assumes the connection can be
     *  kept alive indefinitely. However, many HTTP servers in general use are configured to drop
     *  persistent connections after a certain period of inactivity in order to conserve system
     *  resources, quite often without informing the client. In case the default strategy turns out
     *  to be too optimistic, one may want to provide a custom keep-alive strategy.
     *
     * */
    private ConnectionKeepAliveStrategy connectionKeepAliveStrategy() {
        return new DefaultConnectionKeepAliveStrategy() {
            @Override
            public long getKeepAliveDuration(final HttpResponse response, final HttpContext context) {
                log.info("Instantiating DefaultConnectionKeepAliveStrategy by determining keep alive time ");
                long keepAliveDuration = super.getKeepAliveDuration(response, context);

                if (keepAliveDuration < 0) {
                    keepAliveDuration = DEFAULT_KEEP_ALIVE_TIME_MILLIS;
                } else if (keepAliveDuration > MAX_KEEP_ALIVE_TIME_MILLIS) {
                    keepAliveDuration = MAX_KEEP_ALIVE_TIME_MILLIS;
                }
                log.info("Keep alive duration = {} ", keepAliveDuration);
                return keepAliveDuration;
            }
        };
    }

    private RequestConfig prepareHttpClientRequestConfig() {

        RequestConfig requestConfig =  RequestConfig
                                        .custom()
                                        .setConnectTimeout(CONNECTION_TIMEOUT)
                                        .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
                                        .setSocketTimeout(SOCKET_TIMEOUT)
                                        .build();

        log.info("Instantiated HTTP client's RequestConfig object with connection timeout = {}, " +
                        "connection request timeout = {}, socket timeout = {}", CONNECTION_TIMEOUT,
                CONNECTION_REQUEST_TIMEOUT, SOCKET_TIMEOUT);

        return requestConfig;
    }

    @Bean
    public PoolingHttpClientConnectionManager poolingConnectionManager() {

        PoolingHttpClientConnectionManager poolingConnectionManager =
            new PoolingHttpClientConnectionManager(getConnectionSocketFactoryRegistry());
        poolingConnectionManager.setMaxTotal(MAX_CONNECTIONS);
        poolingConnectionManager.setDefaultMaxPerRoute(MAX_PER_ROUTE_CONNECTION);
        poolingConnectionManager.setValidateAfterInactivity(VALIDATE_AFTER_INACTIVITY_IN_MILLIS);
        log.info("Connection pool instantiated with max connections = {}, max per route connections = {}," +
                "validate after inactivity in millis = {}", MAX_CONNECTIONS, MAX_PER_ROUTE_CONNECTION,
                VALIDATE_AFTER_INACTIVITY_IN_MILLIS);
        return poolingConnectionManager;
    }

    /**
     * a dedicated monitor thread used to evict connections that are considered expired due to a long
     * period of inactivity. The monitor thread can periodically call
     * ClientConnectionManager#closeExpiredConnections() method to close all expired connections and
     * evict closed connections from the pool. It can also optionally call
     * ClientConnectionManager#closeIdleConnections() method to close all connections that have been
     * idle over a given period of time.
     * */
    @Bean
    public Runnable idleAndExpiredConnectionProcessor(final PoolingHttpClientConnectionManager connectionManager) {
        return new Runnable() {
            @Override
            @Scheduled(fixedDelay = 20000)
            public void run() {
                log.info("Entering idleAndExpiredConnectionProcessor");
                try {
                    if (connectionManager != null) {
                        log.info("Closing expired and idle connections...");
                        connectionManager.closeExpiredConnections();
                        connectionManager.closeIdleConnections(IDLE_CONNECTION_WAIT_TIME_SECS, TimeUnit.SECONDS);
                    } else {
                        log.info("Http Client Connection manager has not been initialised");
                    }
                } catch (Exception e) {
                    log.error("Exception occurred whilst closing expired and idel connections. msg = {}, e = {}", e.getMessage(), e);
                }
                log.info("Leaving idleAndExpiredConnectionProcessor");
            }
        };
    }

    /**
     * Routes -
     * PoolingHttpClientConnectionManager maintains a maximum limit of connections on a per route
     * basis and in total. Per default this implementation will create no more than 2 concurrent
     * connections per given route and no more 20 connections in total. For many real-world applications
     * these limits may prove too constraining, especially if they use HTTP as a transport protocol
     * for their services.
     *
     *
     * */
    @Bean
    public Runnable connectionPoolMetricsLogger(final PoolingHttpClientConnectionManager connectionManager) {
        return new Runnable() {
            @Override
            @Scheduled(fixedDelay = 30000)
            public void run() {
                log.info("Entering connectionPoolMetricsLogger");
                final StringBuilder buffer = new StringBuilder();
                try {
                    if (connectionManager != null) {
                        final PoolStats totalPoolStats = connectionManager.getTotalStats();
                        log.info(" ** HTTP Client Connection Pool Stats : Available = {}, Leased = {}, Pending = {}, Max = {} **",
                                totalPoolStats.getAvailable(), totalPoolStats.getLeased(), totalPoolStats.getPending(), totalPoolStats.getMax());

                        connectionManager
                            .getRoutes()
                            .stream()
                            .forEach(route -> {
                                final PoolStats routeStats = connectionManager.getStats(route);
                                buffer
                                        .append(" ++ HTTP Client Connection Pool Route Pool Stats ++ ")
                                        .append(" Route : " + route.toString())
                                        .append(" Available : " + routeStats.getAvailable())
                                        .append(" Leased : " + routeStats.getLeased())
                                        .append(" Pending : " + routeStats.getPending())
                                        .append(" Max : " + routeStats.getMax());
                            });
                        log.info(buffer.toString());
                    } else {
                        log.info("Http Client Connection manager has not been initialised");
                    }
                } catch (Exception e) {
                    log.error("Exception occurred whilst logging http connection pool stats. msg = {}, e = {}", e.getMessage(), e);
                }
                log.info("Leaving connectionPoolMetricsLogger");
            }
        };
    }

    private Registry<ConnectionSocketFactory> getConnectionSocketFactoryRegistry() {
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(httpClientSslHelper.getSslContext());
        Registry<ConnectionSocketFactory> registry = RegistryBuilder
                                                        .<ConnectionSocketFactory>create()
                                                        .register("https", sslsf)
                                                        .register("http",
                                                                new PlainConnectionSocketFactory())
                                                        .build();

        return registry;
    }
}
