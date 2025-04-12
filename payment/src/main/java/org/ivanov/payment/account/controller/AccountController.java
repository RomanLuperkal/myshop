package org.ivanov.payment.account.controller;

import lombok.RequiredArgsConstructor;
import org.ivanov.payment.account.dto.BalanceResponseDto;
import org.ivanov.payment.account.dto.ProcessPaymentDto;
import org.ivanov.payment.account.service.AccountService;
import org.ivanov.payment.api.AccountApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class AccountController implements AccountApi {
    private final AccountService accountService;

    @Override
    public Mono<ResponseEntity<BalanceResponseDto>> accountBalanceAccountIdGet(Long accountId, ServerWebExchange exchange) {
        return accountService.getBalance(accountId);
    }

    @Override
    public Mono<ResponseEntity<BalanceResponseDto>> accountProcessPaymentPatch(Long xVer, Mono<ProcessPaymentDto> processPaymentDto, ServerWebExchange exchange) {
        return processPaymentDto.flatMap(dto -> accountService.processOrder(xVer, dto));
    }
}
