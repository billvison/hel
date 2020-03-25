package com.xingkaichun.helloworldblockchain.node.timer;

import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xingkaichun.helloworldblockchain.node.dto.blockchainbranch.BlockchainBranchBlockDto;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 分支处理
 */
public class BlockchainBranchHandler {

    private static final Logger logger = LoggerFactory.getLogger(BlockchainBranchHandler.class);

    @Autowired
    private BlockChainBranchService blockChainBranchService;

    @Autowired
    private Gson gson;

    private Map<String,String> blockHeightBlockHashMap = new HashMap<>();

    @PostConstruct
    private void startThread() throws IOException {

        if(!blockChainBranchService.isConfirmBlockchainBranch()){
            InputStream inputStream = ClassLoader.getSystemResourceAsStream("InitBlockHash.txt");
            String context = CharStreams.toString(new InputStreamReader(inputStream, "UTF-8"));
            Type jsonType = new TypeToken<InitBlockHash>() {}.getType();
            InitBlockHash initBlockHash = gson.fromJson(context,jsonType);
            blockChainBranchService.update(initBlockHash);
        }

        List<BlockchainBranchBlockDto> blockchainBranchBlockDtoList = blockChainBranchService.queryBlockchainBranch();
        if(blockchainBranchBlockDtoList != null){
            for(BlockchainBranchBlockDto blockchainBranchBlockDto:blockchainBranchBlockDtoList){
                blockHeightBlockHashMap.put(String.valueOf(blockchainBranchBlockDto.getBlockHeight()),blockchainBranchBlockDto.getBlockHash());
            }
        }

        new Thread(()->{
            while (true){
                try {
                    blockChainBranchService.checkBlockchainBranch();
                } catch (Exception e) {
                    logger.error("在区块链网络中搜索新的节点出现异常",e);
                }
                try {
                    Thread.sleep(10*60*1000);
                } catch (InterruptedException e) {
                }
            }
        }).start();
    }

    public boolean isFork(int blockHeight,String blockHash){
        String stringBlockHeight = String.valueOf(blockHeight);
        String blockHashTemp = blockHeightBlockHashMap.get(stringBlockHeight);
        if(blockHashTemp == null){
            return false;
        }
        return !blockHashTemp.equals(blockHash);
    }

    public int getNearBlockHeight(int blockHeight){
        int nearBlockHeight = 0;
        Set<String> set = blockHeightBlockHashMap.keySet();
        for(String stringBlockHeight:set){
            int intBlockHeight = Integer.valueOf(stringBlockHeight);
            if(intBlockHeight < blockHeight && intBlockHeight > nearBlockHeight){
                nearBlockHeight = intBlockHeight;
            }
        }
        return nearBlockHeight;
    }
}
