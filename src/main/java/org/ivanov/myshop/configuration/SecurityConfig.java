package org.ivanov.myshop.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
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
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // Отключаем CSRF
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/login").permitAll() // Разрешаем доступ к /login
                        .pathMatchers("/admin/**").authenticated() // Защищаем /admin/**
                        .anyExchange().permitAll() // Все остальные запросы разрешены
                )
                .formLogin(form -> form
                        .loginPage("/login") // Страница входа
                        .authenticationSuccessHandler(successHandler()) // Кастомный обработчик успешного входа
                )
                .logout(logout -> logout
                        .logoutSuccessHandler(logoutSuccessHandler()) // Кастомный обработчик выхода
                )
                .build();
    }

    @Bean
    public ReactiveUserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        // Создаем пользователя с ролью из конфигурации
        UserDetails user = User.builder()
                .username(administratorConfig.getLogin())
                .password(passwordEncoder.encode(administratorConfig.getPassword()))
                .roles(administratorConfig.getRole())
                .build();

        return new MapReactiveUserDetailsService(user); // Используем реактивный менеджер пользователей
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Используем тот же кодировщик паролей
    }

    /**
     * Обработчик успешной аутентификации
     */
    private ServerAuthenticationSuccessHandler successHandler() {
        return (exchange, authentication) -> {
            ServerHttpResponse response = exchange.getExchange().getResponse(); // Получаем HTTP-ответ
            response.setStatusCode(HttpStatus.FOUND); // Устанавливаем статус 302 (перенаправление)
            response.getHeaders().setLocation(URI.create("/admin/products")); // Указываем URL для перенаправления
            return Mono.empty(); // Завершаем поток
        };
    }

    /**
     * Обработчик успешного выхода
     */
    private ServerLogoutSuccessHandler logoutSuccessHandler() {
        return (exchange, authentication) -> {
            ServerHttpResponse response = exchange.getExchange().getResponse(); // Получаем HTTP-ответ
            response.setStatusCode(HttpStatus.FOUND); // Устанавливаем статус 302 (перенаправление)
            response.getHeaders().setLocation(URI.create("/products")); // Указываем URL для перенаправления
            return Mono.empty(); // Завершаем поток
        };
    }
}