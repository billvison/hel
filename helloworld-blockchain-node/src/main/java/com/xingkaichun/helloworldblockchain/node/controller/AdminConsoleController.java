package com.xingkaichun.helloworldblockchain.node.controller;

import com.xingkaichun.helloworldblockchain.node.dto.adminconsole.AdminConsoleApiRoute;
import com.xingkaichun.helloworldblockchain.node.dto.adminconsole.response.*;
import com.xingkaichun.helloworldblockchain.node.dto.common.ServiceResult;
import com.xingkaichun.helloworldblockchain.node.service.BlockChainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 区块链相关
 */
@Controller
@RequestMapping
public class AdminConsoleController {

    private static final Logger logger = LoggerFactory.getLogger(AdminConsoleController.class);

    @Autowired
    private BlockChainService blockChainService;

    /**
     * 矿工是否激活
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.IS_MINER_ACTIVE,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<IsMinerActiveResponse> isMineActive(){
        try {
            boolean isMineActive = blockChainService.isMinerActive();

            IsMinerActiveResponse response = new IsMinerActiveResponse();
            response.setMineActive(isMineActive);
            return ServiceResult.createSuccessServiceResult("查询矿工是否激活挖矿成功",response);
        } catch (Exception e){
            String message = "查询矿工是否激活挖矿失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }
    /**
     * 激活矿工
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.ACTIVE_MINER,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<ActiveMinerResponse> activeMiner(){
        try {
            blockChainService.activeMiner();
            ActiveMinerResponse response = new ActiveMinerResponse();
            response.setStartMineSuccess(true);
            return ServiceResult.createSuccessServiceResult("开启挖矿成功",response);
        } catch (Exception e){
            String message = "开启挖矿失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }
    /**
     * 停用矿工
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.DEACTIVE_MINER,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<DeactiveMinerResponse> deactiveMiner(){
        try {
            blockChainService.deactiveMiner();
            DeactiveMinerResponse response = new DeactiveMinerResponse();
            response.setStopMineSuccess(true);
            return ServiceResult.createSuccessServiceResult("关闭挖矿成功",response);
        } catch (Exception e){
            String message = "关闭挖矿失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }



    /**
     * 同步器是否激活
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.IS_SYNCHRONIZER_ACTIVE,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<IsSynchronizerActiveResponse> isSynchronizerActive(){
        try {
            boolean isSynchronizerActive = blockChainService.isSynchronizerActive();

            IsSynchronizerActiveResponse response = new IsSynchronizerActiveResponse();
            response.setSynchronizerActive(isSynchronizerActive);
            return ServiceResult.createSuccessServiceResult("查询矿工是否激活挖矿成功",response);
        } catch (Exception e){
            String message = "查询矿工是否激活挖矿失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }
    /**
     * 激活同步器
     */
    @ResponseBody
    @RequestMapping(value = AdminConsoleApiRoute.ACTIVE_SYNCHRONIZER,method={RequestMethod.GET,RequestMethod.POST})
    public ServiceResult<ActiveSynchronizerResponse> activeSynchronizer(){
        try {
            blockChainService.activeSynchronizer();
            ActiveSynchronizerResponse response = new ActiveSynchronizerResponse();
            response.setResumeSynchronizerSuccess(true);
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
    public ServiceResult<DeactiveSynchronizerResponse> deactiveSynchronizer(){
        try {
            blockChainService.deactiveSynchronizer();
            DeactiveSynchronizerResponse response = new DeactiveSynchronizerResponse();
            response.setStopMineSuccess(true);
            return ServiceResult.createSuccessServiceResult("停用同步器成功",response);
        } catch (Exception e){
            String message = "停用同步器失败";
            logger.error(message,e);
            return ServiceResult.createFailServiceResult(message);
        }
    }
}