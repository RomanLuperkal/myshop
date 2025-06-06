package org.ivanov.myshop.account.client;

import org.ivanov.myshop.account.dto.BalanceResponseDto;
import org.ivanov.myshop.account.dto.ProcessPaymentDto;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

public interface AccountServiceClient {
    Mono<BalanceResponseDto> getBalance(Long accountId, WebSession session);

    Mono<BalanceResponseDto> processOrder(Long xVer, ProcessPaymentDto dto);
}
