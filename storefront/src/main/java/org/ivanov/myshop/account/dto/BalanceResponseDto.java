package org.ivanov.myshop.account.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class BalanceResponseDto {
    private BigDecimal balance;
}
