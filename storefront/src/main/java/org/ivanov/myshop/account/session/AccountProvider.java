package org.ivanov.myshop.account.session;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.math.BigDecimal;

@SessionScope
@Component
@Getter
@Setter
public class AccountProvider {
    private BigDecimal balance = BigDecimal.ZERO;
    private String xVer = null;
}
