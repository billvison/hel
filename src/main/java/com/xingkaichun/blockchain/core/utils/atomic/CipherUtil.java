package com.xingkaichun.blockchain.core.utils.atomic;

import com.xingkaichun.blockchain.core.model.transaction.Transaction;

import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.List;

/**
 * 密码学工具类
 */
public class CipherUtil {

    /**
     * hash
     */
    public static String applySha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(Constant.CHARSET));
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * ECDSA签名
     */
    public static byte[] applyECDSASig(PrivateKey privateKey, String data) {
        try {
            Signature signature = Signature.getInstance("ECDSA", "BC");
            signature.initSign(privateKey);
            byte[] strByte = data.getBytes();
            signature.update(strByte);
            byte[] sign = signature.sign();
            return sign;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * ECDSA签名验证
     */
    public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
        try {
            Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
            ecdsaVerify.initVerify(publicKey);
            ecdsaVerify.update(data.getBytes());
            return ecdsaVerify.verify(signature);
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 挖矿难度目标
     * 难度为5返回"00000"
     */
    public static String getDificultyString(int difficulty) {
        return new String(new char[difficulty]).replace('\0', '0');
    }

    /**
     * 默克尔根值
     */
    public static String getMerkleRoot(List<Transaction> transactions) {
        if(transactions==null || transactions.size()==0){
            return "";
        }

        int count = transactions.size();

        List<String> previousTreeLayer = new ArrayList<String>();
        for(Transaction transaction : transactions) {
            previousTreeLayer.add(transaction.getTransactionUUID());
        }
        List<String> treeLayer = previousTreeLayer;

        while(count > 1) {
            treeLayer = new ArrayList<String>();
            for(int i=1; i < previousTreeLayer.size(); i+=2) {
                treeLayer.add(applySha256(previousTreeLayer.get(i-1) + previousTreeLayer.get(i)));
            }
            count = treeLayer.size();
            previousTreeLayer = treeLayer;
        }

        String merkleRoot = (treeLayer.size() == 1) ? treeLayer.get(0) : "";
        return merkleRoot;
    }
}
