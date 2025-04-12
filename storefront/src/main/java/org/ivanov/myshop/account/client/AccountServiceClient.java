package org.ivanov.myshop.account.client;

import org.ivanov.myshop.account.dto.BalanceResponseDto;
import org.ivanov.myshop.account.dto.ProcessPaymentDto;
import reactor.core.publisher.Mono;

public interface AccountServiceClient {
    Mono<BalanceResponseDto> getBalance(Long accountId);

    Mono<BalanceResponseDto> processOrder(Long xVer, ProcessPaymentDto dto);
}
