package com.xingkaichun.helloworldblockchain.application.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * IP拦截器：只允许指定的IP访问
 *
 * @author 邢开春 409060350@qq.com
 */
@Component
public class IpInterceptor implements HandlerInterceptor {

	//*代表允许所有ip访问。
	private static final String ALL_IP = "*";

	//默认允许访问的ip列表。
	private static final List<String> DEFAULT_ALLOW_IPS = Arrays.asList("localhost","127.0.0.1","0:0:0:0:0:0:0:1");

	//允许的ip列表，多个ip之间以分隔符逗号(,)进行分割分隔。
	private static final String ALLOW_IPS_KEY = "allowIps";
	private static final String ALLOW_IPS_VALUE_SEPARATOR = ",";

	@Override
	public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object){
		String remoteHost = httpServletRequest.getRemoteHost();
		if(DEFAULT_ALLOW_IPS.contains(remoteHost)){
			return true;
		}
		List<String> allowIPs = getAllowIps();
		if(allowIPs != null && !allowIPs.isEmpty()){
			if(allowIPs.contains(ALL_IP)){
				return true;
			}
			if(allowIPs.contains(remoteHost)){
				return true;
			}
		}
		throw new RuntimeException("该IP无访问权限!");
	}

	//获取允许的ip列表
	private List<String> getAllowIps(){
		String allowIps = System.getProperty(ALLOW_IPS_KEY);
		if(allowIps != null && !allowIps.isEmpty()){
			List<String> allowIpList = Arrays.asList(allowIps.split(ALLOW_IPS_VALUE_SEPARATOR));
			return allowIpList;
		}
		return null;
	}
}