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
     * 拼接字节数组。计算[传入字节数组]的长度，然后将长度转为4个字节的字节数组(大端)，然后将长度字节数组拼接在[传入字节数组]前，然后返回。
     */
    public static byte[] concatLengthBytes(byte[] value) {
        return Bytes.concat(NumberUtil.long64ToBytes64WithBigEndian(value.length),value);
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
