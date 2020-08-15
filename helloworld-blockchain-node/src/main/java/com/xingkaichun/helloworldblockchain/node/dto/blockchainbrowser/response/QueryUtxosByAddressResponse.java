package com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.response;

import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;

import java.util.List;

/**
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class QueryUtxosByAddressResponse {

    private List<TransactionOutput> utxos;




    //region get set

    public List<TransactionOutput> getUtxos() {
        return utxos;
    }

    public void setUtxos(List<TransactionOutput> utxos) {
        this.utxos = utxos;
    }

    //endregion
}
