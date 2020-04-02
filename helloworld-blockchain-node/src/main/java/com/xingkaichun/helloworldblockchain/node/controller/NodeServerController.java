package com.xingkaichun.helloworldblockchain.node.controller;

import com.xingkaichun.helloworldblockchain.dto.BlockDTO;
import com.xingkaichun.helloworldblockchain.node.dto.common.ServiceResult;
import com.xingkaichun.helloworldblockchain.node.dto.nodeserver.Node;
import com.xingkaichun.helloworldblockchain.node.dto.nodeserver.NodeServerApiRoute;
import com.xingkaichun.helloworldblockchain.node.dto.nodeserver.request.*;
import com.xingkaichun.helloworldblockchain.node.dto.nodeserver.response.*;
import com.xingkaichun.helloworldblockchain.node.service.BlockChainCoreService;
import com.xingkaichun.helloworldblockchain.node.service.BlockchainNodeServerService;
import com.xingkaichun.helloworldblockchain.node.service.ConfigurationService;
import com.xingkaichun.helloworldblockchain.node.service.NodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.util.List;

/**
 * 节点之间交换信息的控制器
 */
@Controller
@RequestMapping
public class NodeServerController {

    private static final Logger logger = LoggerFactory.getLogger(NodeServerController.class);

    @Autowired
    private BlockChainCoreService blockChainCoreService;

    @Autowired
    private NodeService nodeService;

    @Autowired
    private BlockchainNodeServerService blockchainNodeServerService;

    @Autowired
    private ConfigurationService configurationService;

    /**
     * Ping节点
     */
    @ResponseBody
    @RequestMapping(value = NodeServerApiRoute.PING,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<PingResponse> ping(HttpServletRequest httpServletRequest, @RequestBody PingRequest request){
        try {
            List<Node> nodeList = nodeService.queryAllNoForkNodeList();
            BigInteger blockChainHeight = blockChainCoreService.queryBlockChainHeight();

            //将ping的来路作为区块链节点
           if(configurationService.autoSearchNode()){
                Node node = new Node();
                String ip = httpServletRequest.getRemoteHost();
                node.setIp(ip);
                node.setPort(request.getPort());
                node.setIsNodeAvailable(true);
                nodeService.addOrUpdateNode(node);
                logger.debug(String.format("有节点[%s:%d]尝试Ping本地节点，将来路节点加入节点数据库。",ip,request.getPort()));
            }

            PingResponse response = new PingResponse();
            response.setNodeList(nodeList);
            response.setBlockChainHeight(blockChainHeight);
            return ServiceResult.createSuccessServiceResult("查询节点信息成功",response);
        } catch (Exception e){
            String message = "查询节点信息成功失败";
            logger.error(message,e);
            return ServiceResult.createSuccessServiceResult(message,null);
        }
    }

    /**
     * 更新节点信息：其它节点通知本地节点它的信息有变更
     */
    @ResponseBody
    @RequestMapping(value = NodeServerApiRoute.ADD_OR_UPDATE_NODE,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<AddOrUpdateNodeResponse> addOrUpdateNode(HttpServletRequest httpServletRequest, @RequestBody AddOrUpdateNodeRequest request){
        try {
            Node node = new Node();
            String ip = httpServletRequest.getRemoteHost();
            node.setIp(ip);
            node.setPort(request.getPort());
            node.setIsNodeAvailable(true);
            node.setBlockChainHeight(request.getBlockChainHeight());
            node.setErrorConnectionTimes(0);

            if(!configurationService.autoSearchNode()){
                Node nodeInDb = nodeService.queryNode(node);
                if(nodeInDb == null){
                    return ServiceResult.createSuccessServiceResult("你不是该节点的授信节点。",null);
                }
                logger.debug(String.format("有节点[%s:%d]尝试Ping本地节点，将来路节点加入节点数据库。",ip,request.getPort()));
            }

            nodeService.addOrUpdateNode(node);
            AddOrUpdateNodeResponse response = new AddOrUpdateNodeResponse();
            return ServiceResult.createSuccessServiceResult("更新节点成功",response);
        } catch (Exception e){
            String message = "更新节点失败";
            logger.error(message,e);
            return ServiceResult.createSuccessServiceResult(message,null);
        }
    }


    /**
     * 根据区块高度查询区块Hash
     */
    @ResponseBody
    @RequestMapping(value = NodeServerApiRoute.QUERY_BLOCK_HASH_BY_BLOCK_HEIGHT,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<QueryBlockHashByBlockHeightResponse> queryBlockHashByBlockHeight(@RequestBody QueryBlockHashByBlockHeightRequest request){
        try {
            String blockHash = blockChainCoreService.queryBlockHashByBlockHeight(request.getBlockHeight());

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
     * 根据区块高度查询区块
     */
    @ResponseBody
    @RequestMapping(value = NodeServerApiRoute.QUERY_BLOCKDTO_BY_BLOCK_HEIGHT,method={RequestMethod.GET,RequestMethod.POST})
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

    /**
     * 接收其它节点提交的交易
     */
    @ResponseBody
    @RequestMapping(value = NodeServerApiRoute.RECEIVE_TRANSACTION,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<ReceiveTransactionResponse> receiveTransaction(@RequestBody ReceiveTransactionRequest request){
        try {
            blockchainNodeServerService.receiveTransaction(request.getTransactionDTO());

            ReceiveTransactionResponse response = new ReceiveTransactionResponse();
            return ServiceResult.createSuccessServiceResult("提交交易成功",response);
        } catch (Exception e){
            String message = "提交交易失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }
}