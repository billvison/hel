package com.xingkaichun.blockchain.core.utils;

import com.xingkaichun.blockchain.core.model.transaction.Transaction;
import com.xingkaichun.blockchain.core.utils.atomic.CipherUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 默克尔树工具类
 */
public class MerkleUtils {
    //TODO 轻钱包验证
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
                treeLayer.add(CipherUtil.applySha256(previousTreeLayer.get(i-1) + previousTreeLayer.get(i)));
            }
            count = treeLayer.size();
            previousTreeLayer = treeLayer;
        }

        String merkleRoot = (treeLayer.size() == 1) ? treeLayer.get(0) : "";
        return merkleRoot;
    }
}
