package org.ivanov.myshop.account.client;

import lombok.RequiredArgsConstructor;
import org.ivanov.myshop.account.dto.BalanceResponseDto;
import org.ivanov.myshop.account.session.AccountProvider;
import org.ivanov.myshop.configuration.AccountServiceProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.server.WebSession;
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
                    .exchangeToMono(this::setHeader)
                    .onErrorResume(e -> {
                        // Проверяем, является ли исключение WebClientResponseException
                        if (e instanceof WebClientRequestException) {
                            // Логируем ошибку
                            System.err.println("Ошибка WebClient: " + e.getMessage());
                            // Пробрасываем исключение наверх
                            return Mono.just(new BalanceResponseDto());
                        }
                        return Mono.error(new RuntimeException(e.getMessage()));
                    });

    }

    private Mono<BalanceResponseDto> setHeader(ClientResponse clientResponse) {

        return Mono.deferContextual(context -> {
            WebSession session = context.get("webSession");
            String xVer = clientResponse.headers().asHttpHeaders().getFirst("X-Ver");
            session.getAttributes().put("X-Ver", xVer);
            return clientResponse.bodyToMono(BalanceResponseDto.class);
        });
    }
}
