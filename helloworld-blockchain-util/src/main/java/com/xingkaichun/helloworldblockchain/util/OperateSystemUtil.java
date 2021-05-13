package com.xingkaichun.helloworldblockchain.util;

/**
 * 系统工具类
 *
 * @author 邢开春 409060350@qq.com
 */
public class OperateSystemUtil {

    public static boolean isWindowsOperateSystem(){
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    public static boolean isMacOperateSystem(){
        return System.getProperty("os.name").toLowerCase().contains("mac");
    }
}
