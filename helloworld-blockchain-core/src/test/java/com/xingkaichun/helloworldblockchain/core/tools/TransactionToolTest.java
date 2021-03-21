package com.xingkaichun.helloworldblockchain.core.tools;

import com.xingkaichun.helloworldblockchain.core.StackBasedVirtualMachine;
import com.xingkaichun.helloworldblockchain.core.model.script.OperationCodeEnum;
import com.xingkaichun.helloworldblockchain.core.model.script.OutputScript;
import com.xingkaichun.helloworldblockchain.crypto.HexUtil;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.*;
import com.xingkaichun.helloworldblockchain.util.ByteUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class TransactionToolTest {

    @Test
    public void bytesTransactionTest()
    {
        TransactionDTO transactionDTO = new TransactionDTO();
        List<TransactionInputDTO> transactionInputDtoList = new ArrayList<>();
        TransactionInputDTO transactionInputDTO = new TransactionInputDTO();
        InputScriptDTO inputScriptDTO = new InputScriptDTO();
        transactionInputDTO.setInputScriptDTO(inputScriptDTO);
        UnspendTransactionOutputDTO unspendTransactionOutputDTO = new UnspendTransactionOutputDTO();
        unspendTransactionOutputDTO.setTransactionHash("53b780303a801edbf75fe3463799547daf88ae152c06d16769218cec78b5d48e");
        unspendTransactionOutputDTO.setTransactionOutputIndex(0);
        transactionInputDTO.setUnspendTransactionOutputDTO(unspendTransactionOutputDTO);
        transactionInputDtoList.add(transactionInputDTO);
        transactionDTO.setTransactionInputDtoList(transactionInputDtoList);

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
        byte[] bytesTransaction = TransactionTool.bytesTransaction(transactionDTO);

        TransactionDTO transactionDTO2 = from(bytesTransaction);
        
        System.out.println(bytesTransaction);
    }

    private TransactionDTO from(byte[] bytesTransaction) {
        TransactionDTO transactionDTO = new TransactionDTO();
        int start = 0;
        byte[] length = Arrays.copyOfRange(bytesTransaction,start,8);
        start+=8;
        byte[] bytesTransactionInputDtoList = Arrays.copyOfRange(bytesTransaction,start, (int) ByteUtil.bytes8BigEndianToLong(length));
        start+=ByteUtil.bytes8BigEndianToLong(length);


/*        byte[] bytesTransactionHash = HexUtil.hexStringToBytes(unspendTransactionOutputDto.getTransactionHash());
        byte[] bytesTransactionOutputIndex = ByteUtil.longToBytes8BigEndian(unspendTransactionOutputDto.getTransactionOutputIndex());
        byte[] bytesInputScript = ScriptTool.bytesScript(transactionInputDTO.getInputScriptDTO());*/


        return null;
    }
}
