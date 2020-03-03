package com.xingkaichun.helloworldblockchain.node;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.Gson;
import com.xingkaichun.helloworldblockchain.core.BlockChainCore;
import com.xingkaichun.helloworldblockchain.core.BlockChainCoreFactory;
import com.xingkaichun.helloworldblockchain.node.plugins.AddressUtxoPlugin;
import com.xingkaichun.helloworldblockchain.node.timer.TimerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SpringBootApplication
@ServletComponentScan
public class HelloWorldBlockChainNodeApplication {

	@Value("${blockchainDataPath}")
	protected String blockchainDataPath;
	@Value("${miner.minerAddress}")
	protected String minerAddress;


	public static void main(String[] args) throws Exception {
		SpringApplication.run(HelloWorldBlockChainNodeApplication.class, args);
	}

	@Bean
	public BlockChainCore buildBlockChainCore() throws Exception {
		BlockChainCore blockChainCore = new BlockChainCoreFactory().createBlockChainCore(blockchainDataPath,minerAddress);
		blockChainCore.getMiner().stop();
		blockChainCore.getSynchronizer().stop();
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
	public AddressUtxoPlugin buildPublicKeyUtxoPlugin(BlockChainCore blockChainCore) throws Exception {
		AddressUtxoPlugin addressUtxoPlugin = new AddressUtxoPlugin(blockchainDataPath,blockChainCore);
		return addressUtxoPlugin;
	}

	@Bean
	public TimerService buildTimeService(){
		TimerService timerService = new TimerService();
		return timerService;
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
