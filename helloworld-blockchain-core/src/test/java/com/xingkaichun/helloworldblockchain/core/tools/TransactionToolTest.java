package com.xingkaichun.helloworldblockchain.core.tools;

import com.xingkaichun.helloworldblockchain.core.model.script.OperationCodeEnum;
import com.xingkaichun.helloworldblockchain.crypto.HexUtil;
import com.xingkaichun.helloworldblockchain.netcore.dto.*;
import com.xingkaichun.helloworldblockchain.util.JsonUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


public class TransactionToolTest {

    @Test
    public void bytesTransactionOmitTrueTest()
    {
        TransactionDto transactionDTO = new TransactionDto();

        TransactionDto transactionDto2 = TransactionTool.transactionDTO(TransactionTool.bytesTransaction(transactionDTO,true),true);
        Assert.assertEquals(JsonUtil.toJson(transactionDTO),JsonUtil.toJson(transactionDto2));

        List<TransactionInputDto> transactionInputDtoList = new ArrayList<>();
        TransactionInputDto transactionInputDTO = new TransactionInputDto();
        transactionInputDTO.setTransactionHash("53b780303a801edbf75fe3463799547daf88ae152c06d16769218cec78b5d48e");
        transactionInputDTO.setTransactionOutputIndex(0);
        transactionInputDtoList.add(transactionInputDTO);
        transactionDTO.setInputs(transactionInputDtoList);

        transactionDto2 = TransactionTool.transactionDTO(TransactionTool.bytesTransaction(transactionDTO,true),true);
        Assert.assertEquals(JsonUtil.toJson(transactionDTO),JsonUtil.toJson(transactionDto2));

        List<TransactionOutputDto> transactionOutputDtoList = new ArrayList<>();
        TransactionOutputDto transactionOutputDTO = new TransactionOutputDto();
        OutputScriptDto outputScriptDTO = new OutputScriptDto();
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

        transactionDto2 = TransactionTool.transactionDTO(TransactionTool.bytesTransaction(transactionDTO,true),true);
        Assert.assertEquals(JsonUtil.toJson(transactionDTO),JsonUtil.toJson(transactionDto2));

        TransactionInputDto transactionInputDto2 = new TransactionInputDto();
        transactionInputDto2.setTransactionHash("53b780303a801edbf75fe3463799547daf88ae152c06d16769218cec78b5d48e");
        transactionInputDto2.setTransactionOutputIndex(0);
        transactionInputDtoList.add(transactionInputDto2);

        transactionDto2 = TransactionTool.transactionDTO(TransactionTool.bytesTransaction(transactionDTO,true),true);
        Assert.assertEquals(JsonUtil.toJson(transactionDTO),JsonUtil.toJson(transactionDto2));

        TransactionOutputDto transactionOutputDto2 = new TransactionOutputDto();
        OutputScriptDto outputScriptDto2 = new OutputScriptDto();
        outputScriptDto2.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_DUP.getCode()));
        outputScriptDto2.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_HASH160.getCode()));
        outputScriptDto2.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_PUSHDATA.getCode()));
        outputScriptDto2.add("955c1464982a1c904b7b1029598de6ace11bd2b1");
        outputScriptDto2.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_EQUALVERIFY.getCode()));
        outputScriptDto2.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_CHECKSIG.getCode()));
        transactionOutputDto2.setOutputScript(outputScriptDto2);
        transactionOutputDto2.setValue(20);
        transactionOutputDtoList.add(transactionOutputDto2);

        transactionDto2 = TransactionTool.transactionDTO(TransactionTool.bytesTransaction(transactionDTO,true),true);
        Assert.assertEquals(JsonUtil.toJson(transactionDTO),JsonUtil.toJson(transactionDto2));
    }

    @Test
    public void bytesTransactionOmitFalseTest()
    {
        TransactionDto transactionDTO = new TransactionDto();

        TransactionDto transactionDto2 = TransactionTool.transactionDTO(TransactionTool.bytesTransaction(transactionDTO,false),false);
        Assert.assertEquals(JsonUtil.toJson(transactionDTO),JsonUtil.toJson(transactionDto2));

        List<TransactionInputDto> transactionInputDtoList = new ArrayList<>();
        TransactionInputDto transactionInputDTO = new TransactionInputDto();
        transactionInputDTO.setTransactionHash("53b780303a801edbf75fe3463799547daf88ae152c06d16769218cec78b5d48e");
        transactionInputDTO.setTransactionOutputIndex(0);
        InputScriptDto inputScriptDTO = new InputScriptDto();
        inputScriptDTO.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_PUSHDATA.getCode()));
        inputScriptDTO.add("955c1464982a1c904b7b1029598de6ace11bd2b1");
        transactionInputDTO.setInputScript(inputScriptDTO);
        transactionInputDtoList.add(transactionInputDTO);
        transactionDTO.setInputs(transactionInputDtoList);

        transactionDto2 = TransactionTool.transactionDTO(TransactionTool.bytesTransaction(transactionDTO,false),false);
        Assert.assertEquals(JsonUtil.toJson(transactionDTO),JsonUtil.toJson(transactionDto2));

        List<TransactionOutputDto> transactionOutputDtoList = new ArrayList<>();
        TransactionOutputDto transactionOutputDTO = new TransactionOutputDto();
        OutputScriptDto outputScriptDTO = new OutputScriptDto();
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

        transactionDto2 = TransactionTool.transactionDTO(TransactionTool.bytesTransaction(transactionDTO,false),false);
        Assert.assertEquals(JsonUtil.toJson(transactionDTO),JsonUtil.toJson(transactionDto2));

        TransactionInputDto transactionInputDto2 = new TransactionInputDto();
        transactionInputDto2.setTransactionHash("53b780303a801edbf75fe3463799547daf88ae152c06d16769218cec78b5d48e");
        transactionInputDto2.setTransactionOutputIndex(0);
        InputScriptDto inputScriptDto2 = new InputScriptDto();
        inputScriptDto2.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_PUSHDATA.getCode()));
        inputScriptDto2.add("955c1464982a1c904b7b1029598de6ace11bd2b1");
        transactionInputDto2.setInputScript(inputScriptDto2);
        transactionInputDtoList.add(transactionInputDto2);

        transactionDto2 = TransactionTool.transactionDTO(TransactionTool.bytesTransaction(transactionDTO,false),false);
        Assert.assertEquals(JsonUtil.toJson(transactionDTO),JsonUtil.toJson(transactionDto2));

        TransactionOutputDto transactionOutputDto2 = new TransactionOutputDto();
        OutputScriptDto outputScriptDto2 = new OutputScriptDto();
        outputScriptDto2.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_DUP.getCode()));
        outputScriptDto2.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_HASH160.getCode()));
        outputScriptDto2.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_PUSHDATA.getCode()));
        outputScriptDto2.add("955c1464982a1c904b7b1029598de6ace11bd2b1");
        outputScriptDto2.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_EQUALVERIFY.getCode()));
        outputScriptDto2.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_CHECKSIG.getCode()));
        transactionOutputDto2.setOutputScript(outputScriptDto2);
        transactionOutputDto2.setValue(20);
        transactionOutputDtoList.add(transactionOutputDto2);

        transactionDto2 = TransactionTool.transactionDTO(TransactionTool.bytesTransaction(transactionDTO,false),false);
        Assert.assertEquals(JsonUtil.toJson(transactionDTO),JsonUtil.toJson(transactionDto2));
    }
}
