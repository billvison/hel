package com.xingkaichun.helloworldblockchain.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * 时间工具类
 *
 * @author 邢开春
 */
public class DateUtil {

    /**
     * 时间戳转换为(24小时制的)中国时间
     */
    public static String timestamp2ChinaTime(long timestamp) {
        Date date = new Date(timestamp);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        return simpleDateFormat.format(date);
    }
}
