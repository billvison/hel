package com.xingkaichun.helloworldblockchain.core.utils;

import com.xingkaichun.helloworldblockchain.setting.GlobalSetting;

import java.io.UnsupportedEncodingException;

/**
 * Byte工具类
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class ByteUtil {

    public static byte[] stringToBytes(String str) {
        try {
            return str.getBytes(GlobalSetting.GLOBAL_CHARSET.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
