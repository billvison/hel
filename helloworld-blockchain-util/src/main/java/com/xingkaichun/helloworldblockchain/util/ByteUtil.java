package com.xingkaichun.helloworldblockchain.util;

import com.xingkaichun.helloworldblockchain.setting.GlobalSetting;

/**
 * Byte工具类
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class ByteUtil {

    public static byte[] stringToBytes(String strValue) {
        return strValue.getBytes(GlobalSetting.GLOBAL_CHARSET);
    }

    public static String bytesToString(byte[] bytesValue) {
        return new String(bytesValue, GlobalSetting.GLOBAL_CHARSET);
    }
}
