package com.xingkaichun.helloworldblockchain.util;

import org.slf4j.LoggerFactory;

/**
 * 日志工具
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class LogUtil {

    public static void error(String message, Exception exception) {
        StackTraceElement stackTraceElement = getStackTraceElement();
        LoggerFactory.getLogger(stackTraceElement.getClassName()).error("["+stackTraceElement.getLineNumber()+"] - "+message,exception);
    }

    public static void debug(String message) {
        StackTraceElement stackTraceElement = getStackTraceElement();
        LoggerFactory.getLogger(stackTraceElement.getClassName()).debug("["+stackTraceElement.getLineNumber()+"] - "+message);
    }

    private static StackTraceElement getStackTraceElement(){
        StackTraceElement[] classArray= new Exception().getStackTrace() ;
        return classArray[2];
    }
}
