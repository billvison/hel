package com.xingkaichun.helloworldblockchain.crypto;

import java.util.Base64;

/**
 * Base64工具类
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class Base64Util {

    /**
     * Base64编码
     */
    public static String encode(byte[] input) {
        return Base64.getEncoder().encodeToString(input);
    }

    /**
     * Base64解码
     */
    public static byte[] decode(String input) {
        return Base64.getDecoder().decode(input);
    }
}