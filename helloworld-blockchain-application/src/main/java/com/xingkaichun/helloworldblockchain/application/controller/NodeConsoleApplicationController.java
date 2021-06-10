package com.xingkaichun.helloworldblockchain.application.controller;

import com.xingkaichun.helloworldblockchain.application.vo.NodeConsoleApplicationApi;
import com.xingkaichun.helloworldblockchain.application.vo.block.DeleteBlocksRequest;
import com.xingkaichun.helloworldblockchain.application.vo.block.DeleteBlocksResponse;
import com.xingkaichun.helloworldblockchain.application.vo.framwork.ServiceResult;
import com.xingkaichun.helloworldblockchain.application.vo.miner.*;
import com.xingkaichun.helloworldblockchain.application.vo.node.*;
import com.xingkaichun.helloworldblockchain.application.vo.synchronizer.*;
import com.xingkaichun.helloworldblockchain.core.BlockchainCore;
import com.xingkaichun.helloworldblockchain.netcore.BlockchainNetCore;
import com.xingkaichun.helloworldblockchain.netcore.model.Node;
import com.xingkaichun.helloworldblockchain.util.LogUtil;
import com.xingkaichun.helloworldblockchain.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 节点控制台应用控制器：用于控制本地区块链节点，如激活矿工、停用矿工、同步其它节点数据等。
 * 这里的操作都应该需要权限才可以操作，不适合对所有人开放。
 *
 * @author 邢开春 409060350@qq.com
 */
@RestController
public class NodeConsoleApplicationController {

    @Autowired
    private BlockchainNetCore blockchainNetCore;

    @Autowired
    private BlockchainCore blockchainCore;



    /**
     * 矿工是否激活
     */
    @RequestMapping(value = NodeConsoleApplicationApi.IS_MINER_ACTIVE,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<IsMinerActiveResponse> isMineActive(@RequestBody IsMinerActiveRequest request){
        try {
            boolean isMineActive = blockchainCore.getMiner().isActive();
            IsMinerActiveResponse response = new IsMinerActiveResponse();
            response.setMinerInActiveState(isMineActive);
            return ServiceResult.createSuccessServiceResult("查询矿工是否处于激活状态成功",response);
        } catch (Exception e){
            String message = "查询矿工是否处于激活状态失败";
            LogUtil.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }
    /**
     * 激活矿工
     */
    @RequestMapping(value = NodeConsoleApplicationApi.ACTIVE_MINER,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<ActiveMinerResponse> activeMiner(@RequestBody ActiveMinerRequest request){
        try {
            blockchainCore.getMiner().active();
            ActiveMinerResponse response = new ActiveMinerResponse();
            response.setActiveMinerSuccess(true);
            return ServiceResult.createSuccessServiceResult("激活矿工成功",response);
        } catch (Exception e){
            String message = "激活矿工失败";
            LogUtil.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }
    /**
     * 停用矿工
     */
    @RequestMapping(value = NodeConsoleApplicationApi.DEACTIVE_MINER,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<DeactiveMinerResponse> deactiveMiner(@RequestBody DeactiveMinerRequest request){
        try {
            blockchainCore.getMiner().deactive();
            DeactiveMinerResponse response = new DeactiveMinerResponse();
            response.setDeactiveMinerSuccess(true);
            return ServiceResult.createSuccessServiceResult("停用矿工成功",response);
        } catch (Exception e){
            String message = "停用矿工失败";
            LogUtil.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }



    /**
     * 是否"自动搜索新区块"
     */
    @RequestMapping(value = NodeConsoleApplicationApi.IS_AUTO_SEARCH_BLOCK,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<IsAutoSearchBlockResponse> isAutoSearchBlock(@RequestBody IsAutoSearchBlockRequest request){
        try {
            boolean isAutoSearchBlock = blockchainNetCore.getNetCoreConfiguration().isAutoSearchBlock();
            IsAutoSearchBlockResponse response = new IsAutoSearchBlockResponse();
            response.setAutoSearchBlock(isAutoSearchBlock);
            return ServiceResult.createSuccessServiceResult("查询[是否自动搜索新区块]成功",response);
        } catch (Exception e){
            String message = "查询[是否自动搜索新区块]失败";
            LogUtil.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }
    /**
     * 开启"自动搜索新区块"选项
     */
    @RequestMapping(value = NodeConsoleApplicationApi.ACTIVE_AUTO_SEARCH_BLOCK,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<ActiveAutoSearchBlockResponse> activeAutoSearchBlock(@RequestBody ActiveAutoSearchBlockRequest request){
        try {
            blockchainNetCore.getNetCoreConfiguration().activeAutoSearchBlock();
            ActiveAutoSearchBlockResponse response = new ActiveAutoSearchBlockResponse();
            response.setActiveAutoSearchBlockSuccess(true);
            return ServiceResult.createSuccessServiceResult("开启自动搜索新区块选项成功",response);
        } catch (Exception e){
            String message = "开启自动搜索新区块选项失败";
            LogUtil.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }
    /**
     * 关闭"自动搜索新区块"选项
     */
    @RequestMapping(value = NodeConsoleApplicationApi.DEACTIVE_AUTO_SEARCH_BLOCK,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<DeactiveAutoSearchBlockResponse> deactiveSynchronizer(@RequestBody DeactiveAutoSearchBlockRequest request){
        try {
            blockchainNetCore.getNetCoreConfiguration().deactiveAutoSearchBlock();
            DeactiveAutoSearchBlockResponse response = new DeactiveAutoSearchBlockResponse();
            response.setDeactiveAutoSearchBlockSuccess(true);
            return ServiceResult.createSuccessServiceResult("关闭自动搜索新区块选项成功",response);
        } catch (Exception e){
            String message = "关闭自动搜索新区块选项失败";
            LogUtil.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }



    /**
     * 新增节点
     */
    @RequestMapping(value = NodeConsoleApplicationApi.ADD_NODE,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<AddNodeResponse> addNode(@RequestBody AddNodeRequest request){
        try {
            Node node = request.getNode();
            if(StringUtil.isNullOrEmpty(node.getIp())){
                return ServiceResult.createFailServiceResult("节点IP不能为空");
            }
            if(blockchainNetCore.getNodeService().queryNode(node.getIp()) != null){
                return ServiceResult.createFailServiceResult("节点已经存在，不需要重复添加");
            }
            blockchainNetCore.getNodeService().addNode(node);
            AddNodeResponse response = new AddNodeResponse();
            response.setAddNodeSuccess(true);
            return ServiceResult.createSuccessServiceResult("新增节点成功",response);
        } catch (Exception e){
            String message = "新增节点失败";
            LogUtil.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }
    /**
     * 更新节点信息
     */
    @RequestMapping(value = NodeConsoleApplicationApi.UPDATE_NODE,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<UpdateNodeResponse> updateNode(@RequestBody UpdateNodeRequest request){
        try {
            if(request.getNode() == null){
                return ServiceResult.createFailServiceResult("请填写节点信息");
            }
            blockchainNetCore.getNodeService().updateNode(request.getNode());
            UpdateNodeResponse response = new UpdateNodeResponse();
            return ServiceResult.createSuccessServiceResult("更新节点信息成功",response);
        } catch (Exception e){
            String message = "更新节点信息失败";
            LogUtil.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }
    /**
     * 删除节点
     */
    @RequestMapping(value = NodeConsoleApplicationApi.DELETE_NODE,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<DeleteNodeResponse> deleteNode(@RequestBody DeleteNodeRequest request){
        try {
            blockchainNetCore.getNodeService().deleteNode(request.getNode().getIp());
            DeleteNodeResponse response = new DeleteNodeResponse();
            return ServiceResult.createSuccessServiceResult("删除节点成功",response);
        } catch (Exception e){
            String message = "删除节点失败";
            LogUtil.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }
    /**
     * 查询所有节点
     */
    @RequestMapping(value = NodeConsoleApplicationApi.QUERY_ALL_NODES,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<QueryAllNodesResponse> queryAllNodes(@RequestBody QueryAllNodesRequest request){
        try {
            List<Node> nodes = blockchainNetCore.getNodeService().queryAllNodes();
            QueryAllNodesResponse response = new QueryAllNodesResponse();
            response.setNodes(nodes);
            return ServiceResult.createSuccessServiceResult("查询节点成功",response);
        } catch (Exception e){
            String message = "查询节点失败";
            LogUtil.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }



    /**
     * 是否开启了自动寻找区块链节点的功能
     */
    @RequestMapping(value = NodeConsoleApplicationApi.IS_AUTO_SEARCH_NODE,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<IsAutoSearchNodeResponse> isAutoSearchNode(@RequestBody IsAutoSearchNodeRequest request){
        try {
            boolean isAutoSearchNode = blockchainNetCore.getNetCoreConfiguration().isAutoSearchNode();
            IsAutoSearchNodeResponse response = new IsAutoSearchNodeResponse();
            response.setAutoSearchNode(isAutoSearchNode);
            return ServiceResult.createSuccessServiceResult("查询是否允许自动搜索区块链节点成功",response);
        } catch (Exception e){
            String message = "查询是否允许自动搜索区块链节点失败";
            LogUtil.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }
    /**
     * 开启"自动搜索节点"选项
     */
    @RequestMapping(value = NodeConsoleApplicationApi.ACTIVE_AUTO_SEARCH_NODE,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<ActiveAutoSearchNodeResponse> activeAutoSearchNode(@RequestBody ActiveAutoSearchNodeRequest request){
        try {
            blockchainNetCore.getNetCoreConfiguration().activeAutoSearchNode();
            ActiveAutoSearchNodeResponse response = new ActiveAutoSearchNodeResponse();
            return ServiceResult.createSuccessServiceResult("开启自动搜索节点选项成功",response);
        } catch (Exception e){
            String message = "开启自动搜索节点选项失败";
            LogUtil.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }
    /**
     * 关闭"自动搜索节点"选项
     */
    @RequestMapping(value = NodeConsoleApplicationApi.DEACTIVE_AUTO_SEARCH_NODE,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<DeactiveAutoSearchNodeResponse> deactiveAutoSearchNode(@RequestBody DeactiveAutoSearchNodeRequest request){
        try {
            blockchainNetCore.getNetCoreConfiguration().deactiveAutoSearchNode();
            DeactiveAutoSearchNodeResponse response = new DeactiveAutoSearchNodeResponse();
            return ServiceResult.createSuccessServiceResult("关闭自动搜索新区块选项成功",response);
        } catch (Exception e){
            String message = "关闭自动搜索新区块选项失败";
            LogUtil.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }



    /**
     * 删除区块
     */
    @RequestMapping(value = NodeConsoleApplicationApi.DELETE_BLOCKS,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<DeleteBlocksResponse> deleteBlocks(@RequestBody DeleteBlocksRequest request){
        try {
            if(request.getBlockHeight() == null){
                return ServiceResult.createFailServiceResult("删除区块失败，区块高度不能空。");
            }
            blockchainCore.deleteBlocks(request.getBlockHeight());
            DeleteBlocksResponse response = new DeleteBlocksResponse();
            return ServiceResult.createSuccessServiceResult("删除区块成功",response);
        } catch (Exception e){
            String message = "删除区块失败";
            LogUtil.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }
}