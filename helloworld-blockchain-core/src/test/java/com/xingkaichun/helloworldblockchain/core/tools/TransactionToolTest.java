package com.xingkaichun.helloworldblockchain.core.tools;

import com.google.gson.Gson;
import com.xingkaichun.helloworldblockchain.core.model.script.OperationCodeEnum;
import com.xingkaichun.helloworldblockchain.crypto.HexUtil;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


public class TransactionToolTest {

    @Test
    public void bytesTransactionTest()
    {
        TransactionDTO transactionDTO = new TransactionDTO();

        TransactionDTO transactionDTO2 = TransactionTool.transactionDTO(TransactionTool.bytesTransaction(transactionDTO));
        Assert.assertEquals(new Gson().toJson(transactionDTO),new Gson().toJson(transactionDTO2));

        List<TransactionInputDTO> transactionInputDtoList = new ArrayList<>();
        TransactionInputDTO transactionInputDTO = new TransactionInputDTO();
        InputScriptDTO inputScriptDTO = new InputScriptDTO();
        inputScriptDTO.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_PUSHDATA1024.getCode()));
        inputScriptDTO.add("955c1464982a1c904b7b1029598de6ace11bd2b1");
        transactionInputDTO.setInputScriptDTO(inputScriptDTO);
        UnspendTransactionOutputDTO unspendTransactionOutputDTO = new UnspendTransactionOutputDTO();
        unspendTransactionOutputDTO.setTransactionHash("53b780303a801edbf75fe3463799547daf88ae152c06d16769218cec78b5d48e");
        unspendTransactionOutputDTO.setTransactionOutputIndex(0);
        transactionInputDTO.setUnspendTransactionOutputDTO(unspendTransactionOutputDTO);
        transactionInputDtoList.add(transactionInputDTO);
        transactionDTO.setTransactionInputDtoList(transactionInputDtoList);

        transactionDTO2 = TransactionTool.transactionDTO(TransactionTool.bytesTransaction(transactionDTO));
        Assert.assertEquals(new Gson().toJson(transactionDTO),new Gson().toJson(transactionDTO2));

        List<TransactionOutputDTO> transactionOutputDtoList = new ArrayList<>();
        TransactionOutputDTO transactionOutputDTO = new TransactionOutputDTO();
        OutputScriptDTO outputScriptDTO = new OutputScriptDTO();
        outputScriptDTO.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_DUP.getCode()));
        outputScriptDTO.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_HASH160.getCode()));
        outputScriptDTO.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_PUSHDATA1024.getCode()));
        outputScriptDTO.add("955c1464982a1c904b7b1029598de6ace11bd2b1");
        outputScriptDTO.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_EQUALVERIFY.getCode()));
        outputScriptDTO.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_CHECKSIG.getCode()));
        transactionOutputDTO.setOutputScriptDTO(outputScriptDTO);
        transactionOutputDTO.setValue(10);
        transactionOutputDtoList.add(transactionOutputDTO);
        transactionDTO.setTransactionOutputDtoList(transactionOutputDtoList);

        transactionDTO2 = TransactionTool.transactionDTO(TransactionTool.bytesTransaction(transactionDTO));
        Assert.assertEquals(new Gson().toJson(transactionDTO),new Gson().toJson(transactionDTO2));

        TransactionInputDTO transactionInputDTO2 = new TransactionInputDTO();
        InputScriptDTO inputScriptDTO2 = new InputScriptDTO();
        inputScriptDTO2.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_PUSHDATA1024.getCode()));
        inputScriptDTO2.add("955c1464982a1c904b7b1029598de6ace11bd2b1");
        transactionInputDTO2.setInputScriptDTO(inputScriptDTO2);
        UnspendTransactionOutputDTO unspendTransactionOutputDTO2 = new UnspendTransactionOutputDTO();
        unspendTransactionOutputDTO2.setTransactionHash("53b780303a801edbf75fe3463799547daf88ae152c06d16769218cec78b5d48e");
        unspendTransactionOutputDTO2.setTransactionOutputIndex(0);
        transactionInputDTO2.setUnspendTransactionOutputDTO(unspendTransactionOutputDTO2);
        transactionInputDtoList.add(transactionInputDTO2);

        transactionDTO2 = TransactionTool.transactionDTO(TransactionTool.bytesTransaction(transactionDTO));
        Assert.assertEquals(new Gson().toJson(transactionDTO),new Gson().toJson(transactionDTO2));

        TransactionOutputDTO transactionOutputDTO2 = new TransactionOutputDTO();
        OutputScriptDTO outputScriptDTO2 = new OutputScriptDTO();
        outputScriptDTO2.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_DUP.getCode()));
        outputScriptDTO2.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_HASH160.getCode()));
        outputScriptDTO2.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_PUSHDATA1024.getCode()));
        outputScriptDTO2.add("955c1464982a1c904b7b1029598de6ace11bd2b1");
        outputScriptDTO2.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_EQUALVERIFY.getCode()));
        outputScriptDTO2.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_CHECKSIG.getCode()));
        transactionOutputDTO2.setOutputScriptDTO(outputScriptDTO2);
        transactionOutputDTO2.setValue(20);
        transactionOutputDtoList.add(transactionOutputDTO2);

        transactionDTO2 = TransactionTool.transactionDTO(TransactionTool.bytesTransaction(transactionDTO));
        Assert.assertEquals(new Gson().toJson(transactionDTO),new Gson().toJson(transactionDTO2));
    }
}
