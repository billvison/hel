package com.xingkaichun.helloworldblockchain.crypto;

import com.google.common.primitives.Longs;

/**
 * Number工具类
 *
 * @author 邢开春 409060350@qq.com
 */
public class NumberUtil {

    /**
     * long转换为(大端模式)8个字节的字节数组(8*8=64个bit)。
     */
    public static byte[] long64ToBytes64WithBigEndian(long number) {
        return Longs.toByteArray(number);
    }

    /**
     * (大端模式)8个字节的字节数组(8*8=64个bit)转换为long。
     */
    public static long bytes64ToLong64WithBigEndian(byte[] bytes) {
        return Longs.fromByteArray(bytes);
    }

}
