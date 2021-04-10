package com.xingkaichun.helloworldblockchain.netcore.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class HttpServerChannelInitializer extends ChannelInitializer<SocketChannel> {

	private HttpServerHandlerResolver httpServerHandlerResolver;

	public HttpServerChannelInitializer(HttpServerHandlerResolver httpServerHandlerResolver) {
		super();
		this.httpServerHandlerResolver = httpServerHandlerResolver;
	}
	
	@Override
	protected void initChannel(SocketChannel ch) {
		ch.pipeline().addLast("codec", new HttpServerCodec());
		ch.pipeline().addLast("aggregator", new HttpObjectAggregator(10*1024*1024));
		ch.pipeline().addLast("serverHandler", new HttpServerHandler(httpServerHandlerResolver));
	}

}
