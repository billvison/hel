package com.xingkaichun.helloworldblockchain.crypto;

import com.google.common.primitives.Bytes;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Bytes工具类
 *
 * @author 邢开春 409060350@qq.com
 */
public class ByteUtil {

    public static byte[] encode(String strValue) {
        return strValue.getBytes(StandardCharsets.UTF_8);
    }

    public static String decodeToUtf8String(byte[] bytesValue) {
        return new String(bytesValue, StandardCharsets.UTF_8);
    }

    public static byte[] encode(long longValue) {
        return encode(String.valueOf(longValue));
    }

    public static long decodeToLong(byte[] bytesValue) {
        return Long.parseLong(decodeToUtf8String(bytesValue));
    }



    /**
     * 拼接数组。
     */
    public static byte[] concat(byte[]... arrays) {
        return Bytes.concat(arrays);
    }

    /**
     * 拼接长度。
     * 计算[传入字节数组]的长度，然后将长度转为4个字节的字节数组(大端)，然后将长度字节数组拼接在[传入字节数组]前，然后返回。
     */
    public static byte[] concatLength(byte[] value) {
        return concat(NumberUtil.long64ToBytes64ByBigEndian(value.length),value);
    }

    /**
     * 拼接长度。
     * 将[传入字节数组]列表拼接生成新的字节数组，然后计算[新的字节数组]的长度，
     * 然后将长度转为4个字节的字节数组(大端)，然后将长度字节数组拼接在[新的字节数组]前，然后返回。
     */
    public static byte[] flatAndConcatLength(List<byte[]> values) {
        byte[] concatBytes = new byte[0];
        for(byte[] value:values){
            concatBytes = concat(concatBytes,value);
        }
        return concatLength(concatBytes);
    }
}
