package com.xingkaichun.helloworldblockchain.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间工具类
 *
 * @author 邢开春 409060350@qq.com
 */
public class TimeUtil {

    public static String timestamp2FormatDate(long timestamp) {
        Date date = new Date(timestamp);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(date);
    }

    public static long currentTimeMillis(){
        return System.currentTimeMillis();
    }
}
