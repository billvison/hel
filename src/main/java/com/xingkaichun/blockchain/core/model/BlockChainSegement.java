package com.xingkaichun.blockchain.core.model;

import lombok.Data;

import java.util.List;

/**
 * 区块链上的一段连续的block。
 */
@Data
public class BlockChainSegement {

    private List<Block> blockList;

}
