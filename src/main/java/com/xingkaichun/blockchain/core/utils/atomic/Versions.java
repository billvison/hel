package com.xingkaichun.blockchain.core.utils.atomic;

public class Versions {

    public static class Version{
        public static final int VERSION_NUMBER_1 = 1;
        public static final int VERSION_NUMBER_CURRENT = 1;
    }

    public static class MinTransactionCoin{
        public static float getValue(int version){
            if(Version.VERSION_NUMBER_1 == version){
                return 0;
            }
            throw new RuntimeException("没有找到区块链版本，请升级区块链。");
        }
    }
}
