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

@Api(tags = "支付相关接口")
@RestController
@RequestMapping("pay-orders")
@RequiredArgsConstructor
public class PayController {

    private final IPayOrderService payOrderService;

    @ApiOperation("生成支付单")
    @PostMapping
    public Map<String, String> applyPayOrder(@RequestBody PayApplyDTO applyDTO){
        if(!PayType.BALANCE.equalsValue(applyDTO.getPayType())){
            // 目前只支持余额支付
            throw new BizIllegalException("抱歉，目前只支持余额支付");
        }
        return Map.of("id", payOrderService.applyPayOrder(applyDTO));
    }

    @ApiOperation("尝试基于用户余额支付")
    @ApiImplicitParam(value = "支付单id", name = "id")
    @PostMapping("{id}")
    public void tryPayOrderByBalance(@PathVariable("id") Long id, @RequestBody PayOrderFormDTO payOrderFormDTO){
        payOrderFormDTO.setId(id);
        payOrderService.tryPayOrderByBalance(payOrderFormDTO);
    }

    @ApiOperation("根据业务订单号查询支付单")
    @ApiImplicitParam(value = "业务订单号", name = "bizOrderNo")
    @GetMapping("biz/{bizOrderNo}")
    public PayOrder queryPayOrderByBizOrderNo(@PathVariable("bizOrderNo") Long bizOrderNo){
        return payOrderService.lambdaQuery()
                .eq(PayOrder::getBizOrderNo, bizOrderNo)
                .one();
    }
}