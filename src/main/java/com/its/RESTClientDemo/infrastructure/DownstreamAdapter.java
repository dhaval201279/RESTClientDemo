package com.its.RESTClientDemo.infrastructure;

import com.its.RESTClientDemo.service.exception.RemoteServiceUnavailableException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;

public interface DownstreamAdapter {
    @Retryable(value = {RemoteServiceUnavailableException.class}, maxAttempts = 8,
        backoff = @Backoff(delay = 1000, multiplier = 2), label = "generate-alias-retry-label")
    String generateAlias(String cardNo);

    @Recover
    String fallbackForGenerateAlias(Throwable th, String cardNo);
}
