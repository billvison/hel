package com.xingkaichun.helloworldblockchain.netcore.tool;

import com.xingkaichun.helloworldblockchain.crypto.model.account.StringAccount;
import com.xingkaichun.helloworldblockchain.crypto.model.account.StringAddress;
import com.xingkaichun.helloworldblockchain.crypto.model.account.StringPrivateKey;
import com.xingkaichun.helloworldblockchain.crypto.model.account.StringPublicKey;
import com.xingkaichun.helloworldblockchain.netcore.dto.wallet.WalletDTO;

/**
 * 钱包工具类
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class WalletDtoTool {

    /**
     * 类型转换
     */
    public static WalletDTO classCast(StringAccount stringAccount){
        WalletDTO walletDTO = new WalletDTO(stringAccount.getStringPrivateKey().getValue(),stringAccount.getStringPublicKey().getValue(),stringAccount.getStringAddress().getValue());
        return walletDTO;
    }
    
    /**
     * 类型转换
     */
    public static StringAccount classCast(WalletDTO walletDTO){
        StringAccount stringAccount = new StringAccount(new StringPrivateKey(walletDTO.getPrivateKey())
                ,new StringPublicKey(walletDTO.getPublicKey())
                ,new StringAddress(walletDTO.getAddress()));
        return stringAccount;
    }
}
