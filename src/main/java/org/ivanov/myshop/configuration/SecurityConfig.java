package org.ivanov.myshop.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final AdministratorConfig administratorConfig;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf((AbstractHttpConfigurer::disable))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login").permitAll() // Доступ к странице логина для всех
                        .requestMatchers("/admin/**").authenticated() // Доступ к /admin/** только для авторизованных
                        .anyRequest().permitAll() // Остальные запросы доступны всем
                )
                .formLogin(form -> form
                        .defaultSuccessUrl("/admin/products", true) // Перенаправление после успешного входа
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/products") // Перенаправление после выхода
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.builder()
                .username(administratorConfig.getLogin())
                .password(passwordEncoder().encode(administratorConfig.getPassword())) // Пароль кодируется
                .roles(administratorConfig.getRole())
                .build();

        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Кодировщик паролей
    }
}
