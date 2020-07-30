package com.xingkaichun.helloworldblockchain.crypto.model.account;


import java.io.Serializable;

/**
 * 字符串格式的数字货币的账户
 *
 * 通常情况下，数字货币的账户由一个非对称秘钥对（私钥、公钥）、一个地址构成。
 * 非对称秘钥由私钥、公钥构成。私钥可以推导出公钥。公钥不能逆推出私钥。
 * 公钥可以推导出数字货币的地址。地址不能逆推出公钥。
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class StringAccount implements Serializable {

    private StringPrivateKey stringPrivateKey;
    private StringPublicKey stringPublicKey;
    private String address;

    public StringAccount(StringPrivateKey stringPrivateKey, StringPublicKey stringPublicKey, String address) {
        this.stringPrivateKey = stringPrivateKey;
        this.stringPublicKey = stringPublicKey;
        this.address = address;
    }


    //region get set

    public StringPrivateKey getStringPrivateKey() {
        return stringPrivateKey;
    }

    public void setStringPrivateKey(StringPrivateKey stringPrivateKey) {
        this.stringPrivateKey = stringPrivateKey;
    }

    public StringPublicKey getStringPublicKey() {
        return stringPublicKey;
    }

    public void setStringPublicKey(StringPublicKey stringPublicKey) {
        this.stringPublicKey = stringPublicKey;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

//endregion
}
