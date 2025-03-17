package org.ivanov.myshop.cart.service;

import org.ivanov.myshop.cart.dto.*;
import reactor.core.publisher.Mono;

public interface CartService {
    Mono<CartResponseDto> addToCart(CreateCartDto dto, String userIp);

    Mono<CartResponseDto> removeFromCart(DeleteCartDto dto, String userIp);

    Mono<ActualCartResponseDto> getActualCart(String userIp);

    Mono<Void> deleteProductFromCart(Long productId, String userIp);

    //ConfirmCartResponseDto getConfirmCart(Long cartId);

    //Long confirmCart(String userIp);

    //ListConfirmCartDto getConfirmCartList(String userIp);
}
