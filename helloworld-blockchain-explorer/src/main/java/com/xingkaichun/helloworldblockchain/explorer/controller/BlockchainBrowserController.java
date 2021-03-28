package com.xingkaichun.helloworldblockchain.explorer.controller;

import com.xingkaichun.helloworldblockchain.core.BlockchainCore;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.transaction.Transaction;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutputId;
import com.xingkaichun.helloworldblockchain.core.tools.BlockTool;
import com.xingkaichun.helloworldblockchain.core.tools.Dto2ModelTool;
import com.xingkaichun.helloworldblockchain.core.tools.SizeTool;
import com.xingkaichun.helloworldblockchain.core.tools.WalletTool;
import com.xingkaichun.helloworldblockchain.crypto.AccountUtil;
import com.xingkaichun.helloworldblockchain.crypto.model.Account;
import com.xingkaichun.helloworldblockchain.explorer.dto.BlockchainApiRoute;
import com.xingkaichun.helloworldblockchain.explorer.dto.account.GenerateAccountRequest;
import com.xingkaichun.helloworldblockchain.explorer.dto.account.GenerateAccountResponse;
import com.xingkaichun.helloworldblockchain.explorer.dto.account.QueryAccountDetailByAddressRequest;
import com.xingkaichun.helloworldblockchain.explorer.dto.account.QueryAccountDetailByAddressResponse;
import com.xingkaichun.helloworldblockchain.explorer.dto.block.*;
import com.xingkaichun.helloworldblockchain.explorer.dto.node.QueryBlockchainHeightRequest;
import com.xingkaichun.helloworldblockchain.explorer.dto.node.QueryBlockchainHeightResponse;
import com.xingkaichun.helloworldblockchain.explorer.dto.transaction.*;
import com.xingkaichun.helloworldblockchain.explorer.service.BlockchainBrowserService;
import com.xingkaichun.helloworldblockchain.netcore.NetBlockchainCore;
import com.xingkaichun.helloworldblockchain.netcore.dto.common.PageCondition;
import com.xingkaichun.helloworldblockchain.netcore.dto.common.ServiceResult;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.TransactionDTO;
import com.xingkaichun.helloworldblockchain.util.DateUtil;
import com.xingkaichun.helloworldblockchain.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 区块链浏览器控制器
 *
 * @author 邢开春 409060350@qq.com
 */
@RestController
public class BlockchainBrowserController {

    private static final Logger logger = LoggerFactory.getLogger(BlockchainBrowserController.class);

    @Autowired
    private NetBlockchainCore netBlockchainCore;
    @Autowired
    private BlockchainBrowserService blockchainBrowserService;

   /**
     * 生成账户(公钥、私钥、地址)
     */
    @RequestMapping(value = BlockchainApiRoute.GENERATE_ACCOUNT,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<GenerateAccountResponse> generateAccount(@RequestBody GenerateAccountRequest request){
        try {
            Account account = AccountUtil.randomAccount();
            GenerateAccountResponse response = new GenerateAccountResponse();
            response.setAccount(account);
            return ServiceResult.createSuccessServiceResult("生成账户成功",response);
        } catch (Exception e){
            String message = "生成账户失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }

    /**
     * 提交交易到区块链网络
     */
    @RequestMapping(value = BlockchainApiRoute.SUBMIT_TRANSACTION_TO_BLOCKCHIAINNEWWORK,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<SubmitTransactionToBlockchainNetworkResponse> submitTransactionToBlockchainNetwork(@RequestBody SubmitTransactionToBlockchainNetworkRequest request){
        try {
            SubmitTransactionToBlockchainNetworkResponse response = blockchainBrowserService.submitTransactionToBlockchainNetwork(request);
            return ServiceResult.createSuccessServiceResult("提交交易到区块链网络成功", response);
        } catch (Exception e){
            String message = "提交交易到区块链网络失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }

    /**
     * 根据交易哈希查询交易
     */
    @RequestMapping(value = BlockchainApiRoute.QUERY_TRANSACTION_BY_TRANSACTION_HASH,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<QueryTransactionByTransactionHashResponse> queryTransactionByTransactionHash(@RequestBody QueryTransactionByTransactionHashRequest request){
        try {
            TransactionView transactionView = blockchainBrowserService.queryTransactionByTransactionHash(request.getTransactionHash());
            if(transactionView == null){
                return ServiceResult.createFailServiceResult("根据交易哈希未能查询到交易");
            }

            QueryTransactionByTransactionHashResponse response = new QueryTransactionByTransactionHashResponse();
            response.setTransactionView(transactionView);
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
    @RequestMapping(value = BlockchainApiRoute.QUERY_TRANSACTION_LIST_BY_TRANSACTION_HEIGHT,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<QueryTransactionListByTransactionHeightResponse> queryTransactionListByTransactionHeight(@RequestBody QueryTransactionListByTransactionHeightRequest request){
        try {
            PageCondition pageCondition = request.getPageCondition();
            List<Transaction> transactionList = getBlockchainCore().queryTransactionListByTransactionHeight(pageCondition.getFrom(),pageCondition.getSize());
            if(transactionList == null){
                return ServiceResult.createFailServiceResult(String.format("区块链中不存在交易高度[%s]，请检查输入的交易哈希。",request.getPageCondition().getFrom()));
            }
            QueryTransactionListByTransactionHeightResponse response = new QueryTransactionListByTransactionHeightResponse();
            response.setTransactionList(transactionList);
            return ServiceResult.createSuccessServiceResult("根据交易高度查询交易成功",response);
        } catch (Exception e){
            String message = "根据交易高度查询交易失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }

    /**
     * 根据区块哈希与交易高度查询交易列表
     */
    @RequestMapping(value = BlockchainApiRoute.QUERY_TRANSACTION_LIST_BY_BLOCK_HASH_TRANSACTION_HEIGHT,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<QueryTransactionListByBlockHashTransactionHeightResponse> queryTransactionListByBlockHashTransactionHeight(@RequestBody QueryTransactionListByBlockHashTransactionHeightRequest request){
        try {
            PageCondition pageCondition = request.getPageCondition();
            if(StringUtil.isNullOrEmpty(request.getBlockHash())){
                return ServiceResult.createFailServiceResult("区块哈希不能是空");
            }
            List<TransactionView> transactionViewList = blockchainBrowserService.queryTransactionListByBlockHashTransactionHeight(request.getBlockHash(),pageCondition.getFrom(),pageCondition.getSize());
            QueryTransactionListByBlockHashTransactionHeightResponse response = new QueryTransactionListByBlockHashTransactionHeightResponse();
            response.setTransactionViewList(transactionViewList);
            return ServiceResult.createSuccessServiceResult("根据交易高度查询交易成功",response);
        } catch (Exception e){
            String message = "根据交易高度查询交易失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }

    /**
     * 根据地址查询账户详情
     */
    @RequestMapping(value = BlockchainApiRoute.QUERY_ACCOUNT_DETAIL_BY_ADDRESS,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<QueryAccountDetailByAddressResponse> queryAccountDetailByAddress(@RequestBody QueryAccountDetailByAddressRequest request){
        try {
            String address = request.getAddress();

            QueryAccountDetailByAddressResponse response = new QueryAccountDetailByAddressResponse();
            response.setAddress(address);
            response.setBalance(String.valueOf(WalletTool.obtainBalance(getBlockchainCore(),address)));
            response.setReceipt(String.valueOf(WalletTool.obtainReceipt(getBlockchainCore(),address)));
            response.setSpend(String.valueOf(WalletTool.obtainSpend(getBlockchainCore(),address)));
            return ServiceResult.createSuccessServiceResult("[查询交易输出]成功",response);
        } catch (Exception e){
            String message = "[根据地址查询账户详情]失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }


    /**
     * 根据地址查询交易列表
     */
    @RequestMapping(value = BlockchainApiRoute.QUERY_TRANSACTION_LIST_BY_ADDRESS,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<QueryTransactionListByAddressResponse> queryTransactionListByAddress(@RequestBody QueryTransactionListByAddressRequest request){
        try {
            PageCondition pageCondition = request.getPageCondition();
            List<TransactionView> transactionViewList = blockchainBrowserService.queryTransactionListByAddress(request.getAddress(),pageCondition.getFrom(),pageCondition.getSize());
            if(transactionViewList == null || transactionViewList.size()==0){
                return ServiceResult.createFailServiceResult("根据地址未能查询到交易列表");
            }

            QueryTransactionListByAddressResponse response = new QueryTransactionListByAddressResponse();
            response.setTransactionViewList(transactionViewList);
            return ServiceResult.createSuccessServiceResult("[查询交易输出]成功",response);
        } catch (Exception e){
            String message = "[查询交易输出]失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }

    /**
     * 根据交易哈希查询挖矿中交易
     */
    @RequestMapping(value = BlockchainApiRoute.QUERY_MINING_TRANSACTION_BY_TRANSACTION_HASH,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<QueryMiningTransactionByTransactionHashResponse> queryMiningTransactionByTransactionHash(@RequestBody QueryMiningTransactionByTransactionHashRequest request){
        try {
            TransactionDTO transactionDTO = getBlockchainCore().queryMiningTransactionDtoByTransactionHash(request.getTransactionHash());
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
    @RequestMapping(value = BlockchainApiRoute.QUERY_UNSPEND_TRANSACTION_OUTPUT_LIST_BY_ADDRESS,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<QueryUnspendTransactionOutputListByAddressResponse> queryUnspendTransactionOutputListByAddress(@RequestBody QueryUnspendTransactionOutputListByAddressRequest request){
        try {
            PageCondition pageCondition = request.getPageCondition();
            List<TransactionOutputDetailView> transactionOutputDetailViewList = blockchainBrowserService.queryUnspendTransactionOutputListByAddress(request.getAddress(),pageCondition.getFrom(),pageCondition.getSize());
            QueryUnspendTransactionOutputListByAddressResponse response = new QueryUnspendTransactionOutputListByAddressResponse();
            response.setTransactionOutputDetailViewList(transactionOutputDetailViewList);
            return ServiceResult.createSuccessServiceResult("[查询交易输出]成功",response);
        } catch (Exception e){
            String message = "[查询交易输出]失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }

    /**
     * 根据地址获取已花费交易输出
     */
    @RequestMapping(value = BlockchainApiRoute.QUERY_SPEND_TRANSACTION_OUTPUT_LIST_BY_ADDRESS,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<QuerySpendTransactionOutputListByAddressResponse> querySpendTransactionOutputListByAddress(@RequestBody QuerySpendTransactionOutputListByAddressRequest request){
        try {
            PageCondition pageCondition = request.getPageCondition();
            List<TransactionOutputDetailView> transactionOutputDetailViewList = blockchainBrowserService.querySpendTransactionOutputListByAddress(request.getAddress(),pageCondition.getFrom(),pageCondition.getSize());
            QuerySpendTransactionOutputListByAddressResponse response = new QuerySpendTransactionOutputListByAddressResponse();
            response.setTransactionOutputDetailViewList(transactionOutputDetailViewList);
            return ServiceResult.createSuccessServiceResult("[查询交易输出]成功",response);
        } catch (Exception e){
            String message = "[查询交易输出]失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }

    /**
     * 根据地址获取交易输出
     */
    @RequestMapping(value = BlockchainApiRoute.QUERY_TRANSACTION_OUTPUT_LIST_BY_ADDRESS,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<QueryTransactionOutputListByAddressResponse> queryTransactionOutputListByAddress(@RequestBody QueryTransactionOutputListByAddressRequest request){
        try {
            PageCondition pageCondition = request.getPageCondition();
            List<TransactionOutputDetailView> transactionOutputDetailViewList = blockchainBrowserService.queryTransactionOutputListByAddress(request.getAddress(),pageCondition.getFrom(),pageCondition.getSize());
            QueryTransactionOutputListByAddressResponse response = new QueryTransactionOutputListByAddressResponse();
            response.setTransactionOutputDetailViewList(transactionOutputDetailViewList);
            return ServiceResult.createSuccessServiceResult("[查询交易输出]成功",response);
        } catch (Exception e){
            String message = "[查询交易输出]失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }

    /**
     * 根据交易输出ID获取交易输出
     */
    @RequestMapping(value = BlockchainApiRoute.QUERY_TRANSACTION_OUTPUT_BY_TRANSACTION_OUTPUT_ID,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<QueryTransactionOutputByTransactionOutputIdResponse> queryTransactionOutputByTransactionOutputId(@RequestBody QueryTransactionOutputByTransactionOutputIdRequest request){
        try {
            TransactionOutputId transactionOutputId = request.getTransactionOutputId();
            TransactionOutputDetailView transactionOutputDetailView = blockchainBrowserService.queryTransactionOutputByTransactionOutputId(transactionOutputId);
            QueryTransactionOutputByTransactionOutputIdResponse response = new QueryTransactionOutputByTransactionOutputIdResponse();
            response.setTransactionOutputDetailView(transactionOutputDetailView);
            return ServiceResult.createSuccessServiceResult("[查询交易输出]成功",response);
        } catch (Exception e){
            String message = "[查询交易输出]失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }

    /**
     * 查询区块链高度
     */
    @RequestMapping(value = BlockchainApiRoute.QUERY_BLOCKCHAIN_HEIGHT,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<QueryBlockchainHeightResponse> queryBlockchainHeight(@RequestBody QueryBlockchainHeightRequest request){
        try {
            long blockchainHeight = getBlockchainCore().queryBlockchainHeight();
            QueryBlockchainHeightResponse response = new QueryBlockchainHeightResponse();
            response.setBlockchainHeight(blockchainHeight);
            return ServiceResult.createSuccessServiceResult("查询区块链高度成功",response);
        } catch (Exception e){
            String message = "查询区块链高度失败";
            logger.error(message,e);
            return ServiceResult.createSuccessServiceResult(message,null);
        }
    }

    /**
     * 查询挖矿中的交易
     */
    @RequestMapping(value = BlockchainApiRoute.QUERY_MINING_TRANSACTION_LIST,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<QueryMiningTransactionListResponse> queryMiningTransactionList(@RequestBody QueryMiningTransactionListRequest request){
        try {
            PageCondition pageCondition = request.getPageCondition();
            List<TransactionDTO> transactionDtoList = getBlockchainCore().queryMiningTransactionList(pageCondition.getFrom(),pageCondition.getSize());
            if(transactionDtoList == null){
                return ServiceResult.createSuccessServiceResult("未查询到未确认的交易");
            }

            List<Transaction> transactionDtoListResp = new ArrayList<>();
            for(TransactionDTO transactionDto : transactionDtoList){
                Transaction transaction = Dto2ModelTool.transactionDto2Transaction(getBlockchainCore().getBlockchainDataBase(),transactionDto);
                transactionDtoListResp.add(transaction);
            }
            QueryMiningTransactionListResponse response = new QueryMiningTransactionListResponse();
            response.setTransactionDtoList(transactionDtoListResp);
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
    @RequestMapping(value = BlockchainApiRoute.QUERY_BLOCKDTO_BY_BLOCK_HEIGHT,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<QueryBlockDtoByBlockHeightResponse> queryBlockDtoByBlockHeight(@RequestBody QueryBlockDtoByBlockHeightRequest request){
        try {
            Block block = getBlockchainCore().queryBlockByBlockHeight(request.getBlockHeight());
            if(block == null){
                return ServiceResult.createFailServiceResult(String.format("区块链中不存在区块高度[%d]，请检查输入高度。",request.getBlockHeight()));
            }
            Block nextBlock = getBlockchainCore().queryBlockByBlockHeight(block.getHeight()+1);

            QueryBlockDtoByBlockHashResponse.BlockDto blockDto = new QueryBlockDtoByBlockHashResponse.BlockDto();
            blockDto.setHeight(block.getHeight());
            blockDto.setConfirmCount(BlockTool.getTransactionCount(block));
            blockDto.setBlockSize(SizeTool.calculateBlockSize(block)+"字符");
            blockDto.setTransactionCount(BlockTool.getTransactionCount(block));
            blockDto.setTime(DateUtil.timestamp2ChinaTime(block.getTimestamp()));
            blockDto.setMinerIncentiveValue(BlockTool.getMinerIncentiveValue(block));
            blockDto.setDifficulty(BlockTool.formatDifficulty(block.getDifficulty()));
            blockDto.setNonce(block.getNonce());
            blockDto.setHash(block.getHash());
            blockDto.setPreviousBlockHash(block.getPreviousBlockHash());
            blockDto.setNextBlockHash(nextBlock==null?null:nextBlock.getHash());
            blockDto.setMerkleTreeRoot(block.getMerkleTreeRoot());

            QueryBlockDtoByBlockHeightResponse response = new QueryBlockDtoByBlockHeightResponse();
            response.setBlockDto(blockDto);
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
    @RequestMapping(value = BlockchainApiRoute.QUERY_BLOCKDTO_BY_BLOCK_HASH,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<QueryBlockDtoByBlockHashResponse> queryBlockDtoByBlockHash(@RequestBody QueryBlockDtoByBlockHashRequest request){
        try {
            Block block = getBlockchainCore().queryBlockByBlockHash(request.getBlockHash());
            if(block == null){
                return ServiceResult.createFailServiceResult(String.format("区块链中不存在区块哈希[%s]，请检查输入哈希。",request.getBlockHash()));
            }
            Block nextBlock = getBlockchainCore().queryBlockByBlockHeight(block.getHeight()+1);

            QueryBlockDtoByBlockHashResponse.BlockDto blockDto = new QueryBlockDtoByBlockHashResponse.BlockDto();
            blockDto.setHeight(block.getHeight());
            blockDto.setConfirmCount(BlockTool.getTransactionCount(block));
            blockDto.setBlockSize(SizeTool.calculateBlockSize(block)+"字符");
            blockDto.setTransactionCount(BlockTool.getTransactionCount(block));
            blockDto.setTime(DateUtil.timestamp2ChinaTime(block.getTimestamp()));
            blockDto.setMinerIncentiveValue(BlockTool.getMinerIncentiveValue(block));
            blockDto.setDifficulty(BlockTool.formatDifficulty(block.getDifficulty()));
            blockDto.setNonce(block.getNonce());
            blockDto.setHash(block.getHash());
            blockDto.setPreviousBlockHash(block.getPreviousBlockHash());
            blockDto.setNextBlockHash(nextBlock==null?null:nextBlock.getHash());
            blockDto.setMerkleTreeRoot(block.getMerkleTreeRoot());

            QueryBlockDtoByBlockHashResponse response = new QueryBlockDtoByBlockHashResponse();
            response.setBlockDto(blockDto);
            return ServiceResult.createSuccessServiceResult("[根据区块哈希查询区块]成功",response);
        } catch (Exception e){
            String message = "[根据区块哈希查询区块]失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }

    /**
     * 查询最近的10个区块
     */
    @RequestMapping(value = BlockchainApiRoute.QUERY_LAST10_BLOCKDTO,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<QueryLast10BlockDtoResponse> queryLast10BlockDto(@RequestBody QueryLast10BlockDtoRequest request){
        try {
            List<Block> blockList = new ArrayList<>();
            long blockchainHeight = getBlockchainCore().queryBlockchainHeight();
            long minBlockHeight = blockchainHeight-9>0?blockchainHeight-9:1;
            while (blockchainHeight >= minBlockHeight){
                Block block = getBlockchainCore().queryBlockByBlockHeight(blockchainHeight);
                blockList.add(block);
                blockchainHeight--;
            }

            QueryLast10BlockDtoResponse response = new QueryLast10BlockDtoResponse();
            List<QueryLast10BlockDtoResponse.BlockDto> blockDtoList = new ArrayList<>();
            for(Block block : blockList){
                QueryLast10BlockDtoResponse.BlockDto blockDto = new QueryLast10BlockDtoResponse.BlockDto();
                blockDto.setHeight(block.getHeight());
                blockDto.setBlockSize(SizeTool.calculateBlockSize(block)+"字符");
                blockDto.setTransactionCount(BlockTool.getTransactionCount(block));
                blockDto.setMinerIncentiveValue(BlockTool.getMinerIncentiveValue(block));
                blockDto.setTime(DateUtil.timestamp2ChinaTime(block.getTimestamp()));
                blockDto.setHash(block.getHash());
                blockDtoList.add(blockDto);
            }
            response.setBlockDtoList(blockDtoList);
            return ServiceResult.createSuccessServiceResult("[查询最近的10个区块]成功",response);
        } catch (Exception e){
            String message = "[查询最近的10个区块]失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }
    private BlockchainCore getBlockchainCore(){
        return netBlockchainCore.getBlockchainCore();
    }
}