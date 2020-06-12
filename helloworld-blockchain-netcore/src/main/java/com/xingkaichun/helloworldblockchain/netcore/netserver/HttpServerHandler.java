package com.xingkaichun.helloworldblockchain.netcore.netserver;

import com.google.gson.Gson;
import com.xingkaichun.helloworldblockchain.netcore.dto.nodeserver.NodeServerApiRoute;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;


public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

	NodeServerController nodeServerController;

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
		this.readRequest(msg);

		String sendMsg;
		String uri = msg.uri();

		switch (uri) {
		case "/":
			sendMsg = "HelloworldBlockchain";
			break;
		case NodeServerApiRoute.PING:
			sendMsg = toString(nodeServerController.ping(ctx,null));
			break;
		case NodeServerApiRoute.ADD_OR_UPDATE_NODE:
			sendMsg = toString(nodeServerController.addOrUpdateNode(ctx,null));
			break;
		case NodeServerApiRoute.QUERY_BLOCK_HASH_BY_BLOCK_HEIGHT:
			sendMsg = toString(nodeServerController.queryBlockHashByBlockHeight(null));
			break;
		case NodeServerApiRoute.QUERY_BLOCKDTO_BY_BLOCK_HEIGHT:
			sendMsg = toString(nodeServerController.queryBlockDtoByBlockHeight(null));
			break;
		case NodeServerApiRoute.RECEIVE_TRANSACTION:
			sendMsg = toString(nodeServerController.receiveTransaction(null));
			break;
		default:
			sendMsg = "404 PAGE NOT FOUND";
			break;
		}

		this.writeResponse(ctx, sendMsg);
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
		ctx.writeAndFlush(res).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
	}



	private String toString(Object ping) {
		return new Gson().toJson(ping);
	}

}