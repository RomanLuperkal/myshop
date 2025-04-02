package org.ivanov.myshop.account.client;

import lombok.RequiredArgsConstructor;
import org.ivanov.myshop.account.dto.BalanceResponseDto;
import org.ivanov.myshop.account.dto.ProcessPaymentDto;
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
                        if (e instanceof WebClientRequestException) {
                            System.err.println("Ошибка WebClient: " + e.getMessage());
                            return Mono.just(new BalanceResponseDto());
                        }
                        return Mono.error(new RuntimeException(e.getMessage()));
                    });

    }

    @Override
    //TODO Исключение на случай оптимистичной блокировки
    public Mono<BalanceResponseDto> processOrder(Long xVer, ProcessPaymentDto dto) {
        return webClient.patch()
                .uri(accountServiceProperties.getMethods().get("patch-processOrder"))
                .header("X-Ver", xVer.toString())
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(BalanceResponseDto.class)
                .onErrorResume(e -> {
                    if (e instanceof WebClientRequestException) {
                        System.err.println("Ошибка WebClient: " + e.getMessage());
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
