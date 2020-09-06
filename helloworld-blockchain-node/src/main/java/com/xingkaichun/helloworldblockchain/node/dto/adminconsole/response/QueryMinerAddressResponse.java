package com.xingkaichun.helloworldblockchain.node.dto.adminconsole.response;

import com.xingkaichun.helloworldblockchain.crypto.model.Account;

/**
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class QueryMinerAddressResponse {


    private String minerAddress;

    private Account defaultMinerAccount;


    //region get set

    public String getMinerAddress() {
        return minerAddress;
    }

    public void setMinerAddress(String minerAddress) {
        this.minerAddress = minerAddress;
    }

    public Account getDefaultMinerAccount() {
        return defaultMinerAccount;
    }

    public void setDefaultMinerAccount(Account defaultMinerAccount) {
        this.defaultMinerAccount = defaultMinerAccount;
    }
//endregion
}
