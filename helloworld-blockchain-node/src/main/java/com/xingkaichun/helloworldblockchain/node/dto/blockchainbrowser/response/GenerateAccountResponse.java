package com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.response;


import com.xingkaichun.helloworldblockchain.crypto.model.account.Account;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class GenerateAccountResponse {

    private Account account;




    //region get set

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }


    //endregion
}
