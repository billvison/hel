package com.xingkaichun.helloworldblockchain.util;

import com.google.common.primitives.Bytes;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * Bytes工具类
 *
 * @author 邢开春 409060350@qq.com
 */
public class ByteUtil {

    /**
     * long转换为(大端模式)8个字节的字节数组(8*8=64个bit)。
     */
    public static byte[] longToBytes8BigEndian(long value) {
        byte[] bytes = new byte[8];
        bytes[7] = (byte)(0xFF & (value));
        bytes[6] = (byte)(0xFF & (value >> 8));
        bytes[5] = (byte)(0xFF & (value >> 16));
        bytes[4] = (byte)(0xFF & (value >> 24));
        bytes[3] = (byte)(0xFF & (value >> 32));
        bytes[2] = (byte)(0xFF & (value >> 40));
        bytes[1] = (byte)(0xFF & (value >> 48));
        bytes[0] = (byte)(0xFF & (value >> 56));
        return bytes;
    }

    /**
     * (大端模式)8个字节的字节数组(8*8=64个bit)转换为long。
     */
    public static long bytes8BigEndianToLong(byte[] valueBytes) {
        long s = 0;
        long s0 = valueBytes[0] & 0xff;
        long s1 = valueBytes[1] & 0xff;
        long s2 = valueBytes[2] & 0xff;
        long s3 = valueBytes[3] & 0xff;
        long s4 = valueBytes[4] & 0xff;
        long s5 = valueBytes[5] & 0xff;
        long s6 = valueBytes[6] & 0xff;
        long s7 = valueBytes[7] & 0xff;

        // s0不变
        s1 <<= 8;
        s2 <<= 16;
        s3 <<= 24;
        s4 <<= 8 * 4;
        s5 <<= 8 * 5;
        s6 <<= 8 * 6;
        s7 <<= 8 * 7;
        s = s0 | s1 | s2 | s3 | s4 | s5 | s6 | s7;
        return s;
    }

    /**
     * 拼接字节数组。计算[传入字节数组]的长度，然后将长度转为4个字节的字节数组(大端)，然后将长度字节数组拼接在[传入字节数组]前，然后返回。
     */
    public static byte[] concatLengthBytes(byte[] value) {
        return Bytes.concat(longToBytes8BigEndian(value.length),value);
    }

    /**
     * 拼接字节数组。
     */
    public static byte[] concatLengthBytes(List<byte[]> values) {
        byte[] concatBytes = longToBytes8BigEndian(values.size());
        for(byte[] value:values){
            concatBytes = Bytes.concat(concatBytes,value);
        }
        return concatBytes;
    }
}
