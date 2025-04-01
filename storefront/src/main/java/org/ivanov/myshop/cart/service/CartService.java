package org.ivanov.myshop.cart.service;

import org.ivanov.myshop.cart.dto.*;
import reactor.core.publisher.Mono;

public interface CartService {
    Mono<CartResponseDto> addToCart(CreateCartDto dto, String userIp);

    Mono<CartResponseDto> removeFromCart(DeleteCartDto dto, String userIp);

    Mono<ActualCartResponseDto> getActualCart(String userIp);

    Mono<CartResponseDto> deleteProductFromCart(Long productId, String userIp);

    Mono<ConfirmCartResponseDto> getConfirmCart(Long cartId);

    Mono<Long> confirmCart(String userIp);

    Mono<ListConfirmCartDto> getConfirmCartList(String userIp);
}
