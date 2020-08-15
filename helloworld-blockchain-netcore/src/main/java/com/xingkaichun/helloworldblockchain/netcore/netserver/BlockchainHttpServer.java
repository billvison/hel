package com.xingkaichun.helloworldblockchain.netcore.netserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 区块链节点服务器：其它节点与之通信，同步节点数据、区块数据、交易数据等。
 * @author 邢开春 xingkaichun@qq.com
 */
public class BlockchainHttpServer {

	private final static Logger logger = LoggerFactory.getLogger(BlockchainHttpServer.class);

	private int serverPort;
	private HttpServerHandlerResolver httpServerHandlerResolver;

	public BlockchainHttpServer(int serverPort, HttpServerHandlerResolver httpServerHandlerResolver) {
		super();
		this.serverPort = serverPort;
		this.httpServerHandlerResolver = httpServerHandlerResolver;
	}


	public void start() {
		new Thread(
				()->{
					int port= serverPort;

					// 多线程事件循环器
					EventLoopGroup bossGroup = new NioEventLoopGroup(1); // boss
					EventLoopGroup workerGroup = new NioEventLoopGroup(); // worker

					try {
						// 启动NIO服务的引导程序类
						ServerBootstrap b = new ServerBootstrap();

						b.group(bossGroup, workerGroup) // 设置EventLoopGroup
								.channel(NioServerSocketChannel.class) // 指明新的Channel的类型
								.childHandler(new HttpServerChannelInitializer(httpServerHandlerResolver)) // 指定ChannelHandler
								.option(ChannelOption.SO_BACKLOG, 128) // 设置的ServerChannel的一些选项
								.childOption(ChannelOption.SO_KEEPALIVE, true); // 设置的ServerChannel的子Channel的选项

						// 绑定端口，开始接收进来的连接
						ChannelFuture f = b.bind(port).sync();

						System.out.println("HttpServer已启动，端口：" + port);

						// 等待服务器 socket 关闭 。
						// 在这个例子中，这不会发生，但你可以优雅地关闭你的服务器。
						f.channel().closeFuture().sync();
					} catch (InterruptedException e) {
						logger.error("BlockchainHttpServer运行出现异常",e);
					} finally {
						// 优雅的关闭
						workerGroup.shutdownGracefully();
						bossGroup.shutdownGracefully();
					}
				}
		).start();
	}
}