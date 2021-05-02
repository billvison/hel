package com.xingkaichun.helloworldblockchain.core.tools;

import com.xingkaichun.helloworldblockchain.core.BlockchainCore;
import com.xingkaichun.helloworldblockchain.core.model.pay.BuildTransactionResponse;
import com.xingkaichun.helloworldblockchain.core.model.pay.Recipient;
import com.xingkaichun.helloworldblockchain.core.model.script.InputScript;
import com.xingkaichun.helloworldblockchain.core.model.script.OutputScript;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.crypto.AccountUtil;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.TransactionDTO;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.TransactionInputDTO;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.TransactionOutputDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WalletTool {

    public static long obtainBalance(BlockchainCore blockchainCore, String address) {
        TransactionOutput utxo = blockchainCore.getBlockchainDataBase().queryUnspentTransactionOutputByAddress(address);
        if(utxo != null){
            return utxo.getValue();
        }
        return 0L;
    }

    public static long obtainSpent(BlockchainCore blockchainCore, String address) {
        TransactionOutput utxo = blockchainCore.getBlockchainDataBase().querySpentTransactionOutputByAddress(address);
        if(utxo != null){
            return utxo.getValue();
        }
        return 0L;
    }

    public static long obtainReceipt(BlockchainCore blockchainCore, String address) {
        //交易输出总金额
        TransactionOutput txo = blockchainCore.getBlockchainDataBase().queryTransactionOutputByAddress(address);
        return txo==null?0:txo.getValue();
    }

    public static BuildTransactionResponse buildTransactionDTO(Map<String,TransactionOutput> privateKeyUtxoMap, List<Recipient> recipientList, String payerChangeAddress, long fee) {
        //付款总金额
        long outputValues = 0;
        if(recipientList != null){
            //支付钱款
            for(Recipient recipient : recipientList){
                outputValues += recipient.getValue();
            }
        }
        //交易手续费
        outputValues += fee;

        //创建交易输出
        List<TransactionOutputDTO> transactionOutputDtoList = new ArrayList<>();
        List<BuildTransactionResponse.InnerTransactionOutput> innerTransactionOutputList = new ArrayList<>();

        if(recipientList != null){
            for(Recipient recipient : recipientList){
                TransactionOutputDTO transactionOutputDTO = new TransactionOutputDTO();
                transactionOutputDTO.setValue(recipient.getValue());
                OutputScript outputScript = ScriptTool.createPayToPublicKeyHashOutputScript(recipient.getAddress());
                transactionOutputDTO.setOutputScript(Model2DtoTool.outputScript2OutputScriptDTO(outputScript));
                transactionOutputDtoList.add(transactionOutputDTO);

                BuildTransactionResponse.InnerTransactionOutput innerTransactionOutput = new BuildTransactionResponse.InnerTransactionOutput();
                innerTransactionOutput.setAddress(recipient.getAddress());
                innerTransactionOutput.setValue(recipient.getValue());
                innerTransactionOutput.setOutputScript(outputScript);
                innerTransactionOutputList.add(innerTransactionOutput);
            }
        }

        //获取足够的金额
        //交易输入列表
        List<TransactionOutput> inputs = new ArrayList<>();
        List<String> inputPrivateKeyList = new ArrayList<>();
        //交易输入总金额
        long inputValues = 0;
        boolean haveEnoughMoneyToPay = false;
        for(Map.Entry<String,TransactionOutput> entry: privateKeyUtxoMap.entrySet()){
            if(haveEnoughMoneyToPay){
                break;
            }
            String privateKey = entry.getKey();
            TransactionOutput utxo = entry.getValue();
            if(utxo == null){
                break;
            }
            inputValues += utxo.getValue();
            //交易输入
            inputs.add(utxo);
            inputPrivateKeyList.add(privateKey);
            if(inputValues >= outputValues){
                haveEnoughMoneyToPay = true;
                break;
            }
        }

        if(!haveEnoughMoneyToPay){
            BuildTransactionResponse buildTransactionResponse = new BuildTransactionResponse();
            buildTransactionResponse.setBuildTransactionSuccess(false);
            buildTransactionResponse.setMessage("账户没有足够的金额去支付");
            return buildTransactionResponse;
        }

        //构建交易输入
        List<TransactionInputDTO> transactionInputDtoList = new ArrayList<>();
        for(TransactionOutput input:inputs){
            TransactionInputDTO transactionInputDTO = new TransactionInputDTO();
            transactionInputDTO.setTransactionHash(input.getTransactionHash());
            transactionInputDTO.setTransactionOutputIndex(input.getTransactionOutputIndex());
            transactionInputDtoList.add(transactionInputDTO);
        }

        //构建交易
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setInputs(transactionInputDtoList);
        transactionDTO.setOutputs(transactionOutputDtoList);

        //找零
        long change = inputValues - outputValues - fee;
        BuildTransactionResponse.InnerTransactionOutput payerChange = null;
        if(change > 0){
            TransactionOutputDTO transactionOutputDTO = new TransactionOutputDTO();
            transactionOutputDTO.setValue(change);
            OutputScript outputScript = ScriptTool.createPayToPublicKeyHashOutputScript(payerChangeAddress);
            transactionOutputDTO.setOutputScript(Model2DtoTool.outputScript2OutputScriptDTO(outputScript));
            transactionOutputDtoList.add(transactionOutputDTO);

            payerChange = new BuildTransactionResponse.InnerTransactionOutput();
            payerChange.setAddress(payerChangeAddress);
            payerChange.setValue(change);
            payerChange.setOutputScript(outputScript);
        }

        //签名
        for(int i=0;i<transactionInputDtoList.size();i++){
            String privateKey = inputPrivateKeyList.get(i);
            String publicKey = AccountUtil.accountFromPrivateKey(privateKey).getPublicKey();
            TransactionInputDTO transactionInputDTO = transactionInputDtoList.get(i);
            String signature = TransactionTool.signature(privateKey,transactionDTO);
            InputScript inputScript = ScriptTool.createPayToPublicKeyHashInputScript(signature, publicKey);
            transactionInputDTO.setInputScript(Model2DtoTool.inputScript2InputScriptDTO(inputScript));
        }


        BuildTransactionResponse buildTransactionResponse = new BuildTransactionResponse();
        buildTransactionResponse.setBuildTransactionSuccess(true);
        buildTransactionResponse.setMessage("构建交易成功");
        buildTransactionResponse.setTransactionHash(TransactionTool.calculateTransactionHash(transactionDTO));
        buildTransactionResponse.setFee(fee);
        buildTransactionResponse.setPayerChange(payerChange);
        buildTransactionResponse.setTransactionInputList(inputs);
        buildTransactionResponse.setTransactionOutputList(innerTransactionOutputList);
        buildTransactionResponse.setTransactionDTO(transactionDTO);
        return buildTransactionResponse;
    }
}
