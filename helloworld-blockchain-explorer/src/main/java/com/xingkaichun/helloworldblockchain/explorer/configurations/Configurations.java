package com.xingkaichun.helloworldblockchain.explorer.configurations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.Gson;
import com.xingkaichun.helloworldblockchain.core.tools.ResourcePathTool;
import com.xingkaichun.helloworldblockchain.netcore.NetBlockchainCore;
import com.xingkaichun.helloworldblockchain.netcore.NetBlockchainCoreFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
@Configuration
public class Configurations {

	@Bean
	public Gson gson(){
		return new Gson();
	}

	//TODO 删除
	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
	}

	@Bean
	public NetBlockchainCore buildNetBlockchainCore() {
		NetBlockchainCore netBlockchainCore = NetBlockchainCoreFactory.createNetBlockchainCore(ResourcePathTool.getDataRootPath());
		netBlockchainCore.start();
		return netBlockchainCore;
	}
}