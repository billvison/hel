package com.xingkaichun.helloworldblockchain.crypto;

import com.google.common.primitives.Longs;

/**
 * Number工具类
 *
 * @author 邢开春 409060350@qq.com
 */
public class NumberUtil {

    /**
     * (8个的字节)long转换为(大端模式)8个字节的字节数组。
     */
    public static byte[] long8ToByte8(long number) {
        return Longs.toByteArray(number);
    }

    /**
     * (大端模式)8个字节的字节数组转换为(8个的字节)long。
     */
    public static long byte8ToLong8(byte[] bytes) {
        return Longs.fromByteArray(bytes);
    }

}
