package com.xingkaichun.helloworldblockchain.crypto;

import org.bitcoinj.core.Base58;

/**
 * Base58工具类
 *
 * @author 邢开春 409060350@qq.com
 */
public class Base58Util {

    /**
     * Base58编码
     */
    public static String encode(byte[] input) {
        return Base58.encode(input);
    }

    /**
     * Base58解码
     */
    public static byte[] decode(String input) {
        return Base58.decode(input);
    }
}