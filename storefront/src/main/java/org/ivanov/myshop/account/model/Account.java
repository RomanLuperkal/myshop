package org.ivanov.myshop.account.model;


import lombok.Getter;
import lombok.Setter;
import org.ivanov.myshop.account.enums.Role;
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
    private String username;
    private String password;
    private BigDecimal balance;
    private Role role = Role.ROLE_USER;
    @Version
    private Long version;
}
