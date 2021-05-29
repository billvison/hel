package com.xingkaichun.helloworldblockchain.core.impl;

import com.xingkaichun.helloworldblockchain.core.*;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.tools.BlockTool;
import com.xingkaichun.helloworldblockchain.core.tools.MinerTool;
import com.xingkaichun.helloworldblockchain.crypto.HexUtil;
import com.xingkaichun.helloworldblockchain.crypto.RandomUtil;
import com.xingkaichun.helloworldblockchain.crypto.model.Account;
import com.xingkaichun.helloworldblockchain.util.LogUtil;
import com.xingkaichun.helloworldblockchain.util.SleepUtil;
import com.xingkaichun.helloworldblockchain.util.TimeUtil;

/**
 * 默认实现
 *
 * @author 邢开春 409060350@qq.com
 */
public class MinerDefaultImpl extends Miner {

    //region 属性与构造函数
    public MinerDefaultImpl(CoreConfiguration coreConfiguration, Wallet wallet, BlockchainDatabase blockchainDataBase, UnconfirmedTransactionDatabase unconfirmedTransactionDataBase) {
        super(coreConfiguration, wallet, blockchainDataBase, unconfirmedTransactionDataBase);
    }
    //endregion


    @Override
    public void start() {
        while(true){
            SleepUtil.sleep(10);
            if(!isActive()){
                continue;
            }
            Account minerAccount = wallet.createAccount();
            Block block = MinerTool.buildMiningBlock(blockchainDataBase,unconfirmedTransactionDataBase,minerAccount);
            long startTimestamp = TimeUtil.currentTimeMillis();
            while(true){
                if(!isActive()){
                    break;
                }
                //在挖矿的期间，可能收集到新的交易。每隔一定的时间，重新组装挖矿中的区块，这样新收集到交易就可以被放进挖矿中的区块了。
                if(TimeUtil.currentTimeMillis()-startTimestamp > coreConfiguration.getMinerMineIntervalTimestamp()){
                    break;
                }
                //随机数
                block.setNonce(HexUtil.bytesToHexString(RandomUtil.random32Bytes()));
                block.setHash(BlockTool.calculateBlockHash(block));
                //挖矿成功
                if(blockchainDataBase.getConsensus().isReachConsensus(blockchainDataBase,block)){
                    //将账户放入钱包
                    wallet.saveAccount(minerAccount);
                    LogUtil.debug("祝贺您！挖矿成功！！！区块高度:"+block.getHeight()+",区块哈希:"+block.getHash());
                    //将矿放入区块链
                    boolean isAddBlockToBlockchainSuccess = blockchainDataBase.addBlock(block);
                    if(!isAddBlockToBlockchainSuccess){
                        LogUtil.debug("挖矿成功，但是区块放入区块链失败。");
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void deactive() {
        coreConfiguration.deactiveMiner();
    }

    @Override
    public void active() {
        coreConfiguration.activeMiner();
    }

    @Override
    public boolean isActive() {
        return coreConfiguration.isMinerActive();
    }

}
