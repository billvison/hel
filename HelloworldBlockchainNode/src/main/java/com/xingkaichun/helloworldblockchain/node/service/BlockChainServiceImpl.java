package com.xingkaichun.helloworldblockchain.node.service;

import com.xingkaichun.helloworldblockchain.core.BlockChainCore;
import com.xingkaichun.helloworldblockchain.core.dto.*;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.key.StringPrivateKey;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionType;
import com.xingkaichun.helloworldblockchain.core.model.wallet.Wallet;
import com.xingkaichun.helloworldblockchain.core.utils.atomic.WalletUtil;
import com.xingkaichun.helloworldblockchain.node.dto.blockchain.NormalTransactionDto;
import com.xingkaichun.helloworldblockchain.node.dto.blockchain.request.QueryTransactionByTransactionUuidRequest;
import com.xingkaichun.helloworldblockchain.node.dto.blockchain.request.QueryUtxosByAddressRequest;
import com.xingkaichun.helloworldblockchain.node.dto.node.request.QueryBlockDtoByBlockHeightRequest;
import com.xingkaichun.helloworldblockchain.node.dto.node.request.QueryBlockHashByBlockHeightRequest;
import com.xingkaichun.helloworldblockchain.node.plugins.AddressUtxoPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class BlockChainServiceImpl implements BlockChainService {

    @Autowired
    private BlockChainCore blockChainCore;

    @Autowired
    private AddressUtxoPlugin addressUtxoPlugin;


    @Override
    public Wallet generateWallet() {
        return WalletUtil.generateWallet();
    }

    @Override
    public Transaction QueryTransactionByTransactionUUID(QueryTransactionByTransactionUuidRequest request) throws Exception {
        return blockChainCore.getBlockChainDataBase().findTransactionByTransactionUuid(request.getTransactionUUID());
    }

    @Override
    public List<TransactionOutput> queryUtxoListByAddress(QueryUtxosByAddressRequest request) throws Exception {
        List<TransactionOutput> utxo =  addressUtxoPlugin.queryUtxoListByAddress(request.getAddress());
        return utxo;
    }

    @Override
    public TransactionDTO sumiteTransaction(NormalTransactionDto normalTransactionDto) throws Exception {
        TransactionDTO transactionDTO = classCast(normalTransactionDto);
        blockChainCore.getMiner().getMinerTransactionDtoDataBase().insertTransactionDTO(transactionDTO);
        return transactionDTO;
    }

    private TransactionDTO classCast(NormalTransactionDto normalTransactionDto) throws Exception {
        List<NormalTransactionDto.Output> outputs = normalTransactionDto.getOutputs();
        List<TransactionOutputDTO> transactionOutputDtoList = new ArrayList<>();
        if(outputs != null && outputs.size()!=0){
            for(NormalTransactionDto.Output o:outputs){
                TransactionOutputDTO transactionOutputDTO = new TransactionOutputDTO();
                transactionOutputDTO.setTransactionOutputUUID(UUID.randomUUID().toString());
                transactionOutputDTO.setAddress(o.getAddress());
                transactionOutputDTO.setValue(o.getValue());
                transactionOutputDtoList.add(transactionOutputDTO);
            }
        }
        List<NormalTransactionDto.Input> inputs = normalTransactionDto.getInputs();
        List<TransactionInputDTO> transactionInputDtoList = new ArrayList<>();
        for(NormalTransactionDto.Input input:inputs){
            TransactionInputDTO transactionInputDTO = new TransactionInputDTO();
            transactionInputDTO.setUnspendTransactionOutputUUID(input.getUtxoUuid());
            transactionInputDTO.setPublicKey(input.getPublicKey());
            transactionInputDtoList.add(transactionInputDTO);
        }

        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setTimestamp(System.currentTimeMillis());
        transactionDTO.setTransactionUUID(UUID.randomUUID().toString());
        transactionDTO.setTransactionType(TransactionType.MINER);
        transactionDTO.setInputs(transactionInputDtoList);
        transactionDTO.setOutputs(transactionOutputDtoList);
        signatureTransactionDTO(transactionDTO,new StringPrivateKey(normalTransactionDto.getPrivateKey()));
        return transactionDTO;
    }


    @Override
    public TransactionDTO signatureTransactionDTO(TransactionDTO transactionDTO, StringPrivateKey stringPrivateKey) throws Exception {
        DtoUtils.signature(transactionDTO,stringPrivateKey);
        return transactionDTO;
    }

    @Override
    public void startMine() throws Exception {
        blockChainCore.getMiner().resume();
    }

    @Override
    public void stopMine() throws Exception {
        blockChainCore.getMiner().stop();
    }

    @Override
    public String queryBlockHashByBlockHeight(QueryBlockHashByBlockHeightRequest request) throws Exception {
        Block block = blockChainCore.getBlockChainDataBase().findBlockByBlockHeight(request.getBlockHeight());
        if(block == null){
            return null;
        }
        return block.getHash();
    }

    @Override
    public BlockDTO queryBlockDtoByBlockHeight(QueryBlockDtoByBlockHeightRequest request) throws Exception {
        int blockHeight = request.getBlockHeight();
        Block block = blockChainCore.getBlockChainDataBase().findBlockByBlockHeight(blockHeight);
        BlockDTO blockDTO = DtoUtils.classCast(block);
        return blockDTO;
    }

    @Override
    public int queryBlockChainHeight() throws Exception {
        return blockChainCore.getBlockChainDataBase().obtainBlockChainHeight();
    }
}
