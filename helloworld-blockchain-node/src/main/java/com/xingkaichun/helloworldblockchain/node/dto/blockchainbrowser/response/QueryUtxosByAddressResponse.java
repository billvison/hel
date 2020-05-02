package com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.response;

import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import lombok.Data;

import java.util.List;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
@Data
public class QueryUtxosByAddressResponse {

    private List<TransactionOutput> utxos;
}
