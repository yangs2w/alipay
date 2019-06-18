package com.yalogs.alipay.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.yalogs.alipay.config.AliPayConfig;
import com.yalogs.alipay.entity.OrderInfo;
import com.yalogs.alipay.service.OrderInfoService;
import com.yalogs.alipay.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName PayController
 * @Description 支付控制器
 *      WIKI:
 *          https://docs.open.alipay.com/270/105900/
 *          https://docs.open.alipay.com/270/105902/
 * @Author yangs
 * @Date 6/10/2019 6:24 PM
 * @Version 1.0
 */
@Controller
public class PayController {

    // 日志处理
    private Logger log = LoggerFactory.getLogger(PayController.class);

    @Autowired
    private OrderInfoService orderInfoService;
    @Autowired
    private AliPayConfig aliPayConfig;
    @Autowired
    private AlipayClient alipayClient;


    @RequestMapping(value = "/")
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView("index");
        return modelAndView;
    }

    /*/**
     * 支付宝支付
     * 支付成功后，response直接写回结果值
     * @Author yangs
     * @Description //DOTO 
     * @Date 6/10/2019 6:33 PM
     * @Param [subject: 订单名称
     *         body: 订单描述
     *         money: 金额
     *         response: HttpServletResponse]
     * @Return void
     */  
    @PostMapping(value = "/alipay/payment")
    public void payment(String subject, String body, BigDecimal money, HttpServletResponse response) {
        // 生成订单
        OrderInfo orderInfo = orderInfoService.generateOrder(subject, body, money, aliPayConfig.getSellerId());
        // 开始向支付宝发起收钱请求
        // 1)设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        // 页面跳转同步通知页面路径
        alipayRequest.setReturnUrl(aliPayConfig.getReturnUrl());
        // 服务器异步通知页面路径
        alipayRequest.setNotifyUrl(aliPayConfig.getNotifyUrl());
        // 2)SDK已经封装公共参数，这里传入业务参数即可
        Map<String, String> map = new HashMap<>(16);
        map.put("out_trade_no", orderInfo.getOrderId());
        map.put("total_amount", money.toString());
        map.put("subject", subject);
        map.put("body", body);
        map.put("product_code", "FAST_INSTANT_TRADE_PAY");
        alipayRequest.setBizContent(JsonUtils.objectToJson(map));
        // 设置response的格式
        response.setContentType("text/html;charset=utf-8");
        try {
            // 3)生成支付表单
            AlipayTradePagePayResponse alipayResponse = alipayClient.pageExecute(alipayRequest);
            if (alipayResponse.isSuccess()) {
                String result = alipayResponse.getBody();
                response.getWriter().write(result);
            }else {
                log.error("【支付表单生成】失败，错误信息:{}", alipayResponse.getSubMsg());
                response.getWriter().write("error");
            }
        } catch (Exception e) {
            log.error("【支付表单生成】异常，异常信息:{}", e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     *
     * 该方法是在支付宝支付完成后自动跳转，只会进行一次
     * 支付宝同步通知页面，获取支付成功后get过来的反馈信息
     * 该方法执行完毕后跳转到页面即可
     *  1）该方法不是支付宝主动去调用商户页面，而是支付宝的程序利用页面自动跳转的函数，使用户当前的页面自动跳转
     *  2）返回的url只有一分钟的有效时间，超过一分钟则会失效，验证失效
     *  3）可在本机而不是只能在服务器上调试
     * @Author yangs
     * @Description //DOTO
     * @Date 6/11/2019 11:56 PM
     * @Param [request, response]
     * @Return void
     */
    @GetMapping(value = "/alipay/return")
    public void alipayReturn(HttpServletRequest request, HttpServletResponse response) {
        // 获取参数
        Map<String, String> params = getPayParams(request);
        // 验证订单
        try {
            boolean flag = orderInfoService.validOrder(params);
            response.setContentType("text/html;charset=utf-8");
            // 验证通过
            if (flag) {
                // 修改订单状态
                String orderId = params.get("out_trade_no");
                // 获取支付宝订单号
                String tradeNo = params.get("trade_no");
                // 更新状态
                orderInfoService.changeStatus(orderId, "TRADE_SUCCESS", tradeNo);
                response.getWriter().write("<!DOCTYPE html>\n" +
                        "<html lang=\"en\">\n" +
                        "<head>\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <title>支付成功</title>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "\n" +
                        "<div class=\"container\">\n" +
                        "    <div class=\"row\">\n" +
                        "        <p>订单号："+orderId+"</p>\n" +
                        "        <p>支付宝交易号："+tradeNo+"</p>\n" +
                        "        <a href=\"/\">返回首页</a>\n" +
                        "    </div>\n" +
                        "</div>\n" +
                        "\n" +
                        "</body>\n" +
                        "</html>");
            }else {
                log.error("【支付宝同步订单方法】验证失败");
                response.getWriter().write("支付验证失败");
            }
        } catch (Exception e) {
            log.error("【支付宝同步方法】异常，异常信息:{}", e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     *
     * 服务器异步通知，获取支付宝POST过来反馈信息
     * 该方法无返回值，静默处理
     * 订单的状态以该方法为主，其他的状态修改为辅
     * 1）程序执行完必须打印出"success"
     * 如果商户反馈给支付宝的字符不是success字符，会被支付宝服务器不断重发通知，知道超过24小时22分钟
     * 2）程序之执行完后，该页面不能执行页面跳转
     * 如果页面跳转后，支付宝会接收不到success字符，会被支付宝服务器误判为是服务器发生异常而重发处理结果
     * 3）cookie,session等在此页面会失效，所以无法获取到
     * 4）该方式通知与运行必须在服务器上，即互联网上能访问
     *
     * @Author yangs
     * @Description //DOTO
     * @Date 6/16/2019 8:00 PM
     * @Param [request, response]
     * @Return void
     */
    @RequestMapping(value = "/alipay/notify", method = RequestMethod.POST)
    public void alipayNotify(HttpServletRequest request, HttpServletResponse response) {
        /*
         * 默认只有TRADE_SUCCESS会触发通知，如果需要开通其他通知，请联系客服申请
         * 触发条件名        触发条件描述      触发条件默认值
         * TRADE_FINISHED   交易完成        false(不发送通知)
         * TRADE_SUCCESS    支付成功        true(触发通知)
         * WAIT_BUYER_PAY   交易创建        false(不发送通知)
         * TRADE_CLOSED     交易关闭        false(不发送通知)
         * 来源：https://docs.open.alipay.com/270/105902/#s2
         *
         */
        // 获取参数
        Map<String, String> params = getPayParams(request);
        try {
            // 验证订单
            boolean flag = orderInfoService.validOrder(params);
            if (flag) {
                // 商户订单号
                String orderId = params.get("out_trade_no");
                // 支付宝交易号
                String tradeNo = params.get("trade_no");
                // 交易状态
                String tradeStatus = params.get("trade_status");
                switch (tradeStatus) {
                    case "WAIT_BUYER_PAY":
                        orderInfoService.changeStatus(orderId, tradeStatus);
                        break;
                    case "TRADE_CLOSED":
                        orderInfoService.changeStatus(orderId, tradeStatus);
                        break;
                    case "TRADE_FINISHED":
                        orderInfoService.changeStatus(orderId, tradeStatus);
                        break;
                    case "TRADE_SUCCESS":
                        orderInfoService.changeStatus(orderId, tradeStatus, tradeNo);
                        break;
                        default:break;
                }
                response.getWriter().write("success");
            }else {
                log.error("【支付宝异步方法】验证失败，错误信息:{}", AlipaySignature.getSignCheckContentV1(params));
                response.getWriter().write("fail");
            }
        } catch (Exception e) {
            log.error("【支付宝异步支付】异常，异常信息:{}", e.getMessage());
            e.printStackTrace();
        }
    }



    /**
     * 获取支付参数
     * @Author yangs
     * @Description //DOTO 
     * @Date 6/12/2019 12:03 AM
     * @Param [request]
     * @Return java.util.Map<java.lang.String,java.lang.String>
     */
    private Map<String,String> getPayParams(HttpServletRequest request) {
        Map<String,String> params = new HashMap<>(16);
        Map<String,String[]> requestParams = request.getParameterMap();

        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
//            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }
        return params;
    }
}

























