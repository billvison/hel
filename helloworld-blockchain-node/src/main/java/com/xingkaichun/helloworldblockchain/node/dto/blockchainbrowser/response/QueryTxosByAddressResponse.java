package com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.response;

import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;

import java.util.List;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class QueryTxosByAddressResponse {

    private List<TransactionOutput> txos;




    //region get set

    public List<TransactionOutput> getTxos() {
        return txos;
    }

    public void setTxos(List<TransactionOutput> txos) {
        this.txos = txos;
    }

    //endregion
}
