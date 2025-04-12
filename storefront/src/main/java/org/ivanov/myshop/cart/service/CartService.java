package org.ivanov.myshop.cart.service;

import org.ivanov.myshop.account.dto.ProcessPaymentDto;
import org.ivanov.myshop.cart.dto.*;
import reactor.core.publisher.Mono;

public interface CartService {
    Mono<CartResponseDto> addToCart(CreateCartDto dto, Long accountId);

    Mono<CartResponseDto> removeFromCart(DeleteCartDto dto, Long accountId);

    Mono<ActualCartResponseDto> getActualCart(Long accountId);

    Mono<CartResponseDto> deleteProductFromCart(Long productId, Long accountId);

    Mono<ConfirmCartResponseDto> getConfirmCart(Long cartId);

    Mono<Long> confirmCart(ProcessPaymentDto dto);

    Mono<ListConfirmCartDto> getConfirmCartList(Long accountId);
}
