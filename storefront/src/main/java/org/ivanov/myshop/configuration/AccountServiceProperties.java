package org.ivanov.myshop.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "rest.account-service")
@Getter
@Setter
public class AccountServiceProperties {
    private String host;
    private Map<String, String> methods;
}
