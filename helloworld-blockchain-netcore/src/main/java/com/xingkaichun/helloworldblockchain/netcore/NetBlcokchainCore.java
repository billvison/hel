package com.xingkaichun.helloworldblockchain.netcore;

import com.xingkaichun.helloworldblockchain.core.BlockChainCore;
import com.xingkaichun.helloworldblockchain.netcore.netserver.HttpServer;
import com.xingkaichun.helloworldblockchain.netcore.timer.BlockchainBranchHandler;
import com.xingkaichun.helloworldblockchain.netcore.timer.TimerService;

public class NetBlcokchainCore {

    private BlockChainCore blockChainCore;
    private TimerService timerService;
    private BlockchainBranchHandler blockchainBranchHandler;

    public NetBlcokchainCore(BlockChainCore blockChainCore,TimerService timerService,BlockchainBranchHandler blockchainBranchHandler) {
        this.blockChainCore = blockChainCore;
        this.timerService = timerService;
        this.blockchainBranchHandler = blockchainBranchHandler;
    }

    public void start() throws Exception {

        //同步区块链网络中的节点、区块
        timerService.startThread();
        //处理分叉
        blockchainBranchHandler.startThread();

        new Thread(()->{
            try {
                blockChainCore.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(){
            @Override
            public void run() {
                try {
                    new HttpServer().main(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
