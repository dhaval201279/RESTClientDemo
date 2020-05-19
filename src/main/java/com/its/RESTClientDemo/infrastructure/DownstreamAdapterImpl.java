package com.its.RESTClientDemo.infrastructure;

import com.its.RESTClientDemo.service.exception.RemoteServiceUnavailableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class DownstreamAdapterImpl implements DownstreamAdapter {
    @Autowired
    private final RestTemplate restTemplate;

    @Override
    public String generateAlias(String cardNo) {
        log.info("$$$$$$$$ Entering generateAlias and fetching alias for cardNo = {} by invoking downstream system ", cardNo);
        String cardAlias = null;
        try {
            cardAlias = restTemplate
                .getForObject(String.format("http://localhost:7080/%s", cardNo), String.class);
        } catch (Exception ex) {
            log.error("Exception occurred whilst invoking downstream system for card = {} ", cardNo);
            throw new RemoteServiceUnavailableException("Service Unavailable");
        }
        log.info("Leaving generateAlias with alias = {} for cardNo = {}", cardAlias, cardNo);
        return cardAlias;
    }

    @Override
    public String fallbackForGenerateAlias(Throwable th, String cardNo) {
        log.warn("####### Falling back to alternative of generateAlias for cardNo {}", cardNo);
        log.error("####### Exception occurred whilst invoking downstream system for generating alias " +
                        "cause = {}, Exception message = {}, Exception class = {}, Exception stacktrace = {} ",
                th.getCause(), th.getMessage(), th.getClass(), th.getStackTrace());
        return "default-value";
    }
}
