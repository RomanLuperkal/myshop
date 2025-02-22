package org.ivanov.myshop.handler.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public class ProductException extends RuntimeException {
    private HttpStatus status;
    private String message;
}
