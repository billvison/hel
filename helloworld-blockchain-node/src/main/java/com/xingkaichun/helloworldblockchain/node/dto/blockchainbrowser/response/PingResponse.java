package com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.response;

import com.xingkaichun.helloworldblockchain.node.dto.nodeserver.Node;
import lombok.Data;

import java.math.BigInteger;
import java.util.List;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
@Data
public class PingResponse {

    private BigInteger blockChainHeight ;
    private List<Node> nodeList;
}
