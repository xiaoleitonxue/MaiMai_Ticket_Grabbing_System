package com.mai.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class PayOrderDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * 业务订单号
     */
    private Long bizOrderNo;

    /**
     * 支付单号
     */
    private Long payOrderNo;

    /**
     * 支付用户id
     */
    private Long bizUserId;

    /**
     * 支付渠道编码
     */
    private String payChannelCode;

    /**
     * 支付金额，单位分
     */
    private Integer amount;

    /**
     * 支付类型，1：h5,2:小程序，3：公众号，4：扫码，5：余额支付
     */
    private Integer payType;

    /**
     * 支付状态，0：待提交，1:待支付，2：支付超时或取消，3：支付成功
     */
    private Integer status;

    /**
     * 支付成功时间
     */
    private LocalDateTime paySuccessTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
