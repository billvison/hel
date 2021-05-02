package com.xingkaichun.helloworldblockchain.core.tools;

import com.xingkaichun.helloworldblockchain.core.BlockchainDatabase;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.script.InputScript;
import com.xingkaichun.helloworldblockchain.core.model.script.OutputScript;
import com.xingkaichun.helloworldblockchain.core.model.transaction.*;
import com.xingkaichun.helloworldblockchain.crypto.AccountUtil;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.*;
import com.xingkaichun.helloworldblockchain.setting.GlobalSetting;

import java.util.ArrayList;
import java.util.List;

/**
 * dto转model工具
 *
 * @author 邢开春 409060350@qq.com
 */
public class Dto2ModelTool {

    public static Block blockDto2Block(BlockchainDatabase blockchainDataBase, BlockDTO blockDTO) {
        String previousBlockHash = blockDTO.getPreviousHash();
        Block previousBlock = blockchainDataBase.queryBlockByBlockHash(previousBlockHash);

        Block block = new Block();
        block.setTimestamp(blockDTO.getTimestamp());
        block.setPreviousBlockHash(previousBlockHash);
        block.setNonce(blockDTO.getNonce());

        long blockHeight = previousBlock==null? GlobalSetting.GenesisBlock.HEIGHT+1:previousBlock.getHeight()+1;
        block.setHeight(blockHeight);

        List<Transaction> transactionList = transactionDto2Transaction(blockchainDataBase,blockDTO.getTransactions());
        block.setTransactions(transactionList);

        String merkleTreeRoot = BlockTool.calculateBlockMerkleTreeRoot(block);
        block.setMerkleTreeRoot(merkleTreeRoot);

        block.setHash(BlockTool.calculateBlockHash(block));

        /**
         * 预先校验区块工作量共识。伪造工作量共识是一件十分耗费资源的事情，因此预先校验工作量共识可以抵消绝大部分的攻击。
         * 也可以选择跳过此处预检，后续代码有完整的校验检测。
         * 此处预检，只是想预先抵消绝大部分的攻击。
         */
        if(!blockchainDataBase.getConsensus().isReachConsensus(blockchainDataBase,block)){
            throw new RuntimeException("区块预检失败。");
        }
        return block;
    }

    private static List<Transaction> transactionDto2Transaction(BlockchainDatabase blockchainDataBase, List<TransactionDTO> transactionDtoList) {
        List<Transaction> transactionList = new ArrayList<>();
        if(transactionDtoList != null){
            for(TransactionDTO transactionDTO:transactionDtoList){
                Transaction transaction = transactionDto2Transaction(blockchainDataBase,transactionDTO);
                transactionList.add(transaction);
            }
        }
        return transactionList;
    }

    public static Transaction transactionDto2Transaction(BlockchainDatabase blockchainDataBase, TransactionDTO transactionDTO) {
        List<TransactionInput> inputs = new ArrayList<>();
        List<TransactionInputDTO> transactionInputDtoList = transactionDTO.getInputs();
        if(transactionInputDtoList != null){
            for (TransactionInputDTO transactionInputDTO:transactionInputDtoList){
                TransactionOutputId transactionOutputId = new TransactionOutputId();
                transactionOutputId.setTransactionHash(transactionInputDTO.getTransactionHash());
                transactionOutputId.setTransactionOutputIndex(transactionInputDTO.getTransactionOutputIndex());
                TransactionOutput unspentTransactionOutput = blockchainDataBase.queryUnspentTransactionOutputByTransactionOutputId(transactionOutputId);
                if(unspentTransactionOutput == null){
                    throw new RuntimeException("非法交易。交易输入并不是一笔未花费交易输出。");
                }
                TransactionInput transactionInput = new TransactionInput();
                transactionInput.setUnspentTransactionOutput(TransactionTool.transactionOutput2UnspentTransactionOutput(unspentTransactionOutput));
                transactionInput.setInputScript(inputScriptDto2InputScript(transactionInputDTO.getInputScript()));
                inputs.add(transactionInput);
            }
        }

        List<TransactionOutput> outputs = new ArrayList<>();
        List<TransactionOutputDTO> dtoOutputs = transactionDTO.getOutputs();
        if(dtoOutputs != null){
            for(TransactionOutputDTO transactionOutputDTO:dtoOutputs){
                TransactionOutput transactionOutput = transactionOutputDto2TransactionOutput(transactionOutputDTO);
                outputs.add(transactionOutput);
            }
        }

        Transaction transaction = new Transaction();
        TransactionType transactionType = obtainTransactionDTO(transactionDTO);
        transaction.setTransactionType(transactionType);
        transaction.setTransactionHash(TransactionTool.calculateTransactionHash(transactionDTO));
        transaction.setInputs(inputs);
        transaction.setOutputs(outputs);
        return transaction;
    }

    public static TransactionOutput transactionOutputDto2TransactionOutput(TransactionOutputDTO transactionOutputDTO) {
        TransactionOutput transactionOutput = new TransactionOutput();
        String publicKeyHash = ScriptTool.getPublicKeyHashByPayToPublicKeyHashOutputScript(transactionOutputDTO.getOutputScript());
        String address = AccountUtil.addressFromPublicKeyHash(publicKeyHash);
        transactionOutput.setAddress(address);
        transactionOutput.setValue(transactionOutputDTO.getValue());
        transactionOutput.setOutputScript(outputScriptDto2OutputScript(transactionOutputDTO.getOutputScript()));
        return transactionOutput;
    }

    private static TransactionType obtainTransactionDTO(TransactionDTO transactionDTO) {
        if(transactionDTO.getInputs() == null || transactionDTO.getInputs().size()==0){
            return TransactionType.COINBASE;
        }
        return TransactionType.NORMAL;
    }

    private static OutputScript outputScriptDto2OutputScript(OutputScriptDTO outputScriptDTO) {
        if(outputScriptDTO == null){
            return null;
        }
        OutputScript outputScript = new OutputScript();
        outputScript.addAll(outputScriptDTO);
        return outputScript;
    }

    private static InputScript inputScriptDto2InputScript(InputScriptDTO inputScriptDTO) {
        if(inputScriptDTO == null){
            return null;
        }
        InputScript inputScript = new InputScript();
        inputScript.addAll(inputScriptDTO);
        return inputScript;
    }
}
