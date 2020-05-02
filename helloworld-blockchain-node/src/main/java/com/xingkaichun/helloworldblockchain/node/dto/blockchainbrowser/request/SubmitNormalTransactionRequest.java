package com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.request;

import com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.NormalTransactionDto;
import lombok.Data;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
@Data
public class SubmitNormalTransactionRequest {

    private NormalTransactionDto normalTransactionDto;
}
