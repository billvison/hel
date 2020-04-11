package com.xingkaichun.helloworldblockchain.core.utils;

import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.crypto.SHA256Util;

import java.util.ArrayList;
import java.util.List;

/**
 * 默克尔树工具类
 */
public class MerkleUtils {
    //TODO 改善型功能 轻钱包验证
    public static String getMerkleRoot(List<Transaction> transactions) {
        if(transactions==null || transactions.size()==0){
            return "";
        }

        int count = transactions.size();

        List<String> previousTreeLayer = new ArrayList<>();
        for(Transaction transaction : transactions) {
            previousTreeLayer.add(transaction.getTransactionUUID());
        }
        List<String> treeLayer = previousTreeLayer;

        while(count > 1) {
            treeLayer = new ArrayList<>();
            for(int i=1; i < previousTreeLayer.size(); i+=2) {
                treeLayer.add(SHA256Util.applySha256HexString(previousTreeLayer.get(i-1) + previousTreeLayer.get(i)));
            }
            count = treeLayer.size();
            previousTreeLayer = treeLayer;
        }

        String merkleRoot = (treeLayer.size() == 1) ? SHA256Util.applySha256HexString(treeLayer.get(0)) : "";
        return merkleRoot;
    }
}
