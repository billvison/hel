package com.xingkaichun.helloworldblockchain.node.service;

import com.xingkaichun.helloworldblockchain.core.BlockChainCore;
import com.xingkaichun.helloworldblockchain.core.BlockChainDataBase;
import com.xingkaichun.helloworldblockchain.core.script.ScriptMachine;
import com.xingkaichun.helloworldblockchain.core.utils.NodeTransportUtils;
import com.xingkaichun.helloworldblockchain.core.utils.atomic.BlockchainUuidUtil;
import com.xingkaichun.helloworldblockchain.crypto.KeyUtil;
import com.xingkaichun.helloworldblockchain.crypto.model.StringKey;
import com.xingkaichun.helloworldblockchain.core.utils.WalletUtil;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.crypto.model.StringAddress;
import com.xingkaichun.helloworldblockchain.crypto.model.StringPrivateKey;
import com.xingkaichun.helloworldblockchain.core.model.key.Wallet;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.crypto.model.StringPublicKey;
import com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.NormalTransactionDto;
import com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.request.QueryMiningTransactionListRequest;
import com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.request.QueryTxosByAddressRequest;
import com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.request.QueryUtxosByAddressRequest;
import com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.response.SubmitNormalTransactionResponse;
import com.xingkaichun.helloworldblockchain.node.dto.common.EmptyResponse;
import com.xingkaichun.helloworldblockchain.node.dto.common.ServiceResult;
import com.xingkaichun.helloworldblockchain.node.dto.common.page.PageCondition;
import com.xingkaichun.helloworldblockchain.node.dto.nodeserver.Node;
import com.xingkaichun.helloworldblockchain.node.transport.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
public class BlockChainCoreServiceImpl implements BlockChainCoreService {

    @Autowired
    private BlockChainCore blockChainCore;

    @Autowired
    private NodeService nodeService;

    @Autowired
    private BlockchainNodeClientService blockchainNodeClientService;

    @Autowired
    private BlockchainNodeServerService blockchainNodeServerService;

    @Override
    public WalletDTO generateWalletDTO() {
        Wallet wallet = WalletUtil.generateWallet();
        return NodeTransportUtils.classCast(wallet);
    }

    @Override
    public TransactionDTO queryTransactionDtoByTransactionUUID(String transactionUUID) throws Exception {
        Transaction transaction = blockChainCore.getBlockChainDataBase().findTransactionByTransactionUuid(transactionUUID);
        if(transaction == null){
            return null;
        }
        TransactionDTO transactionDTO = NodeTransportUtils.classCast(transaction);
        return transactionDTO;
    }

    @Override
    public List<Transaction> queryTransactionByTransactionHeight(PageCondition pageCondition) throws Exception {
        BlockChainDataBase blockChainDataBase = blockChainCore.getBlockChainDataBase();
        List<Transaction>  transactionList = blockChainDataBase.queryTransactionByTransactionHeight(BigInteger.valueOf(pageCondition.getFrom()),BigInteger.valueOf(pageCondition.getSize()));
        return transactionList;
    }

    @Override
    public List<TransactionOutput> queryUtxoListByAddress(QueryUtxosByAddressRequest request) throws Exception {
        PageCondition pageCondition = request.getPageCondition();
        if(pageCondition == null){
            pageCondition = PageCondition.defaultPageCondition;
        }
        StringAddress stringAddress = new StringAddress(request.getAddress());
        List<TransactionOutput> utxo =  blockChainCore.getBlockChainDataBase().querUnspendTransactionOuputListByAddress(stringAddress,pageCondition.getFrom(),pageCondition.getSize());
        return utxo;
    }

    @Override
    public List<TransactionOutput> queryTxoListByAddress(QueryTxosByAddressRequest request) throws Exception {
        PageCondition pageCondition = request.getPageCondition();
        if(pageCondition == null){
            pageCondition = PageCondition.defaultPageCondition;
        }
        StringAddress stringAddress = new StringAddress(request.getAddress());
        List<TransactionOutput> utxo =  blockChainCore.getBlockChainDataBase().queryTransactionOuputListByAddress(stringAddress,pageCondition.getFrom(),pageCondition.getSize());
        return utxo;
    }

    @Override
    public SubmitNormalTransactionResponse sumiteTransaction(NormalTransactionDto normalTransactionDto) throws Exception {
        TransactionDTO transactionDTO = classCast(normalTransactionDto);
        blockChainCore.getMiner().getMinerTransactionDtoDataBase().insertTransactionDTO(transactionDTO);
        List<Node> nodes = nodeService.queryAllNoForkAliveNodeList();

        List<SubmitNormalTransactionResponse.Node> successSubmitNode = new ArrayList<>();
        List<SubmitNormalTransactionResponse.Node> failSubmitNode = new ArrayList<>();
        if(nodes != null){
            for(Node node:nodes){
                ServiceResult<EmptyResponse> submitSuccess = blockchainNodeClientService.sumiteTransaction(node,transactionDTO);
                if(ServiceResult.isSuccess(submitSuccess)){
                    successSubmitNode.add(new SubmitNormalTransactionResponse.Node(node.getIp(),node.getPort()));
                } else {
                    failSubmitNode.add(new SubmitNormalTransactionResponse.Node(node.getIp(),node.getPort()));
                }
            }
        }

        SubmitNormalTransactionResponse response = new SubmitNormalTransactionResponse();
        response.setTransactionDTO(transactionDTO);
        response.setSuccessSubmitNode(successSubmitNode);
        response.setFailSubmitNode(failSubmitNode);
        return response;
    }

    private TransactionDTO classCast(NormalTransactionDto normalTransactionDto) throws Exception {
        String privateKey = normalTransactionDto.getPrivateKey();
        StringKey stringKey = KeyUtil.stringKeyFrom(new StringPrivateKey(privateKey));
        long currentTimeMillis = System.currentTimeMillis();

        List<NormalTransactionDto.Output> outputs = normalTransactionDto.getOutputs();
        List<TransactionOutputDTO> transactionOutputDtoList = new ArrayList<>();
        if(outputs != null){
            for(NormalTransactionDto.Output o:outputs){
                TransactionOutputDTO transactionOutputDTO = new TransactionOutputDTO();
                transactionOutputDTO.setAddress(o.getAddress());
                transactionOutputDTO.setValue(o.getValue());
                transactionOutputDTO.setScriptLock(ScriptMachine.createPayToClassicAddressOutputScript(o.getAddress()));
                transactionOutputDTO.setTransactionOutputUUID(BlockchainUuidUtil.calculateTransactionOutputUUID(transactionOutputDTO,currentTimeMillis));
                transactionOutputDtoList.add(transactionOutputDTO);
            }
        }
        List<String> inputs = normalTransactionDto.getInputs();
        List<TransactionInputDTO> transactionInputDtoList = new ArrayList<>();
        for(String input:inputs){
            TransactionInputDTO transactionInputDTO = new TransactionInputDTO();
            transactionInputDTO.setUnspendTransactionOutputUUID(input);
            transactionInputDtoList.add(transactionInputDTO);
        }

        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setTimestamp(currentTimeMillis);
        transactionDTO.setInputs(transactionInputDtoList);
        transactionDTO.setOutputs(transactionOutputDtoList);
        transactionDTO.setTransactionUUID(BlockchainUuidUtil.calculateTransactioUUID(transactionDTO));

        for(TransactionInputDTO transactionInputDTO:transactionInputDtoList){
            StringPublicKey stringPublicKey = stringKey.getStringPublicKey();
            String signature = signatureTransactionDTO(transactionDTO,new StringPrivateKey(privateKey));
            transactionInputDTO.setScriptKey(ScriptMachine.createPayToClassicAddressInputScript(signature,stringPublicKey.getValue()));
        }
        return transactionDTO;
    }

    public String signatureTransactionDTO(TransactionDTO transactionDTO, StringPrivateKey stringPrivateKey) throws Exception {
        String signature = NodeTransportUtils.signature(transactionDTO,stringPrivateKey);
        return signature;
    }

    @Override
    public String queryBlockHashByBlockHeight(BigInteger blockHeight) throws Exception {
        Block block = blockChainCore.getBlockChainDataBase().findNoTransactionBlockByBlockHeight(blockHeight);
        if(block == null){
            return null;
        }
        return block.getHash();
    }

    @Override
    public BlockDTO queryBlockDtoByBlockHeight(BigInteger blockHeight) throws Exception {
        Block block = blockChainCore.getBlockChainDataBase().findBlockByBlockHeight(blockHeight);
        if(block == null){
            return null;
        }
        BlockDTO blockDTO = NodeTransportUtils.classCast(block);
        return blockDTO;
    }

    @Override
    public Block queryNoTransactionBlockDtoByBlockHash(String blockHash) throws Exception {
        BlockChainDataBase blockChainDataBase = blockChainCore.getBlockChainDataBase();
        BigInteger blockHeight = blockChainDataBase.findBlockHeightByBlockHash(blockHash);
        if(blockHeight == null){
            return null;
        }
        Block block = blockChainDataBase.findNoTransactionBlockByBlockHeight(blockHeight);
        return block;
    }

    @Override
    public Block queryNoTransactionBlockDtoByBlockHeight(BigInteger blockHeight) throws Exception {
        Block block = blockChainCore.getBlockChainDataBase().findNoTransactionBlockByBlockHeight(blockHeight);
        return block;
    }

    @Override
    public BigInteger queryBlockChainHeight() throws Exception {
        return blockChainCore.getBlockChainDataBase().obtainBlockChainHeight();
    }

    @Override
    public List<TransactionDTO> queryMiningTransactionList(QueryMiningTransactionListRequest request) throws Exception {
        PageCondition pageCondition = request.getPageCondition();
        if(pageCondition == null){
            pageCondition = PageCondition.defaultPageCondition;
        }
        List<TransactionDTO> transactionDtoList = blockChainCore.getMiner().getMinerTransactionDtoDataBase().selectTransactionDtoList(blockChainCore.getBlockChainDataBase(),pageCondition.getFrom(),pageCondition.getSize());
        return transactionDtoList;
    }

    @Override
    public TransactionDTO queryMiningTransactionDtoByTransactionUUID(String transactionUUID) throws Exception {
        TransactionDTO transactionDTO = blockChainCore.getMiner().getMinerTransactionDtoDataBase().selectTransactionDtoByTransactionUUID(transactionUUID);
        return transactionDTO;
    }

    @Override
    public void removeBlocksUtilBlockHeightLessThan(BigInteger blockHeight) throws Exception {
        blockChainCore.getBlockChainDataBase().removeBlocksUtilBlockHeightLessThan(blockHeight);
    }
}
