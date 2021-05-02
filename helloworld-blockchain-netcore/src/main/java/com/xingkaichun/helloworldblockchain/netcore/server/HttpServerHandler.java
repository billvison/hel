package com.xingkaichun.helloworldblockchain.netcore.server;

import com.xingkaichun.helloworldblockchain.netcore.transport.dto.API;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.BlockDTO;
import com.xingkaichun.helloworldblockchain.netcore.transport.dto.TransactionDTO;
import com.xingkaichun.helloworldblockchain.util.JsonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

	private HttpServerHandlerResolver httpServerHandlerResolver;

	public HttpServerHandler(HttpServerHandlerResolver httpServerHandlerResolver) {
		super();
		this.httpServerHandlerResolver = httpServerHandlerResolver;
	}


	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) {

		String responseMessage;
		String requestUri = msg.uri();
		String requestApi = parseRequestApi(requestUri);
		Map<String,String> requestParameter = parseRequestParameter(requestUri);
		String requestBody = msg.content().toString(CharsetUtil.UTF_8);

		//因为任何节点都可以访问这里的接口，请不要在这里写任何能泄露用户私钥的代码。
		if("/".equals(requestApi)){
			responseMessage = "HelloworldBlockchain";
		}else if(API.PING.equals(requestApi)){
			responseMessage = httpServerHandlerResolver.ping(ctx);
		}else if(API.GET_NODES.equals(requestApi)){
			responseMessage = httpServerHandlerResolver.getNodes();
		}else if(API.GET_BLOCK.equals(requestApi)){
			responseMessage = httpServerHandlerResolver.getBlock(Long.parseLong(requestParameter.get("height")));
		}else if(API.GET_TRANSACTION.equals(requestApi)){
			responseMessage = httpServerHandlerResolver.getTransaction(Long.parseLong(requestParameter.get("height")));
		}else if(API.POST_TRANSACTION.equals(requestApi)){
			TransactionDTO transactionDTO = JsonUtil.fromJson(requestBody, TransactionDTO.class);
			responseMessage = httpServerHandlerResolver.postTransaction(transactionDTO);
		}else if(API.POST_BLOCK.equals(requestApi)){
			BlockDTO blockDTO = JsonUtil.fromJson(requestBody, BlockDTO.class);
			responseMessage = httpServerHandlerResolver.postBlock(blockDTO);
		}else if(API.POST_BLOCKCHAIN_HEIGHT.equals(requestApi)){
			responseMessage = httpServerHandlerResolver.postBlockchainHeight(ctx,Long.parseLong(requestParameter.get("height")));
		}else if(API.GET_BLOCKCHAIN_HEIGHT.equals(requestApi)){
			responseMessage = httpServerHandlerResolver.getBlockchainHeight();
		}else {
			responseMessage = "404 NOT FOUND";
		}
		writeResponse(ctx, responseMessage);
	}

	private Map<String, String> parseRequestParameter(String uri) {
		Map<String,String> requestParameterMap = new HashMap<>();
		if(uri.contains("?")){
			int index = uri.indexOf("?");
			String[] parameterKeyValues = uri.substring(index+1).split("&");
			if(parameterKeyValues != null){
				for (String parameterKeyValue:parameterKeyValues) {
					String parameterKey = parameterKeyValue.split("=")[0];
					String parameterValue = parameterKeyValue.split("=")[1];
					requestParameterMap.put(parameterKey,parameterValue);
				}
			}
		}
		return requestParameterMap;
	}

	private String parseRequestApi(String uri) {
		if(uri.contains("?")){
			return uri.split("\\?")[0];
		}else {
			return uri;
		}
	}

	private void writeResponse(ChannelHandlerContext ctx, String msg) {
		ByteBuf bf = Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8);

		FullHttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, bf);
		res.headers().set(HttpHeaderNames.CONTENT_LENGTH, msg.length());
		res.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
		ctx.writeAndFlush(res).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
	}
}