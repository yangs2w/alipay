package com.yalogs.alipay.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @ClassName OrderInfo
 * @Description 订单信息表
 * @Author yangs
 * @Date 6/10/2019 3:30 PM
 * @Version 1.0
 */
@Getter
@Setter
public class OrderInfo implements Serializable {

    // 订单号
    @TableId(type = IdType.INPUT)
    private String orderId;

    // 订单名称
    private String subject;

    // 订单描述
    private String body;

    // 付款金额
    private BigDecimal money;

    // 商户UID
    private String sellerId;

    // 支付宝订单号
    private String alipayNo;

    /**
     * 订单状态(与官方统一)
     * WAIT_BUYER_PAY: 交易创建，等待买家付款
     * TRADE_CLOSED: 未付款超时关闭，或支付完成后退款成功
     * TRADE_SUCCESS: 交易支付成功
     * TRADE_FINISHED: 交易结束，不可退款
     */
    private String status;

    // 总计退款金额
    private BigDecimal refundMoney;

    // 订单创建时间
    private Date createDate;

    // 订单修改时间
    @TableField(update = "now()")
    private Date updateDate;
}
