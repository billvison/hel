package com.xingkaichun.helloworldblockchain.node.controller;

import com.xingkaichun.helloworldblockchain.node.dto.adminconsole.AdminConsoleApiRoute;
import com.xingkaichun.helloworldblockchain.node.dto.adminconsole.request.*;
import com.xingkaichun.helloworldblockchain.node.dto.adminconsole.response.*;
import com.xingkaichun.helloworldblockchain.node.dto.common.ServiceResult;
import com.xingkaichun.helloworldblockchain.node.dto.user.request.NewUserRequest;
import com.xingkaichun.helloworldblockchain.node.dto.user.response.NewUserResponse;
import com.xingkaichun.helloworldblockchain.node.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * admin控制台：用于控制本地区块链节点，如激活矿工、停用矿工、同步其它节点数据等。
 */
@Controller
@RequestMapping
public class AdminConsoleController {

    private static final Logger logger = LoggerFactory.getLogger(AdminConsoleController.class);

    @Autowired
    private AdminConsoleService adminConsoleService;

    @Autowired
    private BlockChainBranchService blockChainBranchService;

    @Autowired
    private UserService userService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private NodeService nodeService;

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
     * 新增节点
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.ADD_NODE,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<AddNodeResponse> addNode(@RequestBody AddNodeRequest request){
        try {
            adminConsoleService.addNode(request);
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
     * 重新创建系统管理员用户
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.NEW_USER,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<NewUserResponse> newUser(@RequestBody NewUserRequest request){
        try {
            userService.newUser(request.getUserDto());
            NewUserResponse response = new NewUserResponse();
            return ServiceResult.createSuccessServiceResult("新增用户成功",response);
        } catch (Exception e){
            String message = "新增用户失败";
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
            configurationService.writeMinerAddress(request.getMinerAddress());
            SetMinerAddressResponse response = new SetMinerAddressResponse();
            return ServiceResult.createSuccessServiceResult("重置矿工的地址成功，配置将在下次重启应用后生效！",response);
        } catch (Exception e){
            String message = "重置矿工的地址失败";
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
            nodeService.addOrUpdateNode(request.getNode());
            UpdateNodeResponse response = new UpdateNodeResponse();
            return ServiceResult.createSuccessServiceResult("更新节点信息成功",response);
        } catch (Exception e){
            String message = "更新节点信息失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }
}