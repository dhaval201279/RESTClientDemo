package com.its.RESTClientDemo.service;

import com.its.RESTClientDemo.entity.CardEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
@EnableRetry
public class CardService {

    @Autowired
    private final RestTemplate restTemplate;

    /*public CardService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }*/

    public CardEntity enroll(CardEntity aCard) {
        log.info("Entering enroll");
        String cardNo = aCard.getCardNumber();
        String alias = generateAlias(cardNo);
        log.info("Alias for card = {} is : {}", cardNo, alias);
        aCard.setAlias(alias);
        log.info("Leaving enroll");
        return aCard;
    }

    @Retryable(label = "generate-alias-retry-label", value = {Exception.class}, maxAttempts = 3,
        backoff = @Backoff(delayExpression = "2"))
    public String generateAlias(String cardNo) {
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
