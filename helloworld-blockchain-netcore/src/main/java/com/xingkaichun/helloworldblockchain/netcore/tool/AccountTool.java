package com.xingkaichun.helloworldblockchain.netcore.tool;

import com.xingkaichun.helloworldblockchain.crypto.model.account.StringAccount;
import com.xingkaichun.helloworldblockchain.crypto.model.account.StringAddress;
import com.xingkaichun.helloworldblockchain.crypto.model.account.StringPrivateKey;
import com.xingkaichun.helloworldblockchain.crypto.model.account.StringPublicKey;
import com.xingkaichun.helloworldblockchain.netcore.dto.account.AccountDTO;

/**
 * 账户工具类
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class AccountTool {

    /**
     * 类型转换
     */
    public static AccountDTO classCast(StringAccount stringAccount){
        AccountDTO accountDTO = new AccountDTO(stringAccount.getStringPrivateKey().getValue(),stringAccount.getStringPublicKey().getValue(),stringAccount.getStringAddress().getValue());
        return accountDTO;
    }
    
    /**
     * 类型转换
     */
    public static StringAccount classCast(AccountDTO accountDTO){
        StringAccount stringAccount = new StringAccount(new StringPrivateKey(accountDTO.getPrivateKey())
                ,new StringPublicKey(accountDTO.getPublicKey())
                ,new StringAddress(accountDTO.getAddress()));
        return stringAccount;
    }
}
