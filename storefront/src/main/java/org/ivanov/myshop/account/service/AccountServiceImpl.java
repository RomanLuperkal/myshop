package org.ivanov.myshop.account.service;

import lombok.RequiredArgsConstructor;
import org.ivanov.myshop.account.dto.CreateAccountDto;
import org.ivanov.myshop.account.mapper.AccountMapper;
import org.ivanov.myshop.account.model.Account;

import org.ivanov.myshop.account.repository.AccountRepository;
import org.ivanov.myshop.handler.exception.AccountException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Mono<Void> createAccount(CreateAccountDto createAccountDto) {
        return accountRepository.findByUsername(createAccountDto.getUsername())
                .flatMap(existingAccount -> Mono.error(new AccountException(HttpStatus.CONFLICT, "Такой аккаунт уже существует")))
                .switchIfEmpty(createNewAccount(createAccountDto))
                .then();
    }

    private Mono<Account> createNewAccount(CreateAccountDto createAccountDto) {
        Account newAccount = accountMapper.mapToAccount(createAccountDto);
        newAccount.setPassword(passwordEncoder.encode(createAccountDto.getPassword()));
        return accountRepository.save(newAccount);
    }
}
