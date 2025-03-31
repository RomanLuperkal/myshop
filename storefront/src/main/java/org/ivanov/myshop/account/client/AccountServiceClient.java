package org.ivanov.myshop.account.client;

import org.ivanov.myshop.account.dto.BalanceResponseDto;
import reactor.core.publisher.Mono;

public interface AccountServiceClient {
    Mono<BalanceResponseDto> getBalance(String userIp);
}
