package com.xingkaichun.helloworldblockchain.core.tools;

import com.google.gson.Gson;
import com.xingkaichun.helloworldblockchain.core.model.script.OperationCodeEnum;
import com.xingkaichun.helloworldblockchain.crypto.ByteUtil;
import com.xingkaichun.helloworldblockchain.crypto.HexUtil;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class TransactionToolTest {

    @Test
    public void bytesTransactio4SigneTest()
    {
        TransactionDTO transactionDTO = new TransactionDTO();

        TransactionDTO transactionDTO2 = transactionDTO(TransactionTool.bytesTransactio4SignatureHashAll(transactionDTO));
        Assert.assertEquals(new Gson().toJson(transactionDTO),new Gson().toJson(transactionDTO2));

        List<TransactionInputDTO> transactionInputDtoList = new ArrayList<>();
        TransactionInputDTO transactionInputDTO = new TransactionInputDTO();
        transactionInputDTO.setTransactionHash("53b780303a801edbf75fe3463799547daf88ae152c06d16769218cec78b5d48e");
        transactionInputDTO.setTransactionOutputIndex(0);
        transactionInputDtoList.add(transactionInputDTO);
        transactionDTO.setInputs(transactionInputDtoList);

        transactionDTO2 = transactionDTO(TransactionTool.bytesTransactio4SignatureHashAll(transactionDTO));
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
        transactionOutputDTO.setOutputScript(outputScriptDTO);
        transactionOutputDTO.setValue(10);
        transactionOutputDtoList.add(transactionOutputDTO);
        transactionDTO.setOutputs(transactionOutputDtoList);

        transactionDTO2 = transactionDTO(TransactionTool.bytesTransactio4SignatureHashAll(transactionDTO));
        Assert.assertEquals(new Gson().toJson(transactionDTO),new Gson().toJson(transactionDTO2));

        TransactionInputDTO transactionInputDTO2 = new TransactionInputDTO();
        transactionInputDTO2.setTransactionHash("53b780303a801edbf75fe3463799547daf88ae152c06d16769218cec78b5d48e");
        transactionInputDTO2.setTransactionOutputIndex(0);
        transactionInputDtoList.add(transactionInputDTO2);

        transactionDTO2 = transactionDTO(TransactionTool.bytesTransactio4SignatureHashAll(transactionDTO));
        Assert.assertEquals(new Gson().toJson(transactionDTO),new Gson().toJson(transactionDTO2));

        TransactionOutputDTO transactionOutputDTO2 = new TransactionOutputDTO();
        OutputScriptDTO outputScriptDTO2 = new OutputScriptDTO();
        outputScriptDTO2.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_DUP.getCode()));
        outputScriptDTO2.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_HASH160.getCode()));
        outputScriptDTO2.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_PUSHDATA1024.getCode()));
        outputScriptDTO2.add("955c1464982a1c904b7b1029598de6ace11bd2b1");
        outputScriptDTO2.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_EQUALVERIFY.getCode()));
        outputScriptDTO2.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_CHECKSIG.getCode()));
        transactionOutputDTO2.setOutputScript(outputScriptDTO2);
        transactionOutputDTO2.setValue(20);
        transactionOutputDtoList.add(transactionOutputDTO2);

        transactionDTO2 = transactionDTO(TransactionTool.bytesTransactio4SignatureHashAll(transactionDTO));
        Assert.assertEquals(new Gson().toJson(transactionDTO),new Gson().toJson(transactionDTO2));
    }

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
        transactionInputDTO.setInputScript(inputScriptDTO);
        transactionInputDTO.setTransactionHash("53b780303a801edbf75fe3463799547daf88ae152c06d16769218cec78b5d48e");
        transactionInputDTO.setTransactionOutputIndex(0);
        transactionInputDtoList.add(transactionInputDTO);
        transactionDTO.setInputs(transactionInputDtoList);

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
        transactionOutputDTO.setOutputScript(outputScriptDTO);
        transactionOutputDTO.setValue(10);
        transactionOutputDtoList.add(transactionOutputDTO);
        transactionDTO.setOutputs(transactionOutputDtoList);

        transactionDTO2 = TransactionTool.transactionDTO(TransactionTool.bytesTransaction(transactionDTO));
        Assert.assertEquals(new Gson().toJson(transactionDTO),new Gson().toJson(transactionDTO2));

        TransactionInputDTO transactionInputDTO2 = new TransactionInputDTO();
        InputScriptDTO inputScriptDTO2 = new InputScriptDTO();
        inputScriptDTO2.add(HexUtil.bytesToHexString(OperationCodeEnum.OP_PUSHDATA1024.getCode()));
        inputScriptDTO2.add("955c1464982a1c904b7b1029598de6ace11bd2b1");
        transactionInputDTO2.setInputScript(inputScriptDTO2);
        transactionInputDTO2.setTransactionHash("53b780303a801edbf75fe3463799547daf88ae152c06d16769218cec78b5d48e");
        transactionInputDTO2.setTransactionOutputIndex(0);
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
        transactionOutputDTO2.setOutputScript(outputScriptDTO2);
        transactionOutputDTO2.setValue(20);
        transactionOutputDtoList.add(transactionOutputDTO2);

        transactionDTO2 = TransactionTool.transactionDTO(TransactionTool.bytesTransaction(transactionDTO));
        Assert.assertEquals(new Gson().toJson(transactionDTO),new Gson().toJson(transactionDTO2));
    }










    /**
     * 反序列化。将字节数组转换为交易
     */
    private static TransactionDTO transactionDTO(byte[] bytesTransaction) {
        TransactionDTO transactionDTO = new TransactionDTO();
        int start = 0;
        long bytesTransactionInputDtoListLength = ByteUtil.bytes64ToLong64WithBigEndian(Arrays.copyOfRange(bytesTransaction,start,start+8));
        start += 8;
        byte[] bytesTransactionInputDtoList = Arrays.copyOfRange(bytesTransaction,start, start+(int) bytesTransactionInputDtoListLength);
        start += bytesTransactionInputDtoListLength;
        List<TransactionInputDTO> transactionInputDtoList = transactionInputDTOList(bytesTransactionInputDtoList);
        transactionDTO.setInputs(transactionInputDtoList);

        long bytesTransactionOutputListLength = ByteUtil.bytes64ToLong64WithBigEndian(Arrays.copyOfRange(bytesTransaction,start,start+8));
        start += 8;
        byte[] bytesTransactionOutputList = Arrays.copyOfRange(bytesTransaction,start, start+(int) bytesTransactionOutputListLength);
        start += bytesTransactionOutputListLength;
        List<TransactionOutputDTO> transactionOutputDtoList = transactionOutputDTOList(bytesTransactionOutputList);
        transactionDTO.setOutputs(transactionOutputDtoList);
        return transactionDTO;
    }
    private static List<TransactionOutputDTO> transactionOutputDTOList(byte[] bytesTransactionOutputList) {
        if(bytesTransactionOutputList == null || bytesTransactionOutputList.length == 0){
            return null;
        }
        int start = 0;
        List<TransactionOutputDTO> transactionOutputDTOList = new ArrayList<>();
        while (start < bytesTransactionOutputList.length){
            long bytesTransactionOutputDTOLength = ByteUtil.bytes64ToLong64WithBigEndian(Arrays.copyOfRange(bytesTransactionOutputList,start,start+8));
            start += 8;
            byte[] bytesTransactionOutput = Arrays.copyOfRange(bytesTransactionOutputList,start, start+(int) bytesTransactionOutputDTOLength);
            start += bytesTransactionOutputDTOLength;
            TransactionOutputDTO transactionOutputDTO = transactionOutputDTO(bytesTransactionOutput);
            transactionOutputDTOList.add(transactionOutputDTO);
            if(start >= bytesTransactionOutputList.length){
                break;
            }
        }
        return transactionOutputDTOList;
    }
    private static TransactionOutputDTO transactionOutputDTO(byte[] bytesTransactionOutput) {
        int start = 0;
        long bytesOutputScriptLength = ByteUtil.bytes64ToLong64WithBigEndian(Arrays.copyOfRange(bytesTransactionOutput,start,start+8));
        start += 8;
        byte[] bytesOutputScript = Arrays.copyOfRange(bytesTransactionOutput,start, start+(int) bytesOutputScriptLength);
        start += bytesOutputScriptLength;
        OutputScriptDTO outputScriptDTO = ScriptTool.outputScriptDTO(bytesOutputScript);

        long bytesValueLength = ByteUtil.bytes64ToLong64WithBigEndian(Arrays.copyOfRange(bytesTransactionOutput,start,start+8));
        start += 8;
        byte[] bytesValue = Arrays.copyOfRange(bytesTransactionOutput,start, start+(int) bytesValueLength);
        start += bytesValueLength;

        TransactionOutputDTO transactionOutputDTO = new TransactionOutputDTO();
        transactionOutputDTO.setOutputScript(outputScriptDTO);
        transactionOutputDTO.setValue(ByteUtil.bytes64ToLong64WithBigEndian(bytesValue));
        return transactionOutputDTO;
    }
    private static List<TransactionInputDTO> transactionInputDTOList(byte[] bytesTransactionInputDtoList) {
        if(bytesTransactionInputDtoList == null || bytesTransactionInputDtoList.length == 0){
            return null;
        }
        int start = 0;
        List<TransactionInputDTO> transactionInputDTOList = new ArrayList<>();
        while (start < bytesTransactionInputDtoList.length){
            long bytesTransactionInputDTOLength = ByteUtil.bytes64ToLong64WithBigEndian(Arrays.copyOfRange(bytesTransactionInputDtoList,start,start+8));
            start += 8;
            byte[] bytesTransactionInput = Arrays.copyOfRange(bytesTransactionInputDtoList,start, start+(int) bytesTransactionInputDTOLength);
            start += bytesTransactionInputDTOLength;
            TransactionInputDTO transactionInputDTO = transactionInputDTO(bytesTransactionInput);
            transactionInputDTOList.add(transactionInputDTO);
            if(start >= bytesTransactionInputDtoList.length){
                break;
            }
        }
        return transactionInputDTOList;
    }
    private static TransactionInputDTO transactionInputDTO(byte[] bytesTransactionInputDTO) {
        int start = 0;
        long bytesTransactionHashLength = ByteUtil.bytes64ToLong64WithBigEndian(Arrays.copyOfRange(bytesTransactionInputDTO,start,start+8));
        start += 8;
        byte[] bytesTransactionHash = Arrays.copyOfRange(bytesTransactionInputDTO,start, start+(int) bytesTransactionHashLength);
        start += bytesTransactionHashLength;

        long bytesTransactionOutputIndexLength = ByteUtil.bytes64ToLong64WithBigEndian(Arrays.copyOfRange(bytesTransactionInputDTO,start,start+8));
        start += 8;
        byte[] bytesTransactionOutputIndex = Arrays.copyOfRange(bytesTransactionInputDTO,start, start+(int) bytesTransactionOutputIndexLength);
        start += bytesTransactionOutputIndexLength;


        TransactionInputDTO transactionInputDTO = new TransactionInputDTO();
        transactionInputDTO.setTransactionHash(HexUtil.bytesToHexString(bytesTransactionHash));
        transactionInputDTO.setTransactionOutputIndex(ByteUtil.bytes64ToLong64WithBigEndian(bytesTransactionOutputIndex));
        return transactionInputDTO;
    }
}
