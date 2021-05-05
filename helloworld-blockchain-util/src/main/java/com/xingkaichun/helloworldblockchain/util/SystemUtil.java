package com.xingkaichun.helloworldblockchain.util;
/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class SystemUtil {

    public static void errorExit(String message, Exception exception) {
        LogUtil.error("系统发生异常并退出，请检查！"+message,exception);
        System.exit(1);
    }
}
