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

/**
 * <p>
 * 商品状态消息监听器，根据商品上下架消息同步更新Elasticsearch索引
 * </p>
 */
@Component
public class ItemStatusListener {

    @Autowired
    private ISearchService searchService;

    /**
     * <p>
     * 监听商品上架消息，将商品信息同步到Elasticsearch索引
     * </p>
     *
     * @param itemId 上架的商品ID
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "search.item.up.queue", durable = "true"),
            exchange = @Exchange(value = MqConstants.ITEM_EXCHANGE_NAME, type = ExchangeTypes.TOPIC),
            key = MqConstants.ITEM_UP_KEY
    ))
    public void listenItemUpMsg(Long itemId){
        searchService.saveItemById(itemId);

    }

    /**
     * <p>
     * 监听商品下架消息，从Elasticsearch索引中删除商品
     * </p>
     *
     * @param itemId 下架的商品ID
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "search.item.down.queue", durable = "true"),
            exchange = @Exchange(value = MqConstants.ITEM_EXCHANGE_NAME, type = ExchangeTypes.TOPIC),
            key = MqConstants.ITEM_DOWN_KEY
    ))
    public void listenItemDownMsg(Long itemId){
        searchService.deleteItemById(itemId);

    }
}