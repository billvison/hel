package com.xingkaichun.helloworldblockchain.netcore.dto.blockchainbrowser.request;

import com.xingkaichun.helloworldblockchain.crypto.model.account.StringPrivateKey;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.TransactionDTO;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class SignatureTransactDtoRequest {

    private TransactionDTO transactionDTO;
    private StringPrivateKey stringPrivateKey;




    //region get set

    public TransactionDTO getTransactionDTO() {
        return transactionDTO;
    }

    public void setTransactionDTO(TransactionDTO transactionDTO) {
        this.transactionDTO = transactionDTO;
    }

    //endregion
}
