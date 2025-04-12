package org.ivanov.payment.account.service;

import org.ivanov.payment.account.dto.BalanceResponseDto;
import org.ivanov.payment.account.dto.ProcessPaymentDto;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface AccountService {
    Mono<ResponseEntity<BalanceResponseDto>> processOrder(Long xVer, ProcessPaymentDto processPaymentDto);

    Mono<ResponseEntity<BalanceResponseDto>> getBalance(Long accountId);
}
