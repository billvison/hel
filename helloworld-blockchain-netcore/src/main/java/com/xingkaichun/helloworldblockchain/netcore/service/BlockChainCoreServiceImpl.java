package com.xingkaichun.helloworldblockchain.netcore.service;

import com.xingkaichun.helloworldblockchain.core.BlockChainCore;
import com.xingkaichun.helloworldblockchain.core.BlockChainDataBase;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.core.script.StackBasedVirtualMachine;
import com.xingkaichun.helloworldblockchain.core.tools.NodeTransportDtoTool;
import com.xingkaichun.helloworldblockchain.core.tools.TransactionTool;
import com.xingkaichun.helloworldblockchain.crypto.AccountUtil;
import com.xingkaichun.helloworldblockchain.crypto.model.Account;
import com.xingkaichun.helloworldblockchain.netcore.dto.common.EmptyResponse;
import com.xingkaichun.helloworldblockchain.netcore.dto.common.ServiceResult;
import com.xingkaichun.helloworldblockchain.netcore.dto.common.page.PageCondition;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.NodeDto;
import com.xingkaichun.helloworldblockchain.netcore.dto.transaction.NormalTransactionDto;
import com.xingkaichun.helloworldblockchain.netcore.dto.transaction.SubmitNormalTransactionResultDto;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.BlockDTO;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.TransactionDTO;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.TransactionInputDTO;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.TransactionOutputDTO;
import com.xingkaichun.helloworldblockchain.setting.GlobalSetting;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class BlockChainCoreServiceImpl implements BlockChainCoreService {

    private BlockChainCore blockChainCore;
    private NodeService nodeService;
    private BlockchainNodeClientService blockchainNodeClientService;

    public BlockChainCoreServiceImpl(BlockChainCore blockChainCore, NodeService nodeService, BlockchainNodeClientService blockchainNodeClientService) {
        this.blockChainCore = blockChainCore;
        this.nodeService = nodeService;
        this.blockchainNodeClientService = blockchainNodeClientService;
    }

    @Override
    public TransactionDTO queryTransactionDtoByTransactionHash(String transactionHash) {
        Transaction transaction = blockChainCore.getBlockChainDataBase().queryTransactionByTransactionHash(transactionHash);
        if(transaction == null){
            return null;
        }
        TransactionDTO transactionDTO = NodeTransportDtoTool.classCast(transaction);
        return transactionDTO;
    }

    @Override
    public List<Transaction> queryTransactionByTransactionHeight(PageCondition pageCondition) {
        BlockChainDataBase blockChainDataBase = blockChainCore.getBlockChainDataBase();
        List<Transaction>  transactionList = blockChainDataBase.queryTransactionByTransactionHeight(BigInteger.valueOf(pageCondition.getFrom()),BigInteger.valueOf(pageCondition.getSize()));
        return transactionList;
    }

    @Override
    public List<TransactionOutput> queryUtxoListByAddress(String address,PageCondition pageCondition) {
        if(pageCondition == null){
            pageCondition = PageCondition.DEFAULT_PAGE_CONDITION;
        }
        List<TransactionOutput> utxo =  blockChainCore.getBlockChainDataBase().queryUnspendTransactionOutputListByAddress(address,pageCondition.getFrom(),pageCondition.getSize());
        return utxo;
    }

    @Override
    public List<TransactionOutput> queryTxoListByAddress(String address,PageCondition pageCondition) {
        if(pageCondition == null){
            pageCondition = PageCondition.DEFAULT_PAGE_CONDITION;
        }
        List<TransactionOutput> utxo =  blockChainCore.getBlockChainDataBase().queryTransactionOutputListByAddress(address,pageCondition.getFrom(),pageCondition.getSize());
        return utxo;
    }

    @Override
    public SubmitNormalTransactionResultDto submitTransaction(NormalTransactionDto normalTransactionDto) {
        TransactionDTO transactionDTO = classCast(normalTransactionDto);
        saveTransactionToMinerTransactionDatabase(transactionDTO);
        List<NodeDto> nodes = nodeService.queryAllNoForkAliveNodeList();

        List<SubmitNormalTransactionResultDto.Node> successSubmitNode = new ArrayList<>();
        List<SubmitNormalTransactionResultDto.Node> failSubmitNode = new ArrayList<>();
        if(nodes != null){
            for(NodeDto node:nodes){
                ServiceResult<EmptyResponse> submitSuccess = blockchainNodeClientService.sumiteTransaction(node,transactionDTO);
                if(ServiceResult.isSuccess(submitSuccess)){
                    successSubmitNode.add(new SubmitNormalTransactionResultDto.Node(node.getIp(),node.getPort()));
                } else {
                    failSubmitNode.add(new SubmitNormalTransactionResultDto.Node(node.getIp(),node.getPort()));
                }
            }
        }

        SubmitNormalTransactionResultDto response = new SubmitNormalTransactionResultDto();
        response.setTransactionDTO(transactionDTO);
        response.setSuccessSubmitNode(successSubmitNode);
        response.setFailSubmitNode(failSubmitNode);
        response.setTransactionHash(TransactionTool.calculateTransactionHash(transactionDTO));
        return response;
    }

    private TransactionDTO classCast(NormalTransactionDto normalTransactionDto) {
        long currentTimeMillis = System.currentTimeMillis();

        Account account = AccountUtil.accountFromPrivateKey(normalTransactionDto.getPrivateKey());

        List<NormalTransactionDto.Output> outputs = normalTransactionDto.getOutputs();
        List<TransactionOutputDTO> transactionOutputDtoList = new ArrayList<>();
        //理应支付总金额
        BigDecimal values = BigDecimal.ZERO;
        if(outputs != null){
            for(NormalTransactionDto.Output o:outputs){
                TransactionOutputDTO transactionOutputDTO = new TransactionOutputDTO();
                transactionOutputDTO.setAddress(o.getAddress());
                transactionOutputDTO.setValue(o.getValue());
                transactionOutputDTO.setScriptLock(StackBasedVirtualMachine.createPayToClassicAddressOutputScript(o.getAddress()));
                transactionOutputDtoList.add(transactionOutputDTO);
                values = values.add(new BigDecimal(o.getValue()));
            }
        }
        //手续费
        values = values.add(GlobalSetting.TransactionConstant.MIN_TRANSACTION_FEE);

        List<TransactionOutput> utxoList = blockChainCore.getBlockChainDataBase().queryUnspendTransactionOutputListByAddress(account.getAddress(),0,100);
        //交易输入列表
        List<String> inputs = new ArrayList<>();
        //交易输入总金额
        BigDecimal useValues = BigDecimal.ZERO;
        //找零
        BigDecimal change = BigDecimal.ZERO;
        boolean haveMoreMoneyToPay = false;
        for(TransactionOutput transactionOutput:utxoList){
            useValues = useValues.add(transactionOutput.getValue());
            //交易输入
            inputs.add(transactionOutput.getTransactionOutputHash());
            if(useValues.compareTo(values)>=0){
                haveMoreMoneyToPay = true;
                break;
            }
        }

        if(!haveMoreMoneyToPay){
            throw new ClassCastException("账户没有足够的金额去支付。");
        }else {
            //找零
            change = useValues.subtract(values);
            TransactionOutputDTO transactionOutputDTO = new TransactionOutputDTO();
            transactionOutputDTO.setAddress(account.getAddress());
            transactionOutputDTO.setValue(change.toPlainString());
            transactionOutputDTO.setScriptLock(StackBasedVirtualMachine.createPayToClassicAddressOutputScript(account.getAddress()));
            transactionOutputDtoList.add(transactionOutputDTO);
        }


        List<TransactionInputDTO> transactionInputDtoList = new ArrayList<>();
        for(String input:inputs){
            TransactionInputDTO transactionInputDTO = new TransactionInputDTO();
            transactionInputDTO.setUnspendTransactionOutputHash(input);
            transactionInputDtoList.add(transactionInputDTO);
        }



        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setTimestamp(currentTimeMillis);
        transactionDTO.setInputs(transactionInputDtoList);
        transactionDTO.setOutputs(transactionOutputDtoList);

        for(TransactionInputDTO transactionInputDTO:transactionInputDtoList){
            String signature = signatureTransactionDTO(transactionDTO, account.getPrivateKey());
            transactionInputDTO.setScriptKey(StackBasedVirtualMachine.createPayToClassicAddressInputScript(signature, account.getPublicKey()));
        }
        return transactionDTO;
    }

    public String signatureTransactionDTO(TransactionDTO transactionDTO, String privateKey) {
        String signature = NodeTransportDtoTool.signature(transactionDTO,privateKey);
        return signature;
    }

    @Override
    public String queryBlockHashByBlockHeight(BigInteger blockHeight) {
        Block block = blockChainCore.getBlockChainDataBase().queryNoTransactionBlockByBlockHeight(blockHeight);
        if(block == null){
            return null;
        }
        return block.getHash();
    }

    @Override
    public BlockDTO queryBlockDtoByBlockHeight(BigInteger blockHeight) {
        Block block = blockChainCore.getBlockChainDataBase().queryBlockByBlockHeight(blockHeight);
        if(block == null){
            return null;
        }
        BlockDTO blockDTO = NodeTransportDtoTool.classCast(block);
        return blockDTO;
    }

    @Override
    public Block queryNoTransactionBlockDtoByBlockHash(String blockHash) {
        BlockChainDataBase blockChainDataBase = blockChainCore.getBlockChainDataBase();
        BigInteger blockHeight = blockChainDataBase.queryBlockHeightByBlockHash(blockHash);
        if(blockHeight == null){
            return null;
        }
        Block block = blockChainDataBase.queryNoTransactionBlockByBlockHeight(blockHeight);
        return block;
    }

    @Override
    public Block queryNoTransactionBlockDtoByBlockHeight(BigInteger blockHeight) {
        Block block = blockChainCore.getBlockChainDataBase().queryNoTransactionBlockByBlockHeight(blockHeight);
        return block;
    }

    @Override
    public BigInteger queryBlockChainHeight() {
        return blockChainCore.getBlockChainDataBase().queryBlockChainHeight();
    }

    @Override
    public List<TransactionDTO> queryMiningTransactionList(PageCondition pageCondition) {
        if(pageCondition == null){
            pageCondition = PageCondition.DEFAULT_PAGE_CONDITION;
        }
        List<TransactionDTO> transactionDtoList = blockChainCore.getMiner().getMinerTransactionDtoDataBase().selectTransactionDtoList(pageCondition.getFrom(),pageCondition.getSize());
        return transactionDtoList;
    }

    @Override
    public TransactionDTO queryMiningTransactionDtoByTransactionHash(String transactionHash) {
        TransactionDTO transactionDTO = blockChainCore.getMiner().getMinerTransactionDtoDataBase().selectTransactionDtoByTransactionHash(transactionHash);
        return transactionDTO;
    }

    @Override
    public void removeBlocksUtilBlockHeightLessThan(BigInteger blockHeight) {
        blockChainCore.getBlockChainDataBase().removeTailBlocksUtilBlockHeightLessThan(blockHeight);
    }

    @Override
    public void saveTransactionToMinerTransactionDatabase(TransactionDTO transactionDTO) {
        blockChainCore.getMiner().getMinerTransactionDtoDataBase().insertTransactionDTO(transactionDTO);
    }
}
