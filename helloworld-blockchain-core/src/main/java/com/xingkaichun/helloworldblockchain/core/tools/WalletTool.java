package com.xingkaichun.helloworldblockchain.core.tools;

import com.xingkaichun.helloworldblockchain.crypto.AccountUtil;
import com.xingkaichun.helloworldblockchain.crypto.model.account.StringAccount;
import com.xingkaichun.helloworldblockchain.crypto.model.account.StringAddress;
import com.xingkaichun.helloworldblockchain.crypto.model.account.StringPrivateKey;
import com.xingkaichun.helloworldblockchain.crypto.model.account.StringPublicKey;
import com.xingkaichun.helloworldblockchain.core.model.key.Wallet;

/**
 * 钱包工具类
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class WalletTool {

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
            StringAccount stringAccount = AccountUtil.randomStringAccount();
            StringPublicKey stringPublicKey = stringAccount.getStringPublicKey();
            StringPrivateKey stringPrivateKey = stringAccount.getStringPrivateKey();
            StringAddress stringAddress = stringAccount.getStringAddress();
            Wallet wallet = new Wallet(stringPrivateKey,stringPublicKey,stringAddress);
            return wallet;
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

}
