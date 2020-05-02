package com.xingkaichun.helloworldblockchain.core.utils;

import com.xingkaichun.helloworldblockchain.crypto.KeyUtil;
import com.xingkaichun.helloworldblockchain.crypto.model.StringKey;
import com.xingkaichun.helloworldblockchain.crypto.model.StringAddress;
import com.xingkaichun.helloworldblockchain.crypto.model.StringPrivateKey;
import com.xingkaichun.helloworldblockchain.crypto.model.StringPublicKey;
import com.xingkaichun.helloworldblockchain.core.model.key.Wallet;

/**
 * 钱包工具类
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class WalletUtil {

    public static Wallet loadWallet(StringPrivateKey stringPrivateKey, StringPublicKey stringPublicKey, StringAddress stringAddress){
        try {
            Wallet wallet = new Wallet(stringPrivateKey,stringPublicKey,stringAddress);
            return wallet;
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Wallet generateWallet(){
        try {
            StringKey stringKey = KeyUtil.randomStringKey();
            StringPublicKey stringPublicKey = stringKey.getStringPublicKey();
            StringPrivateKey stringPrivateKey = stringKey.getStringPrivateKey();
            StringAddress stringAddress = stringKey.getStringAddress();
            Wallet wallet = new Wallet(stringPrivateKey,stringPublicKey,stringAddress);
            return wallet;
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

}
