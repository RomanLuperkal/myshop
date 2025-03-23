package org.ivanov.myshop.cart_item.dto;

import java.math.BigDecimal;

public record CartItemResponseDto(Long productId, String productName, Integer count, BigDecimal price) {
}
