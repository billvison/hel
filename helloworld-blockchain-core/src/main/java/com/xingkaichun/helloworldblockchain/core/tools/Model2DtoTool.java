package com.xingkaichun.helloworldblockchain.core.tools;

import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.script.InputScript;
import com.xingkaichun.helloworldblockchain.core.model.script.OutputScript;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionInput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.*;

import java.util.ArrayList;
import java.util.List;

/**
 * model转dto工具
 *
 * @author 邢开春 409060350@qq.com
 */
public class Model2DtoTool {

    public static BlockDto block2BlockDTO(Block block) {
        if(block == null){
            return null;
        }
        List<TransactionDto> transactionDtoList = new ArrayList<>();
        List<Transaction> transactionList = block.getTransactions();
        if(transactionList != null){
            for(Transaction transaction:transactionList){
                TransactionDto transactionDTO = transaction2TransactionDTO(transaction);
                transactionDtoList.add(transactionDTO);
            }
        }

        BlockDto blockDTO = new BlockDto();
        blockDTO.setTimestamp(block.getTimestamp());
        blockDTO.setPreviousHash(block.getPreviousBlockHash());
        blockDTO.setTransactions(transactionDtoList);
        blockDTO.setNonce(block.getNonce());
        return blockDTO;
    }

    public static TransactionDto transaction2TransactionDTO(Transaction transaction) {
        List<TransactionInputDto> inputs = new ArrayList<>();
        List<TransactionInput> transactionInputList = transaction.getInputs();
        if(transactionInputList!=null){
            for (TransactionInput transactionInput:transactionInputList){
                TransactionInputDto transactionInputDTO = new TransactionInputDto();
                transactionInputDTO.setTransactionHash(transactionInput.getUnspentTransactionOutput().getTransactionHash());
                transactionInputDTO.setTransactionOutputIndex(transactionInput.getUnspentTransactionOutput().getTransactionOutputIndex());
                transactionInputDTO.setInputScript(inputScript2InputScriptDTO(transactionInput.getInputScript()));
                inputs.add(transactionInputDTO);
            }
        }

        List<TransactionOutputDto> outputs = new ArrayList<>();
        List<TransactionOutput> transactionOutputList = transaction.getOutputs();
        if(transactionOutputList!=null){
            for(TransactionOutput transactionOutput:transactionOutputList){
                TransactionOutputDto transactionOutputDTO = transactionOutput2TransactionOutputDTO(transactionOutput);
                outputs.add(transactionOutputDTO);
            }
        }

        TransactionDto transactionDTO = new TransactionDto();
        transactionDTO.setInputs(inputs);
        transactionDTO.setOutputs(outputs);
        return transactionDTO;
    }

    public static InputScriptDto inputScript2InputScriptDTO(InputScript inputScript) {
        InputScriptDto inputScriptDTO = new InputScriptDto();
        inputScriptDTO.addAll(inputScript);
        return inputScriptDTO;
    }

    public static OutputScriptDto outputScript2OutputScriptDTO(OutputScript outputScript) {
        OutputScriptDto outputScriptDTO = new OutputScriptDto();
        outputScriptDTO.addAll(outputScript);
        return outputScriptDTO;
    }

    public static TransactionOutputDto transactionOutput2TransactionOutputDTO(TransactionOutput transactionOutput) {
        TransactionOutputDto transactionOutputDTO = new TransactionOutputDto();
        transactionOutputDTO.setValue(transactionOutput.getValue());
        transactionOutputDTO.setOutputScript(outputScript2OutputScriptDTO(transactionOutput.getOutputScript()));
        return transactionOutputDTO;
    }
}
