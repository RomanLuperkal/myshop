package org.ivanov.myshop.product.dto;

import java.math.BigDecimal;

public record ProductShortResponseDto(
        Long productId,
        String productName,
        Long count,
        BigDecimal price
) {}
