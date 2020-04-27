package com.xingkaichun.helloworldblockchain.core;

import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.ConsensusTarget;
import lombok.Data;

import java.io.Serializable;

/**
 * 挖矿共识
 * 区块链是一个分布式的数据库。任何节点都可以产生下一个区块，如果同时有多个节点都产生了下一个区块，
 * 以哪个节点产生的区块为准？
 * 理想状态下，我们希望整个区块链网络下一个区块只产生一个，这样就不存在以哪个为准的问题了。
 * 因此，我们应当控制区块的产生，
 * 因此节点之间应当达成一个共识：下一个区块应当是什么样的？这样，下一个区块不是随意生成的了。
 * 当然，即使有了区块产生的共识，也有可能多个节点都产生了下一个区块(有了共识，产生的下一个区块少了很多很多)。
 * 这个问题，就让他们继续竞争下去，看谁能产生下下个区块。
 */
@Data
public abstract class Consensus implements Serializable {

    /**
     * 共识目标
     * @param blockChainDataBase 区块链
     * @param block              目标区块
     */
    public abstract ConsensusTarget calculateConsensusTarget(BlockChainDataBase blockChainDataBase, Block block) throws Exception;
}

