package com.xingkaichun.blockchain.core.impl;

import com.xingkaichun.blockchain.core.BlockChainCore;
import com.xingkaichun.blockchain.core.listen.BlockChainActionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class BlockChainCoreImpl extends BlockChainCore {

    private Logger logger = LoggerFactory.getLogger(BlockChainCoreImpl.class);

    public BlockChainCoreImpl(List<BlockChainActionListener> blockChainActionListenerList) throws Exception {
        this.blockChainActionListenerList = blockChainActionListenerList;
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