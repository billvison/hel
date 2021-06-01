package com.xingkaichun.helloworldblockchain.util;
/**
 * 系统工具类
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class SystemUtil {

    public static void errorExit(String message, Exception exception) {
        LogUtil.error("system error occurred, and exited, please check the error！"+message,exception);
        System.exit(1);
    }
}
