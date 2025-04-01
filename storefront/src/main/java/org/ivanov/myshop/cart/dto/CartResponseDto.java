package org.ivanov.myshop.cart.dto;

public record CartResponseDto(String message, Boolean isBalancePositive, Boolean isPaymentServiceAvailable) {
}
