package org.ivanov.payment.account;


import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
public class AccountController{

    @GetMapping("/balance")
    public void getBalance() {

    }

    @PatchMapping("/processPayment")
    public void processPayment() {}

    @PatchMapping
    public void setBalance() {}
}
