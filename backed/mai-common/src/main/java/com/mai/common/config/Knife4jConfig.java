package com.mai.common.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

@Configuration
@EnableSwagger2WebMvc
@ConditionalOnProperty(prefix = "knife4j", name = "enable", havingValue = "true")
public class Knife4jConfig {
}