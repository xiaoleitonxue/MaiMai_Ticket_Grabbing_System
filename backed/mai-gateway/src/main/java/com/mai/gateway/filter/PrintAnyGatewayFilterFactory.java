package com.mai.gateway.filter;

import lombok.Data;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class PrintAnyGatewayFilterFactory extends AbstractGatewayFilterFactory<PrintAnyGatewayFilterFactory.Config> {
    @Override
    public GatewayFilter apply(Config config) {
//        return new GatewayFilter() {
//            @Override
//            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//                System.out.println("执行了 PrintAnyGatewayFilterFactory 过滤器 pre...");
//                return chain.filter(exchange);
//            }
//        };
        return new OrderedGatewayFilter(new GatewayFilter() {
            @Override
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                //接收来自配置文件中的参数
                String a = config.getA();
                System.out.println(" a = " + a);
                String b = config.getB();
                System.out.println(" b = " + b);
                String c = config.getC();
                System.out.println(" c = " + c);
                System.out.println("执行了 PrintAnyGatewayFilterFactory 的 pre ... ");
                return chain.filter(exchange);
            }
        }, 100);
    }

    @Data
    public static class Config {
        private String a;
        private String b;
        private String c;
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return List.of("a", "b", "c");
    }

    public PrintAnyGatewayFilterFactory() {
        super(Config.class);
    }
}
