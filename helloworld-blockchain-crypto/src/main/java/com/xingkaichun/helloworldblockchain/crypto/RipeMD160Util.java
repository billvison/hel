package com.xingkaichun.helloworldblockchain.crypto;

import org.bouncycastle.util.encoders.Base64;

import java.security.MessageDigest;

/**
 * 密码学工具类
 */
public class RipeMD160Util {

    /**
     * RipeMD160消息摘要
     */
    public static String ripeMD160(String data) throws Exception{
        byte[] ripeMD160Digest = ripeMD160(data.getBytes());
        return Base64.toBase64String(ripeMD160Digest);
    }

    /**
     * RipeMD160消息摘要
     */
    public static byte[] ripeMD160(byte[] data) throws Exception{
        MessageDigest ripeMD160Digest = MessageDigest.getInstance("RipeMD160");
        return ripeMD160Digest.digest(data);
    }
}
