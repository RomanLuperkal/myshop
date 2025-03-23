package org.ivanov.myshop.cart.proection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ConfirmCart(Long id, LocalDateTime confirmed_date, BigDecimal cart_price) {
}
