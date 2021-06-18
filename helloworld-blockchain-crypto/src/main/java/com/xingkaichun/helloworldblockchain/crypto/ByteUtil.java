package com.xingkaichun.helloworldblockchain.crypto;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Longs;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * 字节工具类
 *
 * @author 邢开春 409060350@qq.com
 */
public class ByteUtil {

    public static final int BYTE8_BYTE_COUNT = 8;

    public static byte[] stringToUtf8Bytes(String stringValue) {
        return stringValue.getBytes(StandardCharsets.UTF_8);
    }
    public static String utf8BytesToString(byte[] bytesValue) {
        return new String(bytesValue, StandardCharsets.UTF_8);
    }

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
    /**
     * (大端模式)8个字节的字节数组转换为十六进制字符串。
     */
    public static String long8ToHexString8(long number) {
        return HexUtil.bytesToHexString(long8ToByte8(number));
    }


    /**
     * 拼接数组。
     */
    public static byte[] concatenate(byte[] byteArray1,byte[] byteArray2) {
        return Bytes.concat(byteArray1,byteArray2);
    }
    public static byte[] concatenate3(byte[] byteArray1,byte[] byteArray2,byte[] byteArray3) {
        return Bytes.concat(byteArray1,byteArray2,byteArray3);
    }
    public static byte[] concatenate4(byte[] byteArray1,byte[] byteArray2,byte[] byteArray3,byte[] byteArray4) {
        return Bytes.concat(byteArray1,byteArray2,byteArray3,byteArray4);
    }

    /**
     * 拼接长度。
     * 计算[传入字节数组]的长度，然后将长度转为8个字节的字节数组(大端)，然后将长度字节数组拼接在[传入字节数组]前，然后返回。
     */
    public static byte[] concatenateLength(byte[] value) {
        return concatenate(long8ToByte8(value.length),value);
    }

    /**
     * 碾平字节数组列表为字节数组。
     */
    public static byte[] flat(List<byte[]> values) {
        byte[] concatBytes = new byte[0];
        for(byte[] value:values){
            concatBytes = concatenate(concatBytes,value);
        }
        return concatBytes;
    }

    /**
     * 碾平字节数组列表为新的字节数组，然后拼接长度并返回。
     */
    public static byte[] flatAndConcatenateLength(List<byte[]> values) {
        byte[] flatBytes = flat(values);
        return concatenateLength(flatBytes);
    }

    public static boolean equals(byte[] a, byte[] a2) {
        return Arrays.equals(a,a2);
    }

    public static byte[] copy(byte[] sourceBytes, int sourceStartPosition, int length) {
        byte[] destinationBytes = new byte[length];
        System.arraycopy(sourceBytes,sourceStartPosition,destinationBytes,0,length);
        return destinationBytes;
    }

    public static void copyTo(byte[] sourceBytes, int sourceStartPosition, int length, byte[] destinationBytes, int destinationStartPosition){
        System.arraycopy(sourceBytes,sourceStartPosition,destinationBytes,destinationStartPosition,length);
    }
}