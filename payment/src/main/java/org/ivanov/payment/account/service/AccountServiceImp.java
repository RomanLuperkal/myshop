package org.ivanov.payment.account.service;

import lombok.RequiredArgsConstructor;
import org.ivanov.payment.account.dto.BalanceReqDto;
import org.ivanov.payment.account.dto.BalanceResponseDto;
import org.ivanov.payment.account.dto.ProcessPaymentDto;
import org.ivanov.payment.account.handler.AccountException;
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
    public Mono<BalanceResponseDto> processOrder(Long xVer, ProcessPaymentDto processPaymentDto) {
        return accountRepository.findAccountByUserIp(processPaymentDto.getUserIp())
                .switchIfEmpty(Mono.error(new AccountException(HttpStatus.NOT_FOUND, "Аккаунт userId=" + processPaymentDto.getUserIp() + " не найден")))
                .flatMap(acc -> {
                    if (!acc.getVersion().equals(xVer)) {
                        return Mono.error(new AccountException(HttpStatus.CONFLICT, "Данные аккаунта были изменены. Попробуйте еще раз"));
                    }

                    BigDecimal newBalance = acc.getBalance().subtract(processPaymentDto.getOrderSum());
                    acc.setBalance(newBalance);

                    return accountRepository.save(acc)
                            .map(savedAcc -> {
                                BalanceResponseDto responseDto = new BalanceResponseDto();
                                responseDto.setBalance(savedAcc.getBalance());
                                return responseDto;
                            });
                });
    }

    @Override
    public Mono<ResponseEntity<BalanceResponseDto>> getBalance(BalanceReqDto balanceReqDto) {
        Mono<Account> account = accountRepository.findAccountByUserIp(balanceReqDto.getUserIp());
        return account.switchIfEmpty(Mono.error(new AccountException(HttpStatus.NOT_FOUND, "Аккаунт userId=" + balanceReqDto.getUserIp() + " не найден")))
                .map(acc -> {
                    BalanceResponseDto responseDto = new BalanceResponseDto();
                    responseDto.setBalance(acc.getBalance());
                    return ResponseEntity.ok()
                            .header("X-Ver", acc.getVersion().toString())
                            .body(responseDto);
                });

        }
    }