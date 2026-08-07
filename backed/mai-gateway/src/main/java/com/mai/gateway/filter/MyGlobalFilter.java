package com.mai.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class MyGlobalFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        System.out.println("执行了 MyGlobalFilter 过滤器 pre... ");
        return chain.filter(exchange).doFinally(signalType ->{
            System.out.println("执行了 MyGlobalFilter 过滤器 post... signalType: " + signalType);
        });
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
