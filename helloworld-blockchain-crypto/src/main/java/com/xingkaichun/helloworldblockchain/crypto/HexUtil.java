package com.xingkaichun.helloworldblockchain.crypto;

import org.bouncycastle.util.encoders.Hex;

/**
 * 十六进制工具类
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class HexUtil {

    /**
     * byte数组转十六进制字符串(十六进制字符串小写，仅包含字符0123456789abcdef)。
     * 不允许省略十六进制字符串前面的零，因此十六进制字符串的长度是字节数量的2倍。
     */
    public static String bytesToHexString(byte[] bytes) {
        return Hex.toHexString(bytes);
    }

    /**
     * 16进制字符串转byte数组(十六进制字符串小写，仅包含字符0123456789abcdef)
     * @param hexString 16进制字符串，该属性值的长度一定是2的整数倍
     */
    public static byte[] hexStringToBytes(String hexString) {
        return Hex.decode(hexString);
    }
}
