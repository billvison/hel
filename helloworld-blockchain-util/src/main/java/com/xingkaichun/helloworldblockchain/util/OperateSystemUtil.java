package com.xingkaichun.helloworldblockchain.util;

/**
 * 系统工具类
 *
 * @author 邢开春
 */
public class OperateSystemUtil {

    private static Boolean androidOperateSystem;
    private static Boolean windowsOperateSystem;
    private static Boolean linuxOperateSystem;

    public static boolean isAndroidOperateSystem(){
        if(androidOperateSystem == null){
            try {
                Class.forName("android.app.Application");
                androidOperateSystem = true;
            } catch (ClassNotFoundException e) {
                androidOperateSystem = false;
            }
        }
        return androidOperateSystem;
    }

    public static boolean isWindowsOperateSystem(){
        if(windowsOperateSystem == null){
            windowsOperateSystem = System.getProperty("os.name").toLowerCase().contains("windows");
        }
        return windowsOperateSystem;
    }

    public static boolean isLinuxOperateSystem(){
        if(linuxOperateSystem == null){
            linuxOperateSystem = System.getProperty("os.name").toLowerCase().contains("linux");
        }
        return linuxOperateSystem;
    }
}
