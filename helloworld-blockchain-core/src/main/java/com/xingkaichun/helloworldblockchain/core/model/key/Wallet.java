package com.xingkaichun.helloworldblockchain.core.model.key;

import com.xingkaichun.helloworldblockchain.crypto.model.account.StringAddress;
import com.xingkaichun.helloworldblockchain.crypto.model.account.StringPrivateKey;
import com.xingkaichun.helloworldblockchain.crypto.model.account.StringPublicKey;

/**
 * 钱包
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class Wallet {

    private StringPrivateKey stringPrivateKey;

    private StringPublicKey stringPublicKey;

    private StringAddress stringAddress;

    public Wallet(StringPrivateKey stringPrivateKey, StringPublicKey stringPublicKey, StringAddress stringAddress) {
        this.stringPrivateKey = stringPrivateKey;
        this.stringPublicKey = stringPublicKey;
        this.stringAddress = stringAddress;
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

    public StringAddress getStringAddress() {
        return stringAddress;
    }

    public void setStringAddress(StringAddress stringAddress) {
        this.stringAddress = stringAddress;
    }

    //endregion

}
