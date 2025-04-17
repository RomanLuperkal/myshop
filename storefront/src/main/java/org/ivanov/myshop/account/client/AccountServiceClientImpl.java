package org.ivanov.myshop.account.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.ivanov.myshop.account.dto.BalanceResponseDto;
import org.ivanov.myshop.account.dto.ProcessPaymentDto;
import org.ivanov.myshop.configuration.AccountServiceProperties;
import org.ivanov.myshop.handler.exception.CartException;
import org.ivanov.myshop.handler.response.ApiError;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
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
    private final ReactiveOAuth2AuthorizedClientManager manager;


    @Override
    public Mono<BalanceResponseDto> getBalance(Long accountId, WebSession session) {
            return getAccessToken().flatMap(accessToken -> webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(accountServiceProperties.getMethods().get("get-getBalance"))
                            .build(accountId))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .exchangeToMono(resp -> setHeader(resp, session))
                    .onErrorResume(e -> {
                        if (e instanceof WebClientRequestException) {
                            System.out.println("Ошибка WebClient: " + e.getMessage());
                            return Mono.just(new BalanceResponseDto());
                        }
                        return Mono.error(new RuntimeException(e.getMessage()));
                    }));

    }

    @Override
    public Mono<BalanceResponseDto> processOrder(Long xVer, ProcessPaymentDto dto) {
        return getAccessToken().flatMap(accessToken -> webClient.patch()
                .uri(accountServiceProperties.getMethods().get("patch-processOrder"))
                .header("X-Ver", xVer.toString())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
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
                .bodyToMono(BalanceResponseDto.class));
    }

    private Mono<BalanceResponseDto> setHeader(ClientResponse clientResponse, WebSession session) {

        return Mono.just(clientResponse).flatMap(resp -> {
        String xVer = clientResponse.headers().asHttpHeaders().getFirst("X-Ver");
        session.getAttributes().put("X-Ver", xVer);
        return clientResponse.bodyToMono(BalanceResponseDto.class);
        });
    }

    private Mono<String> getAccessToken() {
        return manager.authorize(OAuth2AuthorizeRequest
                        .withClientRegistrationId("storefront")
                        .principal("system")
                        .build()).map(OAuth2AuthorizedClient::getAccessToken)
                .map(OAuth2AccessToken::getTokenValue);
    }
}
