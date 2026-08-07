package com.mai.pay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mai.pay.domain.dto.PayApplyDTO;
import com.mai.pay.domain.dto.PayOrderFormDTO;
import com.mai.pay.domain.po.PayOrder;

/**
 * <p>
 * 支付订单服务接口，提供支付单申请、余额支付等核心业务操作
 * </p>
 */
public interface IPayOrderService extends IService<PayOrder> {

    /**
     * <p>
     * 申请支付单，包含幂等性校验，同一业务订单号不会重复创建
     * </p>
     *
     * @param applyDTO 支付申请信息，包含业务订单号和支付渠道
     * @return 支付单ID
     */
    String applyPayOrder(PayApplyDTO applyDTO);

    /**
     * <p>
     * 尝试使用用户余额支付，包含余额扣减、支付单状态更新及订单状态通知
     * </p>
     *
     * @param payOrderFormDTO 支付表单数据，包含支付单ID和支付密码
     * @throws BizIllegalException 当支付单状态异常或余额不足时抛出
     */
    void tryPayOrderByBalance(PayOrderFormDTO payOrderFormDTO);
}