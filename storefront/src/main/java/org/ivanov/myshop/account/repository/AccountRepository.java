package org.ivanov.myshop.account.repository;


import org.ivanov.myshop.account.model.Account;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface AccountRepository extends R2dbcRepository<Account, Long> {
    Mono<Account> findByUsername(String username);
}
