package com.its.RESTClientDemo.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestTemplate;

@Configuration
@Slf4j
public class RestTemplateConfig {

    @Autowired
    CloseableHttpClient httpClient;

    @Bean
    public RestTemplate restTemplate() {
        log.info("Entering restTemplate");
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory());
        log.info("Leaving restTemplate after instantiating RestTemplate");
        return restTemplate;
    }

    @Bean
    public HttpComponentsClientHttpRequestFactory clientHttpRequestFactory() {
        log.info("Entering clientHttpRequestFactory");
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setHttpClient(httpClient);
        log.info("Leaving clientHttpRequestFactory after instantiating HttpComponentsClientHttpRequestFactory");
        return clientHttpRequestFactory;
    }

    /**
     * To support @Scheduled annotation in HttpClientConfig, we have to add support of scheduled
     * execution of thread. For that, we have used bean ThreadPoolTaskScheduler which internally
     * utilizes ScheduledThreadPoolExecutor to schedule commands to run after a given delay, or to
     * execute periodically.
     * */
    @Bean
    public TaskScheduler taskScheduler() {
        log.info("Entering taskScheduler");
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setThreadNamePrefix("threadPoolScheduler");
        scheduler.setPoolSize(50);
        log.info("Leaving taskScheduler");
        return scheduler;
    }
}
