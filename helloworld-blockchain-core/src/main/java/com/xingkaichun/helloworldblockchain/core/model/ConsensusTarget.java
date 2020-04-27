package com.xingkaichun.helloworldblockchain.core.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 挖矿共识目标
 */
@Data
public abstract class ConsensusTarget implements Serializable {

    /**
     * 解释共识的目标
     */
    private String explain;
    /**
     * 这个区块写入的nonce达成共识了吗？
     *
     * @param block 需要被验证是否共识的区块
     */
    public abstract boolean isReachConsensus(Block block) throws Exception;
}
