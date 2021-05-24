package com.xingkaichun.helloworldblockchain.application.service;

import com.xingkaichun.helloworldblockchain.application.vo.block.BlockVo;
import com.xingkaichun.helloworldblockchain.application.vo.transaction.*;
import com.xingkaichun.helloworldblockchain.core.BlockchainCore;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.transaction.*;
import com.xingkaichun.helloworldblockchain.core.tools.*;
import com.xingkaichun.helloworldblockchain.netcore.BlockchainNetCore;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.TransactionDTO;
import com.xingkaichun.helloworldblockchain.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
@Service
public class BlockchainBrowserApplicationServiceImpl implements BlockchainBrowserApplicationService {

    @Autowired
    private BlockchainNetCore blockchainNetCore;

    @Override
    public TransactionOutputDetailVo queryTransactionOutputByTransactionOutputId(TransactionOutputId transactionOutputId) {
        //查询交易输出
        TransactionOutput transactionOutput = getBlockchainCore().getBlockchainDataBase().queryTransactionOutputByTransactionOutputId(transactionOutputId);
        if(transactionOutput == null){
            return null;
        }

        TransactionOutputDetailVo transactionOutputDetailVo = new TransactionOutputDetailVo();
        transactionOutputDetailVo.setFromBlockHeight(transactionOutput.getBlockHeight());
        transactionOutputDetailVo.setFromBlockHash(transactionOutput.getBlockHash());
        transactionOutputDetailVo.setFromTransactionHash(transactionOutput.getTransactionHash());
        transactionOutputDetailVo.setValue(transactionOutput.getValue());
        transactionOutputDetailVo.setFromOutputScript(ScriptTool.toString(transactionOutput.getOutputScript()));
        transactionOutputDetailVo.setFromTransactionOutputIndex(transactionOutput.getTransactionOutputIndex());
        transactionOutputId.setTransactionHash(transactionOutput.getTransactionHash());
        transactionOutputId.setTransactionOutputIndex(transactionOutput.getTransactionOutputIndex());

        //是否是未花费输出
        TransactionOutput transactionOutputTemp = getBlockchainCore().getBlockchainDataBase().queryUnspentTransactionOutputByTransactionOutputId(transactionOutputId);
        transactionOutputDetailVo.setSpent(transactionOutputTemp==null);

        //来源
        TransactionVo inputTransactionVo = queryTransactionByTransactionHash(transactionOutputId.getTransactionHash());
        transactionOutputDetailVo.setInputTransaction(inputTransactionVo);
        transactionOutputDetailVo.setTransactionType(inputTransactionVo.getTransactionType());


        //去向
        TransactionVo outputTransactionVo;
        if(transactionOutputTemp == null){
            Transaction destinationTransaction = getBlockchainCore().getBlockchainDataBase().queryDestinationTransactionByTransactionOutputId(transactionOutputId);
            outputTransactionVo = queryTransactionByTransactionHash(destinationTransaction.getTransactionHash());

            Transaction outputTransaction = getBlockchainCore().getBlockchainDataBase().queryTransactionByTransactionHash(destinationTransaction.getTransactionHash());
            List<TransactionInput> inputs = outputTransaction.getInputs();
            if(inputs != null){
                for(TransactionInput transactionInput : inputs){
                    UnspentTransactionOutput unspentTransactionOutput = transactionInput.getUnspentTransactionOutput();
                    if(transactionOutput.getTransactionHash().equals(unspentTransactionOutput.getTransactionHash()) &&
                            transactionOutput.getTransactionOutputIndex()==unspentTransactionOutput.getTransactionOutputIndex()){
                        transactionOutputDetailVo.setToTransactionInputIndex(outputTransactionVo.getTransactionInputCount());
                        transactionOutputDetailVo.setToInputScript(ScriptTool.toString(transactionInput.getInputScript()));
                        break;
                    }
                }
            }
            transactionOutputDetailVo.setToBlockHeight(outputTransactionVo.getBlockHeight());
            transactionOutputDetailVo.setToBlockHash(outputTransactionVo.getBlockHash());
            transactionOutputDetailVo.setToTransactionHash(outputTransactionVo.getTransactionHash());
            transactionOutputDetailVo.setOutputTransaction(outputTransactionVo);
        }
        return transactionOutputDetailVo;
    }

    @Override
    public TransactionOutputDetailVo queryTransactionOutputByAddress(String address) {
        TransactionOutput transactionOutput = getBlockchainCore().queryTransactionOutputByAddress(address);
        if(transactionOutput == null){
            return null;
        }
        TransactionOutputDetailVo transactionOutputDetailVo = queryTransactionOutputByTransactionOutputId(transactionOutput);
        return transactionOutputDetailVo;
    }

    @Override
    public List<TransactionVo> queryTransactionListByBlockHashTransactionHeight(String blockHash, long from, long size) {
        Block block = getBlockchainCore().queryBlockByBlockHash(blockHash);
        List<TransactionVo> transactionVoList = new ArrayList<>();
        for(long i=from;i<from+size;i++){
            if(from < 0){
                break;
            }
            if(i > block.getTransactionCount()){
                break;
            }
            long transactionHeight = block.getPreviousTransactionHeight() + i;
            Transaction transaction = getBlockchainCore().queryTransactionByTransactionHeight(transactionHeight);
            TransactionVo transactionVo = queryTransactionByTransactionHash(transaction.getTransactionHash());
            transactionVoList.add(transactionVo);
        }
        return transactionVoList;
    }

    @Override
    public BlockVo queryBlockViewByBlockHeight(Long blockHeight) {
        Block block = getBlockchainCore().queryBlockByBlockHeight(blockHeight);
        if(block == null){
            return null;
        }
        Block nextBlock = getBlockchainCore().queryBlockByBlockHeight(block.getHeight()+1);

        BlockVo blockVo = new BlockVo();
        blockVo.setHeight(block.getHeight());
        blockVo.setConfirmCount(BlockTool.getTransactionCount(block));
        blockVo.setBlockSize(SizeTool.calculateBlockSize(block)+"字符");
        blockVo.setTransactionCount(BlockTool.getTransactionCount(block));
        blockVo.setTime(TimeUtil.timestamp2FormatDate(block.getTimestamp()));
        blockVo.setMinerIncentiveValue(BlockTool.getMinerIncentiveValue(block));
        blockVo.setDifficulty(BlockTool.formatDifficulty(block.getDifficulty()));
        blockVo.setNonce(block.getNonce());
        blockVo.setHash(block.getHash());
        blockVo.setPreviousBlockHash(block.getPreviousBlockHash());
        blockVo.setNextBlockHash(nextBlock==null?null:nextBlock.getHash());
        blockVo.setMerkleTreeRoot(block.getMerkleTreeRoot());
        return blockVo;
    }

    @Override
    public MiningTransactionVo queryMiningTransactionByTransactionHash(String transactionHash) {
        TransactionDTO transactionDTO = getBlockchainCore().queryMiningTransactionDtoByTransactionHash(transactionHash);
        if(transactionDTO == null){
            return null;
        }

        Transaction transaction = Dto2ModelTool.transactionDto2Transaction(getBlockchainCore().getBlockchainDataBase(),transactionDTO);
        MiningTransactionVo transactionDtoResp = new MiningTransactionVo();
        transactionDtoResp.setTransactionHash(transaction.getTransactionHash());

        List<MiningTransactionVo.TransactionInputDto> inputDtos = new ArrayList<>();
        List<TransactionInput> inputs = transaction.getInputs();
        if(inputs != null){
            for(TransactionInput input:inputs){
                MiningTransactionVo.TransactionInputDto transactionInputDto = new MiningTransactionVo.TransactionInputDto();
                transactionInputDto.setAddress(input.getUnspentTransactionOutput().getAddress());
                transactionInputDto.setTransactionHash(input.getUnspentTransactionOutput().getTransactionHash());
                transactionInputDto.setTransactionOutputIndex(input.getUnspentTransactionOutput().getTransactionOutputIndex());
                transactionInputDto.setValue(input.getUnspentTransactionOutput().getValue());
                inputDtos.add(transactionInputDto);
            }
        }
        transactionDtoResp.setInputs(inputDtos);

        List<MiningTransactionVo.TransactionOutputDto> outputDtos = new ArrayList<>();
        List<TransactionOutput> outputs = transaction.getOutputs();
        if(outputs != null){
            for(TransactionOutput output:outputs){
                MiningTransactionVo.TransactionOutputDto transactionOutputDto = new MiningTransactionVo.TransactionOutputDto();
                transactionOutputDto.setAddress(output.getAddress());
                transactionOutputDto.setValue(output.getValue());
                outputDtos.add(transactionOutputDto);
            }
        }
        transactionDtoResp.setOutputs(outputDtos);
        return transactionDtoResp;
    }


    @Override
    public TransactionVo queryTransactionByTransactionHash(String transactionHash) {
        Transaction transaction = getBlockchainCore().queryTransactionByTransactionHash(transactionHash);
        if(transaction == null){
            return null;
        }

        TransactionVo transactionVo = new TransactionVo();
        transactionVo.setTransactionHash(transaction.getTransactionHash());
        transactionVo.setBlockHeight(transaction.getBlockHeight());

        transactionVo.setTransactionFee(TransactionTool.calculateTransactionFee(transaction));
        transactionVo.setTransactionType(transaction.getTransactionType().name());
        transactionVo.setTransactionInputCount(TransactionTool.getTransactionInputCount(transaction));
        transactionVo.setTransactionOutputCount(TransactionTool.getTransactionOutputCount(transaction));
        transactionVo.setTransactionInputValues(TransactionTool.getInputsValue(transaction));
        transactionVo.setTransactionOutputValues(TransactionTool.getOutputsValue(transaction));

        long blockchainHeight = getBlockchainCore().queryBlockchainHeight();
        Block block = getBlockchainCore().queryBlockByBlockHeight(transaction.getBlockHeight());
        transactionVo.setConfirmCount(blockchainHeight-block.getHeight()+1);
        transactionVo.setBlockTime(TimeUtil.timestamp2FormatDate(block.getTimestamp()));
        transactionVo.setBlockHash(block.getHash());

        List<TransactionInput> inputs = transaction.getInputs();
        List<TransactionInputVo> transactionInputVoList = new ArrayList<>();
        if(inputs != null){
            for(TransactionInput transactionInput:inputs){
                TransactionInputVo transactionInputVo = new TransactionInputVo();
                transactionInputVo.setAddress(transactionInput.getUnspentTransactionOutput().getAddress());
                transactionInputVo.setValue(transactionInput.getUnspentTransactionOutput().getValue());
                transactionInputVo.setInputScript(ScriptTool.toString(transactionInput.getInputScript()));
                transactionInputVo.setTransactionHash(transactionInput.getUnspentTransactionOutput().getTransactionHash());
                transactionInputVo.setTransactionOutputIndex(transactionInput.getUnspentTransactionOutput().getTransactionOutputIndex());
                transactionInputVoList.add(transactionInputVo);
            }
        }
        transactionVo.setTransactionInputVoList(transactionInputVoList);

        List<TransactionOutput> outputs = transaction.getOutputs();
        List<TransactionOutputVo> transactionOutputVoList = new ArrayList<>();
        if(outputs != null){
            for(TransactionOutput transactionOutput:outputs){
                TransactionOutputVo transactionOutputVo = new TransactionOutputVo();
                transactionOutputVo.setAddress(transactionOutput.getAddress());
                transactionOutputVo.setValue(transactionOutput.getValue());
                transactionOutputVo.setOutputScript(ScriptTool.toString(transactionOutput.getOutputScript()));
                transactionOutputVo.setTransactionHash(transactionOutput.getTransactionHash());
                transactionOutputVo.setTransactionOutputIndex(transactionOutput.getTransactionOutputIndex());
                transactionOutputVoList.add(transactionOutputVo);
            }
        }
        transactionVo.setTransactionOutputVoList(transactionOutputVoList);

        List<String> inputScriptList = new ArrayList<>();
        for (TransactionInputVo transactionInputVo : transactionInputVoList){
            inputScriptList.add(transactionInputVo.getInputScript());
        }
        transactionVo.setInputScriptList(inputScriptList);

        List<String> outputScriptList = new ArrayList<>();
        for (TransactionOutputVo transactionOutputVo : transactionOutputVoList){
            outputScriptList.add(transactionOutputVo.getOutputScript());
        }
        transactionVo.setOutputScriptList(outputScriptList);
        return transactionVo;
    }

    private BlockchainCore getBlockchainCore(){
        return blockchainNetCore.getBlockchainCore();
    }
}
