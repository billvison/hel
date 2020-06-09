/**
 * Welcome to https://waylau.com
 */
package com.xingkaichun.helloworldblockchain.netcore.netserver;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;


public class HttpServerChannelInitializer extends ChannelInitializer<SocketChannel> {

	public HttpServerChannelInitializer() {
		
	}
	
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ch.pipeline().addLast("codec", new HttpServerCodec());
		ch.pipeline().addLast("aggregator", new HttpObjectAggregator(1048576));
		ch.pipeline().addLast("serverHandler", new HttpServerHandler());
	}

}
