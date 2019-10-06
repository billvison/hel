package com.xingkaichun.blockchain.core.utils.atomic;

import java.nio.charset.Charset;

public class Constant {

    //第一个区块的PREVIOUS_HASH
    public final static String FIRST_BLOCK_PREVIOUS_HASH = "xingkaichun";
    //第一个区块的高度
    public final static int FIRST_BLOCK_HEIGHT = 1;
    //区块链创造者
    public final static String BLOCK_CHAIN_PUBLIC_KEY = "xingkaichun";

    //字符集
    public static final Charset CHARSET = Charset.forName("UTF-8");

}
