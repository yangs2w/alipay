package com.yalogs.alipay.utils;

import java.util.Random;

/**
 * @ClassName RandomUtils
 * @Description TODO
 * @Author yangs
 * @Date 6/10/2019 6:52 PM
 * @Version 1.0
 */
public class RandomUtils {

    private static final String ALLCHAR
            = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LETTERCHAR
            = "abcdefghijkllmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMBERCHAR = "0123456789";

    /**
     *  根据时间生成随机15位数
     * @Author yangs
     * @Description //DOTO
     * @Date 6/10/2019 6:53 PM
     * @Param []
     * @Return java.lang.String
     */
    public static String time() {
        long millis = System.currentTimeMillis();
        Random random = new Random();
        int end = random.nextInt(99);
        return millis + String.format("%02d", end);
    }

}
