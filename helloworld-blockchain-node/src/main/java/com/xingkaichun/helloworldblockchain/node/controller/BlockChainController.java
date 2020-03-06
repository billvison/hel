package com.xingkaichun.helloworldblockchain.node.controller;

import com.xingkaichun.helloworldblockchain.dto.BlockDTO;
import com.xingkaichun.helloworldblockchain.dto.TransactionDTO;
import com.xingkaichun.helloworldblockchain.dto.WalletDTO;
import com.xingkaichun.helloworldblockchain.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.node.dto.blockchain.BlockChainApiRoute;
import com.xingkaichun.helloworldblockchain.node.dto.blockchain.request.GenerateWalletRequest;
import com.xingkaichun.helloworldblockchain.node.dto.blockchain.request.QueryTransactionByTransactionUuidRequest;
import com.xingkaichun.helloworldblockchain.node.dto.blockchain.request.QueryUtxosByAddressRequest;
import com.xingkaichun.helloworldblockchain.node.dto.blockchain.request.SubmitNormalTransactionRequest;
import com.xingkaichun.helloworldblockchain.node.dto.blockchain.response.*;
import com.xingkaichun.helloworldblockchain.node.dto.common.ServiceResult;
import com.xingkaichun.helloworldblockchain.node.dto.node.request.QueryBlockDtoByBlockHeightRequest;
import com.xingkaichun.helloworldblockchain.node.dto.node.request.QueryBlockHashByBlockHeightRequest;
import com.xingkaichun.helloworldblockchain.node.dto.node.response.QueryBlockDtoByBlockHeightResponse;
import com.xingkaichun.helloworldblockchain.node.dto.node.response.QueryBlockHashByBlockHeightResponse;
import com.xingkaichun.helloworldblockchain.node.service.BlockChainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 区块链相关
 */
@Controller
@RequestMapping
public class BlockChainController {

    private static final Logger logger = LoggerFactory.getLogger(BlockChainController.class);

    @Autowired
    private BlockChainService blockChainService;

    /**
     * 矿工是否激活
     */
    @ResponseBody
    @RequestMapping(value = BlockChainApiRoute.IS_MINER_ACTIVE,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<IsMinerActiveResponse> isMineActive(){
        try {
            boolean isMineActive = blockChainService.isMinerActive();

            IsMinerActiveResponse response = new IsMinerActiveResponse();
            response.setMineActive(isMineActive);
            return ServiceResult.createSuccessServiceResult("查询矿工是否激活挖矿成功",response);
        } catch (Exception e){
            String message = "查询矿工是否激活挖矿失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }
    /**
     * 激活矿工
     */
    @ResponseBody
    @RequestMapping(value = BlockChainApiRoute.ACTIVE_MINER,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<ActiveMinerResponse> activeMiner(){
        try {
            blockChainService.activeMiner();
            ActiveMinerResponse response = new ActiveMinerResponse();
            response.setStartMineSuccess(true);
            return ServiceResult.createSuccessServiceResult("开启挖矿成功",response);
        } catch (Exception e){
            String message = "开启挖矿失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }
    /**
     * 停用矿工
     */
    @ResponseBody
    @RequestMapping(value = BlockChainApiRoute.DEACTIVE_MINER,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<DeactiveMinerResponse> deactiveMiner(){
        try {
            blockChainService.deactiveMiner();
            DeactiveMinerResponse response = new DeactiveMinerResponse();
            response.setStopMineSuccess(true);
            return ServiceResult.createSuccessServiceResult("关闭挖矿成功",response);
        } catch (Exception e){
            String message = "关闭挖矿失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }



    /**
     * 同步器是否激活
     */
    @ResponseBody
    @RequestMapping(value = BlockChainApiRoute.IS_SYNCHRONIZER_ACTIVE,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<IsSynchronizerActiveResponse> isSynchronizerActive(){
        try {
            boolean isSynchronizerActive = blockChainService.isSynchronizerActive();

            IsSynchronizerActiveResponse response = new IsSynchronizerActiveResponse();
            response.setSynchronizerActive(isSynchronizerActive);
            return ServiceResult.createSuccessServiceResult("查询矿工是否激活挖矿成功",response);
        } catch (Exception e){
            String message = "查询矿工是否激活挖矿失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }
    /**
     * 激活同步器
     */
    @ResponseBody
    @RequestMapping(value = BlockChainApiRoute.ACTIVE_SYNCHRONIZER,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<ActiveSynchronizerResponse> activeSynchronizer(){
        try {
            blockChainService.activeSynchronizer();
            ActiveSynchronizerResponse response = new ActiveSynchronizerResponse();
            response.setResumeSynchronizerSuccess(true);
            return ServiceResult.createSuccessServiceResult("激活同步器成功",response);
        } catch (Exception e){
            String message = "激活同步器失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }
    /**
     * 停用同步器
     */
    @ResponseBody
    @RequestMapping(value = BlockChainApiRoute.DEACTIVE_SYNCHRONIZER,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<DeactiveSynchronizerResponse> deactiveSynchronizer(){
        try {
            blockChainService.deactiveSynchronizer();
            DeactiveSynchronizerResponse response = new DeactiveSynchronizerResponse();
            response.setStopMineSuccess(true);
            return ServiceResult.createSuccessServiceResult("停用同步器成功",response);
        } catch (Exception e){
            String message = "停用同步器失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }



    /**
     * 生成钱包(公钥、私钥、地址)
     */
    @ResponseBody
    @RequestMapping(value = BlockChainApiRoute.GENERATE_WALLETDTO,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<GenerateWalletResponse> generateWallet(@RequestBody GenerateWalletRequest request){
        try {
            WalletDTO walletDTO = blockChainService.generateWalletDTO();
            GenerateWalletResponse response = new GenerateWalletResponse();
            response.setWalletDTO(walletDTO);
            return ServiceResult.createSuccessServiceResult("生成钱包成功",response);
        } catch (Exception e){
            String message = "生成钱包失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }

    /**
     * 提交交易到区块链网络
     */
    @ResponseBody
    @RequestMapping(value = BlockChainApiRoute.SUBMIT_TRANSACTION,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<SubmitNormalTransactionResponse> submitTransaction(@RequestBody SubmitNormalTransactionRequest request){
        try {
            TransactionDTO transactionDTO = blockChainService.sumiteTransaction(request.getNormalTransactionDto());

            SubmitNormalTransactionResponse response = new SubmitNormalTransactionResponse();
            response.setTransactionDTO(transactionDTO);
            return ServiceResult.createSuccessServiceResult("提交交易到区块链网络成功",response);
        } catch (Exception e){
            String message = "提交交易到区块链网络失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }

    /**
     * 根据区块高度查询区块
     */
    @ResponseBody
    @RequestMapping(value = BlockChainApiRoute.Query_BLOCKDTO_BY_BLOCK_HEIGHT,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<QueryBlockDtoByBlockHeightResponse> queryBlockDtoByBlockHeight(@RequestBody QueryBlockDtoByBlockHeightRequest request){
        try {
            BlockDTO blockDTO = blockChainService.queryBlockDtoByBlockHeight(request);

            QueryBlockDtoByBlockHeightResponse response = new QueryBlockDtoByBlockHeightResponse();
            response.setBlockDTO(blockDTO);
            return ServiceResult.createSuccessServiceResult("成功获取区块",response);
        } catch (Exception e){
            String message = "查询获取失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }

    /**
     * 根据区块高度查询区块Hash
     */
    @ResponseBody
    @RequestMapping(value = BlockChainApiRoute.Query_BLOCK_HASH_BY_BLOCK_HEIGHT,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<QueryBlockHashByBlockHeightResponse> queryBlockHashByBlockHeight(@RequestBody QueryBlockHashByBlockHeightRequest request){
        try {
            String blockHash = blockChainService.queryBlockHashByBlockHeight(request);

            QueryBlockHashByBlockHeightResponse response = new QueryBlockHashByBlockHeightResponse();
            response.setBlockHash(blockHash);
            return ServiceResult.createSuccessServiceResult("成功获取区块Hash",response);
        } catch (Exception e){
            String message = "查询区块Hash失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }

    /**
     * 根据交易UUID查询交易
     */
    @ResponseBody
    @RequestMapping(value = BlockChainApiRoute.QUERY_TRANSACTION_BY_TRANSACTION_UUID,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<QueryTransactionByTransactionUuidResponse> queryTransactionByTransactionUUID(@RequestBody QueryTransactionByTransactionUuidRequest request){
        try {
            TransactionDTO transactionDTO = blockChainService.QueryTransactionDtoByTransactionUUID(request);

            QueryTransactionByTransactionUuidResponse response = new QueryTransactionByTransactionUuidResponse();
            response.setTransactionDTO(transactionDTO);
            return ServiceResult.createSuccessServiceResult("根据交易UUID查询交易成功",response);
        } catch (Exception e){
            String message = "根据交易UUID查询交易失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }

    /**
     * 根据地址获取余额
     */
    @ResponseBody
    @RequestMapping(value = BlockChainApiRoute.QUERY_UTXOS_BY_ADDRESS,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<QueryUtxosByAddressResponse> queryUtxosByAddress(@RequestBody QueryUtxosByAddressRequest queryUtxosByAddressRequest){
        try {
            List<TransactionOutput> utxoList = blockChainService.queryUtxoListByAddress(queryUtxosByAddressRequest);

            QueryUtxosByAddressResponse response = new QueryUtxosByAddressResponse();
            response.setUtxos(utxoList);
            return ServiceResult.createSuccessServiceResult("成功查询用戶余额",response);
        } catch (Exception e){
            String message = "查询用戶余额失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }
}