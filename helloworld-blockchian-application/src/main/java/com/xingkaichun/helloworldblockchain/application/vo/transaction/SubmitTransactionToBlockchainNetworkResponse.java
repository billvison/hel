package com.xingkaichun.helloworldblockchain.application.vo.transaction;

import com.xingkaichun.helloworldblockchain.netcore.transport.dto.TransactionDTO;

import java.util.List;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class SubmitTransactionToBlockchainNetworkResponse {

    //交易
    private TransactionDTO transactionDTO;

    //交易成功提交的节点
    private List<Node> successSubmitNode;

    //交易提交失败的节点
    private List<Node> failSubmitNode;

    public static class Node{
        private String ip;

        public Node() {
        }

        public Node(String ip) {
            this.ip = ip;
        }

        //region get set

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        //endregion
    }




    //region get set

    public TransactionDTO getTransactionDTO() {
        return transactionDTO;
    }

    public void setTransactionDTO(TransactionDTO transactionDTO) {
        this.transactionDTO = transactionDTO;
    }

    public List<Node> getSuccessSubmitNode() {
        return successSubmitNode;
    }

    public void setSuccessSubmitNode(List<Node> successSubmitNode) {
        this.successSubmitNode = successSubmitNode;
    }

    public List<Node> getFailSubmitNode() {
        return failSubmitNode;
    }

    public void setFailSubmitNode(List<Node> failSubmitNode) {
        this.failSubmitNode = failSubmitNode;
    }

    //endregion
}
