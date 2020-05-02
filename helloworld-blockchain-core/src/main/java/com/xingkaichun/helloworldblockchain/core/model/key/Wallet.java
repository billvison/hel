package com.xingkaichun.helloworldblockchain.core.model.key;

import com.xingkaichun.helloworldblockchain.crypto.model.StringAddress;
import com.xingkaichun.helloworldblockchain.crypto.model.StringPrivateKey;
import com.xingkaichun.helloworldblockchain.crypto.model.StringPublicKey;
import lombok.Data;

/**
 * 钱包
 *
 * @author 邢开春 xingkaichun@qq.com
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
