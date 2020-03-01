package com.xingkaichun.blockchain.core.model.wallet;

import com.xingkaichun.blockchain.core.model.key.StringAddress;
import com.xingkaichun.blockchain.core.model.key.StringPrivateKey;
import com.xingkaichun.blockchain.core.model.key.StringPublicKey;
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
