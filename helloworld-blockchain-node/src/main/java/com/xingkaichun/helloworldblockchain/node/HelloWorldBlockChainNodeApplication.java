package com.xingkaichun.helloworldblockchain.node;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.xingkaichun.helloworldblockchain.core.BlockChainCore;
import com.xingkaichun.helloworldblockchain.core.BlockChainCoreFactory;
import com.xingkaichun.helloworldblockchain.node.service.ConfigurationService;
import com.xingkaichun.helloworldblockchain.node.timer.BlockchainBranchHandler;
import com.xingkaichun.helloworldblockchain.node.timer.InitUserHandler;
import com.xingkaichun.helloworldblockchain.node.timer.InitWalletHandler;
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

	@Autowired
	private ConfigurationService configurationService;


	public static void main(String[] args) throws Exception {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		SpringApplication.run(HelloWorldBlockChainNodeApplication.class, args);
	}

	@Bean
	public InitWalletHandler walletHandler(){
		InitWalletHandler initWalletHandler = new InitWalletHandler();
		return initWalletHandler;
	}

	@Bean
	public BlockChainCore buildBlockChainCore(InitWalletHandler initWalletHandler) throws Exception {
		if(Strings.isNullOrEmpty(blockchainDataPath)){
			String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
			blockchainDataPath = new File(path,"blockchaindata").getAbsolutePath();
		}
		System.out.println(String.format("区块链数据存放的路径是%s",blockchainDataPath));
		BlockChainCore blockChainCore = new BlockChainCoreFactory().createBlockChainCore(blockchainDataPath,configurationService.getMinerAddress());
		blockChainCore.getMiner().deactive();
		blockChainCore.getSynchronizer().deactive();
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
	public Gson buildGson(){
		return new Gson();
	}

	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
	}
}
