package com.xingkaichun.helloworldblockchain.netcore.interceptors;

import com.xingkaichun.helloworldblockchain.netcore.dto.user.UserDto;
import com.xingkaichun.helloworldblockchain.netcore.util.SessionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Security过滤器
 *
 * @author 邢开春 xingkaichun@qq.com
 */
@Component
public class SecurityInterceptor implements HandlerInterceptor {

	private Logger logger = LoggerFactory.getLogger(SecurityInterceptor.class);

	@Override
	public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object) throws Exception {
		UserDto userDto = SessionUtil.getAdminUser(httpServletRequest);
		if(userDto == null){
			logger.debug("用户未登录，无操作权限，请登录!");
			return false;
		}
		return true;
	}
}