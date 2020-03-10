package com.xingkaichun.helloworldblockchain.node.controller;

import com.xingkaichun.helloworldblockchain.dto.BlockDTO;
import com.xingkaichun.helloworldblockchain.node.dto.common.ServiceResult;
import com.xingkaichun.helloworldblockchain.node.dto.node.Node;
import com.xingkaichun.helloworldblockchain.node.dto.node.NodeApiRoute;
import com.xingkaichun.helloworldblockchain.node.dto.node.request.*;
import com.xingkaichun.helloworldblockchain.node.dto.node.response.*;
import com.xingkaichun.helloworldblockchain.node.service.BlockChainService;
import com.xingkaichun.helloworldblockchain.node.service.BlockchainNodeServerService;
import com.xingkaichun.helloworldblockchain.node.service.LocalNodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 节点之间交换信息的控制器
 */
@Controller
@RequestMapping
public class NodeController {

    private static final Logger logger = LoggerFactory.getLogger(NodeController.class);

    @Autowired
    private BlockChainService blockChainService;

    @Autowired
    private LocalNodeService localNodeService;

    @Autowired
    private BlockchainNodeServerService blockchainNodeServerService;

    /**
     * Ping节点
     */
    @ResponseBody
    @RequestMapping(value = NodeApiRoute.PING,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<PingResponse> ping(HttpServletRequest httpServletRequest, @RequestBody PingRequest request){
        try {
            List<Node> nodeList = localNodeService.queryNodes();
            int blockChainHeight = blockChainService.queryBlockChainHeight();

            //将ping的来路作为区块链节点
            Node node = new Node();
            String ip = httpServletRequest.getRemoteHost();
            int port = httpServletRequest.getRemotePort();
            node.setIp(ip);
            node.setPort(port);
            node.setNodeAvailable(true);
            localNodeService.addOrUpdateNode(node);
            logger.debug(String.format("有节点[%s:%d]尝试Ping本地节点，将来路节点加入节点数据库。",ip,port));

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
    @RequestMapping(value = NodeApiRoute.ADD_OR_UPDATE_NODE,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<AddOrUpdateNodeResponse> addOrUpdateNode(HttpServletRequest httpServletRequest, AddOrUpdateNodeRequest request){
        try {
            Node node = new Node();
            String ip = httpServletRequest.getRemoteHost();
            int port = httpServletRequest.getRemotePort();
            node.setIp(ip);
            node.setPort(port);
            node.setNodeAvailable(true);
            node.setBlockChainHeight(request.getBlockChainHeight());
            node.setErrorConnectionTimes(0);

            localNodeService.addOrUpdateNode(node);
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
    @RequestMapping(value = NodeApiRoute.QUERY_BLOCK_HASH_BY_BLOCK_HEIGHT,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<QueryBlockHashByBlockHeightResponse> queryBlockHashByBlockHeight(@RequestBody QueryBlockHashByBlockHeightRequest request){
        try {
            String blockHash = blockChainService.queryBlockHashByBlockHeight(request.getBlockHeight());

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
    @RequestMapping(value = NodeApiRoute.QUERY_BLOCKDTO_BY_BLOCK_HEIGHT,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<QueryBlockDtoByBlockHeightResponse> queryBlockDtoByBlockHeight(@RequestBody QueryBlockDtoByBlockHeightRequest request){
        try {
            BlockDTO blockDTO = blockChainService.queryBlockDtoByBlockHeight(request.getBlockHeight());

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
    @RequestMapping(value = NodeApiRoute.RECEIVE_TRANSACTION,method={RequestMethod.GET,RequestMethod.POST})
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