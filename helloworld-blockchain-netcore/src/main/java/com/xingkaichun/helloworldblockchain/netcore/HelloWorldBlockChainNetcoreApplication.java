package com.xingkaichun.helloworldblockchain.netcore;

import java.security.Security;

/**
 * 启动入口
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class HelloWorldBlockChainNetcoreApplication {

	public static void main(String[] args) throws Exception {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		NetBlockchainCore netBlockchainCore = NetBlockchainCoreFactory.createNetBlcokchainCore("F:\\tmp\\helloworldblockchain","1111122222333334444455555");
		netBlockchainCore.start();
	}

}
