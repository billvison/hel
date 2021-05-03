package com.xingkaichun.helloworldblockchain.explorer.controller;

import com.xingkaichun.helloworldblockchain.core.BlockchainCore;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutputId;
import com.xingkaichun.helloworldblockchain.core.tools.BlockTool;
import com.xingkaichun.helloworldblockchain.core.tools.SizeTool;
import com.xingkaichun.helloworldblockchain.core.tools.TransactionTool;
import com.xingkaichun.helloworldblockchain.crypto.AccountUtil;
import com.xingkaichun.helloworldblockchain.crypto.model.Account;
import com.xingkaichun.helloworldblockchain.explorer.service.BlockchainBrowserService;
import com.xingkaichun.helloworldblockchain.explorer.vo.BlockchainApiRoute;
import com.xingkaichun.helloworldblockchain.explorer.vo.account.GenerateAccountRequest;
import com.xingkaichun.helloworldblockchain.explorer.vo.account.GenerateAccountResponse;
import com.xingkaichun.helloworldblockchain.explorer.vo.account.GenerateAndSaveAccountRequest;
import com.xingkaichun.helloworldblockchain.explorer.vo.account.GenerateAndSaveAccountResponse;
import com.xingkaichun.helloworldblockchain.explorer.vo.block.*;
import com.xingkaichun.helloworldblockchain.explorer.vo.framwork.PageCondition;
import com.xingkaichun.helloworldblockchain.explorer.vo.framwork.ServiceResult;
import com.xingkaichun.helloworldblockchain.explorer.vo.node.QueryBlockchainHeightRequest;
import com.xingkaichun.helloworldblockchain.explorer.vo.node.QueryBlockchainHeightResponse;
import com.xingkaichun.helloworldblockchain.explorer.vo.transaction.*;
import com.xingkaichun.helloworldblockchain.netcore.NetBlockchainCore;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.TransactionDTO;
import com.xingkaichun.helloworldblockchain.setting.GlobalSetting;
import com.xingkaichun.helloworldblockchain.util.LogUtil;
import com.xingkaichun.helloworldblockchain.util.StringUtil;
import com.xingkaichun.helloworldblockchain.util.TimeUtil;
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

    @Autowired
    private NetBlockchainCore netBlockchainCore;
    @Autowired
    private BlockchainBrowserService blockchainBrowserService;

   /**
     * 生成账户(私钥、公钥、公钥哈希、地址)
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
            LogUtil.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }

    /**
     * 生成账户(私钥、公钥、公钥哈希、地址)并保存
     */
    @RequestMapping(value = BlockchainApiRoute.GENERATE_AND_SAVE_ACCOUNT,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<GenerateAndSaveAccountResponse> generateAndSaveAccount(@RequestBody GenerateAndSaveAccountRequest request){
        try {
            Account account = getBlockchainCore().getWallet().createAndAddAccount();
            GenerateAndSaveAccountResponse response = new GenerateAndSaveAccountResponse();
            response.setAccount(account);
            return ServiceResult.createSuccessServiceResult("[生成账户并保存]成功",response);
        } catch (Exception e){
            String message = "[生成账户并保存]失败";
            LogUtil.error(message,e);
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
            LogUtil.error(message,e);
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
            LogUtil.error(message,e);
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
            LogUtil.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }

    /**
     * 根据地址获取交易输出
     */
    @RequestMapping(value = BlockchainApiRoute.QUERY_TRANSACTION_OUTPUT_BY_ADDRESS,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<QueryTransactionOutputByAddressResponse> queryTransactionOutputListByAddress(@RequestBody QueryTransactionOutputByAddressRequest request){
        try {
            TransactionOutputDetailView transactionOutputDetailView = blockchainBrowserService.queryTransactionOutputByAddress(request.getAddress());
            QueryTransactionOutputByAddressResponse response = new QueryTransactionOutputByAddressResponse();
            response.setTransactionOutputDetailView(transactionOutputDetailView);
            return ServiceResult.createSuccessServiceResult("[查询交易输出]成功",response);
        } catch (Exception e){
            String message = "[查询交易输出]失败";
            LogUtil.error(message,e);
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
            LogUtil.error(message,e);
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
            LogUtil.error(message,e);
            return ServiceResult.createSuccessServiceResult(message,null);
        }
    }

    /**
     * 根据交易哈希查询挖矿中交易
     */
    @RequestMapping(value = BlockchainApiRoute.QUERY_MINING_TRANSACTION_BY_TRANSACTION_HASH,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<QueryMiningTransactionByTransactionHashResponse> queryMiningTransactionByTransactionHash(@RequestBody QueryMiningTransactionByTransactionHashRequest request){
        try {
            MiningTransactionView miningTransactionView = blockchainBrowserService.queryMiningTransactionByTransactionHash(request.getTransactionHash());
            if(miningTransactionView == null){
                return ServiceResult.createFailServiceResult(String.format("交易哈希[%s]不是正在被挖矿的交易。",request.getTransactionHash()));
            }
            QueryMiningTransactionByTransactionHashResponse response = new QueryMiningTransactionByTransactionHashResponse();
            response.setTransactionDTO(miningTransactionView);
            return ServiceResult.createSuccessServiceResult("根据交易哈希查询挖矿中交易成功",response);
        } catch (Exception e){
            String message = "根据交易哈希查询挖矿中交易失败";
            LogUtil.error(message,e);
            return ServiceResult.createFailServiceResult(message);
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

            List<MiningTransactionView> transactionDtoListResp = new ArrayList<>();
            for(TransactionDTO transactionDto : transactionDtoList){
                MiningTransactionView miningTransactionView = blockchainBrowserService.queryMiningTransactionByTransactionHash(TransactionTool.calculateTransactionHash(transactionDto));
                transactionDtoListResp.add(miningTransactionView);
            }
            QueryMiningTransactionListResponse response = new QueryMiningTransactionListResponse();
            response.setTransactionDtoList(transactionDtoListResp);
            return ServiceResult.createSuccessServiceResult("查询挖矿中的交易成功",response);
        } catch (Exception e){
            String message = "查询挖矿中的交易失败";
            LogUtil.error(message,e);
            return ServiceResult.createSuccessServiceResult(message,null);
        }
    }

    /**
     * 根据区块高度查询区块
     */
    @RequestMapping(value = BlockchainApiRoute.QUERY_BLOCK_BY_BLOCK_HEIGHT,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<QueryBlockByBlockHeightResponse> queryBlockDtoByBlockHeight(@RequestBody QueryBlockByBlockHeightRequest request){
        try {
            BlockView blockView = blockchainBrowserService.queryBlockViewByBlockHeight(request.getBlockHeight());
            if(blockView == null){
                return ServiceResult.createFailServiceResult(String.format("区块链中不存在区块高度[%d]，请检查输入高度。",request.getBlockHeight()));
            }
            QueryBlockByBlockHeightResponse response = new QueryBlockByBlockHeightResponse();
            response.setBlockView(blockView);
            return ServiceResult.createSuccessServiceResult("成功获取区块",response);
        } catch (Exception e){
            String message = "查询获取失败";
            LogUtil.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }

    /**
     * 根据区块哈希查询区块
     */
    @RequestMapping(value = BlockchainApiRoute.QUERY_BLOCK_BY_BLOCK_HASH,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<QueryBlockByBlockHashResponse> queryBlockDtoByBlockHash(@RequestBody QueryBlockByBlockHashRequest request){
        try {
            Block block = getBlockchainCore().queryBlockByBlockHash(request.getBlockHash());
            if(block == null){
                return ServiceResult.createFailServiceResult(String.format("区块链中不存在区块哈希[%s]，请检查输入哈希。",request.getBlockHash()));
            }
            BlockView blockView = blockchainBrowserService.queryBlockViewByBlockHeight(block.getHeight());
            QueryBlockByBlockHashResponse response = new QueryBlockByBlockHashResponse();
            response.setBlockView(blockView);
            return ServiceResult.createSuccessServiceResult("[根据区块哈希查询区块]成功",response);
        } catch (Exception e){
            String message = "[根据区块哈希查询区块]失败";
            LogUtil.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }

    /**
     * 查询最近的10个区块
     */
    @RequestMapping(value = BlockchainApiRoute.QUERY_TOP10_BLOCK,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<QueryTop10BlockResponse> queryLast10BlockDto(@RequestBody QueryTop10BlockRequest request){
        try {
            List<Block> blockList = new ArrayList<>();
            long blockHeight = getBlockchainCore().queryBlockchainHeight();
            while (true){
                if(blockHeight <= GlobalSetting.GenesisBlock.HEIGHT){
                    break;
                }
                Block block = getBlockchainCore().queryBlockByBlockHeight(blockHeight);
                blockList.add(block);
                if(blockList.size() >= 10){
                    break;
                }
                blockHeight--;
            }

            List<QueryTop10BlockResponse.BlockView> blockDtoList = new ArrayList<>();
            for(Block block : blockList){
                QueryTop10BlockResponse.BlockView blockDto = new QueryTop10BlockResponse.BlockView();
                blockDto.setHeight(block.getHeight());
                blockDto.setBlockSize(SizeTool.calculateBlockSize(block)+"字符");
                blockDto.setTransactionCount(BlockTool.getTransactionCount(block));
                blockDto.setMinerIncentiveValue(BlockTool.getMinerIncentiveValue(block));
                blockDto.setTime(TimeUtil.timestamp2FormatDate(block.getTimestamp()));
                blockDto.setHash(block.getHash());
                blockDtoList.add(blockDto);
            }

            QueryTop10BlockResponse response = new QueryTop10BlockResponse();
            response.setBlockViewList(blockDtoList);
            return ServiceResult.createSuccessServiceResult("[查询最近的10个区块]成功",response);
        } catch (Exception e){
            String message = "[查询最近的10个区块]失败";
            LogUtil.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }

    private BlockchainCore getBlockchainCore(){
        return netBlockchainCore.getBlockchainCore();
    }
}