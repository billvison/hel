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
     * 达成共识了吗？
     * //TODO model不存放方法
     */
    public abstract boolean isReachConsensus() throws Exception;
}
