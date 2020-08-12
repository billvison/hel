package com.xingkaichun.helloworldblockchain.netcore.netserver;

import com.google.gson.Gson;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.NodeServerApiRoute;
import com.xingkaichun.helloworldblockchain.netcore.dto.netserver.request.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;


public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

	HttpServerHandlerResolver httpServerHandlerResolver;

	protected HttpServerHandler(HttpServerHandlerResolver httpServerHandlerResolver) {
		super();
		this.httpServerHandlerResolver = httpServerHandlerResolver;
	}


	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) {
		this.readRequest(msg);

		String body = msg.content().toString(CharsetUtil.UTF_8);

		String sendMsg;
		String uri = msg.uri();

		switch (uri) {
		case "/":
			sendMsg = "HelloworldBlockchain";
			break;
		case NodeServerApiRoute.PING:
			PingRequest request1 = new Gson().fromJson(body,PingRequest.class);
			sendMsg = toString(httpServerHandlerResolver.ping(ctx,request1));
			break;
		case NodeServerApiRoute.ADD_OR_UPDATE_NODE:
			AddOrUpdateNodeRequest request2 = new Gson().fromJson(body,AddOrUpdateNodeRequest.class);
			sendMsg = toString(httpServerHandlerResolver.addOrUpdateNode(ctx,request2));
			break;
		case NodeServerApiRoute.QUERY_BLOCK_HASH_BY_BLOCK_HEIGHT:
			QueryBlockHashByBlockHeightRequest request3 = new Gson().fromJson(body, QueryBlockHashByBlockHeightRequest.class);
			sendMsg = toString(httpServerHandlerResolver.queryBlockHashByBlockHeight(request3));
			break;
		case NodeServerApiRoute.QUERY_BLOCKDTO_BY_BLOCK_HEIGHT:
			QueryBlockDtoByBlockHeightRequest request4 = new Gson().fromJson(body, QueryBlockDtoByBlockHeightRequest.class);
			sendMsg = toString(httpServerHandlerResolver.queryBlockDtoByBlockHeight(request4));
			break;
		case NodeServerApiRoute.RECEIVE_TRANSACTION:
			ReceiveTransactionRequest request5 = new Gson().fromJson(body, ReceiveTransactionRequest.class);
			sendMsg = toString(httpServerHandlerResolver.receiveTransaction(request5));
			break;
		default:
			sendMsg = "404 PAGE NOT FOUND";
			break;
		}
		writeResponse(ctx, sendMsg);
	}

	private void readRequest(FullHttpRequest msg) {
		System.out.println("======请求行======");
		System.out.println(msg.method() + " " + msg.uri() + " " + msg.protocolVersion());

		System.out.println("======请求头======");
		for (String name : msg.headers().names()) {
			System.out.println(name + ": " + msg.headers().get(name));
		}

		System.out.println("======消息体======");
		System.out.println(msg.content().toString(CharsetUtil.UTF_8));
	}

	private void writeResponse(ChannelHandlerContext ctx, String msg) {
		ByteBuf bf = Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8);

		FullHttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, bf);
		res.headers().set(HttpHeaderNames.CONTENT_LENGTH, msg.length());
		res.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
		ctx.writeAndFlush(res).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
	}



	private String toString(Object ping) {
		return new Gson().toJson(ping);
	}

}