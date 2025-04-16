package org.ivanov.myshop.cart.service;

import org.ivanov.myshop.account.dto.ProcessPaymentDto;
import org.ivanov.myshop.cart.dto.*;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

public interface CartService {
    Mono<CartResponseDto> addToCart(CreateCartDto dto, Long accountId, WebSession session);

    Mono<CartResponseDto> removeFromCart(DeleteCartDto dto, Long accountId, WebSession session);

    Mono<ActualCartResponseDto> getActualCart(Long accountId, WebSession session);

    Mono<CartResponseDto> deleteProductFromCart(Long productId, Long accountId, WebSession session);

    Mono<ConfirmCartResponseDto> getConfirmCart(Long cartId);

    Mono<Long> confirmCart(ProcessPaymentDto dto, WebSession session);

    Mono<ListConfirmCartDto> getConfirmCartList(Long accountId);
}
