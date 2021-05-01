package com.xingkaichun.helloworldblockchain.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 休眠工具类
 *
 * @author 邢开春 409060350@qq.com
 */
public class SleepUtil {

    private static final Logger logger = LoggerFactory.getLogger(SleepUtil.class);

    public static void sleep(long millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            logger.error("sleep failed.",e);
        }
    }
}
