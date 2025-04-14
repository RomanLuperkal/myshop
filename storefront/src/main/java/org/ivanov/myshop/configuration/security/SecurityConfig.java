package org.ivanov.myshop.configuration.security;

import lombok.RequiredArgsConstructor;
import org.ivanov.myshop.account.repository.AccountRepository;
import org.ivanov.myshop.configuration.AdministratorConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AdministratorConfig administratorConfig;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .securityContextRepository(new WebSessionServerSecurityContextRepository())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/login", "/register", "/products", "/products/**").permitAll()
                        .pathMatchers("/admin/**").hasRole("ADMIN")
                        .pathMatchers("/js/**").permitAll()
                        .anyExchange().authenticated()
                ).anonymous(Customizer.withDefaults())
                .formLogin(form -> form
                        .loginPage("/login")
                        .authenticationSuccessHandler(successHandler())
                        .authenticationFailureHandler(failureHandler())
                )
                .logout(logout -> logout
                        .logoutSuccessHandler(logoutSuccessHandler())
                )
                .build();
    }

    @Bean
    public ReactiveUserDetailsService userDetailsService(AccountRepository accountRepository) {
        return new AccountReactiveUserDetailsService(accountRepository);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private ServerAuthenticationSuccessHandler successHandler() {
        return (exchange, authentication) -> {
            ServerHttpResponse response = exchange.getExchange().getResponse();
            response.setStatusCode(HttpStatus.FOUND);
            response.getHeaders().setLocation(URI.create("/products"));
            return Mono.empty();
        };
    }

    private ServerAuthenticationFailureHandler failureHandler() {
        return (webFilterExchange, exception) -> {
            ServerWebExchange exchange = webFilterExchange.getExchange();
            exchange.getResponse().setStatusCode(HttpStatus.FOUND);
            exchange.getResponse().getHeaders().setLocation(URI.create("/login?error=true"));
            return Mono.empty();
        };
    }
    private ServerLogoutSuccessHandler logoutSuccessHandler() {
        return (exchange, authentication) -> {
            ServerHttpResponse response = exchange.getExchange().getResponse();
            response.setStatusCode(HttpStatus.FOUND);
            response.getHeaders().setLocation(URI.create("/products"));
            return Mono.empty();
        };
    }
}