package org.ivanov.myshop.product.dto;

import java.math.BigDecimal;

public record ProductResponseDto(
        Long productId,
        String productName,
        String image,
        String description,
        Long count,
        BigDecimal price
) {}
