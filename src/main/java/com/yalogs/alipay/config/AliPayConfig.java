package com.yalogs.alipay.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties(prefix = "alipay")
@PropertySource("classpath:alipayconfig.properties")
@Getter
public class AliPayConfig {

    @Value("${app_id}")
    private String appId;

    @Value("${merchant_private_key}")
    private String merchantPrivateKey;

    @Value("${alipay_public_key}")
    private String alipayPublicKey;

    @Value("${notify_url}")
    private String notifyUrl;

    @Value("${reurn_url}")
    private String returnUrl;

    @Value("${sign_type}")
    private String signType;

    @Value("${charset}")
    private String charset;

    @Value("${gateway_url}")
    private String gatewayUrl;

    @Value("${seller_id}")
    private String sellerId;


    @Bean
    AlipayClient alipayClient() {
        return new DefaultAlipayClient(gatewayUrl, appId, merchantPrivateKey, "json", charset, alipayPublicKey, signType);
    }

}
