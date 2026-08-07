package com.mai.search.listener;

import com.mai.common.constants.MqConstants;
import com.mai.search.service.ISearchService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ItemStatusListener {

    @Autowired
    private ISearchService searchService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "search.item.up.queue", durable = "true"),
            exchange = @Exchange(value = MqConstants.ITEM_EXCHANGE_NAME, type = ExchangeTypes.TOPIC),
            key = MqConstants.ITEM_UP_KEY
    ))
    public void listenItemUpMsg(Long itemId){
        searchService.saveItemById(itemId);

    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "search.item.down.queue", durable = "true"),
            exchange = @Exchange(value = MqConstants.ITEM_EXCHANGE_NAME, type = ExchangeTypes.TOPIC),
            key = MqConstants.ITEM_DOWN_KEY
    ))
    public void listenItemDownMsg(Long itemId){
        searchService.deleteItemById(itemId);

    }
}
