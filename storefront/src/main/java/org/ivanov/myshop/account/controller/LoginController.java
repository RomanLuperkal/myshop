package org.ivanov.myshop.account.controller;

import lombok.RequiredArgsConstructor;
import org.ivanov.myshop.account.dto.CreateAccountDto;
import org.ivanov.myshop.account.service.AccountService;
import org.ivanov.myshop.handler.exception.AccountException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class LoginController {
    private final AccountService accountService;

    @GetMapping("/login")
    public Mono<Rendering> login(@RequestParam(value = "error", required = false) String error) {
        Rendering r = Rendering.view("login")
                .modelAttribute("error", error != null)
                .build();
        return Mono.just(r);
    }

    @GetMapping("/register")
    public Mono<Rendering> register(@RequestParam(value = "error", required = false) String error,
                                    @ModelAttribute CreateAccountDto accountDto) {
        Rendering r = Rendering.view("registration").build();
        return Mono.just(r);
    }

    @PostMapping("/register")
    public Mono<Rendering> register(@ModelAttribute CreateAccountDto accountDto) {
        return accountService.createAccount(accountDto)
                .thenReturn(Rendering.redirectTo("/login").build())
                .onErrorResume(AccountException.class, e -> {
                    Rendering r = Rendering.view("registration")
                            .modelAttribute("errorMessage", e.getMessage())
                            .build();
                    return Mono.just(r);
                });
    }
}
