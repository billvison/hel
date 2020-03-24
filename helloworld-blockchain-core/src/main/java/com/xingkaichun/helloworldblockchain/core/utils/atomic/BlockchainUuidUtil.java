package com.xingkaichun.helloworldblockchain.core.utils.atomic;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BlockchainUuidUtil {

    /**
     * 13-8-4-4-4-12的格式
     */
    private static final Pattern UUID_PATTERN = Pattern.compile("[0-9]{13}[0-9a-f]{8}(-[0-9a-f]{4}){3}-[0-9a-f]{12}");

    /**
     * 判断一个字符串是否是格式正确的UUID
     */
    public static boolean isBlockchainUuidFormatRight(String uuid) {
        Matcher matcher = UUID_PATTERN.matcher(uuid);
        return matcher.matches();
    }

    public static String randomBlockchainUUID(long timestamp){
        return timestamp + UUID.randomUUID().toString();
    }
}
