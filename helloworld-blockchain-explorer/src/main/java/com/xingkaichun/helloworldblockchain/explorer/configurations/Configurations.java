package com.xingkaichun.helloworldblockchain.explorer.configurations;

import com.xingkaichun.helloworldblockchain.core.tools.ResourcePathTool;
import com.xingkaichun.helloworldblockchain.netcore.BlockchainNetCore;
import com.xingkaichun.helloworldblockchain.netcore.BlockchainNetCoreFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 *
 * @author 邢开春 409060350@qq.com
 */
@Configuration
public class Configurations {

	@Bean
	public BlockchainNetCore buildBlockchainNetCore() {
		BlockchainNetCore blockchainNetCore = BlockchainNetCoreFactory.createBlockchainNetCore(ResourcePathTool.getDataRootPath());
		blockchainNetCore.start();
		return blockchainNetCore;
	}
}