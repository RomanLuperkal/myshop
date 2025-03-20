package org.ivanov.myshop.cart;

import org.ivanov.myshop.cart.filter.MockRemoteAddressFilter;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.reactive.server.WebTestClient;

@TestConfiguration
public class WebTestClientConfiguration {

    @Bean
    public WebTestClient webTestClient(ApplicationContext applicationContext) {
        return WebTestClient
                .bindToApplicationContext(applicationContext)
                .webFilter(new MockRemoteAddressFilter("127.0.0.1"))
                .configureClient()
                .build();
    }
}
