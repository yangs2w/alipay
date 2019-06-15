package com.yalogs.alipay.serviceImpl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yalogs.alipay.config.AliPayConfig;
import com.yalogs.alipay.entity.OrderInfo;
import com.yalogs.alipay.mapper.OrderInfoMapper;
import com.yalogs.alipay.service.OrderInfoService;
import com.yalogs.alipay.utils.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @ClassName OrderInfoServiceImpl
 * @Description TODO
 * @Author yangs
 * @Date 6/10/2019 6:43 PM
 * @Version 1.0
 */
@Service
@Transactional
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private AliPayConfig aliPayConfig;

    private List<String> statusList = Arrays.asList("WAIT_BUYER_PAY", "TRADE_CLOSED", "TRADE_SUCCESS", "TRADE_FINISHED");

    /**
     * 生成订单
     * @Author yangs
     * @Description //DOTO
     * @Date 6/10/2019 6:49 PM
     * @Param [subject:订单名称, body: 订单描述, money: 金额, sellerId: 商户UID]
     * @Return com.yalogs.alipay.entity.OrderInfo
     */
    @Override
    public OrderInfo generateOrder(String subject, String body, BigDecimal money, String sellerId) {
        // 生成商户订单号
        String orderId = RandomUtils.time();
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderId(orderId);
        orderInfo.setSubject(subject);
        orderInfo.setBody(body);
        orderInfo.setMoney(money);
        orderInfo.setSellerId(sellerId);
        orderInfo.setCreateDate(new Date());
        /*
         * 订单状态(与官方统一)
         * WAIT_BUYER_PAY: 交易创建，等待买家付款
         * TRADE_CLOSED: 未付款超时交易关闭，或支付完成后全额退款
         * TRADE_SUCCESS: 交易支付成功
         * TRADE_FINISHED: 交易完成,不可退款
         *
         */
        orderInfo.setStatus("WAIT_BUYER_PAY");
        orderInfoMapper.insert(orderInfo);
        return orderInfo;
    }

    /**
     * 验证订单
     * @Author yangs
     * @Description //DOTO
     * @Date 6/12/2019 12:11 AM
     * @Param [params]
     * @Return boolean
     */
    @Override
    public boolean validOrder(Map<String, String> params) throws AlipayApiException {
        // 实际验证过程中建议商户必须添加以下验证
        // 1)需要验证该通知数据中心的out_trade_no是否为商户系统中创建的订单号
        // 2)判断total_amount是否确实为点单中的实际金额
        // 3)校验通知中的seller_id(或者是seller_email)是否为out_trade_no这笔订单的对应操作方(有的时候一个商户可能有多个seller_id/seller_email)
        // 4)验证app_id是否为该商户本身

        // 1.调用SDK验证签名
        boolean signVerified = AlipaySignature.rsaCheckV1(params, aliPayConfig.getAlipayPublicKey(), aliPayConfig.getCharset(), aliPayConfig.getSignType());
        if (!signVerified)
            return false;
        // 获取订单数据
        String orderId = params.get("out_trade_no");
        OrderInfo orderInfo = orderInfoMapper.selectById(orderId);
        if (null == orderId)
            return false;
        // 判断金额是否相等
        String moneyStr = params.get("total_amount");
        BigDecimal money = new BigDecimal(moneyStr);
        if (0 != money.compareTo(orderInfo.getMoney()))
            return false;
        // 判断商户uid是否相等
        if (!params.get("seller_id").equals(orderInfo.getSellerId()))
            return false;
        // 判断app_id是否相等
        // 因为前面都是正确的，那么这里只需要返回app_id的boolean就ok
        return params.get("app_id").equals(aliPayConfig.getAppId());
    }

    /**
     * 修改订单状态
     * 订单状态(与官方一致)
     * WAIT_BUYER_PAY：交易创建，等待买家付款；
     * TRADE_CLOSED：未付款交易超时关闭，或支付完成后全额退款；
     * TRADE_SUCCESS：交易支付成功；
     * TRADE_FINISHED：交易结束，不可退款
     *
     * @Author yangs
     * @Description //DOTO 
     * @Date 6/12/2019 12:47 AM
     * @Param [orderId, trade_success, tradeNo]
     * @Return boolean
     */  
    @Override
    public boolean changeStatus(String orderId, String status, String... tradeNo) {
        // 判断参数是否合法
        OrderInfo orderInfo = orderInfoMapper.selectById(orderId);
        if (null == orderInfo)
            return false;
        if (StringUtils.isBlank(status) || !statusList.contains(status))
            return false;
        // 如果订单状态相同，不发生改变
        if (status.equals(orderInfo.getStatus()))
            return false;
        // 如果是TRADE_SUCCESS，设置订单号
        if ("TRADE_SUCCESS".equals(status) && tradeNo.length > 0)
            orderInfo.setAlipayNo(tradeNo[0]);
        orderInfo.setStatus(status);
        orderInfoMapper.updateById(orderInfo);
        return true;
    }
}






















