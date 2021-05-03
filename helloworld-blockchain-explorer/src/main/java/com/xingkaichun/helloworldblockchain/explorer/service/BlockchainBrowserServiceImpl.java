package com.xingkaichun.helloworldblockchain.explorer.service;

import com.xingkaichun.helloworldblockchain.core.BlockchainCore;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.transaction.*;
import com.xingkaichun.helloworldblockchain.core.tools.*;
import com.xingkaichun.helloworldblockchain.explorer.vo.block.BlockView;
import com.xingkaichun.helloworldblockchain.explorer.vo.transaction.*;
import com.xingkaichun.helloworldblockchain.netcore.NetBlockchainCore;
import com.xingkaichun.helloworldblockchain.netcore.client.BlockchainNodeClientImpl;
import com.xingkaichun.helloworldblockchain.netcore.entity.NodeEntity;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.PostTransactionRequest;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.PostTransactionResponse;
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
public class BlockchainBrowserServiceImpl implements BlockchainBrowserService {

    @Autowired
    private NetBlockchainCore netBlockchainCore;

    @Override
    public TransactionOutputDetailView queryTransactionOutputByTransactionOutputId(TransactionOutputId transactionOutputId) {
        //查询交易输出
        TransactionOutput transactionOutput = getBlockchainCore().getBlockchainDataBase().queryTransactionOutputByTransactionOutputId(transactionOutputId);
        if(transactionOutput == null){
            return null;
        }

        TransactionOutputDetailView transactionOutputDetailView = new TransactionOutputDetailView();
        transactionOutputDetailView.setFromBlockHeight(transactionOutput.getBlockHeight());
        transactionOutputDetailView.setFromBlockHash(transactionOutput.getBlockHash());
        transactionOutputDetailView.setFromTransactionHash(transactionOutput.getTransactionHash());
        transactionOutputDetailView.setValue(transactionOutput.getValue());
        transactionOutputDetailView.setFromOutputScript(ScriptTool.toString(transactionOutput.getOutputScript()));
        transactionOutputDetailView.setFromTransactionOutputIndex(transactionOutput.getTransactionOutputIndex());
        transactionOutputId.setTransactionHash(transactionOutput.getTransactionHash());
        transactionOutputId.setTransactionOutputIndex(transactionOutput.getTransactionOutputIndex());

        //是否是未花费输出
        TransactionOutput transactionOutputTemp = getBlockchainCore().getBlockchainDataBase().queryUnspentTransactionOutputByTransactionOutputId(transactionOutputId);
        transactionOutputDetailView.setSpent(transactionOutputTemp==null);

        //来源
        TransactionView inputTransactionView = queryTransactionByTransactionHash(transactionOutputId.getTransactionHash());
        transactionOutputDetailView.setInputTransaction(inputTransactionView);
        transactionOutputDetailView.setTransactionType(inputTransactionView.getTransactionType());


        //去向
        TransactionView outputTransactionView = null;
        if(transactionOutputTemp == null){
            Transaction destinationTransaction = getBlockchainCore().getBlockchainDataBase().queryDestinationTransactionByTransactionOutputId(transactionOutputId);
            outputTransactionView = queryTransactionByTransactionHash(destinationTransaction.getTransactionHash());

            Transaction outputTransaction = getBlockchainCore().getBlockchainDataBase().queryTransactionByTransactionHash(destinationTransaction.getTransactionHash());
            List<TransactionInput> inputs = outputTransaction.getInputs();
            if(inputs != null){
                for(TransactionInput transactionInput : inputs){
                    UnspentTransactionOutput unspentTransactionOutput = transactionInput.getUnspentTransactionOutput();
                    if(transactionOutput.getTransactionHash().equals(unspentTransactionOutput.getTransactionHash()) &&
                            transactionOutput.getTransactionOutputIndex()==unspentTransactionOutput.getTransactionOutputIndex()){
                        transactionOutputDetailView.setToTransactionInputIndex(outputTransactionView.getTransactionInputCount());
                        transactionOutputDetailView.setToInputScript(ScriptTool.toString(transactionInput.getInputScript()));
                        break;
                    }
                }
            }
            transactionOutputDetailView.setToBlockHeight(outputTransactionView.getBlockHeight());
            transactionOutputDetailView.setToBlockHash(outputTransactionView.getBlockHash());
            transactionOutputDetailView.setToTransactionHash(outputTransactionView.getTransactionHash());
            transactionOutputDetailView.setOutputTransaction(outputTransactionView);
        }
        return transactionOutputDetailView;
    }

    @Override
    public TransactionOutputDetailView queryTransactionOutputByAddress(String address) {
        TransactionOutput transactionOutput = getBlockchainCore().queryTransactionOutputByAddress(address);
        if(transactionOutput == null){
            return null;
        }
        TransactionOutputDetailView transactionOutputDetailView = queryTransactionOutputByTransactionOutputId(transactionOutput);
        return transactionOutputDetailView;
    }

    @Override
    public List<TransactionView> queryTransactionListByBlockHashTransactionHeight(String blockHash, long from, long size) {
        Block block = getBlockchainCore().queryBlockByBlockHash(blockHash);
        List<TransactionView> transactionViewList = new ArrayList<>();
        for(long i=from;i<from+size;i++){
            if(from < 0){
                break;
            }
            if(i > block.getTransactionCount()){
                break;
            }
            long transactionHeight = block.getPreviousTransactionHeight() + i;
            Transaction transaction = getBlockchainCore().queryTransactionByTransactionHeight(transactionHeight);
            TransactionView transactionView = queryTransactionByTransactionHash(transaction.getTransactionHash());
            transactionViewList.add(transactionView);
        }
        return transactionViewList;
    }

    @Override
    public SubmitTransactionToBlockchainNetworkResponse submitTransactionToBlockchainNetwork(SubmitTransactionToBlockchainNetworkRequest request) {
        TransactionDTO transactionDTO = request.getTransactionDTO();
        //将交易提交到本地区块链
        getBlockchainCore().submitTransaction(transactionDTO);
        //提交交易到网络
        List<NodeEntity> nodes = netBlockchainCore.getNodeService().queryAllNodeList();
        List<SubmitTransactionToBlockchainNetworkResponse.Node> successSubmitNode = new ArrayList<>();
        List<SubmitTransactionToBlockchainNetworkResponse.Node> failSubmitNode = new ArrayList<>();
        if(nodes != null){
            for(NodeEntity node:nodes){
                PostTransactionRequest postTransactionRequest = new PostTransactionRequest();
                postTransactionRequest.setTransaction(transactionDTO);
                PostTransactionResponse postTransactionResponse = new BlockchainNodeClientImpl(node.getIp()).postTransaction(postTransactionRequest);
                if(postTransactionResponse != null){
                    successSubmitNode.add(new SubmitTransactionToBlockchainNetworkResponse.Node(node.getIp()));
                } else {
                    failSubmitNode.add(new SubmitTransactionToBlockchainNetworkResponse.Node(node.getIp()));
                }
            }
        }

        SubmitTransactionToBlockchainNetworkResponse response = new SubmitTransactionToBlockchainNetworkResponse();
        response.setTransactionDTO(transactionDTO);
        response.setSuccessSubmitNode(successSubmitNode);
        response.setFailSubmitNode(failSubmitNode);
        return response;
    }

    @Override
    public BlockView queryBlockViewByBlockHeight(Long blockHeight) {
        Block block = getBlockchainCore().queryBlockByBlockHeight(blockHeight);
        if(block == null){
            return null;
        }
        Block nextBlock = getBlockchainCore().queryBlockByBlockHeight(block.getHeight()+1);

        BlockView blockDto = new BlockView();
        blockDto.setHeight(block.getHeight());
        blockDto.setConfirmCount(BlockTool.getTransactionCount(block));
        blockDto.setBlockSize(SizeTool.calculateBlockSize(block)+"字符");
        blockDto.setTransactionCount(BlockTool.getTransactionCount(block));
        blockDto.setTime(TimeUtil.timestamp2FormatDate(block.getTimestamp()));
        blockDto.setMinerIncentiveValue(BlockTool.getMinerIncentiveValue(block));
        blockDto.setDifficulty(BlockTool.formatDifficulty(block.getDifficulty()));
        blockDto.setNonce(block.getNonce());
        blockDto.setHash(block.getHash());
        blockDto.setPreviousBlockHash(block.getPreviousBlockHash());
        blockDto.setNextBlockHash(nextBlock==null?null:nextBlock.getHash());
        blockDto.setMerkleTreeRoot(block.getMerkleTreeRoot());
        return blockDto;
    }

    @Override
    public MiningTransactionView queryMiningTransactionByTransactionHash(String transactionHash) {
        TransactionDTO transactionDTO = getBlockchainCore().queryMiningTransactionDtoByTransactionHash(transactionHash);
        if(transactionDTO == null){
            return null;
        }

        Transaction transaction = Dto2ModelTool.transactionDto2Transaction(getBlockchainCore().getBlockchainDataBase(),transactionDTO);
        MiningTransactionView transactionDtoResp = new MiningTransactionView();
        transactionDtoResp.setTransactionHash(transaction.getTransactionHash());

        List<MiningTransactionView.TransactionInputDto> inputDtos = new ArrayList<>();
        List<TransactionInput> inputs = transaction.getInputs();
        if(inputs != null){
            for(TransactionInput input:inputs){
                MiningTransactionView.TransactionInputDto transactionInputDto = new MiningTransactionView.TransactionInputDto();
                transactionInputDto.setAddress(input.getUnspentTransactionOutput().getAddress());
                transactionInputDto.setTransactionHash(input.getUnspentTransactionOutput().getTransactionHash());
                transactionInputDto.setTransactionOutputIndex(input.getUnspentTransactionOutput().getTransactionOutputIndex());
                transactionInputDto.setValue(input.getUnspentTransactionOutput().getValue());
                inputDtos.add(transactionInputDto);
            }
        }
        transactionDtoResp.setInputs(inputDtos);

        List<MiningTransactionView.TransactionOutputDto> outputDtos = new ArrayList<>();
        List<TransactionOutput> outputs = transaction.getOutputs();
        if(outputs != null){
            for(TransactionOutput output:outputs){
                MiningTransactionView.TransactionOutputDto transactionOutputDto = new MiningTransactionView.TransactionOutputDto();
                transactionOutputDto.setAddress(output.getAddress());
                transactionOutputDto.setValue(output.getValue());
                outputDtos.add(transactionOutputDto);
            }
        }
        transactionDtoResp.setOutputs(outputDtos);
        return transactionDtoResp;
    }


    @Override
    public TransactionView queryTransactionByTransactionHash(String transactionHash) {
        Transaction transaction = getBlockchainCore().queryTransactionByTransactionHash(transactionHash);
        if(transaction == null){
            return null;
        }

        TransactionView transactionView = new TransactionView();
        transactionView.setTransactionHash(transaction.getTransactionHash());
        transactionView.setBlockHeight(transaction.getBlockHeight());

        transactionView.setTransactionFee(TransactionTool.calculateTransactionFee(transaction));
        transactionView.setTransactionType(transaction.getTransactionType().name());
        transactionView.setTransactionInputCount(TransactionTool.getTransactionInputCount(transaction));
        transactionView.setTransactionOutputCount(TransactionTool.getTransactionOutputCount(transaction));
        transactionView.setTransactionInputValues(TransactionTool.getInputsValue(transaction));
        transactionView.setTransactionOutputValues(TransactionTool.getOutputsValue(transaction));

        long blockchainHeight = getBlockchainCore().queryBlockchainHeight();
        Block block = getBlockchainCore().queryBlockByBlockHeight(transaction.getBlockHeight());
        transactionView.setConfirmCount(blockchainHeight-block.getHeight()+1);
        transactionView.setBlockTime(TimeUtil.timestamp2FormatDate(block.getTimestamp()));
        transactionView.setBlockHash(block.getHash());

        List<TransactionInput> inputs = transaction.getInputs();
        List<TransactionInputView> transactionInputViewList = new ArrayList<>();
        if(inputs != null){
            for(TransactionInput transactionInput:inputs){
                TransactionInputView transactionInputView = new TransactionInputView();
                transactionInputView.setAddress(transactionInput.getUnspentTransactionOutput().getAddress());
                transactionInputView.setValue(transactionInput.getUnspentTransactionOutput().getValue());
                transactionInputView.setInputScript(ScriptTool.toString(transactionInput.getInputScript()));
                transactionInputView.setTransactionHash(transactionInput.getUnspentTransactionOutput().getTransactionHash());
                transactionInputView.setTransactionOutputIndex(transactionInput.getUnspentTransactionOutput().getTransactionOutputIndex());
                transactionInputViewList.add(transactionInputView);
            }
        }
        transactionView.setTransactionInputViewList(transactionInputViewList);

        List<TransactionOutput> outputs = transaction.getOutputs();
        List<TransactionOutputView> transactionOutputViewList = new ArrayList<>();
        if(outputs != null){
            for(TransactionOutput transactionOutput:outputs){
                TransactionOutputView transactionOutputView = new TransactionOutputView();
                transactionOutputView.setAddress(transactionOutput.getAddress());
                transactionOutputView.setValue(transactionOutput.getValue());
                transactionOutputView.setOutputScript(ScriptTool.toString(transactionOutput.getOutputScript()));
                transactionOutputView.setTransactionHash(transactionOutput.getTransactionHash());
                transactionOutputView.setTransactionOutputIndex(transactionOutput.getTransactionOutputIndex());
                transactionOutputViewList.add(transactionOutputView);
            }
        }
        transactionView.setTransactionOutputViewList(transactionOutputViewList);

        List<String> inputScriptList = new ArrayList<>();
        for (TransactionInputView transactionInputView : transactionInputViewList){
            inputScriptList.add(transactionInputView.getInputScript());
        }
        transactionView.setInputScriptList(inputScriptList);

        List<String> outputScriptList = new ArrayList<>();
        for (TransactionOutputView transactionOutputView : transactionOutputViewList){
            outputScriptList.add(transactionOutputView.getOutputScript());
        }
        transactionView.setOutputScriptList(outputScriptList);
        return transactionView;
    }

    private BlockchainCore getBlockchainCore(){
        return netBlockchainCore.getBlockchainCore();
    }
}
