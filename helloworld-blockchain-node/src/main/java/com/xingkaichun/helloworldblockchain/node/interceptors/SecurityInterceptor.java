package com.xingkaichun.helloworldblockchain.node.interceptors;

import com.xingkaichun.helloworldblockchain.node.dto.user.UserDto;
import com.xingkaichun.helloworldblockchain.node.util.SessionUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Security过滤器
 * @author 邢开春
 */
@Component
public class SecurityInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object) throws Exception {
/*		UserDto userDto = SessionUtils.getUser(httpServletRequest);
		if(userDto == null){
			throw new RuntimeException("用户未登录，无操作权限，请登录!");
		}*/
		return true;
	}
}