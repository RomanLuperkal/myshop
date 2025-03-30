package org.ivanov.payment.account.service;

import org.ivanov.payment.account.dto.BalanceReqDto;
import org.ivanov.payment.account.dto.BalanceResponseDto;
import org.ivanov.payment.account.dto.ProcessPaymentDto;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface AccountService {
    Mono<BalanceResponseDto> processOrder(Long xVer, ProcessPaymentDto processPaymentDto);

    Mono<ResponseEntity<BalanceResponseDto>> getBalance(BalanceReqDto balanceReqDto);
}
