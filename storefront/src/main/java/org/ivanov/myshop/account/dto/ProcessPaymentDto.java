package org.ivanov.myshop.account.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProcessPaymentDto {
    private BigDecimal orderSum;

    private String userIp;
}
