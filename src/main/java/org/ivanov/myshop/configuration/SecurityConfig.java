package org.ivanov.myshop.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.logout.RedirectServerLogoutSuccessHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.web.reactive.config.EnableWebFlux;

import java.net.URI;

@Configuration
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final AdministratorConfig administratorConfig;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // Отключаем CSRF
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/login").permitAll() // Разрешаем доступ к /login всем
                        .pathMatchers("/admin/**").authenticated() // Требуем аутентификации для /admin/**
                        .anyExchange().permitAll() // Разрешаем доступ ко всем остальным endpoint'ам
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login") // Страница входа
                        .authenticationSuccessHandler(authenticationSuccessHandler()) // Обработчик успешной аутентификации
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // URL для выхода
                        .logoutSuccessHandler(logoutSuccessHandler()) // Обработчик успешного выхода
                )
                .build();
    }

    @Bean
    public ServerAuthenticationSuccessHandler authenticationSuccessHandler() {
        // Перенаправление после успешного входа
        RedirectServerAuthenticationSuccessHandler handler = new RedirectServerAuthenticationSuccessHandler("/admin/products");
        return handler;
    }

    @Bean
    public ServerLogoutSuccessHandler logoutSuccessHandler() {
        // Перенаправление после успешного выхода
        RedirectServerLogoutSuccessHandler handler = new RedirectServerLogoutSuccessHandler();
        handler.setLogoutSuccessUrl(URI.create("/products"));
        return handler;
    }

    @Bean
    public ReactiveUserDetailsService userDetailsService() {
        UserDetails user = User.builder()
                .username(administratorConfig.getLogin())
                .password(passwordEncoder().encode(administratorConfig.getPassword()))
                .roles(administratorConfig.getRole())
                .build();

        return new MapReactiveUserDetailsService(user);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
