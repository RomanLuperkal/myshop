package org.ivanov.myshop.account.mapper;

import org.ivanov.myshop.account.dto.CreateAccountDto;
import org.ivanov.myshop.account.model.Account;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    Account mapToAccount(CreateAccountDto createAccountDto);
}
