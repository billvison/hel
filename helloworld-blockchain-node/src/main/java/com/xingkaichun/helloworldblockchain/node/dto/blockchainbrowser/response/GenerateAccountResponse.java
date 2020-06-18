package com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.response;

import com.xingkaichun.helloworldblockchain.crypto.model.account.StringAccount;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class GenerateAccountResponse {

    private StringAccount stringAccount;




    //region get set

    public StringAccount getStringAccount() {
        return stringAccount;
    }

    public void setStringAccount(StringAccount stringAccount) {
        this.stringAccount = stringAccount;
    }


    //endregion
}
