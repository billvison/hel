package com.xingkaichun.helloworldblockchain.crypto.model;


import java.io.Serializable;

/**
 * 秘钥
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class StringKey implements Serializable {

    private StringPrivateKey stringPrivateKey;
    private StringPublicKey stringPublicKey;
    private StringAddress stringAddress;




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

    public StringAddress getStringAddress() {
        return stringAddress;
    }

    public void setStringAddress(StringAddress stringAddress) {
        this.stringAddress = stringAddress;
    }

    //endregion
}
