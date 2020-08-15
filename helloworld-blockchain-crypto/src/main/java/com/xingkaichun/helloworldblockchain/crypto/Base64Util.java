package com.xingkaichun.helloworldblockchain.crypto;

import org.bouncycastle.util.encoders.Base64;

/**
 * Base64工具类
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class Base64Util {

    /**
     * Base64编码
     */
    public static String encode(byte[] input) {
        return Base64.toBase64String(input);
    }

    /**
     * Base64解码
     */
    public static byte[] decode(String input) {
        return Base64.decode(input);
    }
}