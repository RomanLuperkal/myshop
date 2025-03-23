package org.ivanov.myshop.handler.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class CartException extends RuntimeException {
    private HttpStatus status;
    private String message;
}
