package org.ivanov.myshop.cart.dto;

import org.ivanov.myshop.cart_item.dto.CartItemResponseDto;

import java.math.BigDecimal;
import java.util.List;

public record ActualCartResponseDto(List<CartItemResponseDto> cartItems, BigDecimal totalPrice) {
}
