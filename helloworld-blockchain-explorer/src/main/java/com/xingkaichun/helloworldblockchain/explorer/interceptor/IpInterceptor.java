package com.xingkaichun.helloworldblockchain.explorer.interceptor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * IP拦截器：只允许指定的IP访问服务器
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
@Component
public class IpInterceptor implements HandlerInterceptor {

	//*代表允许所有ip访问。
	private static final String ALL_IP = "*";
	//默认允许访问的ip列表。
	public static final List<String> DEFAULT_PERMIT_VISIT_IP = Arrays.asList("127.0.0.1","0:0:0:0:0:0:0:1");

	//允许的ip列表，多个ip之间以逗号(,)分隔。
	@Value("#{'${permitVisitIp}'.split(',')}")
	private List<String> permitVisitIp;

	@Override
	public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object){
		if(permitVisitIp != null){
			if(permitVisitIp.contains(ALL_IP)){
				return true;
			}
			String remoteHost = httpServletRequest.getRemoteHost();
			if(DEFAULT_PERMIT_VISIT_IP.contains(remoteHost)){
				return true;
			}
			if(permitVisitIp.contains(remoteHost)){
				return true;
			}
		}
		throw new RuntimeException("该IP无访问权限!");
	}
}