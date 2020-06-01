package com.xingkaichun.helloworldblockchain.core.impl;

import com.xingkaichun.helloworldblockchain.core.BlockChainCore;
import com.xingkaichun.helloworldblockchain.core.BlockChainDataBase;
import com.xingkaichun.helloworldblockchain.core.Miner;
import com.xingkaichun.helloworldblockchain.core.Synchronizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 默认实现
 * 
 * @author 邢开春 xingkaichun@qq.com
 */
public class BlockChainCoreImpl extends BlockChainCore {

    private Logger logger = LoggerFactory.getLogger(BlockChainCoreImpl.class);

    public BlockChainCoreImpl(BlockChainDataBase blockChainDataBase, Miner miner, Synchronizer synchronizer) {
        super(blockChainDataBase,miner,synchronizer);
    }

    @Override
    public void start() {
        //启动区块链同步器线程
        new Thread(
                ()->{
                    try {
                        synchronizer.start();
                    } catch (Exception e) {
                        logger.error("区块链同步器在运行中发生异常并退出，请检查修复异常！",e);
                    }
                }
        ).start();
        //启动矿工线程
        new Thread(
                ()->{
                    try {
                        miner.start();
                    } catch (Exception e) {
                        logger.error("矿工在运行中发生异常并退出，请检查修复异常！",e);
                    }
                }
        ).start();
    }
}