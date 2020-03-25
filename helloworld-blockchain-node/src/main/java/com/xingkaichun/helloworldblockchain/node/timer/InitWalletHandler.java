package com.xingkaichun.helloworldblockchain.node.timer;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.xingkaichun.helloworldblockchain.core.utils.DtoUtils;
import com.xingkaichun.helloworldblockchain.core.utils.atomic.WalletUtil;
import com.xingkaichun.helloworldblockchain.dto.WalletDTO;
import com.xingkaichun.helloworldblockchain.model.key.Wallet;
import com.xingkaichun.helloworldblockchain.node.service.ConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 初始化钱包
 */
public class InitWalletHandler {

    private static final Logger logger = LoggerFactory.getLogger(InitWalletHandler.class);

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private Gson gson;


    @PostConstruct
    private void startThread() throws IOException {

        if(Strings.isNullOrEmpty(configurationService.getMinerAddress())){
            //创建钱包
            Wallet wallet = WalletUtil.generateWallet();
            WalletDTO walletDTO =  DtoUtils.classCast(wallet);

            //将钱包的地址当做矿工的地址写入数据库
            configurationService.writeMinerAddress(walletDTO.getAddress());

            //将钱包写入到文本 TODO 删除 由管理页面查看
            FileWriter fileWriter = null;
            try {
                fileWriter = new FileWriter(new File("InitWallet.txt"));
                fileWriter.write(gson.toJson(walletDTO));
                fileWriter.close();
            } catch (IOException e) {
                logger.error("创建用户出错",e);
                if(fileWriter != null){
                    fileWriter.close();
                }
            }
        }
    }
}
