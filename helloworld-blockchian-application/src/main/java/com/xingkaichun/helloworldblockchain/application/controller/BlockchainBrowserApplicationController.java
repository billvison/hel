package com.xingkaichun.helloworldblockchain.application.controller;

import com.xingkaichun.helloworldblockchain.application.service.BlockchainBrowserApplicationService;
import com.xingkaichun.helloworldblockchain.application.vo.BlockchainBrowserApplicationApi;
import com.xingkaichun.helloworldblockchain.application.vo.block.*;
import com.xingkaichun.helloworldblockchain.application.vo.framwork.PageCondition;
import com.xingkaichun.helloworldblockchain.application.vo.framwork.ServiceResult;
import com.xingkaichun.helloworldblockchain.application.vo.node.QueryBlockchainHeightRequest;
import com.xingkaichun.helloworldblockchain.application.vo.node.QueryBlockchainHeightResponse;
import com.xingkaichun.helloworldblockchain.application.vo.transaction.*;
import com.xingkaichun.helloworldblockchain.core.BlockchainCore;
import com.xingkaichun.helloworldblockchain.core.model.Block;
import com.xingkaichun.helloworldblockchain.core.model.transaction.TransactionOutputId;
import com.xingkaichun.helloworldblockchain.core.tools.BlockTool;
import com.xingkaichun.helloworldblockchain.core.tools.SizeTool;
import com.xingkaichun.helloworldblockchain.core.tools.TransactionTool;
import com.xingkaichun.helloworldblockchain.netcore.BlockchainNetCore;
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
 * 区块链浏览器应用控制器：查询区块、交易、地址等功能。
 *
 * @author 邢开春 409060350@qq.com
 */
@RestController
public class BlockchainBrowserApplicationController {

    @Autowired
    private BlockchainNetCore blockchainNetCore;
    @Autowired
    private BlockchainBrowserApplicationService blockchainBrowserApplicationService;


    /**
     * 根据交易哈希查询交易
     */
    @RequestMapping(value = BlockchainBrowserApplicationApi.QUERY_TRANSACTION_BY_TRANSACTION_HASH,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<QueryTransactionByTransactionHashResponse> queryTransactionByTransactionHash(@RequestBody QueryTransactionByTransactionHashRequest request){
        try {
            TransactionVo transactionVo = blockchainBrowserApplicationService.queryTransactionByTransactionHash(request.getTransactionHash());
            if(transactionVo == null){
                return ServiceResult.createFailServiceResult("根据交易哈希未能查询到交易");
            }

            QueryTransactionByTransactionHashResponse response = new QueryTransactionByTransactionHashResponse();
            response.setTransactionVo(transactionVo);
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
    @RequestMapping(value = BlockchainBrowserApplicationApi.QUERY_TRANSACTION_LIST_BY_BLOCK_HASH_TRANSACTION_HEIGHT,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<QueryTransactionListByBlockHashTransactionHeightResponse> queryTransactionListByBlockHashTransactionHeight(@RequestBody QueryTransactionListByBlockHashTransactionHeightRequest request){
        try {
            PageCondition pageCondition = request.getPageCondition();
            if(StringUtil.isNullOrEmpty(request.getBlockHash())){
                return ServiceResult.createFailServiceResult("区块哈希不能是空");
            }
            List<TransactionVo> transactionVoList = blockchainBrowserApplicationService.queryTransactionListByBlockHashTransactionHeight(request.getBlockHash(),pageCondition.getFrom(),pageCondition.getSize());
            QueryTransactionListByBlockHashTransactionHeightResponse response = new QueryTransactionListByBlockHashTransactionHeightResponse();
            response.setTransactionVoList(transactionVoList);
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
    @RequestMapping(value = BlockchainBrowserApplicationApi.QUERY_TRANSACTION_OUTPUT_BY_ADDRESS,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<QueryTransactionOutputByAddressResponse> queryTransactionOutputListByAddress(@RequestBody QueryTransactionOutputByAddressRequest request){
        try {
            TransactionOutputDetailVo transactionOutputDetailVo = blockchainBrowserApplicationService.queryTransactionOutputByAddress(request.getAddress());
            QueryTransactionOutputByAddressResponse response = new QueryTransactionOutputByAddressResponse();
            response.setTransactionOutputDetailVo(transactionOutputDetailVo);
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
    @RequestMapping(value = BlockchainBrowserApplicationApi.QUERY_TRANSACTION_OUTPUT_BY_TRANSACTION_OUTPUT_ID,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<QueryTransactionOutputByTransactionOutputIdResponse> queryTransactionOutputByTransactionOutputId(@RequestBody QueryTransactionOutputByTransactionOutputIdRequest request){
        try {
            TransactionOutputId transactionOutputId = request.getTransactionOutputId();
            TransactionOutputDetailVo transactionOutputDetailVo = blockchainBrowserApplicationService.queryTransactionOutputByTransactionOutputId(transactionOutputId);
            QueryTransactionOutputByTransactionOutputIdResponse response = new QueryTransactionOutputByTransactionOutputIdResponse();
            response.setTransactionOutputDetailVo(transactionOutputDetailVo);
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
    @RequestMapping(value = BlockchainBrowserApplicationApi.QUERY_BLOCKCHAIN_HEIGHT,method={RequestMethod.GET,RequestMethod.POST})
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
    @RequestMapping(value = BlockchainBrowserApplicationApi.QUERY_MINING_TRANSACTION_BY_TRANSACTION_HASH,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<QueryMiningTransactionByTransactionHashResponse> queryMiningTransactionByTransactionHash(@RequestBody QueryMiningTransactionByTransactionHashRequest request){
        try {
            MiningTransactionVo miningTransactionVo = blockchainBrowserApplicationService.queryMiningTransactionByTransactionHash(request.getTransactionHash());
            if(miningTransactionVo == null){
                return ServiceResult.createFailServiceResult(String.format("交易哈希[%s]不是正在被挖矿的交易。",request.getTransactionHash()));
            }
            QueryMiningTransactionByTransactionHashResponse response = new QueryMiningTransactionByTransactionHashResponse();
            response.setTransactionDTO(miningTransactionVo);
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
    @RequestMapping(value = BlockchainBrowserApplicationApi.QUERY_MINING_TRANSACTION_LIST,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<QueryMiningTransactionListResponse> queryMiningTransactionList(@RequestBody QueryMiningTransactionListRequest request){
        try {
            PageCondition pageCondition = request.getPageCondition();
            List<TransactionDTO> transactionDtoList = getBlockchainCore().queryMiningTransactionList(pageCondition.getFrom(),pageCondition.getSize());
            if(transactionDtoList == null){
                return ServiceResult.createSuccessServiceResult("未查询到未确认的交易");
            }

            List<MiningTransactionVo> transactionDtoListResp = new ArrayList<>();
            for(TransactionDTO transactionDto : transactionDtoList){
                MiningTransactionVo miningTransactionVo = blockchainBrowserApplicationService.queryMiningTransactionByTransactionHash(TransactionTool.calculateTransactionHash(transactionDto));
                transactionDtoListResp.add(miningTransactionVo);
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
    @RequestMapping(value = BlockchainBrowserApplicationApi.QUERY_BLOCK_BY_BLOCK_HEIGHT,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<QueryBlockByBlockHeightResponse> queryBlockDtoByBlockHeight(@RequestBody QueryBlockByBlockHeightRequest request){
        try {
            BlockVo blockVo = blockchainBrowserApplicationService.queryBlockViewByBlockHeight(request.getBlockHeight());
            if(blockVo == null){
                return ServiceResult.createFailServiceResult(String.format("区块链中不存在区块高度[%d]，请检查输入高度。",request.getBlockHeight()));
            }
            QueryBlockByBlockHeightResponse response = new QueryBlockByBlockHeightResponse();
            response.setBlockVo(blockVo);
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
    @RequestMapping(value = BlockchainBrowserApplicationApi.QUERY_BLOCK_BY_BLOCK_HASH,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<QueryBlockByBlockHashResponse> queryBlockDtoByBlockHash(@RequestBody QueryBlockByBlockHashRequest request){
        try {
            Block block = getBlockchainCore().queryBlockByBlockHash(request.getBlockHash());
            if(block == null){
                return ServiceResult.createFailServiceResult(String.format("区块链中不存在区块哈希[%s]，请检查输入哈希。",request.getBlockHash()));
            }
            BlockVo blockVo = blockchainBrowserApplicationService.queryBlockViewByBlockHeight(block.getHeight());
            QueryBlockByBlockHashResponse response = new QueryBlockByBlockHashResponse();
            response.setBlockVo(blockVo);
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
    @RequestMapping(value = BlockchainBrowserApplicationApi.QUERY_TOP10_BLOCK,method={RequestMethod.GET,RequestMethod.POST})
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

            List<QueryTop10BlockResponse.BlockVo> BlockVos = new ArrayList<>();
            for(Block block : blockList){
                QueryTop10BlockResponse.BlockVo blockVo = new QueryTop10BlockResponse.BlockVo();
                blockVo.setHeight(block.getHeight());
                blockVo.setBlockSize(SizeTool.calculateBlockSize(block)+"字符");
                blockVo.setTransactionCount(BlockTool.getTransactionCount(block));
                blockVo.setMinerIncentiveValue(BlockTool.getMinerIncentiveValue(block));
                blockVo.setTime(TimeUtil.timestamp2FormatDate(block.getTimestamp()));
                blockVo.setHash(block.getHash());
                BlockVos.add(blockVo);
            }

            QueryTop10BlockResponse response = new QueryTop10BlockResponse();
            response.setBlockVos(BlockVos);
            return ServiceResult.createSuccessServiceResult("[查询最近的10个区块]成功",response);
        } catch (Exception e){
            String message = "[查询最近的10个区块]失败";
            LogUtil.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }

    private BlockchainCore getBlockchainCore(){
        return blockchainNetCore.getBlockchainCore();
    }
}