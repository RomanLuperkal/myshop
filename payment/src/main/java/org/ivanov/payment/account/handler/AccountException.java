package org.ivanov.payment.account.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public class AccountException extends RuntimeException {
    private HttpStatus status;
    private String message;
}
