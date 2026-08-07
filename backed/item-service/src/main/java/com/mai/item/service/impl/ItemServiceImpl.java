package com.mai.item.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mai.common.constants.MqConstants;
import com.mai.common.exception.BizIllegalException;
import com.mai.common.utils.BeanUtils;
import com.mai.item.domain.dto.ItemDTO;
import com.mai.item.domain.dto.OrderDetailDTO;
import com.mai.item.domain.po.Item;
import com.mai.item.mapper.ItemMapper;
import com.mai.item.service.IItemService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 商品服务实现类，负责商品库存扣减、批量查询及状态更新等核心业务逻辑
 * </p>
 */
@Service
public class ItemServiceImpl extends ServiceImpl<ItemMapper, Item> implements IItemService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * <p>
     * 批量扣减商品库存，通过MyBatis批量执行SQL实现，扣减失败则抛出异常
     * </p>
     *
     * @param items 订单明细列表，包含商品ID和扣减数量
     * @throws BizIllegalException 当库存不足时抛出
     */
    @Override
    @Transactional
    public void deductStock(List<OrderDetailDTO> items) {
        String sqlStatement = "com.mai.item.mapper.ItemMapper.updateStock";
        boolean r = false;
        try {
            r = executeBatch(items, (sqlSession, entity) -> sqlSession.update(sqlStatement, entity));
        } catch (Exception e) {
            log.error("更新库存异常", e);
            return;
        }
        if (!r) {
            throw new BizIllegalException("库存不足！");
        }
    }

    /**
     * <p>
     * 根据商品ID集合批量查询商品信息
     * </p>
     *
     * @param ids 商品ID集合
     * @return 商品信息DTO列表
     */
    @Override
    public List<ItemDTO> queryItemByIds(Collection<Long> ids) {
        return BeanUtils.copyList(listByIds(ids), ItemDTO.class);
    }

    /**
     * <p>
     * 更新商品上下架状态，并根据状态发送上架或下架消息到RabbitMQ通知搜索服务
     * </p>
     *
     * @param id 商品ID
     * @param status 商品状态，1表示上架，其他表示下架
     */
    @Override
    public void updateItemStatus(Long id, Integer status) {
        Item item = new Item();
        item.setId(id);
        item.setStatus(status);
        updateById(item);

        String routingKey = status == 1 ? MqConstants.ITEM_UP_KEY : MqConstants.ITEM_DOWN_KEY;

        rabbitTemplate.convertAndSend(MqConstants.ITEM_EXCHANGE_NAME, routingKey, id);
    }
}