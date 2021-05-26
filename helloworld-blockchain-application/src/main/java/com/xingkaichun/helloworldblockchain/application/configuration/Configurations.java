package com.xingkaichun.helloworldblockchain.application.configuration;

import com.xingkaichun.helloworldblockchain.core.BlockchainCore;
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
	public BlockchainNetCore blockchainNetCore() {
		BlockchainNetCore blockchainNetCore = BlockchainNetCoreFactory.createBlockchainNetCore(ResourcePathTool.getDataRootPath());
		blockchainNetCore.start();
		return blockchainNetCore;
	}

    @Bean
    public BlockchainCore blockchainCore(BlockchainNetCore blockchainNetCore) {
        return blockchainNetCore.getBlockchainCore();
    }
}