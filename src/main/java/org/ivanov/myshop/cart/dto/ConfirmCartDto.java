package org.ivanov.myshop.cart.dto;

import org.ivanov.myshop.cart.proection.ConfirmCart;

import java.math.BigDecimal;
import java.util.List;

public record ConfirmCartDto(List<ConfirmCart> confirmCarts, BigDecimal totalPrice) {
}
