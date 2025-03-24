package org.ivanov.payment.accaunt;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
public class AccountController {

    @GetMapping("/balance")
    public void getBalance() {

    }

    @PatchMapping("/withdraw")
    public void withdraw() {}

    @PatchMapping
    public void setBalance() {}
}
