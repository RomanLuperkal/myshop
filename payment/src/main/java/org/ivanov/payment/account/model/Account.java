package org.ivanov.payment.account.model;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;

import java.math.BigDecimal;

@Getter
@Setter
public class Account {
    @Id
    @Column("account_id")
    private Long accountId;
    @Column("user_ip")
    private String userIp;
    private BigDecimal balance;
    @Version
    private Long version;
}
