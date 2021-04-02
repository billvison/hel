package com.xingkaichun.helloworldblockchain.crypto;

import com.google.common.primitives.Bytes;

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
    public static byte[] long64ToBytes64WithBigEndian(long value) {
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
    public static long bytes64ToLong64WithBigEndian(byte[] bytes) {
        long n0 = bytes[0] & 0xff;
        long n1 = bytes[1] & 0xff;
        long n2 = bytes[2] & 0xff;
        long n3 = bytes[3] & 0xff;
        long n4 = bytes[4] & 0xff;
        long n5 = bytes[5] & 0xff;
        long n6 = bytes[6] & 0xff;
        long n7 = bytes[7] & 0xff;

        n6 <<= 0;
        n6 <<= 8;
        n5 <<= 16;
        n4 <<= 24;
        n3 <<= 32;
        n2 <<= 40;
        n1 <<= 48;
        n0 <<= 56;
        long n = n0 | n1 | n2 | n3 | n4 | n5 | n6 | n7;
        return n;
    }

    /**
     * 拼接字节数组。计算[传入字节数组]的长度，然后将长度转为4个字节的字节数组(大端)，然后将长度字节数组拼接在[传入字节数组]前，然后返回。
     */
    public static byte[] concatLengthBytes(byte[] value) {
        return Bytes.concat(long64ToBytes64WithBigEndian(value.length),value);
    }

    /**
     * 拼接字节数组。
     */
    public static byte[] concatLengthBytes(List<byte[]> values) {
        byte[] concatBytes = new byte[0];
        for(byte[] value:values){
            concatBytes = Bytes.concat(concatBytes,value);
        }
        return concatLengthBytes(concatBytes);
    }
}
