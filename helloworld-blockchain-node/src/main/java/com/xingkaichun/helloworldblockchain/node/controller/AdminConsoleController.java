package com.xingkaichun.helloworldblockchain.node.controller;

import com.google.common.base.Strings;
import com.xingkaichun.helloworldblockchain.core.BlockChainCore;
import com.xingkaichun.helloworldblockchain.model.key.StringAddress;
import com.xingkaichun.helloworldblockchain.node.dto.adminconsole.request.*;
import com.xingkaichun.helloworldblockchain.node.dto.adminconsole.response.*;
import com.xingkaichun.helloworldblockchain.node.service.*;
import com.xingkaichun.helloworldblockchain.node.dto.adminconsole.AdminConsoleApiRoute;
import com.xingkaichun.helloworldblockchain.node.dto.adminconsole.ConfigurationDto;
import com.xingkaichun.helloworldblockchain.node.dto.adminconsole.ConfigurationEnum;
import com.xingkaichun.helloworldblockchain.node.dto.common.ServiceResult;
import com.xingkaichun.helloworldblockchain.node.dto.nodeserver.Node;
import com.xingkaichun.helloworldblockchain.node.dto.user.request.UpdateAdminUserRequest;
import com.xingkaichun.helloworldblockchain.node.dto.user.response.UpdateAdminUserResponse;
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
 * 管理员控制台的控制器：用于控制本地区块链节点，如激活矿工、停用矿工、同步其它节点数据等。
 */
@Controller
@RequestMapping
public class AdminConsoleController {

    private static final Logger logger = LoggerFactory.getLogger(AdminConsoleController.class);

    @Autowired
    private AdminConsoleService adminConsoleService;

    @Autowired
    private BlockChainCoreService blockChainCoreService;

    @Autowired
    private BlockChainBranchService blockChainBranchService;

    @Autowired
    private UserService userService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private NodeService nodeService;

    @Autowired
    private BlockChainCore blockChainCore;

    /**
     * 矿工是否激活
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.IS_MINER_ACTIVE,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<IsMinerActiveResponse> isMineActive(@RequestBody IsMinerActiveRequest request){
        try {
            boolean isMineActive = adminConsoleService.isMinerActive();

            IsMinerActiveResponse response = new IsMinerActiveResponse();
            response.setMinerInActiveState(isMineActive);
            return ServiceResult.createSuccessServiceResult("查询矿工是否处于激活状态成功",response);
        } catch (Exception e){
            String message = "查询矿工是否处于激活状态失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }
    /**
     * 激活矿工
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.ACTIVE_MINER,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<ActiveMinerResponse> activeMiner(@RequestBody ActiveMinerRequest request){
        try {
            adminConsoleService.activeMiner();
            ActiveMinerResponse response = new ActiveMinerResponse();
            response.setActiveMinerSuccess(true);
            return ServiceResult.createSuccessServiceResult("激活矿工成功",response);
        } catch (Exception e){
            String message = "激活矿工失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }
    /**
     * 停用矿工
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.DEACTIVE_MINER,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<DeactiveMinerResponse> deactiveMiner(@RequestBody DeactiveMinerRequest request){
        try {
            adminConsoleService.deactiveMiner();
            DeactiveMinerResponse response = new DeactiveMinerResponse();
            response.setDeactiveMinerSuccess(true);
            return ServiceResult.createSuccessServiceResult("停用矿工成功",response);
        } catch (Exception e){
            String message = "停用矿工失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }



    /**
     * 同步器是否激活
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.IS_SYNCHRONIZER_ACTIVE,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<IsSynchronizerActiveResponse> isSynchronizerActive(@RequestBody IsSynchronizerActiveRequest request){
        try {
            boolean isSynchronizerActive = adminConsoleService.isSynchronizerActive();

            IsSynchronizerActiveResponse response = new IsSynchronizerActiveResponse();
            response.setSynchronizerInActiveState(isSynchronizerActive);
            return ServiceResult.createSuccessServiceResult("查询同步器是否激活成功",response);
        } catch (Exception e){
            String message = "查询同步器是否激活失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }
    /**
     * 激活同步器
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.ACTIVE_SYNCHRONIZER,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<ActiveSynchronizerResponse> activeSynchronizer(@RequestBody ActiveSynchronizerRequest request){
        try {
            adminConsoleService.activeSynchronizer();
            ActiveSynchronizerResponse response = new ActiveSynchronizerResponse();
            response.setActiveSynchronizerSuccess(true);
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
    @RequestMapping(value = AdminConsoleApiRoute.DEACTIVE_SYNCHRONIZER,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<DeactiveSynchronizerResponse> deactiveSynchronizer(@RequestBody DeactiveSynchronizerRequest request){
        try {
            adminConsoleService.deactiveSynchronizer();
            DeactiveSynchronizerResponse response = new DeactiveSynchronizerResponse();
            response.setDeactiveSynchronizerSuccess(true);
            return ServiceResult.createSuccessServiceResult("停用同步器成功",response);
        } catch (Exception e){
            String message = "停用同步器失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }

    /**
     * 更换当前区块链分支
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.UPDATE_BLOCKCHAINBRANCH,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<UpdateBlockchainBranchResponse> updateBranchchainBranch(@RequestBody UpdateBlockchainBranchRequest request){
        try {
            blockChainBranchService.updateBranchchainBranch(request.getBlockList());
            UpdateBlockchainBranchResponse response = new UpdateBlockchainBranchResponse();
            return ServiceResult.createSuccessServiceResult("成功更换当前区块链分支",response);
        } catch (Exception e){
            String message = "更换当前区块链分支失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }

    /**
     * 更新系统管理员用户
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.UPDATE_ADMIN_USER,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<UpdateAdminUserResponse> updateAdminUserRequest(@RequestBody UpdateAdminUserRequest request){
        try {
            userService.updateUser(request.getUserDto());
            UpdateAdminUserResponse response = new UpdateAdminUserResponse();
            return ServiceResult.createSuccessServiceResult("更新系统用户成功",response);
        } catch (Exception e){
            String message = "更新系统用户失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }

    /**
     * 查询矿工的地址
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.QUERY_MINER_ADDRESS,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<QueryMinerAddressResponse> queryMinerAddress(@RequestBody QueryMinerAddressRequest request){
        try {
            String minerAddress = configurationService.getMinerAddress();
            QueryMinerAddressResponse response = new QueryMinerAddressResponse();
            response.setMinerAddress(minerAddress);
            return ServiceResult.createSuccessServiceResult("查询矿工的地址成功",response);
        } catch (Exception e){
            String message = "查询矿工的地址失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }

    /**
     * 设置矿工地址
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.SET_MINER_ADDRESS,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<SetMinerAddressResponse> setMinerAddress(@RequestBody SetMinerAddressRequest request){
        try {
            if(adminConsoleService.isMinerActive()){
                return ServiceResult.createFailServiceResult("矿工正在挖矿，请先暂停挖矿，再设置矿工钱包地址");
            }
            blockChainCore.getMiner().setMinerStringAddress(new StringAddress(request.getMinerAddress()));
            configurationService.setMinerAddress(request.getMinerAddress());
            SetMinerAddressResponse response = new SetMinerAddressResponse();
            return ServiceResult.createSuccessServiceResult("成功重置矿工的钱包地址！",response);
        } catch (Exception e){
            String message = "重置矿工的地址失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }


    /**
     * 新增节点
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.ADD_NODE,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<AddNodeResponse> addNode(@RequestBody AddNodeRequest request){
        try {
            Node node = request.getNode();
            if(Strings.isNullOrEmpty(node.getIp())){
                return ServiceResult.createFailServiceResult("节点IP不能为空");
            }
            if(Strings.isNullOrEmpty(node.getIp())){
                return ServiceResult.createFailServiceResult("节点端口不能为空");
            }
            if(nodeService.queryNode(node) != null){
                return ServiceResult.createFailServiceResult("节点已经存在，不需要重复添加");
            }
            nodeService.addNode(node);
            AddNodeResponse response = new AddNodeResponse();
            response.setAddNodeSuccess(true);
            return ServiceResult.createSuccessServiceResult("新增节点成功",response);
        } catch (Exception e){
            String message = "新增节点失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }

    /**
     * 更新节点信息
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.UPDATE_NODE,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<UpdateNodeResponse> updateNode(@RequestBody UpdateNodeRequest request){
        try {
            if(request.getNode() == null){
                return ServiceResult.createFailServiceResult("请填写节点信息");
            }
            if(nodeService.queryNode(request.getNode()) == null){
                return ServiceResult.createFailServiceResult("节点不存在，无法更新");
            }
            nodeService.updateNode(request.getNode());
            UpdateNodeResponse response = new UpdateNodeResponse();
            return ServiceResult.createSuccessServiceResult("更新节点信息成功",response);
        } catch (Exception e){
            String message = "更新节点信息失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }

    /**
     * 删除节点
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.DELETE_NODE,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<DeleteNodeResponse> deleteNode(@RequestBody DeleteNodeRequest request){
        try {
            nodeService.deleteNode(request.getNode());
            DeleteNodeResponse response = new DeleteNodeResponse();
            return ServiceResult.createSuccessServiceResult("删除节点成功",response);
        } catch (Exception e){
            String message = "删除节点失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }

    /**
     * 查询节点
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.QUERY_NODE_LIST,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<QueryNodeListResponse> queryNodeList(@RequestBody QueryNodeListRequest request){
        try {
            List<Node> nodeList = nodeService.queryNodeList(request);
            QueryNodeListResponse response = new QueryNodeListResponse();
            response.setNodeList(nodeList);
            return ServiceResult.createSuccessServiceResult("查询节点成功",response);
        } catch (Exception e){
            String message = "查询节点失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }

    /**
     * 查询是否允许自动搜索区块链节点
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.IS_AUTO_SEARCH_NODE,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<IsAutoSearchNodeResponse> isAutoSearchNewNode(@RequestBody IsAutoSearchNodeRequest request){
        try {
            ConfigurationDto configurationDto = configurationService.getConfigurationByConfigurationKey(ConfigurationEnum.AUTO_SEARCH_NODE.name());
            IsAutoSearchNodeResponse response = new IsAutoSearchNodeResponse();
            response.setAutoSearchNewNode(Boolean.valueOf(configurationDto.getConfValue()));
            return ServiceResult.createSuccessServiceResult("查询是否允许自动搜索区块链节点成功",response);
        } catch (Exception e){
            String message = "查询是否允许自动搜索区块链节点失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }

    /**
     * 设置否允许自动搜索区块链节点开关
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.SET_AUTO_SEARCH_NODE,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<SetAutoSearchNodeResponse> setAutoSearchNode(@RequestBody SetAutoSearchNodeRequest request){
        try {
            ConfigurationDto configurationDto = new ConfigurationDto();
            configurationDto.setConfKey(ConfigurationEnum.AUTO_SEARCH_NODE.name());
            configurationDto.setConfValue(String.valueOf(request.isAutoSearchNode()));
            configurationService.setConfiguration(configurationDto);
            SetAutoSearchNodeResponse response = new SetAutoSearchNodeResponse();
            return ServiceResult.createSuccessServiceResult("设置否允许自动搜索区块链节点开关成功",response);
        } catch (Exception e){
            String message = "设置否允许自动搜索区块链节点开关失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }

    /**
     * 删除区块
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.REMOVE_BLOCK,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<RemoveBlockResponse> removeBlock(@RequestBody RemoveBlockRequest request){
        try {
            if(request.getBlockHeight() == null){
                return ServiceResult.createFailServiceResult("删除区块失败，区块高度不能空。");
            }
            blockChainCoreService.removeBlocksUtilBlockHeightLessThan(request.getBlockHeight());
            RemoveBlockResponse response = new RemoveBlockResponse();
            return ServiceResult.createSuccessServiceResult("删除区块成功",response);
        } catch (Exception e){
            String message = "删除区块失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }

    /**
     * 根据配置Key获取配置
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.GET_CONFIGURATION_BY_CONFIGURATION_KEY_KEY,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<GetConfigurationByConfigurationKeyResponse> getConfigurationByConfigurationKey(@RequestBody GetConfigurationByConfigurationKeyRequest request){
        try {
            String confKey = request.getConfKey();
            if(Strings.isNullOrEmpty(confKey)){
                return ServiceResult.createFailServiceResult("查询的配置Key不能为空。");
            }
            ConfigurationDto configurationDto = configurationService.getConfigurationByConfigurationKey(confKey);
            GetConfigurationByConfigurationKeyResponse response = new GetConfigurationByConfigurationKeyResponse();
            response.setConfigurationDto(configurationDto);
            return ServiceResult.createSuccessServiceResult(String.format("查询配置[%s]成功",request.getConfKey()),response);
        } catch (Exception e){
            String message = String.format("查询配置[%s]失败",request.getConfKey());
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }

    /**
     * 配置
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.SET_CONFIGURATION,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<SetConfigurationResponse> setConfiguration(@RequestBody SetConfigurationRequest request){
        try {
            configurationService.setConfiguration(request.getConfigurationDto());
            SetConfigurationResponse response = new SetConfigurationResponse();
            return ServiceResult.createSuccessServiceResult("配置成功",response);
        } catch (Exception e){
            String message = "配置失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }
}