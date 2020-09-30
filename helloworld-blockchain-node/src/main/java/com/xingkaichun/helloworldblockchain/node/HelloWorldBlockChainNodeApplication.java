package com.xingkaichun.helloworldblockchain.node;

import com.xingkaichun.helloworldblockchain.core.BlockChainCore;
import com.xingkaichun.helloworldblockchain.core.tools.ResourcePathTool;
import com.xingkaichun.helloworldblockchain.netcore.NetBlockchainCore;
import com.xingkaichun.helloworldblockchain.netcore.NetBlockchainCoreFactory;
import com.xingkaichun.helloworldblockchain.netcore.service.BlockChainCoreService;
import com.xingkaichun.helloworldblockchain.netcore.service.ConfigurationService;
import com.xingkaichun.helloworldblockchain.netcore.service.NodeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 启动入口
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
@Configuration
@SpringBootApplication
@ServletComponentScan
public class HelloWorldBlockChainNodeApplication {

	@Value("${blockchainDataPath:}")
	private String blockchainDataPath;


	public static void main(String[] args) {
		SpringApplication.run(HelloWorldBlockChainNodeApplication.class, args);
	}

	@Bean
	public NetBlockchainCoreFactory netBlockchainCoreFactory() {
		NetBlockchainCoreFactory netBlockchainCoreFactory = new NetBlockchainCoreFactory(ResourcePathTool.getDataRootPath(blockchainDataPath),8444);
		return netBlockchainCoreFactory;
	}

	@Bean
	public NetBlockchainCore buildNetBlockchainCore(NetBlockchainCoreFactory netBlockchainCoreFactory) {
		NetBlockchainCore netBlockchainCore = netBlockchainCoreFactory.getNetBlockchainCore();
		netBlockchainCore.start();
		return netBlockchainCore;
	}

	@Bean
	public BlockChainCore buildBlockChainCore(NetBlockchainCoreFactory netBlockchainCoreFactory){
		return netBlockchainCoreFactory.getBlockChainCore();
	}

	@Bean
	public ConfigurationService buildConfigurationService(NetBlockchainCoreFactory netBlockchainCoreFactory){
		return netBlockchainCoreFactory.getConfigurationService();
	}

	@Bean
	public BlockChainCoreService buildBlockChainCoreService(NetBlockchainCoreFactory netBlockchainCoreFactory){
		return netBlockchainCoreFactory.getBlockChainCoreService();
	}

	@Bean
	public NodeService buildNodeService(NetBlockchainCoreFactory netBlockchainCoreFactory){
		return netBlockchainCoreFactory.getNodeService();
	}

}
