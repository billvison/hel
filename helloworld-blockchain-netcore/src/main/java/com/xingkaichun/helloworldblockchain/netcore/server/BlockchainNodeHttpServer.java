package com.xingkaichun.helloworldblockchain.netcore.server;

import com.xingkaichun.helloworldblockchain.setting.GlobalSetting;
import com.xingkaichun.helloworldblockchain.util.LogUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 区块链节点服务器：其它节点与之通信，同步节点数据、区块数据、交易数据等。
 *
 * @author 邢开春 409060350@qq.com
 */
public class BlockchainNodeHttpServer {

	private HttpServerHandlerResolver httpServerHandlerResolver;

	public BlockchainNodeHttpServer(HttpServerHandlerResolver httpServerHandlerResolver) {
		super();
		this.httpServerHandlerResolver = httpServerHandlerResolver;
	}


	public void start() {
		new Thread(
				()->{
					EventLoopGroup bossGroup = new NioEventLoopGroup(1);
					EventLoopGroup workerGroup = new NioEventLoopGroup();

					try {
						ServerBootstrap b = new ServerBootstrap();

						b.group(bossGroup, workerGroup)
								.channel(NioServerSocketChannel.class)
								.childHandler(new HttpServerChannelInitializer(httpServerHandlerResolver))
								.option(ChannelOption.SO_BACKLOG, 128)
								.childOption(ChannelOption.SO_KEEPALIVE, true);

						ChannelFuture f = b.bind(GlobalSetting.DEFAULT_PORT).sync();
						LogUtil.debug("HttpServer已启动，端口：" + GlobalSetting.DEFAULT_PORT);
						f.channel().closeFuture().sync();
					} catch (InterruptedException e) {
						LogUtil.error("BlockchainNodeHttpServer运行出现异常",e);
					} finally {
						// 优雅的关闭
						workerGroup.shutdownGracefully();
						bossGroup.shutdownGracefully();
					}
				}
		).start();
	}
}