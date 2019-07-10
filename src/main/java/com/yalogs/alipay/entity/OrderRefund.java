package com.yalogs.alipay.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @ClassName OrderRefund
 * @Description 订单退款
 * @Author yangs
 * @Date 6/10/2019 3:41 PM
 * @Version 1.0
 */
@Data
public class OrderRefund implements Serializable {

    // 退款号
    @TableId(type = IdType.INPUT)
    private String refundId;

    // 订单号
    private String orderId;

    // 退款金额
    private BigDecimal money;

    // 退款账户
    private String account;

    // 退款原因
    private String reason;

    // 退款时间
    private Date refundDate;

    public OrderRefund() {
    }

    public OrderRefund(String refundId, String orderId, BigDecimal money, String account, String reason, Date refundDate) {
        this.refundId = refundId;
        this.orderId = orderId;
        this.money = money;
        this.account = account;
        this.reason = reason;
        this.refundDate = refundDate;
    }
}
