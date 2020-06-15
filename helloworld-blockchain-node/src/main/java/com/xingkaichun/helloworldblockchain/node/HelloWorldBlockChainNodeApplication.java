package com.xingkaichun.helloworldblockchain.node;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.Gson;
import com.xingkaichun.helloworldblockchain.core.BlockChainCore;
import com.xingkaichun.helloworldblockchain.netcore.NetBlockchainCore;
import com.xingkaichun.helloworldblockchain.netcore.NetBlcokchainCoreFactory;
import com.xingkaichun.helloworldblockchain.netcore.service.BlockChainBranchService;
import com.xingkaichun.helloworldblockchain.netcore.service.BlockChainCoreService;
import com.xingkaichun.helloworldblockchain.netcore.service.ConfigurationService;
import com.xingkaichun.helloworldblockchain.netcore.service.NodeService;
import com.xingkaichun.helloworldblockchain.node.timer.InitUserHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.Security;

/**
 * 启动入口
 *
 * @author 邢开春 xingkaichun@qq.com
 */
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



	@Bean
	public NetBlockchainCore netBlcokchainCore() throws Exception {
		NetBlockchainCore netBlockchainCore = NetBlcokchainCoreFactory.createNetBlcokchainCore("F:\\tmp\\helloworldblockchain","1111122222333334444455555");
		netBlockchainCore.start();
		return netBlockchainCore;
	}

	@Bean
	public BlockChainCore buildBlockChainCore(NetBlockchainCore netBlockchainCore){
		return netBlockchainCore.getBlockChainCore();
	}

	@Bean
	public ConfigurationService buildConfigurationService(NetBlockchainCore netBlockchainCore){
		return NetBlcokchainCoreFactory.configurationService;
	}
	@Bean
	public BlockChainCoreService buildBlockChainCoreService(NetBlockchainCore netBlockchainCore){
		return NetBlcokchainCoreFactory.blockChainCoreService;
	}
	@Bean
	public NodeService buildNodeService(NetBlockchainCore netBlockchainCore){
		return NetBlcokchainCoreFactory.nodeService;
	}
	@Bean
	public BlockChainBranchService buildBlockChainBranchService(NetBlockchainCore netBlockchainCore){
		return NetBlcokchainCoreFactory.blockChainBranchService;
	}



	@Bean
	public InitUserHandler initUserHandler(){
		InitUserHandler initUserHandler = new InitUserHandler();
		return initUserHandler;
	}

/*	@Bean
	public InitMinerTool initMinerHandler(){
		InitMinerTool initMinerHandler = new InitMinerTool();
		return initMinerHandler;
	}*/

	@Bean
	public Gson buildGson(){
		return new Gson();
	}

	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
	}
}
