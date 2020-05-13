package com.its.RESTClientDemo.service;

import com.its.RESTClientDemo.entity.CardEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class CardService {

    @Autowired
    private final RestTemplate restTemplate;

    /*public CardService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }*/

    public CardEntity enroll(CardEntity aCard) {
        log.info("Entering enroll");
        String cardNo = aCard.getCardNumber();
        log.info("Generating card alias");
        String alias = generateAlias(cardNo);
        aCard.setAlias(alias);
        log.info("Leaving enroll");
        return aCard;
    }

    @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delayExpression = "2"))
    private String generateAlias(String cardNo) {
        log.info("Generating alias for cardNo : {} by invoking downstream system ", cardNo);
        return restTemplate
                .getForObject(String.format("http://localhost:7080/%s", cardNo), String.class);
    }

    @Recover
    private String fallbackForGenerateAlias(Throwable th) {
        log.warn("Falling back to alternative of generateAlias");
        log.error("Exception occurred whilst invoking downstream system for generating alias " +
            "cause = {}, Exception message = {}, Exception class = {}, Exception stacktrace = {} ",
                th.getCause(), th.getMessage(), th.getClass(), th.getStackTrace());
        return null;
    }
}
