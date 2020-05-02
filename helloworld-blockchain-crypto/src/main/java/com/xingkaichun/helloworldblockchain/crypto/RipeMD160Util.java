package com.xingkaichun.helloworldblockchain.crypto;

import java.security.MessageDigest;

/**
 * RipeMD160消息摘要工具类
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class RipeMD160Util {

    /**
     * RipeMD160消息摘要
     */
    public static byte[] applyRipeMD160(byte[] data) throws Exception{
        MessageDigest ripeMD160Digest = MessageDigest.getInstance("RipeMD160");
        return ripeMD160Digest.digest(data);
    }
}
