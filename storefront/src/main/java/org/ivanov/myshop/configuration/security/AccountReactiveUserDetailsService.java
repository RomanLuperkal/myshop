package org.ivanov.myshop.configuration.security;

import lombok.RequiredArgsConstructor;
import org.ivanov.myshop.account.repository.AccountRepository;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class AccountReactiveUserDetailsService implements ReactiveUserDetailsService {
    private final AccountRepository accountRepository;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return accountRepository.findByUsername(username)
                .map(account ->
                    new AccountUserDetails(
                            account.getAccountId(),
                            account.getUsername(),
                            account.getPassword(),
                            AuthorityUtils.createAuthorityList(account.getRole().toString())
                    )
                );
    }
}
