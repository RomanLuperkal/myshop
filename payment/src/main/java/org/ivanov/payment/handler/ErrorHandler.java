package org.ivanov.payment.handler;

import org.ivanov.payment.account.dto.ApiError;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(AccountException.class)
    private Mono<ResponseEntity<ApiError>> handleException(AccountException e) {
        ApiError errorResponse = new ApiError();
        errorResponse.setMessage(e.getMessage());
        errorResponse.setCode(e.getStatus().toString());
        return Mono.just(ResponseEntity.status(e.getStatus()).body(errorResponse));
    }
}
