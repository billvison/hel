package com.xingkaichun.helloworldblockchain.core;

import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.ConsensusVariableHolder;

import java.io.Serializable;

/**
 * 挖矿共识
 * 区块链是一个分布式的数据库。任何节点都可以产生下一个区块，如果同时有多个节点都产生了下一个区块，
 * 以哪个节点产生的区块为准？
 * 理想状态下，我们希望整个区块链网络下一个区块只产生一个，这样就不存在以哪个为准的问题了。
 * 因此，我们应当控制区块产生的难度，使得一个时间间隔内最好只产生一个区块。
 * 区块再也不是随意产生的了，只有满足一定条件的区块才能被认定为下一个区块。
 * 而这个满足的条件就是节点间需要共识的，如果区块满足了这些条件，这个区块也就达到了所有节点的共识，代表它是一个合格的区块。
 * 当然，即使有了区块产生的共识，也有可能多个节点同时都产生了下一个区块(有了共识，产生的下一个区块只是少了很多很多)。
 * 这个问题，就让他们继续竞争下去，看谁能产生下下个区块。
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public abstract class Consensus implements Serializable {

    /**
     * 区块满足共识的要求了吗？
     * 如果满足共识的要求，这个区块就可以添加进区块链，否则，不可以。
     */
    public abstract boolean isReachConsensus(BlockChainDataBase blockChainDataBase, Block block) throws Exception;

    /**
     * 计算共识的中间变量持有者
     * 在做共识计算时，可能会产生很多的中间变量，如果每次都重新计算一次，比较浪费算力，
     * 这里的设计是将计算好的中间变量保存到这个持有者中，下次计算共识，直接从持有者中获取中间变量。
     */
    public abstract ConsensusVariableHolder calculateConsensusVariableHolder(BlockChainDataBase blockChainDataBase, Block block) throws Exception;
}

