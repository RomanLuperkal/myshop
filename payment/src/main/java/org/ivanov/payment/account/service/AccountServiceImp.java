package org.ivanov.payment.account.service;

import lombok.RequiredArgsConstructor;
import org.ivanov.payment.account.dto.BalanceResponseDto;
import org.ivanov.payment.account.dto.ProcessPaymentDto;
import org.ivanov.payment.handler.AccountException;
import org.ivanov.payment.account.model.Account;
import org.ivanov.payment.account.repository.AccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountServiceImp implements AccountService {
    private final AccountRepository accountRepository;


    @Override
    public Mono<ResponseEntity<BalanceResponseDto>> processOrder(Long xVer, ProcessPaymentDto processPaymentDto) {
        return accountRepository.findAccountByAccountId(processPaymentDto.getAccountId())
                .switchIfEmpty(Mono.error(new AccountException(HttpStatus.NOT_FOUND, "Аккаунт userId=" + processPaymentDto.getAccountId() + " не найден")))
                .flatMap(acc -> {
                    if (!acc.getVersion().equals(xVer)) {
                        return Mono.error(new AccountException(HttpStatus.CONFLICT, "Данные аккаунта были изменены. Попробуйте еще раз"));
                    }

                    BigDecimal newBalance = acc.getBalance().subtract(processPaymentDto.getOrderSum());
                    acc.setBalance(newBalance);

                    return accountRepository.save(acc).map(savedAcc -> {
                        BalanceResponseDto dto = new BalanceResponseDto();
                        dto.setBalance(savedAcc.getBalance());
                        return ResponseEntity.ok(dto);
                    });
                });
    }

    @Override
    public Mono<ResponseEntity<BalanceResponseDto>> getBalance(Long accountId) {
        Mono<Account> account = accountRepository.findAccountByAccountId(accountId);
        return account.switchIfEmpty(Mono.error(new AccountException(HttpStatus.NOT_FOUND, "Аккаунт accountId=" + accountId + " не найден")))
                .map(acc -> {
                    BalanceResponseDto responseDto = new BalanceResponseDto();
                    responseDto.setBalance(acc.getBalance());
                    return ResponseEntity.ok()
                            .header("X-Ver", acc.getVersion().toString())
                            .body(responseDto);
                });
        }
    }