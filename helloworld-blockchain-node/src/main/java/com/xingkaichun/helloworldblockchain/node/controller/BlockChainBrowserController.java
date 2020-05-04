package com.xingkaichun.helloworldblockchain.node.controller;

import com.google.common.base.Strings;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutput;
import com.xingkaichun.helloworldblockchain.core.utils.BlockChainCoreConstant;
import com.xingkaichun.helloworldblockchain.core.utils.NumberUtil;
import com.xingkaichun.helloworldblockchain.crypto.KeyUtil;
import com.xingkaichun.helloworldblockchain.crypto.model.StringPrivateKey;
import com.xingkaichun.helloworldblockchain.node.dto.blockchainbranch.BlockchainBranchBlockDto;
import com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.BlockChainApiRoute;
import com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.NormalTransactionDto;
import com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.request.*;
import com.xingkaichun.helloworldblockchain.node.dto.blockchainbrowser.response.*;
import com.xingkaichun.helloworldblockchain.node.dto.common.ServiceResult;
import com.xingkaichun.helloworldblockchain.node.dto.common.page.PageCondition;
import com.xingkaichun.helloworldblockchain.node.dto.nodeserver.Node;
import com.xingkaichun.helloworldblockchain.node.dto.wallet.WalletDTO;
import com.xingkaichun.helloworldblockchain.node.service.BlockChainBranchService;
import com.xingkaichun.helloworldblockchain.node.service.BlockChainCoreService;
import com.xingkaichun.helloworldblockchain.node.service.NodeService;
import com.xingkaichun.helloworldblockchain.node.transport.dto.TransactionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigInteger;
import java.util.List;

/**
 * 区块链浏览器控制器
 *
 * @author 邢开春 xingkaichun@qq.com
 */
@Controller
@RequestMapping
public class BlockChainBrowserController {

    private static final Logger logger = LoggerFactory.getLogger(BlockChainBrowserController.class);

    @Autowired
    private BlockChainCoreService blockChainCoreService;

    @Autowired
    private NodeService nodeService;

    @Autowired
    private BlockChainBranchService blockChainBranchService;

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
            NormalTransactionDto normalTransactionDto = request.getNormalTransactionDto();
            String privateKey = normalTransactionDto.getPrivateKey();
            if(Strings.isNullOrEmpty(privateKey)){
                return ServiceResult.createFailServiceResult("私钥不能为空");
            }
            try {
                KeyUtil.stringKeyFrom(new StringPrivateKey(privateKey));
            } catch (Exception e){
                return ServiceResult.createFailServiceResult("私钥不正确，请检查输入的私钥");
            }
            List<String> inputs = normalTransactionDto.getInputs();
            if(inputs == null || inputs.size() == 0){
                return ServiceResult.createFailServiceResult("交易输入不能为空。");
            }
            for(String input:inputs){
                if(Strings.isNullOrEmpty(input)){
                    return ServiceResult.createFailServiceResult("交易输入不能为空。");
                }
            }
            List<NormalTransactionDto.Output> outputs = normalTransactionDto.getOutputs();
            if(outputs == null || outputs.size() == 0){
                return ServiceResult.createFailServiceResult("交易输出不能为空。");
            }
            for(NormalTransactionDto.Output output:outputs){
                if(Strings.isNullOrEmpty(output.getAddress())){
                    return ServiceResult.createFailServiceResult("交易输出的地址不能为空。");
                }
                if(Strings.isNullOrEmpty(output.getValue())){
                    return ServiceResult.createFailServiceResult("交易输出的金额不能为空。");
                }
                if(!NumberUtil.isNumber(output.getValue())){
                    return ServiceResult.createFailServiceResult("交易输出的金额不是一个数值。");
                }
            }
            SubmitNormalTransactionResponse response = blockChainCoreService.sumiteTransaction(request.getNormalTransactionDto());
            return ServiceResult.createSuccessServiceResult("提交交易到区块链网络成功",response);
        } catch (Exception e){
            String message = "提交交易到区块链网络失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }

    /**
     * 根据交易Hash查询交易
     */
    @ResponseBody
    @RequestMapping(value = BlockChainApiRoute.QUERY_TRANSACTION_BY_TRANSACTION_HASH,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<QueryTransactionByTransactionHashResponse> queryTransactionByTransactionHash(@RequestBody QueryTransactionByTransactionHashRequest request){
        try {
            TransactionDTO transactionDTO = blockChainCoreService.queryTransactionDtoByTransactionHash(request.getTransactionHash());
            if(transactionDTO == null){
                return ServiceResult.createFailServiceResult(String.format("区块链中不存在交易哈希[%s]，请检查输入的交易哈希。",request.getTransactionHash()));
            }
            QueryTransactionByTransactionHashResponse response = new QueryTransactionByTransactionHashResponse();
            response.setTransactionDTO(transactionDTO);
            return ServiceResult.createSuccessServiceResult("根据交易哈希查询交易成功",response);
        } catch (Exception e){
            String message = "根据交易哈希查询交易失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }

    /**
     * 根据交易高度查询交易
     */
    @ResponseBody
    @RequestMapping(value = BlockChainApiRoute.QUERY_TRANSACTION_BY_TRANSACTION_HEIGHT,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<QueryTransactionByTransactionHeightResponse> queryTransactionByTransactionHeight(@RequestBody QueryTransactionByTransactionHeightRequest request){
        try {
            if(request.getPageCondition()==null){
                request.setPageCondition(PageCondition.defaultPageCondition);
            }
            List<Transaction> transactionList = blockChainCoreService.queryTransactionByTransactionHeight(request.getPageCondition());
            if(transactionList == null){
                return ServiceResult.createFailServiceResult(String.format("区块链中不存在交易高度[%s]，请检查输入的交易哈希。",request.getPageCondition().getFrom()));
            }
            QueryTransactionByTransactionHeightResponse response = new QueryTransactionByTransactionHeightResponse();
            response.setTransactionList(transactionList);
            return ServiceResult.createSuccessServiceResult("根据交易高度查询交易成功",response);
        } catch (Exception e){
            String message = "根据交易高度查询交易失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }

    /**
     * 根据交易哈希查询挖矿中交易
     */
    @ResponseBody
    @RequestMapping(value = BlockChainApiRoute.QUERY_MINING_TRANSACTION_BY_TRANSACTION_HASH,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<QueryMiningTransactionByTransactionHashResponse> queryMiningTransactionByTransactionHash(@RequestBody QueryMiningTransactionByTransactionHashRequest request){
        try {
            TransactionDTO transactionDTO = blockChainCoreService.queryMiningTransactionDtoByTransactionHash(request.getTransactionHash());
            if(transactionDTO == null){
                return ServiceResult.createFailServiceResult(String.format("交易哈希[%s]不是正在被挖矿的交易。",request.getTransactionHash()));
            }

            QueryMiningTransactionByTransactionHashResponse response = new QueryMiningTransactionByTransactionHashResponse();
            response.setTransactionDTO(transactionDTO);
            return ServiceResult.createSuccessServiceResult("根据交易哈希查询挖矿中交易成功",response);
        } catch (Exception e){
            String message = "根据交易哈希查询挖矿中交易失败";
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
            List<TransactionOutput> utxoList = blockChainCoreService.queryUtxoListByAddress(request);

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
     * 根据地址获取交易输出
     */
    @ResponseBody
    @RequestMapping(value = BlockChainApiRoute.QUERY_TXOS_BY_ADDRESS,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<QueryTxosByAddressResponse> queryTxosByAddress(@RequestBody QueryTxosByAddressRequest request){
        try {
            List<TransactionOutput> txoList = blockChainCoreService.queryTxoListByAddress(request);
            if(txoList == null){
                return ServiceResult.createFailServiceResult(String.format("地址[%s]没有对应的交易输出列表。",request.getAddress()));
            }
            QueryTxosByAddressResponse response = new QueryTxosByAddressResponse();
            response.setTxos(txoList);
            return ServiceResult.createSuccessServiceResult("[根据地址获取交易输出]成功",response);
        } catch (Exception e){
            String message = "[根据地址获取交易输出]失败";
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
            List<Node> nodeList = nodeService.queryAllNoForkNodeList();
            BigInteger blockChainHeight = blockChainCoreService.queryBlockChainHeight();
            PingResponse response = new PingResponse();
            response.setNodeList(nodeList);
            response.setBlockChainHeight(blockChainHeight);
            response.setBlockChainId(BlockChainCoreConstant.BLOCK_CHAIN_ID);
            response.setBlockChainVersion(BlockChainCoreConstant.SystemVersionConstant.obtainVersion());
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
            List<TransactionDTO> transactionDtoList = blockChainCoreService.queryMiningTransactionList(request);
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
            Block block = blockChainCoreService.queryNoTransactionBlockDtoByBlockHeight(request.getBlockHeight());
            if(block == null){
                return ServiceResult.createFailServiceResult(String.format("区块链中不存在区块高度[%d]，请检查输入高度。",request.getBlockHeight()));
            }
            QueryBlockDtoByBlockHeightResponse response = new QueryBlockDtoByBlockHeightResponse();
            response.setBlock(block);
            return ServiceResult.createSuccessServiceResult("成功获取区块",response);
        } catch (Exception e){
            String message = "查询获取失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }

    /**
     * 根据区块哈希查询区块
     */
    @ResponseBody
    @RequestMapping(value = BlockChainApiRoute.QUERY_BLOCKDTO_BY_BLOCK_HASH,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<QueryBlockDtoByBlockHashResponse> queryBlockDtoByBlockHash(@RequestBody QueryBlockDtoByBlockHashRequest request){
        try {
            Block block = blockChainCoreService.queryNoTransactionBlockDtoByBlockHash(request.getBlockHash());
            if(block == null){
                return ServiceResult.createFailServiceResult(String.format("区块链中不存在区块哈希[%d]，请检查输入高度。",request.getBlockHash()));
            }
            QueryBlockDtoByBlockHashResponse response = new QueryBlockDtoByBlockHashResponse();
            response.setBlock(block);
            return ServiceResult.createSuccessServiceResult("[根据区块哈希查询区块]成功",response);
        } catch (Exception e){
            String message = "[根据区块哈希查询区块]失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }

    /**
     * 获取当前区块链分支
     */
    @ResponseBody
    @RequestMapping(value = BlockChainApiRoute.QUERY_BLOCKCHAINBRANCH,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<QueryBlockchainBranchResponse> queryBlockchainBranch(@RequestBody QueryBlockchainBranchRequest request){
        try {
            List<BlockchainBranchBlockDto> blockList = blockChainBranchService.queryBlockchainBranch();

            QueryBlockchainBranchResponse response = new QueryBlockchainBranchResponse();
            response.setBlockList(blockList);
            return ServiceResult.createSuccessServiceResult("成功获取当前区块链分支",response);
        } catch (Exception e){
            String message = "获取当前区块链分支失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }
}