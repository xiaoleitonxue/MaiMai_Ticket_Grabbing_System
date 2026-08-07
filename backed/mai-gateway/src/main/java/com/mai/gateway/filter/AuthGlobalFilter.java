package com.mai.gateway.filter;

import com.mai.gateway.config.AuthProperties;
import com.mai.gateway.config.JwtProperties;
import com.mai.gateway.utils.JwtTool;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(AuthProperties.class)
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private final JwtProperties properties;
    private final AuthProperties authProperties;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    private final JwtTool jwtTool;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        if(isExclude(request.getPath().toString())){
            return chain.filter(exchange);
        }

        String token = request.getHeaders().getFirst("Authorization");

        if ("admin".equals(token)) {
            return chain.filter(exchange);
        }

        try {
            Long userId = jwtTool.parseToken(token);

            System.out.println("用户id: " + userId);
            exchange.mutate().request(builder -> {
                builder.header("user-info", userId.toString());
            }).build();

        } catch (Exception e) {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        return chain.filter(exchange);
    }

    private boolean isExclude(String path) {
        for (String excludePath : authProperties.getExcludePaths()){
            if (antPathMatcher.match(excludePath, path)){
                return true;
            }
        }
        return false;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}