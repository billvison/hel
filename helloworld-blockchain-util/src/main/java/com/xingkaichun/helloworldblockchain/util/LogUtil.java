package com.xingkaichun.helloworldblockchain.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志工具
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class LogUtil {

    private static final Logger logger = LoggerFactory.getLogger(LogUtil.class);

    public static void error(String msg, Exception e) {
        logger.error(msg,e);
    }

    public static void debug(String msg) {
        logger.debug(msg);
    }
}
