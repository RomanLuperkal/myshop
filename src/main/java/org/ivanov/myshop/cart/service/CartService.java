package org.ivanov.myshop.cart.service;

import org.ivanov.myshop.cart.dto.CartResponseDto;
import org.ivanov.myshop.cart.dto.CreateCartDto;
import org.ivanov.myshop.cart.dto.DeleteCartDto;

public interface CartService {
    CartResponseDto addToCurt(CreateCartDto dto, String userIp);
    CartResponseDto removeFromCart(DeleteCartDto dto, String userIp);
}
