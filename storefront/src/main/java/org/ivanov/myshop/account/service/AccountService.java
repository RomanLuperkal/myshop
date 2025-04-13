package org.ivanov.myshop.account.service;

import org.ivanov.myshop.account.dto.CreateAccountDto;
import reactor.core.publisher.Mono;

public interface AccountService {
    Mono<Void> createAccount(CreateAccountDto createAccountDto);
}
