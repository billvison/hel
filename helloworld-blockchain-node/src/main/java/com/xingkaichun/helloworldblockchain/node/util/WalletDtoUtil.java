package com.xingkaichun.helloworldblockchain.node.util;

import com.xingkaichun.helloworldblockchain.core.model.key.Wallet;
import com.xingkaichun.helloworldblockchain.crypto.model.account.StringAddress;
import com.xingkaichun.helloworldblockchain.crypto.model.account.StringPrivateKey;
import com.xingkaichun.helloworldblockchain.crypto.model.account.StringPublicKey;
import com.xingkaichun.helloworldblockchain.node.dto.wallet.WalletDTO;

/**
 * 钱包工具类
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class WalletDtoUtil {

    /**
     * 类型转换
     */
    public static WalletDTO classCast(Wallet wallet){
        WalletDTO walletDTO = new WalletDTO(wallet.getStringPrivateKey().getValue(),wallet.getStringPublicKey().getValue(),wallet.getStringAddress().getValue());
        return walletDTO;
    }
    
    /**
     * 类型转换
     */
    public static Wallet classCast(WalletDTO walletDTO){
        Wallet wallet = new Wallet(new StringPrivateKey(walletDTO.getPrivateKey()),
                new StringPublicKey(walletDTO.getPublicKey()),
                new StringAddress(walletDTO.getAddress()));
        return wallet;
    }
}
