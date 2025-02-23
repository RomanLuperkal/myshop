package org.ivanov.myshop.product.dto;

import java.math.BigDecimal;

public record PurchasedProductDto(String productName, String image, BigDecimal price) {
}
