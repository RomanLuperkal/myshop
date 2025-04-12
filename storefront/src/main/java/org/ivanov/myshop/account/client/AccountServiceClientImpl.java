package org.ivanov.myshop.account.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.ivanov.myshop.account.dto.BalanceResponseDto;
import org.ivanov.myshop.account.dto.ProcessPaymentDto;
import org.ivanov.myshop.configuration.AccountServiceProperties;
import org.ivanov.myshop.handler.exception.CartException;
import org.ivanov.myshop.handler.response.ApiError;
import org.springframework.http.HttpStatus;
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
    private final ObjectMapper objectMapper;


    @Override
    public Mono<BalanceResponseDto> getBalance(Long accountId) {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(accountServiceProperties.getMethods().get("get-getBalance"))
                            .build(accountId))
                    .exchangeToMono(this::setHeader)
                    .onErrorResume(e -> {
                        if (e instanceof WebClientRequestException) {
                            System.out.println("Ошибка WebClient: " + e.getMessage());
                            return Mono.just(new BalanceResponseDto());
                        }
                        return Mono.error(new RuntimeException(e.getMessage()));
                    });

    }

    @Override
    public Mono<BalanceResponseDto> processOrder(Long xVer, ProcessPaymentDto dto) {
        return webClient.patch()
                .uri(accountServiceProperties.getMethods().get("patch-processOrder"))
                .header("X-Ver", xVer.toString())
                .bodyValue(dto)
                .retrieve()
                .onStatus(
                        status -> status == HttpStatus.CONFLICT,
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> {
                                    System.err.println("Ошибка 409 Conflict: " + body);

                                    try {
                                        ApiError errorResponse = objectMapper.readValue(body, ApiError.class);

                                        return Mono.error(new CartException(HttpStatus.CONFLICT, errorResponse.getMessage()));
                                    } catch (Exception parseException) {
                                        return Mono.error(new RuntimeException(parseException.getMessage()));
                                    }
                                })
                )
                .bodyToMono(BalanceResponseDto.class);
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
