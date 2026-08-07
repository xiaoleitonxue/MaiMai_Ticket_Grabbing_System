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
 * 商品表 服务实现类
 * </p>
 *
 * @author 虎哥
 */
@Service
public class ItemServiceImpl extends ServiceImpl<ItemMapper, Item> implements IItemService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

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

    @Override
    public List<ItemDTO> queryItemByIds(Collection<Long> ids) {
        return BeanUtils.copyList(listByIds(ids), ItemDTO.class);
    }

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