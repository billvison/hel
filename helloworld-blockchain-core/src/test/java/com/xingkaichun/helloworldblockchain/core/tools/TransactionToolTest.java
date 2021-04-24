package com.xingkaichun.helloworldblockchain.core.tools;

import com.xingkaichun.helloworldblockchain.core.model.script.OperationCodeEnum;
import com.xingkaichun.helloworldblockchain.crypto.HexUtil;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.*;
import com.xingkaichun.helloworldblockchain.util.JsonUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


public class TransactionToolTest {

    @Test
    public void bytesTransactionOmitTrueTest()
    {
        TransactionDTO transactionDTO = new TransactionDTO();

        TransactionDTO transactionDTO2 = TransactionTool.transactionDTO(TransactionTool.bytesTransaction(transactionDTO,true),true);
        Assert.assertEquals(JsonUtil.toJson(transactionDTO),JsonUtil.toJson(transactionDTO2));

        List<TransactionInputDTO> transactionInputDtoList = new ArrayList<>();
        TransactionInputDTO transactionInputDTO = new TransactionInputDTO();
        transactionInputDTO.setTransactionHash("53b780303a801edbf75fe3463799547daf88ae152c06d16769218cec78b5d48e");
        transactionInputDTO.setTransactionOutputIndex(0);
        transactionInputDtoList.add(transactionInputDTO);
        transactionDTO.setInputs(transactionInputDtoList);

        transactionDTO2 = TransactionTool.transactionDTO(TransactionTool.bytesTransaction(transactionDTO,true),true);
        Assert.assertEquals(JsonUtil.toJson(transactionDTO),JsonUtil.toJson(transactionDTO2));

        List<TransactionOutputDTO> transactionOutputDtoList = new ArrayList<>();
        TransactionOutputDTO transactionOutputDTO = new TransactionOutputDTO();
        OutputScriptDTO outputScriptDTO = new OutputScriptDTO();
        outputScriptDTO.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_DUP.getCode()));
        outputScriptDTO.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_HASH160.getCode()));
        outputScriptDTO.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_PUSHDATA.getCode()));
        outputScriptDTO.add("955c1464982a1c904b7b1029598de6ace11bd2b1");
        outputScriptDTO.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_EQUALVERIFY.getCode()));
        outputScriptDTO.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_CHECKSIG.getCode()));
        transactionOutputDTO.setOutputScript(outputScriptDTO);
        transactionOutputDTO.setValue(10);
        transactionOutputDtoList.add(transactionOutputDTO);
        transactionDTO.setOutputs(transactionOutputDtoList);

        transactionDTO2 = TransactionTool.transactionDTO(TransactionTool.bytesTransaction(transactionDTO,true),true);
        Assert.assertEquals(JsonUtil.toJson(transactionDTO),JsonUtil.toJson(transactionDTO2));

        TransactionInputDTO transactionInputDTO2 = new TransactionInputDTO();
        transactionInputDTO2.setTransactionHash("53b780303a801edbf75fe3463799547daf88ae152c06d16769218cec78b5d48e");
        transactionInputDTO2.setTransactionOutputIndex(0);
        transactionInputDtoList.add(transactionInputDTO2);

        transactionDTO2 = TransactionTool.transactionDTO(TransactionTool.bytesTransaction(transactionDTO,true),true);
        Assert.assertEquals(JsonUtil.toJson(transactionDTO),JsonUtil.toJson(transactionDTO2));

        TransactionOutputDTO transactionOutputDTO2 = new TransactionOutputDTO();
        OutputScriptDTO outputScriptDTO2 = new OutputScriptDTO();
        outputScriptDTO2.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_DUP.getCode()));
        outputScriptDTO2.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_HASH160.getCode()));
        outputScriptDTO2.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_PUSHDATA.getCode()));
        outputScriptDTO2.add("955c1464982a1c904b7b1029598de6ace11bd2b1");
        outputScriptDTO2.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_EQUALVERIFY.getCode()));
        outputScriptDTO2.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_CHECKSIG.getCode()));
        transactionOutputDTO2.setOutputScript(outputScriptDTO2);
        transactionOutputDTO2.setValue(20);
        transactionOutputDtoList.add(transactionOutputDTO2);

        transactionDTO2 = TransactionTool.transactionDTO(TransactionTool.bytesTransaction(transactionDTO,true),true);
        Assert.assertEquals(JsonUtil.toJson(transactionDTO),JsonUtil.toJson(transactionDTO2));
    }

    @Test
    public void bytesTransactionOmitFalseTest()
    {
        TransactionDTO transactionDTO = new TransactionDTO();

        TransactionDTO transactionDTO2 = TransactionTool.transactionDTO(TransactionTool.bytesTransaction(transactionDTO,false),false);
        Assert.assertEquals(JsonUtil.toJson(transactionDTO),JsonUtil.toJson(transactionDTO2));

        List<TransactionInputDTO> transactionInputDtoList = new ArrayList<>();
        TransactionInputDTO transactionInputDTO = new TransactionInputDTO();
        transactionInputDTO.setTransactionHash("53b780303a801edbf75fe3463799547daf88ae152c06d16769218cec78b5d48e");
        transactionInputDTO.setTransactionOutputIndex(0);
        InputScriptDTO inputScriptDTO = new InputScriptDTO();
        inputScriptDTO.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_PUSHDATA.getCode()));
        inputScriptDTO.add("955c1464982a1c904b7b1029598de6ace11bd2b1");
        transactionInputDTO.setInputScript(inputScriptDTO);
        transactionInputDtoList.add(transactionInputDTO);
        transactionDTO.setInputs(transactionInputDtoList);

        transactionDTO2 = TransactionTool.transactionDTO(TransactionTool.bytesTransaction(transactionDTO,false),false);
        Assert.assertEquals(JsonUtil.toJson(transactionDTO),JsonUtil.toJson(transactionDTO2));

        List<TransactionOutputDTO> transactionOutputDtoList = new ArrayList<>();
        TransactionOutputDTO transactionOutputDTO = new TransactionOutputDTO();
        OutputScriptDTO outputScriptDTO = new OutputScriptDTO();
        outputScriptDTO.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_DUP.getCode()));
        outputScriptDTO.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_HASH160.getCode()));
        outputScriptDTO.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_PUSHDATA.getCode()));
        outputScriptDTO.add("955c1464982a1c904b7b1029598de6ace11bd2b1");
        outputScriptDTO.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_EQUALVERIFY.getCode()));
        outputScriptDTO.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_CHECKSIG.getCode()));
        transactionOutputDTO.setOutputScript(outputScriptDTO);
        transactionOutputDTO.setValue(10);
        transactionOutputDtoList.add(transactionOutputDTO);
        transactionDTO.setOutputs(transactionOutputDtoList);

        transactionDTO2 = TransactionTool.transactionDTO(TransactionTool.bytesTransaction(transactionDTO,false),false);
        Assert.assertEquals(JsonUtil.toJson(transactionDTO),JsonUtil.toJson(transactionDTO2));

        TransactionInputDTO transactionInputDTO2 = new TransactionInputDTO();
        transactionInputDTO2.setTransactionHash("53b780303a801edbf75fe3463799547daf88ae152c06d16769218cec78b5d48e");
        transactionInputDTO2.setTransactionOutputIndex(0);
        InputScriptDTO inputScriptDTO2 = new InputScriptDTO();
        inputScriptDTO2.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_PUSHDATA.getCode()));
        inputScriptDTO2.add("955c1464982a1c904b7b1029598de6ace11bd2b1");
        transactionInputDTO2.setInputScript(inputScriptDTO2);
        transactionInputDtoList.add(transactionInputDTO2);

        transactionDTO2 = TransactionTool.transactionDTO(TransactionTool.bytesTransaction(transactionDTO,false),false);
        Assert.assertEquals(JsonUtil.toJson(transactionDTO),JsonUtil.toJson(transactionDTO2));

        TransactionOutputDTO transactionOutputDTO2 = new TransactionOutputDTO();
        OutputScriptDTO outputScriptDTO2 = new OutputScriptDTO();
        outputScriptDTO2.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_DUP.getCode()));
        outputScriptDTO2.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_HASH160.getCode()));
        outputScriptDTO2.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_PUSHDATA.getCode()));
        outputScriptDTO2.add("955c1464982a1c904b7b1029598de6ace11bd2b1");
        outputScriptDTO2.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_EQUALVERIFY.getCode()));
        outputScriptDTO2.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_CHECKSIG.getCode()));
        transactionOutputDTO2.setOutputScript(outputScriptDTO2);
        transactionOutputDTO2.setValue(20);
        transactionOutputDtoList.add(transactionOutputDTO2);

        transactionDTO2 = TransactionTool.transactionDTO(TransactionTool.bytesTransaction(transactionDTO,false),false);
        Assert.assertEquals(JsonUtil.toJson(transactionDTO),JsonUtil.toJson(transactionDTO2));
    }
}
