package com.xingkaichun.helloworldblockchain.node.timer;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.xingkaichun.helloworldblockchain.core.model.key.Wallet;
import com.xingkaichun.helloworldblockchain.core.utils.WalletUtil;
import com.xingkaichun.helloworldblockchain.node.dto.adminconsole.ConfigurationDto;
import com.xingkaichun.helloworldblockchain.node.dto.adminconsole.ConfigurationEnum;
import com.xingkaichun.helloworldblockchain.node.dto.wallet.WalletDTO;
import com.xingkaichun.helloworldblockchain.node.service.ConfigurationService;
import com.xingkaichun.helloworldblockchain.node.util.WalletDtoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 初始化矿工钱包地址。系统每次启动，会校验是否配置了矿工钱包地址。
 * 若是没有配置，则系统自动生成一个矿工钱包地址，并且将公钥、私钥、地址写入外部文件供系统使用者查看矿工钱包地址。
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class InitMinerHandler {

    private static final Logger logger = LoggerFactory.getLogger(InitMinerHandler.class);

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private Gson gson;


    @PostConstruct
    private void startThread() throws IOException {
        ConfigurationDto minerAddressConfigurationDto =  configurationService.getConfigurationByConfigurationKey(ConfigurationEnum.MINER_ADDRESS.name());
        if(Strings.isNullOrEmpty(minerAddressConfigurationDto.getConfValue())){
            //创建钱包
            Wallet wallet = WalletUtil.generateWallet();
            WalletDTO walletDTO =  WalletDtoUtil.classCast(wallet);

            //将钱包的地址当做矿工的地址写入数据库
            minerAddressConfigurationDto.setConfKey(ConfigurationEnum.MINER_ADDRESS.name());
            minerAddressConfigurationDto.setConfValue(walletDTO.getAddress());
            configurationService.setConfiguration(minerAddressConfigurationDto);

            //将钱包写入到外部文件
            FileWriter fileWriter = null;
            try {
                String minerWalletInfo = String.format("由于您是第一次启动系统，系统自动为您分配了矿工钱包地址。\n" +
                                "钱包私钥是[%s]\n" +
                                "钱包地址是[%s]\n" +
                                "为保安全，请另在其它地方妥善保存您的矿工钱包私钥、公钥、地址，并删除此文件。"
                        ,walletDTO.getPrivateKey(),walletDTO.getAddress());

                fileWriter = new FileWriter(new File("InitMiner.txt"));
                fileWriter.write(minerWalletInfo);
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
