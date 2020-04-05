package com.xingkaichun.helloworldblockchain.node;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.xingkaichun.helloworldblockchain.core.BlockChainCore;
import com.xingkaichun.helloworldblockchain.core.BlockChainCoreFactory;
import com.xingkaichun.helloworldblockchain.node.dto.adminconsole.ConfigurationDto;
import com.xingkaichun.helloworldblockchain.node.dto.adminconsole.ConfigurationEnum;
import com.xingkaichun.helloworldblockchain.node.service.ConfigurationService;
import com.xingkaichun.helloworldblockchain.node.timer.BlockchainBranchHandler;
import com.xingkaichun.helloworldblockchain.node.timer.InitMinerHandler;
import com.xingkaichun.helloworldblockchain.node.timer.InitUserHandler;
import com.xingkaichun.helloworldblockchain.node.timer.TimerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.security.Security;

@Configuration
@SpringBootApplication
@ServletComponentScan
public class HelloWorldBlockChainNodeApplication {

	@Value("${blockchainDataPath:}")
	private String blockchainDataPath;


	public static void main(String[] args) throws Exception {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		SpringApplication.run(HelloWorldBlockChainNodeApplication.class, args);
	}

	@Autowired
	private ConfigurationService configurationService;

	@Bean
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

	@Bean
	public TimerService buildTimeService(){
		TimerService timerService = new TimerService();
		return timerService;
	}

	@Bean
	public BlockchainBranchHandler blockchainBranchHandler(){
		BlockchainBranchHandler blockchainBranchHandler = new BlockchainBranchHandler();
		return blockchainBranchHandler;
	}

	@Bean
	public InitUserHandler initUserHandler(){
		InitUserHandler initUserHandler = new InitUserHandler();
		return initUserHandler;
	}
	@Bean
	public InitMinerHandler initMinerHandler(){
		InitMinerHandler initMinerHandler = new InitMinerHandler();
		return initMinerHandler;
	}

	@Bean
	public Gson buildGson(){
		return new Gson();
	}

	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
	}
}
