package com.xingkaichun.helloworldblockchain.crypto;

import java.security.MessageDigest;

/**
 * 密码学工具类
 */
public class RipeMD160Util {

    /**
     * RipeMD160消息摘要
     */
    public static String ripeMD160(String data) throws Exception{
        byte[] ripeMD160Data = ripeMD160(data.getBytes());
        return new String(ripeMD160Data);
    }

    /**
     * RipeMD160消息摘要
     */
    private static byte[] ripeMD160(byte[] data) throws Exception{
        MessageDigest md = MessageDigest.getInstance("RipeMD160");
        return md.digest(data);
    }
}
