package org.ivanov.myshop.handler;

import org.ivanov.myshop.handler.exception.CartException;
import org.ivanov.myshop.handler.exception.ProductException;
import org.ivanov.myshop.handler.response.ApiError;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(ProductException.class)
    private ResponseEntity<ApiError> handleException(ProductException e) {
        ApiError errorResponse = ApiError.builder()
                .message(e.getMessage())
                .status(e.getStatus().toString())
                .build();
        return ResponseEntity.status(e.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(CartException.class)
    private ResponseEntity<ApiError> handleException(CartException e) {
        ApiError errorResponse = ApiError.builder()
                .message(e.getMessage())
                .status(e.getStatus().toString())
                .build();
        return ResponseEntity.status(e.getStatus()).body(errorResponse);
    }

    /*@ExceptionHandler(AccountException.class)
    private Mono<Rendering> handleException(AccountException e) {
        Rendering r = Rendering.redirectTo("/register").modelAttribute("errorMessage", e.getMessage()).build();
        return Mono.just(r);
    }*/
}
