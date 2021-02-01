package com.xingkaichun.helloworldblockchain.explorer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 区块链浏览器
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
@Configuration
@SpringBootApplication
@ServletComponentScan
public class HelloWorldBlockchainExplorerApplication {

	public static void main(String[] args) {
		SpringApplication.run(HelloWorldBlockchainExplorerApplication.class, args);
	}
}
