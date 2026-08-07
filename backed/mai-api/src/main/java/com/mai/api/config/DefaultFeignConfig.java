package com.mai.api.config;

import com.mai.api.fallback.CartClientFallback;
import com.mai.api.fallback.ItemClientFallback;
import com.mai.api.fallback.PayClientFallback;
import com.mai.api.fallback.TradeClientFallback;
import com.mai.api.fallback.UserClientFallback;
import com.mai.common.utils.UserContext;
import feign.Logger;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;

public class DefaultFeignConfig {

    @Bean
    public ItemClientFallback itemClientFallback(){
        return new ItemClientFallback();
    }

    @Bean
    public CartClientFallback cartClientFallback(){
        return new CartClientFallback();
    }

    @Bean
    public UserClientFallback userClientFallback(){
        return new UserClientFallback();
    }

    @Bean
    public TradeClientFallback tradeClientFallback(){
        return new TradeClientFallback();
    }

    @Bean
    public PayClientFallback payClientFallback(){
        return new PayClientFallback();
    }



    @Bean
    public Logger.Level feignLoggerLevel(){
        return Logger.Level.FULL;
    }

    @Bean
    public RequestInterceptor feginRequestInterceptor(){
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                Long userId = UserContext.getUser();
                if(userId != null){
                    template.header("user-info", String.valueOf(userId));
                }
            }
        };
    }
}