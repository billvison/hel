package com.xingkaichun.helloworldblockchain.node.controller;

import com.xingkaichun.helloworldblockchain.dto.BlockDTO;
import com.xingkaichun.helloworldblockchain.dto.TransactionDTO;
import com.xingkaichun.helloworldblockchain.dto.WalletDTO;
import com.xingkaichun.helloworldblockchain.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.BlockChainApiRoute;
import com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.request.*;
import com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.response.*;
import com.xingkaichun.helloworldblockchain.node.dto.common.ServiceResult;
import com.xingkaichun.helloworldblockchain.node.dto.nodeserver.Node;
import com.xingkaichun.helloworldblockchain.node.service.BlockChainCoreService;
import com.xingkaichun.helloworldblockchain.node.service.NodeService;
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
 * 区块链浏览器
 */
@Controller
@RequestMapping
public class BlockChainBrowserController {

    private static final Logger logger = LoggerFactory.getLogger(BlockChainBrowserController.class);

    @Autowired
    private BlockChainCoreService blockChainCoreService;

    @Autowired
    private NodeService nodeService;

   /**
     * 生成钱包(公钥、私钥、地址)
     */
    @ResponseBody
    @RequestMapping(value = BlockChainApiRoute.GENERATE_WALLETDTO,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<GenerateWalletResponse> generateWallet(@RequestBody GenerateWalletRequest request){
        try {
            WalletDTO walletDTO = blockChainCoreService.generateWalletDTO();
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
            SubmitNormalTransactionResponse response = blockChainCoreService.sumiteTransaction(request.getNormalTransactionDto());
            return ServiceResult.createSuccessServiceResult("提交交易到区块链网络成功",response);
        } catch (Exception e){
            String message = "提交交易到区块链网络失败";
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
            TransactionDTO transactionDTO = blockChainCoreService.queryTransactionDtoByTransactionUUID(request.getTransactionUUID());

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
     * 根据交易UUID查询挖矿中交易
     */
    @ResponseBody
    @RequestMapping(value = BlockChainApiRoute.QUERY_MINING_TRANSACTION_BY_TRANSACTION_UUID,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<QueryMiningTransactionByTransactionUuidResponse> queryMiningTransactionByTransactionUUID(@RequestBody QueryMiningTransactionByTransactionUuidRequest request){
        try {
            TransactionDTO transactionDTO = blockChainCoreService.queryMiningTransactionDtoByTransactionUUID(request.getTransactionUUID());

            QueryMiningTransactionByTransactionUuidResponse response = new QueryMiningTransactionByTransactionUuidResponse();
            response.setTransactionDTO(transactionDTO);
            return ServiceResult.createSuccessServiceResult("根据交易UUID查询挖矿中交易成功",response);
        } catch (Exception e){
            String message = "根据交易UUID查询挖矿中交易失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }

    /**
     * 根据地址获取未花费交易输出
     */
    @ResponseBody
    @RequestMapping(value = BlockChainApiRoute.QUERY_UTXOS_BY_ADDRESS,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<QueryUtxosByAddressResponse> queryUtxosByAddress(@RequestBody QueryUtxosByAddressRequest request){
        try {
            List<TransactionOutput> utxoList = blockChainCoreService.queryUtxoListByAddress(request.getAddress());

            QueryUtxosByAddressResponse response = new QueryUtxosByAddressResponse();
            response.setUtxos(utxoList);
            return ServiceResult.createSuccessServiceResult("根据地址获取未花费交易输出成功",response);
        } catch (Exception e){
            String message = "根据地址获取未花费交易输出失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }

    /**
     * 根据地址获取未花费交易输出
     */
    @ResponseBody
    @RequestMapping(value = BlockChainApiRoute.QUERY_TXOS_BY_ADDRESS,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<QueryTxosByAddressResponse> queryTxosByAddress(@RequestBody QueryTxosByAddressRequest request){
        try {
            List<TransactionOutput> txoList = blockChainCoreService.queryTxoListByAddress(request.getAddress());

            QueryTxosByAddressResponse response = new QueryTxosByAddressResponse();
            response.setUtxos(txoList);
            return ServiceResult.createSuccessServiceResult("根据地址获取未花费交易输出成功",response);
        } catch (Exception e){
            String message = "根据地址获取未花费交易输出失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }

    /**
     * Ping节点
     */
    @ResponseBody
    @RequestMapping(value = BlockChainApiRoute.PING,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<PingResponse> ping(@RequestBody PingRequest request){
        try {
            List<Node> nodeList = nodeService.queryNodes();
            int blockChainHeight = blockChainCoreService.queryBlockChainHeight();
            PingResponse response = new PingResponse();
            response.setNodeList(nodeList);
            response.setBlockChainHeight(blockChainHeight);
            return ServiceResult.createSuccessServiceResult("查询节点信息成功",response);
        } catch (Exception e){
            String message = "查询节点信息失败";
            logger.error(message,e);
            return ServiceResult.createSuccessServiceResult(message,null);
        }
    }

    /**
     * 查询挖矿中的交易
     */
    @ResponseBody
    @RequestMapping(value = BlockChainApiRoute.QUERY_MINING_TRANSACTION_LIST,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<QueryMiningTransactionListResponse> queryMiningTransactionList(@RequestBody QueryMiningTransactionListRequest request){
        try {
            List<TransactionDTO> transactionDtoList = blockChainCoreService.queryMiningTransactionList();
            QueryMiningTransactionListResponse response = new QueryMiningTransactionListResponse();
            response.setTransactionDtoList(transactionDtoList);
            return ServiceResult.createSuccessServiceResult("查询挖矿中的交易成功",response);
        } catch (Exception e){
            String message = "查询挖矿中的交易失败";
            logger.error(message,e);
            return ServiceResult.createSuccessServiceResult(message,null);
        }
    }

    /**
     * 根据区块高度查询区块
     */
    @ResponseBody
    @RequestMapping(value = BlockChainApiRoute.QUERY_BLOCKDTO_BY_BLOCK_HEIGHT,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<QueryBlockDtoByBlockHeightResponse> queryBlockDtoByBlockHeight(@RequestBody QueryBlockDtoByBlockHeightRequest request){
        try {
            BlockDTO blockDTO = blockChainCoreService.queryBlockDtoByBlockHeight(request.getBlockHeight());

            QueryBlockDtoByBlockHeightResponse response = new QueryBlockDtoByBlockHeightResponse();
            response.setBlockDTO(blockDTO);
            return ServiceResult.createSuccessServiceResult("成功获取区块",response);
        } catch (Exception e){
            String message = "查询获取失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }
}