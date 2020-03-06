package com.xingkaichun.helloworldblockchain.model.key;

import lombok.Data;

/**
 * 钱包
 */
@Data
public class Wallet {

    private StringPrivateKey stringPrivateKey;

    private StringPublicKey stringPublicKey;

    private StringAddress stringAddress;

    public Wallet(StringPrivateKey stringPrivateKey, StringPublicKey stringPublicKey, StringAddress stringAddress) {
        this.stringPrivateKey = stringPrivateKey;
        this.stringPublicKey = stringPublicKey;
        this.stringAddress = stringAddress;
    }

}
