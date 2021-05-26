package com.xingkaichun.helloworldblockchain.crypto.model;


import java.io.Serializable;

/**
 * (十六进制大端模式)字符串格式的数字货币的账户
 *
 * 数字货币的账户(账户在区块链领域被称为钱包)由账号(账号在区块链领域被称为地址)、密码(密码在区块链领域被称为私钥)组成。
 *
 * 私钥可以推导出公钥，公钥不能逆推出私钥。
 * 公钥可以推导出公钥哈希，公钥哈希不能逆推出公钥。
 * 公钥哈希可以推导出地址，地址可以逆推出公钥哈希。
 *
 * @author 邢开春 409060350@qq.com
 */
public class Account implements Serializable {

    //私钥
    private String privateKey;
    //公钥
    private String publicKey;
    //公钥哈希
    private String publicKeyHash;
    //地址
    private String address;

    public Account(String privateKey, String publicKey, String publicKeyHash, String address) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.publicKeyHash = publicKeyHash;
        this.address = address;
    }


    //region get set
    public String getPrivateKey() {
        return privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getAddress() {
        return address;
    }

    public String getPublicKeyHash() {
        return publicKeyHash;
    }
    //endregion
}
