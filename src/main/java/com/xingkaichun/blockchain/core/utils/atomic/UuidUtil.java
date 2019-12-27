package com.xingkaichun.blockchain.core.utils.atomic;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UuidUtil {

    /**
     * 标准的UUID
     * 32位16进制的数字，用分隔符分成8-4-4-4-12的格式
     */
    private static final Pattern UUID_PATTERN = Pattern.compile("[0-9a-f]{8}(-[0-9a-f]{4}){3}-[0-9a-f]{12}");

    /**
     * 判断一个字符串是否是有效的UUID
     *
     * @param uuid
     * @return
     */
    public static boolean isValidUUID(String uuid) {
        Matcher matcher = UUID_PATTERN.matcher(uuid);
        return matcher.matches();
    }
}
