package com.xingkaichun.helloworldblockchain.netcore;

/**
 * 启动入口
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class HelloWorldBlockChainNetcoreApplication {

	public static void main(String[] args) throws Exception {
		NetBlockchainCore netBlockchainCore = NetBlockchainCoreFactory.createNetBlcokchainCore("F:\\tmp\\helloworldblockchain",8444,null);
		netBlockchainCore.start();
	}

}
