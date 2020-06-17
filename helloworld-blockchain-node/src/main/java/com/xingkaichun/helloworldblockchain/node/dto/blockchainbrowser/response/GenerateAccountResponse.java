package com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.response;

import com.xingkaichun.helloworldblockchain.netcore.dto.account.AccountDTO;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class GenerateAccountResponse {

    private AccountDTO accountDTO;




    //region get set

    public AccountDTO getAccountDTO() {
        return accountDTO;
    }

    public void setAccountDTO(AccountDTO accountDTO) {
        this.accountDTO = accountDTO;
    }

    //endregion
}
