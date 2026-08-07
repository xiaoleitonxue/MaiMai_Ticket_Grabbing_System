package com.mai.cart.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mai.api.dto.ItemDTO;
import com.mai.api.client.ItemClient;
import com.mai.cart.config.CartProperties;
import com.mai.common.exception.BizIllegalException;
import com.mai.common.utils.BeanUtils;
import com.mai.common.utils.CollUtils;
import com.mai.common.utils.UserContext;
import com.mai.cart.domain.dto.CartFormDTO;
import com.mai.cart.domain.po.Cart;
import com.mai.cart.domain.vo.CartVO;
import com.mai.cart.mapper.CartMapper;
import com.mai.cart.service.ICartService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 订单详情表 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2023-05-05
 */
@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(CartProperties.class)
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements ICartService {

    //private final IItemService itemService;
    private final RestTemplate restTemplate;
    private final DiscoveryClient discoveryClient;
    private final ServiceInstance serviceInstance;
    private final ItemClient itemClient;
    private final CartProperties cartProperties;

    @Override
    public void addItem2Cart(CartFormDTO cartFormDTO) {
        // 1.获取登录用户
        Long userId = UserContext.getUser();

        // 2.判断是否已经存在
        if(checkItemExists(cartFormDTO.getItemId(), userId)){
            // 2.1.存在，则更新数量
            baseMapper.updateNum(cartFormDTO.getItemId(), userId);
            return;
        }
        // 2.2.不存在，判断是否超过购物车数量
        checkCartsFull(userId);

        // 3.新增购物车条目
        // 3.1.转换PO
        Cart cart = BeanUtils.copyBean(cartFormDTO, Cart.class);
        // 3.2.保存当前用户
        cart.setUserId(userId);
        // 3.3.保存到数据库
        save(cart);
    }

    @Override
    public List<CartVO> queryMyCarts() {
        // 1.查询我的购物车列表
        List<Cart> carts = lambdaQuery().eq(Cart::getUserId, UserContext.getUser()).list();
//        List<Cart> carts = lambdaQuery().eq(Cart::getUserId, 1L).list();

        if (CollUtils.isEmpty(carts)) {
            return CollUtils.emptyList();
        }

        // 2.转换VO
        List<CartVO> vos = BeanUtils.copyList(carts, CartVO.class);

        // 3.处理VO中的商品信息
        handleCartItems(vos);

        // 4.返回
        return vos;
    }

    private void handleCartItems(List<CartVO> vos) {
        Set<Long> itemIds = vos.stream().map(CartVO::getItemId).collect(Collectors.toSet());

//        List<ItemDTO> items = null;
//
//        List<ServiceInstance> instanceList = discoveryClient.getInstances("item-service");
//
//        ServiceInstance instance = instanceList.get(RandomUtil.randomInt(instanceList.size()));
//
//        String url = serviceInstance.getUri() + "/items?ids={ids}";
//        ResponseEntity<List<ItemDTO>> response = restTemplate.exchange(
//                url,
//                HttpMethod.GET,
//                null,
//                new ParameterizedTypeReference<List<ItemDTO>>() {},
//                Map.of("ids", CollUtils.join(itemIds, ","))
//        );
//
//        if (response.getStatusCode().is2xxSuccessful()) {
//            items = response.getBody();
//        }

        List<ItemDTO> items = itemClient.queryItemsByIds(itemIds);

        if (CollUtils.isEmpty(items)) {
            return;
        }
        Map<Long, ItemDTO> itemMap = items.stream().collect(Collectors.toMap(ItemDTO::getId, Function.identity()));
        for (CartVO v : vos) {
            ItemDTO item = itemMap.get(v.getItemId());
            if (item == null) {
                continue;
            }
            v.setNewPrice(item.getPrice());
            v.setStatus(item.getStatus());
            v.setStock(item.getStock());
        }
    }

    @Override
    public void removeByItemIds(Collection<Long> itemIds) {
        // 1.构建删除条件，userId和itemId
        QueryWrapper<Cart> queryWrapper = new QueryWrapper<Cart>();
        queryWrapper.lambda()
                .eq(Cart::getUserId, UserContext.getUser())
                .in(Cart::getItemId, itemIds);
        // 2.删除
        remove(queryWrapper);
    }

    private void checkCartsFull(Long userId) {
        long count = lambdaQuery().eq(Cart::getUserId, userId).count();
        if (count >= cartProperties.getMaxAmount()) {
            throw new BizIllegalException(StrUtil.format("用户购物车课程不能超过{}", cartProperties.getMaxAmount()));
        }
    }

    private boolean checkItemExists(Long itemId, Long userId) {
        long count = lambdaQuery()
                .eq(Cart::getUserId, userId)
                .eq(Cart::getItemId, itemId)
                .count();
        return count > 0;
    }
}