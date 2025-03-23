package org.ivanov.myshop.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "admin")
@Getter
@Setter
public class AdministratorConfig {
    private String login;
    private String password;
    private String role;
}
