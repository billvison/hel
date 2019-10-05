package com.xingkaichun.blockchain.core.model.wallet;

import com.xingkaichun.blockchain.core.model.key.PrivateKeyString;
import com.xingkaichun.blockchain.core.model.key.PublicKeyString;
import lombok.Data;

/**
 * 钱包
 */
@Data
public class Wallet {

    private PrivateKeyString privateKeyString;

    private PublicKeyString publicKeyString;

    public Wallet(PrivateKeyString privateKeyString, PublicKeyString publicKeyString) {
        this.privateKeyString = privateKeyString;
        this.publicKeyString = publicKeyString;
    }

}
