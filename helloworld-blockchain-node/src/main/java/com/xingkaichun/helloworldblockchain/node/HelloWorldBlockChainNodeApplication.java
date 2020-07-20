package com.xingkaichun.helloworldblockchain.node;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xingkaichun.helloworldblockchain.core.BlockChainCore;
import com.xingkaichun.helloworldblockchain.core.tools.ResourcePathTool;
import com.xingkaichun.helloworldblockchain.netcore.NetBlockchainCore;
import com.xingkaichun.helloworldblockchain.netcore.NetBlockchainCoreFactory;
import com.xingkaichun.helloworldblockchain.netcore.dto.blockchainbranch.BlockchainBranchDto;
import com.xingkaichun.helloworldblockchain.netcore.service.BlockChainBranchService;
import com.xingkaichun.helloworldblockchain.netcore.service.BlockChainCoreService;
import com.xingkaichun.helloworldblockchain.netcore.service.ConfigurationService;
import com.xingkaichun.helloworldblockchain.netcore.service.NodeService;
import com.xingkaichun.helloworldblockchain.node.init.InitUserHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

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
		ConfigurableApplicationContext context = SpringApplication.run(HelloWorldBlockChainNodeApplication.class, args);

		NetBlockchainCore netBlockchainCore = context.getBean(NetBlockchainCore.class);
		netBlockchainCore.start();
	}


	@Bean
	public NetBlockchainCore buildNetBlockchainCore() throws Exception {

		String INIT_BLOCKCHAIN_BRANCH_FILE_NAME = "InitBlockchainBranch.txt";
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(INIT_BLOCKCHAIN_BRANCH_FILE_NAME);
		String context = CharStreams.toString(new InputStreamReader(inputStream, "UTF-8"));
		Type jsonType = new TypeToken<BlockchainBranchDto>() {}.getType();
		BlockchainBranchDto blockchainBranchDto = new Gson().fromJson(context,jsonType);

		NetBlockchainCore netBlockchainCore = NetBlockchainCoreFactory.createNetBlcokchainCore(ResourcePathTool.getDataRootPath(blockchainDataPath),8444,blockchainBranchDto);
		return netBlockchainCore;
	}

	@Bean
	public BlockChainCore buildBlockChainCore(NetBlockchainCore netBlockchainCore){
		return netBlockchainCore.getBlockChainCore();
	}

	@Bean
	public ConfigurationService buildConfigurationService(NetBlockchainCore netBlockchainCore){
		return NetBlockchainCoreFactory.configurationService;
	}
	@Bean
	public BlockChainCoreService buildBlockChainCoreService(NetBlockchainCore netBlockchainCore){
		return NetBlockchainCoreFactory.blockChainCoreService;
	}
	@Bean
	public NodeService buildNodeService(NetBlockchainCore netBlockchainCore){
		return NetBlockchainCoreFactory.nodeService;
	}
	@Bean
	public BlockChainBranchService buildBlockChainBranchService(NetBlockchainCore netBlockchainCore){
		return NetBlockchainCoreFactory.blockChainBranchService;
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
