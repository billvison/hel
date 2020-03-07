package com.xingkaichun.helloworldblockchain.node.service;

import com.xingkaichun.helloworldblockchain.core.BlockChainCore;
import com.xingkaichun.helloworldblockchain.core.utils.DtoUtils;
import com.xingkaichun.helloworldblockchain.core.utils.atomic.KeyUtil;
import com.xingkaichun.helloworldblockchain.core.utils.atomic.WalletUtil;
import com.xingkaichun.helloworldblockchain.dto.*;
import com.xingkaichun.helloworldblockchain.model.Block;
import com.xingkaichun.helloworldblockchain.model.key.StringAddress;
import com.xingkaichun.helloworldblockchain.model.key.StringPrivateKey;
import com.xingkaichun.helloworldblockchain.model.key.Wallet;
import com.xingkaichun.helloworldblockchain.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.NormalTransactionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class BlockChainServiceImpl implements BlockChainService {

    @Autowired
    private BlockChainCore blockChainCore;

    @Override
    public WalletDTO generateWalletDTO() {
        Wallet wallet = WalletUtil.generateWallet();
        return DtoUtils.classCast(wallet);
    }

    @Override
    public TransactionDTO QueryTransactionDtoByTransactionUUID(String transactionUUID) throws Exception {
        Transaction transaction = blockChainCore.getBlockChainDataBase().findTransactionByTransactionUuid(transactionUUID);
        if(transaction == null){
            return null;
        }
        TransactionDTO transactionDTO = DtoUtils.classCast(transaction);
        return transactionDTO;
    }

    @Override
    public List<TransactionOutput> queryUtxoListByAddress(String address) throws Exception {
        StringAddress stringAddress = new StringAddress(address);
        List<TransactionOutput> utxo =  blockChainCore.getBlockChainDataBase().querUnspendTransactionOuputListByAddress(stringAddress);
        return utxo;
    }

    @Override
    public List<TransactionOutput> queryTxoListByAddress(String address) throws Exception {
        StringAddress stringAddress = new StringAddress(address);
        List<TransactionOutput> utxo =  blockChainCore.getBlockChainDataBase().queryTransactionOuputListByAddress(stringAddress);
        return utxo;
    }

    @Override
    public TransactionDTO sumiteTransaction(NormalTransactionDto normalTransactionDto) throws Exception {
        TransactionDTO transactionDTO = classCast(normalTransactionDto);
        blockChainCore.getMiner().getMinerTransactionDtoDataBase().insertTransactionDTO(transactionDTO);
        return transactionDTO;
    }

    private TransactionDTO classCast(NormalTransactionDto normalTransactionDto) throws Exception {
        String privateKey = normalTransactionDto.getPrivateKey();
        ECPublicKey ecPublicKey = KeyUtil.publicFromPrivate((ECPrivateKey) KeyUtil.convertStringPrivateKeyToPrivateKey(new StringPrivateKey(privateKey)));


        List<NormalTransactionDto.Output> outputs = normalTransactionDto.getOutputs();
        List<TransactionOutputDTO> transactionOutputDtoList = new ArrayList<>();
        if(outputs != null){
            for(NormalTransactionDto.Output o:outputs){
                TransactionOutputDTO transactionOutputDTO = new TransactionOutputDTO();
                transactionOutputDTO.setTransactionOutputUUID(UUID.randomUUID().toString());
                transactionOutputDTO.setAddress(o.getAddress());
                transactionOutputDTO.setValue(o.getValue());
                transactionOutputDtoList.add(transactionOutputDTO);
            }
        }
        List<String> inputs = normalTransactionDto.getInputs();
        List<TransactionInputDTO> transactionInputDtoList = new ArrayList<>();
        for(String input:inputs){
            TransactionInputDTO transactionInputDTO = new TransactionInputDTO();
            transactionInputDTO.setUnspendTransactionOutputUUID(input);
            transactionInputDTO.setPublicKey(KeyUtil.convertPublicKeyToStringPublicKey(ecPublicKey).getValue());
            transactionInputDtoList.add(transactionInputDTO);
        }

        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setTimestamp(System.currentTimeMillis());
        transactionDTO.setTransactionUUID(UUID.randomUUID().toString());
        transactionDTO.setTransactionType(TransactionTypeDTO.NORMAL);
        transactionDTO.setInputs(transactionInputDtoList);
        transactionDTO.setOutputs(transactionOutputDtoList);
        signatureTransactionDTO(transactionDTO,new StringPrivateKey(privateKey));
        return transactionDTO;
    }


    @Override
    public TransactionDTO signatureTransactionDTO(TransactionDTO transactionDTO, StringPrivateKey stringPrivateKey) throws Exception {
        String signature = DtoUtils.signature(transactionDTO,stringPrivateKey);
        transactionDTO.setSignature(signature);
        return transactionDTO;
    }

    @Override
    public String queryBlockHashByBlockHeight(int blockHeight) throws Exception {
        Block block = blockChainCore.getBlockChainDataBase().findBlockByBlockHeight(blockHeight);
        if(block == null){
            return null;
        }
        return block.getHash();
    }

    @Override
    public BlockDTO queryBlockDtoByBlockHeight(int blockHeight) throws Exception {
        Block block = blockChainCore.getBlockChainDataBase().findBlockByBlockHeight(blockHeight);
        BlockDTO blockDTO = DtoUtils.classCast(block);
        return blockDTO;
    }

    @Override
    public int queryBlockChainHeight() throws Exception {
        return blockChainCore.getBlockChainDataBase().obtainBlockChainHeight();
    }

    @Override
    public List<TransactionDTO> queryMiningTransactionList() throws Exception {
        List<TransactionDTO> transactionDtoList = blockChainCore.getMiner().getMinerTransactionDtoDataBase().selectTransactionDtoList(blockChainCore.getBlockChainDataBase(),0,100);
        return transactionDtoList;
    }

    @Override
    public TransactionDTO queryMiningTransactionDtoByTransactionUUID(String transactionUUID) throws Exception {
        TransactionDTO transactionDTO = blockChainCore.getMiner().getMinerTransactionDtoDataBase().selectTransactionDtoByTransactionUUID(transactionUUID);
        return transactionDTO;
    }
}
