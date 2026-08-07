package com.mai.cart.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "mai.cart")
@Data
public class CartProperties {
    private Integer maxAmount;

}
