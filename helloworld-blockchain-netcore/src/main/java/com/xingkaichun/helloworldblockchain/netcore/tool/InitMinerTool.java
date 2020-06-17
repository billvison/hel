package com.xingkaichun.helloworldblockchain.netcore.tool;

import com.google.common.base.Strings;
import com.xingkaichun.helloworldblockchain.core.tools.WalletTool;
import com.xingkaichun.helloworldblockchain.crypto.model.account.StringAccount;
import com.xingkaichun.helloworldblockchain.netcore.dto.configuration.ConfigurationDto;
import com.xingkaichun.helloworldblockchain.netcore.dto.configuration.ConfigurationEnum;
import com.xingkaichun.helloworldblockchain.netcore.dto.wallet.WalletDTO;
import com.xingkaichun.helloworldblockchain.netcore.service.ConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 初始化矿工钱包地址。系统每次启动，会校验是否配置了矿工钱包地址。
 * 若是没有配置，则系统自动生成一个矿工钱包地址，并且将公钥、私钥、地址写入外部文件供系统使用者查看矿工钱包地址。
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class InitMinerTool {

    private static final Logger logger = LoggerFactory.getLogger(InitMinerTool.class);


    public static String buildDefaultMinerAddress(ConfigurationService configurationService, String defaultDataRootPath) throws IOException {
        ConfigurationDto minerAddressConfigurationDto =  configurationService.getConfigurationByConfigurationKey(ConfigurationEnum.MINER_ADDRESS.name());
        if(Strings.isNullOrEmpty(minerAddressConfigurationDto.getConfValue())){
            //创建钱包
            StringAccount stringAccount = WalletTool.generateWallet();
            WalletDTO walletDTO =  WalletDtoTool.classCast(stringAccount);

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
                logger.info(minerWalletInfo);
                fileWriter = new FileWriter(new File(defaultDataRootPath,"InitMiner.txt"));
                fileWriter.write(minerWalletInfo);
                fileWriter.close();
            } catch (IOException e) {
                logger.error("创建用户出错",e);
                if(fileWriter != null){
                    fileWriter.close();
                }
            }
            return stringAccount.getStringAddress().getValue();
        }
        return null;
    }
}
