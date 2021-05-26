package com.xingkaichun.helloworldblockchain.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 区块链应用层：包含钱包应用、区块链浏览器应用、节点控制台应用。
 *
 * @author 邢开春 409060350@qq.com
 */
@SpringBootApplication
public class HelloWorldBlockchainApplication {

	public static void main(String[] args) {
		SpringApplication.run(HelloWorldBlockchainApplication.class, args);
	}
}
