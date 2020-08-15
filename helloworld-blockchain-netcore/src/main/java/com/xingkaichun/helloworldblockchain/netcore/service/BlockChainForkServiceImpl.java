package com.xingkaichun.helloworldblockchain.netcore.service;

import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.utils.BigIntegerUtil;
import com.xingkaichun.helloworldblockchain.netcore.dao.BlockChainForkDao;
import com.xingkaichun.helloworldblockchain.netcore.dto.fork.BlockchainForkBlockDto;
import com.xingkaichun.helloworldblockchain.netcore.model.BlockchainForkBlockEntity;

import java.math.BigInteger;
import java.util.*;

/**
 * 区块链分支维护
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class BlockChainForkServiceImpl implements BlockChainForkService {

    private BlockChainForkDao blockChainForkDao;
    private BlockChainCoreService blockChainCoreService;


    private Map<String,String> blockHeightBlockHashMap = new HashMap<>();
    private boolean isRefreshCache = false;

    public BlockChainForkServiceImpl(BlockChainForkDao blockChainForkDao, BlockChainCoreService blockChainCoreService) {
        this.blockChainForkDao = blockChainForkDao;
        this.blockChainCoreService = blockChainCoreService;
    }

    /**
     * 将分支加载到内存
     */
    private void refreshCache() {
        List<BlockchainForkBlockEntity> blockchainForkBlockEntityList = blockChainForkDao.queryAllBlockchainForkBlock();
        if(blockchainForkBlockEntityList != null){
            for(BlockchainForkBlockEntity entity: blockchainForkBlockEntityList){
                blockHeightBlockHashMap.put(String.valueOf(entity.getBlockHeight()),entity.getBlockHash());
            }
        }
        isRefreshCache = true;
    }

    @Override
    public boolean isFork(BigInteger blockHeight,String blockHash){
        String stringBlockHeight = String.valueOf(blockHeight);
        String blockHashTemp = blockHeightBlockHashMap.get(stringBlockHeight);
        if(blockHashTemp == null){
            return false;
        }
        return !blockHashTemp.equals(blockHash);
    }

    @Override
    public BigInteger getFixBlockHashMaxBlockHeight(BigInteger blockHeight){
        BigInteger nearBlockHeight = BigInteger.ZERO;
        Set<String> set = blockHeightBlockHashMap.keySet();
        for(String stringBlockHeight:set){
            BigInteger intBlockHeight = new BigInteger(stringBlockHeight);
            if(BigIntegerUtil.isLessThan(intBlockHeight,blockHeight)  && BigIntegerUtil.isGreatThan(intBlockHeight,nearBlockHeight)){
                nearBlockHeight = intBlockHeight;
            }
        }
        return nearBlockHeight;
    }

    @Override
    public void blockchainForkHandler() {
        if(!isRefreshCache){
            refreshCache();
        }
        List<BlockchainForkBlockEntity> blockchainForkBlockEntityList = blockChainForkDao.queryAllBlockchainForkBlock();
        if(blockchainForkBlockEntityList == null || blockchainForkBlockEntityList.size()==0){
            return;
        }
        blockchainForkBlockEntityList.sort(Comparator.comparing(BlockchainForkBlockEntity::getBlockHeight));
        for(int i = 0; i< blockchainForkBlockEntityList.size(); i++){
            BlockchainForkBlockEntity entity = blockchainForkBlockEntityList.get(i);
            Block block = blockChainCoreService.queryNoTransactionBlockDtoByBlockHeight(entity.getBlockHeight());
            if(block == null){
                return;
            }
            if(entity.getBlockHash().equals(block.getHash()) && BigIntegerUtil.isEquals(entity.getBlockHeight(),block.getHeight())){
                continue;
            }
            BigInteger deleteBlockHeight;
            if(i==0){
                deleteBlockHeight = BigInteger.ONE;
            }else {
                deleteBlockHeight = blockchainForkBlockEntityList.get(i-1).getBlockHeight().add(BigInteger.ONE);
            }
            blockChainCoreService.removeBlocksUtilBlockHeightLessThan(deleteBlockHeight);
            return;
        }
    }

    @Override
    public List<BlockchainForkBlockDto> queryBlockchainFork() {
        List<BlockchainForkBlockEntity> blockchainForkBlockEntityList = blockChainForkDao.queryAllBlockchainForkBlock();
        if(blockchainForkBlockEntityList == null || blockchainForkBlockEntityList.size()==0){
            return null;
        }
        return classCast(blockchainForkBlockEntityList);
    }

    @Override
    public void updateBlockchainFork(List<BlockchainForkBlockDto> blockList) {
        List<BlockchainForkBlockEntity> entityList = new ArrayList<>();
        if(blockList != null){
            for(BlockchainForkBlockDto blockchainForkBlockDto :blockList){
                BlockchainForkBlockEntity entity = classCast(blockchainForkBlockDto);
                entityList.add(entity);
            }
        }
        blockChainForkDao.updateBlockchainFork(entityList);
        refreshCache();
        blockchainForkHandler();
    }

    private List<BlockchainForkBlockDto> classCast(List<BlockchainForkBlockEntity> blockchainForkBlockEntityList) {
        if(blockchainForkBlockEntityList == null || blockchainForkBlockEntityList.size()==0){
            return null;
        }
        List<BlockchainForkBlockDto> dtoList = new ArrayList<>();
        for(BlockchainForkBlockEntity entity: blockchainForkBlockEntityList){
            dtoList.add(classCast(entity));
        }
        return dtoList;
    }

    private BlockchainForkBlockDto classCast(BlockchainForkBlockEntity entity) {
        BlockchainForkBlockDto dto = new BlockchainForkBlockDto();
        dto.setBlockHash(entity.getBlockHash());
        dto.setBlockHeight(entity.getBlockHeight());
        return dto;
    }

    private BlockchainForkBlockEntity classCast(BlockchainForkBlockDto dto) {
        BlockchainForkBlockEntity entity = new BlockchainForkBlockEntity();
        entity.setBlockHash(dto.getBlockHash());
        entity.setBlockHeight(dto.getBlockHeight());
        return entity;
    }
}
