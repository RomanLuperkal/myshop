package org.ivanov.myshop.account.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAccountDto {
    private String username;
    private String password;
}
