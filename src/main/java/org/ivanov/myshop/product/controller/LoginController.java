package org.ivanov.myshop.product.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.result.view.Rendering;

@Controller
public class LoginController {

    @GetMapping("/login")
    public Rendering login() {
        return Rendering.view("login").build();
    }
}
