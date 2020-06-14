/**
 * Welcome to https://waylau.com
 */
package com.xingkaichun.helloworldblockchain.netcore.netserver;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.nio.charset.Charset;


public class HttpServerChannelInitializer extends ChannelInitializer<SocketChannel> {

	NodeServerController nodeServerController;

	public HttpServerChannelInitializer(NodeServerController nodeServerController) {
		super();
		this.nodeServerController = nodeServerController;
	}
	
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ch.pipeline().addLast("codec", new HttpServerCodec());
		ch.pipeline().addLast("aggregator", new HttpObjectAggregator(1048576));
		ch.pipeline().addLast("serverHandler", new HttpServerHandler(nodeServerController));
	}

}
