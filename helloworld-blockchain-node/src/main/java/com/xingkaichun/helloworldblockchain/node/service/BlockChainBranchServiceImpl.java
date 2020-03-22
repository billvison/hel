package com.xingkaichun.helloworldblockchain.node.service;

import com.xingkaichun.helloworldblockchain.dto.BlockDTO;
import com.xingkaichun.helloworldblockchain.node.dao.BlockChainBranchDao;
import com.xingkaichun.helloworldblockchain.node.dto.blockchainbranch.BlockchainBranchBlockDto;
import com.xingkaichun.helloworldblockchain.node.dto.blockchainbranch.InitBlockHash;
import com.xingkaichun.helloworldblockchain.node.model.BlockchainBranchBlockEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class BlockChainBranchServiceImpl implements BlockChainBranchService {

    @Autowired
    private BlockChainBranchDao blockChainBranchDao;
    @Autowired
    private BlockChainCoreService blockChainCoreService;

    @Override
    public boolean isConfirmBlockchainBranch() {
        List<BlockchainBranchBlockEntity> blockchainBranchBlockEntityList = blockChainBranchDao.queryAllBlockchainBranchBlock();
        if(blockchainBranchBlockEntityList == null || blockchainBranchBlockEntityList.size()==0){
            return false;
        }
        return true;
    }

    @Transactional
    @Override
    public void update(InitBlockHash initBlockHash) {
        blockChainBranchDao.removeAll();
        List<BlockchainBranchBlockDto> blockList = initBlockHash.getBlockList();
        for(BlockchainBranchBlockDto blockchainBranchBlockDto:blockList){
            BlockchainBranchBlockEntity entity = classCast(blockchainBranchBlockDto);
            blockChainBranchDao.add(entity);
        }
    }

    @Override
    public void checkBlockchainBranch() throws Exception {
        List<BlockchainBranchBlockEntity> blockchainBranchBlockEntityList = blockChainBranchDao.queryAllBlockchainBranchBlock();
        if(blockchainBranchBlockEntityList == null || blockchainBranchBlockEntityList.size()==0){
            return;
        }
        for(BlockchainBranchBlockEntity entity:blockchainBranchBlockEntityList){
            BlockDTO blockDTO = blockChainCoreService.queryBlockDtoByBlockHeight(entity.getBlockHeight());
            if(blockDTO == null){
                return;
            }
            if(entity.getBlockHash().equals(blockDTO.getHash()) && entity.getBlockHeight()==blockDTO.getHeight()){
                return;
            }
            while(true){
                int blockChainHeight = blockChainCoreService.queryBlockChainHeight();
                if(blockChainHeight<=0 || blockChainHeight < entity.getBlockHeight()){
                    break;
                }
            }
        }
    }

    @Override
    public List<BlockchainBranchBlockDto> queryBlockchainBranch() {
        List<BlockchainBranchBlockEntity> blockchainBranchBlockEntityList = blockChainBranchDao.queryAllBlockchainBranchBlock();
        if(blockchainBranchBlockEntityList == null || blockchainBranchBlockEntityList.size()==0){
            return null;
        }
        return classCast(blockchainBranchBlockEntityList);
    }

    @Transactional
    @Override
    public void updateBranchchainBranch(List<BlockchainBranchBlockDto> blockList) {
        blockChainBranchDao.removeAll();
        for(BlockchainBranchBlockDto blockchainBranchBlockDto:blockList){
            BlockchainBranchBlockEntity entity = classCast(blockchainBranchBlockDto);
            blockChainBranchDao.add(entity);
        }
    }

    private List<BlockchainBranchBlockDto> classCast(List<BlockchainBranchBlockEntity> blockchainBranchBlockEntityList) {
        if(blockchainBranchBlockEntityList == null || blockchainBranchBlockEntityList.size()==0){
            return null;
        }
        List<BlockchainBranchBlockDto> dtoList = new ArrayList<>();
        for(BlockchainBranchBlockEntity entity:blockchainBranchBlockEntityList){
            dtoList.add(classCast(entity));
        }
        return dtoList;
    }

    private BlockchainBranchBlockDto classCast(BlockchainBranchBlockEntity entity) {
        BlockchainBranchBlockDto dto = new BlockchainBranchBlockDto();
        dto.setBlockHash(entity.getBlockHash());
        dto.setBlockHeight(entity.getBlockHeight());
        return dto;
    }

    private BlockchainBranchBlockEntity classCast(BlockchainBranchBlockDto dto) {
        BlockchainBranchBlockEntity entity = new BlockchainBranchBlockEntity();
        entity.setBlockHash(dto.getBlockHash());
        entity.setBlockHeight(dto.getBlockHeight());
        return entity;
    }
}
