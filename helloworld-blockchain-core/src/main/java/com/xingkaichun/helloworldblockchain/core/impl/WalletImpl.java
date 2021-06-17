package com.xingkaichun.helloworldblockchain.core.impl;

import com.xingkaichun.helloworldblockchain.core.BlockchainDatabase;
import com.xingkaichun.helloworldblockchain.core.CoreConfiguration;
import com.xingkaichun.helloworldblockchain.core.Wallet;
import com.xingkaichun.helloworldblockchain.core.model.script.InputScript;
import com.xingkaichun.helloworldblockchain.core.model.script.OutputScript;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.core.model.wallet.BuildTransactionRequest;
import com.xingkaichun.helloworldblockchain.core.model.wallet.BuildTransactionResponse;
import com.xingkaichun.helloworldblockchain.core.model.wallet.Recipient;
import com.xingkaichun.helloworldblockchain.core.tools.EncodeDecodeTool;
import com.xingkaichun.helloworldblockchain.core.tools.Model2DtoTool;
import com.xingkaichun.helloworldblockchain.core.tools.ScriptTool;
import com.xingkaichun.helloworldblockchain.core.tools.TransactionDtoTool;
import com.xingkaichun.helloworldblockchain.crypto.AccountUtil;
import com.xingkaichun.helloworldblockchain.crypto.ByteUtil;
import com.xingkaichun.helloworldblockchain.crypto.model.Account;
import com.xingkaichun.helloworldblockchain.netcore.dto.TransactionDto;
import com.xingkaichun.helloworldblockchain.netcore.dto.TransactionInputDto;
import com.xingkaichun.helloworldblockchain.netcore.dto.TransactionOutputDto;
import com.xingkaichun.helloworldblockchain.util.FileUtil;
import com.xingkaichun.helloworldblockchain.util.KvDbUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 默认实现
 *
 * @author 邢开春 409060350@qq.com
 */
public class WalletImpl extends Wallet {

    private CoreConfiguration coreConfiguration;
    private static final String WALLET_DATABASE_NAME = "WalletDatabase";

    public WalletImpl(CoreConfiguration coreConfiguration, BlockchainDatabase blockchainDatabase) {
        this.coreConfiguration = coreConfiguration;
        this.blockchainDatabase = blockchainDatabase;
    }


    @Override
    public List<Account> getAllAccounts() {
        List<Account> accountList = new ArrayList<>();
        //获取所有
        List<byte[]> bytesAccountList = KvDbUtil.gets(getWalletDatabasePath(),1,100000000);
        if(bytesAccountList != null){
            for(byte[] bytesAccount:bytesAccountList){
                Account account = EncodeDecodeTool.decodeToAccount(bytesAccount);
                accountList.add(account);
            }
        }
        return accountList;
    }

    @Override
    public Account createAccount() {
        return AccountUtil.randomAccount();
    }

    @Override
    public Account createAndSaveAccount() {
        Account account = createAccount();
        saveAccount(account);
        return account;
    }

    @Override
    public void saveAccount(Account account) {
        KvDbUtil.put(getWalletDatabasePath(),getKeyByAccount(account), EncodeDecodeTool.encodeAccount(account));
    }

    @Override
    public void deleteAccountByAddress(String address) {
        KvDbUtil.delete(getWalletDatabasePath(),getKeyByAddress(address));
    }

    @Override
    public long getBalanceByAddress(String address) {
        TransactionOutput utxo = blockchainDatabase.queryUnspentTransactionOutputByAddress(address);
        if(utxo != null){
            return utxo.getValue();
        }
        return 0L;
    }

    @Override
    public BuildTransactionResponse buildTransaction(BuildTransactionRequest request) {
        List<Account> allAccountList = getAllAccounts();
        if(allAccountList == null || allAccountList.isEmpty()){
            BuildTransactionResponse response = new BuildTransactionResponse();
            response.setBuildTransactionSuccess(false);
            response.setMessage("钱包中的余额不足支付。");
            return response;
        }

        BuildTransactionResponse response = new BuildTransactionResponse();
        response.setMessage("请输入足够的金额");
        response.setBuildTransactionSuccess(false);

        //创建一个地址用于存放找零
        Account payerChangeAccount = createAccount();
        saveAccount(payerChangeAccount);

        List<String> privateKeyList = new ArrayList<>();
        for(Account account:allAccountList){
            privateKeyList.add(account.getPrivateKey());
            response = buildTransactionDto(blockchainDatabase,privateKeyList,request.getRecipientList(),payerChangeAccount.getAddress(),request.getFee());
            if(response.isBuildTransactionSuccess()){
                return response;
            }
        }
        return response;
    }




    private String getWalletDatabasePath() {
        return FileUtil.newPath(coreConfiguration.getCorePath(), WALLET_DATABASE_NAME);
    }
    private byte[] getKeyByAddress(String address){
        return ByteUtil.stringToUtf8Bytes(address);
    }
    private byte[] getKeyByAccount(Account account){
        return getKeyByAddress(account.getAddress());
    }


    private BuildTransactionResponse buildTransactionDto(BlockchainDatabase blockchainDatabase, List<String> payerPrivateKeyList, List<Recipient> recipientList, String payerChangeAddress, long fee) {
        Map<String, TransactionOutput> privateKeyUtxoMap = new HashMap<>();
        BuildTransactionResponse response = new BuildTransactionResponse();
        response.setMessage("请输入足够的金额");
        response.setBuildTransactionSuccess(false);

        for(String privateKey : payerPrivateKeyList){
            String address = AccountUtil.accountFromPrivateKey(privateKey).getAddress();
            TransactionOutput utxo = blockchainDatabase.queryUnspentTransactionOutputByAddress(address);
            if(utxo == null || utxo.getValue() <= 0){
                continue;
            }
            privateKeyUtxoMap.put(privateKey,utxo);
            response = buildTransactionDto(privateKeyUtxoMap,recipientList,payerChangeAddress,fee);
            if(response.isBuildTransactionSuccess()){
                break;
            }
        }
        return response;
    }

    private static BuildTransactionResponse buildTransactionDto(Map<String,TransactionOutput> privateKeyUtxoMap, List<Recipient> recipientList, String payerChangeAddress, long fee) {
        //最少付款总金额
        long minInputValues = 0;
        if(recipientList != null){
            //支付钱款
            for(Recipient recipient : recipientList){
                minInputValues += recipient.getValue();
            }
        }
        //交易手续费
        minInputValues += fee;

        //创建交易输出
        List<TransactionOutputDto> transactionOutputDtoList = new ArrayList<>();
        List<TransactionOutput> innerTransactionOutputList = new ArrayList<>();

        if(recipientList != null){
            for(Recipient recipient : recipientList){
                TransactionOutputDto transactionOutputDto = new TransactionOutputDto();
                transactionOutputDto.setValue(recipient.getValue());
                OutputScript outputScript = ScriptTool.createPayToPublicKeyHashOutputScript(recipient.getAddress());
                transactionOutputDto.setOutputScript(Model2DtoTool.outputScript2OutputScriptDto(outputScript));
                transactionOutputDtoList.add(transactionOutputDto);

                TransactionOutput innerTransactionOutput = new TransactionOutput();
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
            String privateKey = entry.getKey();
            TransactionOutput utxo = entry.getValue();
            if(utxo == null){
                break;
            }
            inputValues += utxo.getValue();
            //交易输入
            inputs.add(utxo);
            inputPrivateKeyList.add(privateKey);
            if(inputValues >= minInputValues){
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
        List<TransactionInputDto> transactionInputDtoList = new ArrayList<>();
        for(TransactionOutput input:inputs){
            TransactionInputDto transactionInputDto = new TransactionInputDto();
            transactionInputDto.setTransactionHash(input.getTransactionHash());
            transactionInputDto.setTransactionOutputIndex(input.getTransactionOutputIndex());
            transactionInputDtoList.add(transactionInputDto);
        }

        //构建交易
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setInputs(transactionInputDtoList);
        transactionDto.setOutputs(transactionOutputDtoList);

        //总收款金额
        long outputValues = 0;
        if(recipientList != null){
            for(Recipient recipient : recipientList){
                outputValues += recipient.getValue();
            }
        }

        //找零
        long change = inputValues - outputValues - fee;
        TransactionOutput payerChangeTransactionOutput = null;
        if(change > 0){
            TransactionOutputDto transactionOutputDto = new TransactionOutputDto();
            transactionOutputDto.setValue(change);
            OutputScript outputScript = ScriptTool.createPayToPublicKeyHashOutputScript(payerChangeAddress);
            transactionOutputDto.setOutputScript(Model2DtoTool.outputScript2OutputScriptDto(outputScript));
            transactionOutputDtoList.add(transactionOutputDto);

            payerChangeTransactionOutput = new TransactionOutput();
            payerChangeTransactionOutput.setAddress(payerChangeAddress);
            payerChangeTransactionOutput.setValue(change);
            payerChangeTransactionOutput.setOutputScript(outputScript);
        }

        //签名
        for(int i=0;i<transactionInputDtoList.size();i++){
            String privateKey = inputPrivateKeyList.get(i);
            String publicKey = AccountUtil.accountFromPrivateKey(privateKey).getPublicKey();
            TransactionInputDto transactionInputDto = transactionInputDtoList.get(i);
            String signature = TransactionDtoTool.signature(privateKey,transactionDto);
            InputScript inputScript = ScriptTool.createPayToPublicKeyHashInputScript(signature, publicKey);
            transactionInputDto.setInputScript(Model2DtoTool.inputScript2InputScriptDto(inputScript));
        }


        BuildTransactionResponse buildTransactionResponse = new BuildTransactionResponse();
        buildTransactionResponse.setBuildTransactionSuccess(true);
        buildTransactionResponse.setMessage("构建交易成功");
        buildTransactionResponse.setTransactionHash(TransactionDtoTool.calculateTransactionHash(transactionDto));
        buildTransactionResponse.setFee(fee);
        buildTransactionResponse.setPayerChangeTransactionOutput(payerChangeTransactionOutput);
        buildTransactionResponse.setTransactionInputs(inputs);
        buildTransactionResponse.setTransactionOutputs(innerTransactionOutputList);
        buildTransactionResponse.setTransaction(transactionDto);
        return buildTransactionResponse;
    }
}
