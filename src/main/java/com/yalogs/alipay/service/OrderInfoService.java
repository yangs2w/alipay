package com.yalogs.alipay.service;

import com.alipay.api.AlipayApiException;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yalogs.alipay.entity.OrderInfo;

import java.math.BigDecimal;
import java.util.Map;

/**
 * <p>
 *   订单服务类
 *  </p>
 * @Author yangs
 * @Description //DOTO
 * @Date 6/10/2019 6:40 PM
 */
public interface OrderInfoService extends IService<OrderInfo> {

    /**
     *  生成订单
     * @Author yangs
     * @Description //DOTO
     * @Date 6/10/2019 6:48 PM
     * @Param [subject: 订单名称
     *      , body: 订单描述
     *      , money: 定额
     *      , sellerId: 商户uid]
     * @Return com.yalogs.alipay.entity.OrderInfo
     */
    OrderInfo generateOrder(String subject, String body, BigDecimal money, String sellerId);

    boolean validOrder(Map<String, String> params) throws AlipayApiException;

    boolean changeStatus(String orderId, String status, String... tradeNo);
}
