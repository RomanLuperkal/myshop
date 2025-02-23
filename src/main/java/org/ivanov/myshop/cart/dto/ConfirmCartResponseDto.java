package org.ivanov.myshop.cart.dto;

import org.ivanov.myshop.product.dto.PurchasedProductDto;

import java.util.List;

public record ConfirmCartResponseDto(List<PurchasedProductDto> purchasedProducts) {
}
