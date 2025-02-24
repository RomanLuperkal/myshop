package org.ivanov.myshop.cart.proection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface ConfirmCart {
    Long getId();
    LocalDateTime getConfirmedDate();
    BigDecimal getCartPrice();
}
