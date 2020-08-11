package com.xingkaichun.helloworldblockchain.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadUtil {

    private final static Logger logger = LoggerFactory.getLogger(ThreadUtil.class);

    public static void sleep(long millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            logger.error("线程休眠异常",e);//不可能发生
        }
    }
}
