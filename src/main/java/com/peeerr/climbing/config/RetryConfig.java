package com.peeerr.climbing.config;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import java.util.HashMap;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class RetryConfig {

    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final long INITIAL_INTERVAL = 1000L;
    private static final double MULTIPLIER = 2.0;
    private static final long MAX_INTERVAL = 10000L;

    @Bean
    public RetryTemplate s3RetryTemplate() {
        RetryTemplate template = new RetryTemplate();

        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(INITIAL_INTERVAL);
        backOffPolicy.setMultiplier(MULTIPLIER);
        backOffPolicy.setMaxInterval(MAX_INTERVAL);

        Map<Class<? extends Throwable>, Boolean> retryableExceptions = new HashMap<>();
        retryableExceptions.put(AmazonS3Exception.class, true);

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(MAX_RETRY_ATTEMPTS, retryableExceptions);

        template.setBackOffPolicy(backOffPolicy);
        template.setRetryPolicy(retryPolicy);

        return template;
    }

}
