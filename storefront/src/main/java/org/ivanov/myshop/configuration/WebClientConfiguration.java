package org.ivanov.myshop.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class WebClientConfiguration {
    private final AccountServiceProperties accountServiceProperties;

    @Bean()
    public WebClient accountServiceWebClient() {
        return WebClient.builder().baseUrl(accountServiceProperties.getHost()).build();
    }
}
