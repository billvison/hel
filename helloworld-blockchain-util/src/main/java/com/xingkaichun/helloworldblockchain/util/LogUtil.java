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

    public static void error(String message, Exception exception) {
        logger.error(message,exception);
    }

    public static void debug(String message) {
        logger.debug(message);
    }
}
