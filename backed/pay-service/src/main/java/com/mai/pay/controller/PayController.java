package com.mai.pay.controller;

import com.mai.common.exception.BizIllegalException;
import com.mai.pay.domain.dto.PayApplyDTO;
import com.mai.pay.domain.dto.PayOrderFormDTO;
import com.mai.pay.enums.PayType;
import com.mai.pay.domain.po.PayOrder;
import java.util.Map;
import com.mai.pay.service.IPayOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 支付控制器，负责处理支付单生成、余额支付及支付单查询等HTTP请求
 * </p>
 */
@Api(tags = "支付相关接口")
@RestController
@RequestMapping("pay-orders")
@RequiredArgsConstructor
public class PayController {

    private final IPayOrderService payOrderService;

    /**
     * <p>
     * 生成支付单，目前仅支持余额支付方式
     * </p>
     *
     * @param applyDTO 支付申请信息，包含业务订单号和支付渠道
     * @return 包含支付单ID的Map
     * @throws BizIllegalException 当支付方式不支持时抛出
     */
    @ApiOperation("生成支付单")
    @PostMapping
    public Map<String, String> applyPayOrder(@RequestBody PayApplyDTO applyDTO){
        if(!PayType.BALANCE.equalsValue(applyDTO.getPayType())){
            // 目前只支持余额支付
            throw new BizIllegalException("抱歉，目前只支持余额支付");
        }
        return Map.of("id", payOrderService.applyPayOrder(applyDTO));
    }

    /**
     * <p>
     * 尝试使用用户余额支付指定支付单，包含余额扣减和状态更新
     * </p>
     *
     * @param id 支付单ID
     * @param payOrderFormDTO 支付表单数据，包含支付密码
     */
    @ApiOperation("尝试基于用户余额支付")
    @ApiImplicitParam(value = "支付单id", name = "id")
    @PostMapping("{id}")
    public void tryPayOrderByBalance(@PathVariable("id") Long id, @RequestBody PayOrderFormDTO payOrderFormDTO){
        payOrderFormDTO.setId(id);
        payOrderService.tryPayOrderByBalance(payOrderFormDTO);
    }

    /**
     * <p>
     * 根据业务订单号查询对应的支付单信息
     * </p>
     *
     * @param bizOrderNo 业务订单号
     * @return 支付单实体
     */
    @ApiOperation("根据业务订单号查询支付单")
    @ApiImplicitParam(value = "业务订单号", name = "bizOrderNo")
    @GetMapping("biz/{bizOrderNo}")
    public PayOrder queryPayOrderByBizOrderNo(@PathVariable("bizOrderNo") Long bizOrderNo){
        return payOrderService.lambdaQuery()
                .eq(PayOrder::getBizOrderNo, bizOrderNo)
                .one();
    }
}