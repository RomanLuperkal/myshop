package org.ivanov.payment.account.controller;

import lombok.RequiredArgsConstructor;
import org.ivanov.payment.account.dto.BalanceReqDto;
import org.ivanov.payment.account.dto.BalanceResponseDto;
import org.ivanov.payment.account.dto.ProcessPaymentDto;
import org.ivanov.payment.account.service.AccountService;
import org.ivanov.payment.api.BalanceApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class AccountController implements BalanceApi {
    private final AccountService accountService;

    @Override
    public Mono<ResponseEntity<BalanceResponseDto>> accountProcessPaymentPost(Long xVer, Mono<ProcessPaymentDto> processPaymentDto, ServerWebExchange exchange) {
        return processPaymentDto.flatMap(dto ->
                accountService.processOrder(xVer, dto)
                        .map(balanceResponseDto -> ResponseEntity.ok().body(balanceResponseDto)));
    }

    @Override
    public Mono<ResponseEntity<BalanceResponseDto>> accountBalanceGet(Mono<BalanceReqDto> balanceReqDto, ServerWebExchange exchange) {
        return balanceReqDto.flatMap(accountService::getBalance);
    }
}
