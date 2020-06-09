package com.xingkaichun.helloworldblockchain.netcore.dto.blockchainbrowser.response;

import com.xingkaichun.helloworldblockchain.node.transport.dto.TransactionDTO;

import java.util.List;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class SubmitNormalTransactionResponse {

    //交易Hash
    private String transactionHash;

    //经过处理后的交易
    private TransactionDTO transactionDTO;

    //交易成功提交的节点
    private List<Node> successSubmitNode;

    //交易提交失败的节点
    private List<Node> failSubmitNode;

    public static class Node{
        private String ip;
        private int port;

        public Node() {
        }

        public Node(String ip, int port) {
            this.ip = ip;
            this.port = port;
        }




        //region get set

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
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

    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }

//endregion
}
