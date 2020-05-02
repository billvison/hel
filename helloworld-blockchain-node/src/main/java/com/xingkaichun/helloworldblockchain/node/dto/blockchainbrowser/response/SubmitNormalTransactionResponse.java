package com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.response;

import com.xingkaichun.helloworldblockchain.node.transport.dto.TransactionDTO;
import lombok.Data;

import java.util.List;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
@Data
public class SubmitNormalTransactionResponse {

    //经过处理后的交易
    private TransactionDTO transactionDTO;

    //交易成功提交的节点
    private List<Node> successSubmitNode;

    //交易提交失败的节点
    private List<Node> failSubmitNode;

    @Data
    public static class Node{
        private String ip;
        private int port;

        public Node() {
        }

        public Node(String ip, int port) {
            this.ip = ip;
            this.port = port;
        }
    }
}
