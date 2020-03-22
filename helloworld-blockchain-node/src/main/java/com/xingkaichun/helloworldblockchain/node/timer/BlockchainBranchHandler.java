package com.xingkaichun.helloworldblockchain.node.timer;

import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xingkaichun.helloworldblockchain.node.dto.blockchainbranch.InitBlockHash;
import com.xingkaichun.helloworldblockchain.node.service.BlockChainBranchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

/**
 * 分支处理
 */
public class BlockchainBranchHandler {

    private static final Logger logger = LoggerFactory.getLogger(BlockchainBranchHandler.class);

    @Autowired
    private BlockChainBranchService blockChainBranchService;

    @Autowired
    private Gson gson;


    @PostConstruct
    private void startThread() throws IOException {

        if(!blockChainBranchService.isConfirmBlockchainBranch()){
            InputStream inputStream = ClassLoader.getSystemResourceAsStream("InitUser.txt");
            String context = CharStreams.toString(new InputStreamReader(inputStream, "UTF-8"));
            Type jsonType = new TypeToken<InitBlockHash>() {}.getType();
            InitBlockHash initBlockHash = gson.fromJson(context,jsonType);
            blockChainBranchService.update(initBlockHash);
        }

        new Thread(()->{
            while (true){
                try {
                    blockChainBranchService.checkBlockchainBranch();
                } catch (Exception e) {
                    logger.error("在区块链网络中搜索新的节点出现异常",e);
                }
                try {
                    Thread.sleep(3600*1000);
                } catch (InterruptedException e) {
                }
            }
        }).start();
    }
}
