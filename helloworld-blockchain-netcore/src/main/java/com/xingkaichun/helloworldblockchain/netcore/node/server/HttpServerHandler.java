package com.xingkaichun.helloworldblockchain.netcore.node.server;

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

/**
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

	private HttpServerHandlerResolver httpServerHandlerResolver;

	public HttpServerHandler(HttpServerHandlerResolver httpServerHandlerResolver) {
		super();
		this.httpServerHandlerResolver = httpServerHandlerResolver;
	}


	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) {
		this.readRequest(msg);

		String body = msg.content().toString(CharsetUtil.UTF_8);

		String sendMsg;
		String uri = msg.uri();

		//因为任何节点都可以访问这里的接口，请不要在这里写任何能泄露用户私钥的代码。
		if("/".equals(uri)){
			sendMsg = "HelloworldBlockchain";
		}else if(NodeServerApiRoute.PING.equals(uri)){
			PingRequest request = new Gson().fromJson(body,PingRequest.class);
			sendMsg = toString(httpServerHandlerResolver.ping(ctx,request));
		}else if(NodeServerApiRoute.ADD_OR_UPDATE_NODE.equals(uri)){
			AddOrUpdateNodeRequest request = new Gson().fromJson(body,AddOrUpdateNodeRequest.class);
			sendMsg = toString(httpServerHandlerResolver.addOrUpdateNode(ctx,request));
		}else if(NodeServerApiRoute.QUERY_BLOCK_HASH_BY_BLOCK_HEIGHT.equals(uri)){
			QueryBlockHashByBlockHeightRequest request = new Gson().fromJson(body, QueryBlockHashByBlockHeightRequest.class);
			sendMsg = toString(httpServerHandlerResolver.queryBlockHashByBlockHeight(request));
		}else if(NodeServerApiRoute.QUERY_BLOCKDTO_BY_BLOCK_HEIGHT.equals(uri)){
			QueryBlockDtoByBlockHeightRequest request = new Gson().fromJson(body, QueryBlockDtoByBlockHeightRequest.class);
			sendMsg = toString(httpServerHandlerResolver.queryBlockDtoByBlockHeight(request));
		}else if(NodeServerApiRoute.SUBMIT_TRANSACTION_TO_NODE.equals(uri)){
			SubmitTransactionToNodeRequest request = new Gson().fromJson(body, SubmitTransactionToNodeRequest.class);
			sendMsg = toString(httpServerHandlerResolver.submitTransactionToNodeRequest(request));
		}else {
			sendMsg = "404 NOT FOUND";
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