package com.xingkaichun.helloworldblockchain.netcore;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.xingkaichun.helloworldblockchain.core.BlockChainCore;
import com.xingkaichun.helloworldblockchain.core.BlockChainCoreFactory;
import com.xingkaichun.helloworldblockchain.netcore.dto.adminconsole.ConfigurationDto;
import com.xingkaichun.helloworldblockchain.netcore.dto.adminconsole.ConfigurationEnum;
import com.xingkaichun.helloworldblockchain.netcore.service.ConfigurationService;
import com.xingkaichun.helloworldblockchain.netcore.timer.BlockchainBranchHandler;
import com.xingkaichun.helloworldblockchain.netcore.timer.InitMinerHandler;
import com.xingkaichun.helloworldblockchain.netcore.timer.TimerService;
import org.springframework.boot.SpringApplication;

import java.io.File;
import java.security.Security;

/**
 * 启动入口
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class HelloWorldBlockChainNetcoreApplication {

	private String blockchainDataPath;


	public static void main(String[] args) throws Exception {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		SpringApplication.run(HelloWorldBlockChainNetcoreApplication.class, args);
	}

	private ConfigurationService configurationService;

	public BlockChainCore buildBlockChainCore(/*先初始化*/InitMinerHandler initMinerHandler) throws Exception {
		if(Strings.isNullOrEmpty(blockchainDataPath)){
			String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
			blockchainDataPath = new File(path,"blockchaindata").getAbsolutePath();
		}
		System.out.println(String.format("区块链数据存放的路径是%s",blockchainDataPath));

		//矿工钱包地址
		ConfigurationDto minerAddressConfigurationDto =  configurationService.getConfigurationByConfigurationKey(ConfigurationEnum.MINER_ADDRESS.name());
		BlockChainCore blockChainCore = new BlockChainCoreFactory().createBlockChainCore(blockchainDataPath,minerAddressConfigurationDto.getConfValue());

		//是否激活矿工
		ConfigurationDto isMinerActiveConfigurationDto = configurationService.getConfigurationByConfigurationKey(ConfigurationEnum.IS_MINER_ACTIVE.name());
		if(Boolean.valueOf(isMinerActiveConfigurationDto.getConfValue())){
			blockChainCore.getMiner().active();
		}else {
			blockChainCore.getMiner().deactive();
		}

		//是否激活同步者
		ConfigurationDto isSynchronizerActiveConfigurationDto = configurationService.getConfigurationByConfigurationKey(ConfigurationEnum.IS_SYNCHRONIZER_ACTIVE.name());
		if(Boolean.valueOf(isSynchronizerActiveConfigurationDto.getConfValue())){
			blockChainCore.getSynchronizer().active();
		}else {
			blockChainCore.getSynchronizer().deactive();
		}

		new Thread(()->{
			try {
				blockChainCore.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
		return blockChainCore;
	}

	public TimerService buildTimeService(){
		TimerService timerService = new TimerService();
		return timerService;
	}

	public BlockchainBranchHandler blockchainBranchHandler(){
		BlockchainBranchHandler blockchainBranchHandler = new BlockchainBranchHandler();
		return blockchainBranchHandler;
	}

	public InitMinerHandler initMinerHandler(){
		InitMinerHandler initMinerHandler = new InitMinerHandler();
		return initMinerHandler;
	}

	public Gson buildGson(){
		return new Gson();
	}

}
