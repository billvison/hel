package com.xingkaichun.blockchain.core;

import com.xingkaichun.blockchain.core.model.Block;
import com.xingkaichun.blockchain.core.utils.BlockUtils;

/**
 * 工作量证明
 */
public class ProofOfWorkConsensus implements Consensus{

    @Override
    public boolean isReachConsensus(BlockChainDataBase blockChainDataBase, Block block) {
        //校验区块写入的MerkleRoot是否正确
        if(!BlockUtils.isBlockWriteMerkleRootRight(block)){
            return false;
        }
        //校验区块写入的挖矿是否正确
        String hash = BlockUtils.calculateBlockHash(block);
        if(!hash.equals(block.getHash())){
            return false;
        }
        //校验挖矿是否正确
        String difficulty = difficulty(blockChainDataBase,block);
        return isHashRight(difficulty,hash);
    }

    @Override
    public String difficulty(BlockChainDataBase blockChainDataBase, Block block){
        return "0000";
    }


    //region 挖矿Hash相关
    /**
     * Hash满足挖矿难度的要求吗？
     * @param targetDificulty 目标挖矿难度
     * @param hash 需要校验的Hash
     */
    //TODO 能否改为nonce是否正确
    @Override
    public boolean isHashRight(String targetDificulty,String hash){
        return hash.startsWith(targetDificulty);
    }
    //endregion
}
