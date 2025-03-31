package org.ivanov.myshop.account.client;

import lombok.RequiredArgsConstructor;
import org.ivanov.myshop.account.dto.BalanceResponseDto;
import org.ivanov.myshop.configuration.AccountServiceProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AccountServiceClientImpl implements AccountServiceClient {
    private final WebClient webClient;
    private final AccountServiceProperties accountServiceProperties;

    @Override
    public Mono<BalanceResponseDto> getBalance(String userIp) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(accountServiceProperties.getMethods().get("get-getBalance"))
                        .build(userIp))
                .retrieve()
                .bodyToMono(BalanceResponseDto.class);
    }
}
