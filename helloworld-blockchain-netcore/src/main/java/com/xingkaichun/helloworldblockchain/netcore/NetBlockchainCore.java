package com.xingkaichun.helloworldblockchain.netcore;

import com.xingkaichun.helloworldblockchain.core.BlockChainCore;
import com.xingkaichun.helloworldblockchain.netcore.netserver.HttpServer;
import com.xingkaichun.helloworldblockchain.netcore.daemonservice.BlockchainBranchDaemonService;
import com.xingkaichun.helloworldblockchain.netcore.daemonservice.AutomaticDaemonService;

public class NetBlockchainCore {

    private BlockChainCore blockChainCore;
    private AutomaticDaemonService automaticDaemonService;
    private BlockchainBranchDaemonService blockchainBranchDaemonService;
    private HttpServer httpServer;

    public NetBlockchainCore(BlockChainCore blockChainCore, AutomaticDaemonService automaticDaemonService, BlockchainBranchDaemonService blockchainBranchDaemonService, HttpServer httpServer) {
        this.blockChainCore = blockChainCore;
        this.automaticDaemonService = automaticDaemonService;
        this.blockchainBranchDaemonService = blockchainBranchDaemonService;
        this.httpServer = httpServer;
    }

    public void start() throws Exception {
        //启动本地的单机区块链
        blockChainCore.start();
        //启动区块链节点服务器
        httpServer.start();
        //同步区块链网络中的节点、区块
        automaticDaemonService.start();
        //处理本地区块链的分叉
        blockchainBranchDaemonService.start();
    }




    public BlockChainCore getBlockChainCore() {
        return blockChainCore;
    }

    public void setBlockChainCore(BlockChainCore blockChainCore) {
        this.blockChainCore = blockChainCore;
    }

    public AutomaticDaemonService getAutomaticDaemonService() {
        return automaticDaemonService;
    }

    public void setAutomaticDaemonService(AutomaticDaemonService automaticDaemonService) {
        this.automaticDaemonService = automaticDaemonService;
    }

    public BlockchainBranchDaemonService getBlockchainBranchDaemonService() {
        return blockchainBranchDaemonService;
    }

    public void setBlockchainBranchDaemonService(BlockchainBranchDaemonService blockchainBranchDaemonService) {
        this.blockchainBranchDaemonService = blockchainBranchDaemonService;
    }

    public HttpServer getHttpServer() {
        return httpServer;
    }

    public void setHttpServer(HttpServer httpServer) {
        this.httpServer = httpServer;
    }
}
