package com.yalogs.alipay;

import com.alipay.api.internal.util.AlipaySignature;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import com.yalogs.alipay.config.AliPayConfig;
import com.yalogs.alipay.entity.OrderInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayInputStream;
import java.security.PublicKey;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AlipayApplicationTests {

    @Test
    public void contextLoads() {
    }

    @Test
    public void test() {
        OrderInfo orderInfo = new OrderInfo();
    }

    @Test
    public void testKey() throws Exception {
        String key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtuhv+BrTyZzIypWFCllbq3xIaKfkxEfuZ0yzZyG0rn1UBpAqupiYoUEHbnCpbBDzsi38haZ2AGohfyhOma8o0VVkerD+Wm3/KkRLhWhwJaD2aZ4NtYqviOnpY26gsNKQNDAMRY7A7RXLBCJcOhK3SipHF/+F8BYVOC/PUu+AAyZpdks2nYNY8RneAzavJMXExmhc2jnlD5JtQr3dAVG540upwuH6dAP5j5aCiss8ph4YPdCUlPpgr2j5pTgOdv8tBy37Lb/YG/Eimo95/nUt/21zwPizT+x9+BF9mLQVxyvqPFYsNtqiHD3wbBIhMztqqQjY1a+pQQ1guiFN2DA7eQIDAQAB";
        PublicKey publicKey = AlipaySignature.getPublicKeyFromX509("RSA", new ByteArrayInputStream(key.getBytes()));
        System.out.println(publicKey.toString());
    }
}
