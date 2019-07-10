package com.yalogs.alipay.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;


/**
 * @Autor yangs
 * @Date 2019/10/6 15:26
 * @Description Alipay的一些配置项，加载配置
 */
@Component
@PropertySource("classpath:alipayconfig.properties")
@Getter
public class AliPayConfig {

    @Value("${alipay.app_id}")
    private String appId;

    @Value("${alipay.merchant_private_key}")
    private String merchantPrivateKey;

    @Value("${alipay.alipay_public_key}")
    private String alipayPublicKey;

    @Value("${alipay.notify_url}")
    private String notifyUrl;

    @Value("${alipay.return_url}")
    private String returnUrl;

    @Value("${alipay.sign_type}")
    private String signType;

    @Value("${alipay.charset}")
    private String charset;

    @Value("${alipay.gateway_url}")
    private String gatewayUrl;

    @Value("${alipay.uid}")
    private String sellerId;


    @Bean
    AlipayClient alipayClient() {
        return new DefaultAlipayClient(gatewayUrl, appId, merchantPrivateKey, "json", charset, alipayPublicKey, signType);
    }
}
