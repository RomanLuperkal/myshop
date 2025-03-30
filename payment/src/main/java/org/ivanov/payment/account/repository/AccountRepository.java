package org.ivanov.payment.account.repository;

import org.ivanov.payment.account.model.Account;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface AccountRepository extends ReactiveCrudRepository<Account, Long> {
    Mono<Account> findAccountByUserIp(String userIp);
}
