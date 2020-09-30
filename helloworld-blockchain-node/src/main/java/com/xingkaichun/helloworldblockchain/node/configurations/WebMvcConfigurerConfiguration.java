package com.xingkaichun.helloworldblockchain.node.configurations;

import com.google.gson.Gson;
import com.xingkaichun.helloworldblockchain.netcore.dto.common.ServiceResult;
import com.xingkaichun.helloworldblockchain.node.dto.user.UserApiRoute;
import com.xingkaichun.helloworldblockchain.node.interceptor.SecurityInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * WebMvcConfigurer
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
@Configuration
public class WebMvcConfigurerConfiguration implements WebMvcConfigurer {

	private final Logger logger = LoggerFactory.getLogger(WebMvcConfigurerConfiguration.class);

	@Autowired
	private Gson gson;

	@Autowired
	private SecurityInterceptor securityInterceptor;


	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(securityInterceptor).addPathPatterns("/Api/AdminConsole/**").addPathPatterns(UserApiRoute.UPDATE_USER);
	}

	@Override
	public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
		exceptionResolvers.add(new HandlerExceptionResolver() {
			public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler, Exception ex) {
				responseResult(httpServletResponse,ex);
				logger.error("统一异常拦截",ex);
				return new ModelAndView();
			}
		});
	}

	private void responseResult(HttpServletResponse httpServletResponse, Exception exception) {
		try {
			httpServletResponse.setCharacterEncoding("UTF-8");
			httpServletResponse.setHeader("Content-type", "application/json;charset=UTF-8");
			httpServletResponse.setStatus(200);
			ServiceResult serviceResult = ServiceResult.createFailServiceResult(exception.getMessage());
			String jsonStr = gson.toJson(serviceResult);
			httpServletResponse.getWriter().write(jsonStr);
		} catch (Exception e) {
			logger.error("返回统一异常处理出现错误",e);
		}
	}
}