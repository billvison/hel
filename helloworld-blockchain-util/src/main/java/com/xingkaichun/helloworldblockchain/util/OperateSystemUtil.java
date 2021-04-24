package com.xingkaichun.helloworldblockchain.util;

/**
 * 系统工具类
 *
 * @author 邢开春 409060350@qq.com
 */
public class OperateSystemUtil {

    public static boolean isAndroidOperateSystem(){
        try {
            Class.forName("android.app.Application");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean isWindowsOperateSystem(){
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }

    public static boolean isLinuxOperateSystem(){
        return System.getProperty("os.name").toLowerCase().contains("linux");
    }
}
