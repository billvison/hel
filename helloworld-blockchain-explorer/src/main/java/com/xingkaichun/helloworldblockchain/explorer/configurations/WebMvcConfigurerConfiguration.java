package com.xingkaichun.helloworldblockchain.explorer.configurations;

import com.xingkaichun.helloworldblockchain.explorer.interceptor.IpInterceptor;
import com.xingkaichun.helloworldblockchain.explorer.vo.framwork.ServiceResult;
import com.xingkaichun.helloworldblockchain.util.JsonUtil;
import com.xingkaichun.helloworldblockchain.util.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * WebMvcConfigurer
 *
 * @author 邢开春 409060350@qq.com
 */
@Configuration
public class WebMvcConfigurerConfiguration implements WebMvcConfigurer {

	@Autowired
	private IpInterceptor ipInterceptor;


	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(ipInterceptor).addPathPatterns("/**");
	}

	@Override
	public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
		exceptionResolvers.add((httpServletRequest, httpServletResponse, handler, ex) -> {
			responseResult(httpServletResponse,ex);
			LogUtil.error("统一异常拦截。",ex);
			return new ModelAndView();
		});
	}

	private void responseResult(HttpServletResponse httpServletResponse, Exception exception) {
		try {
			httpServletResponse.setHeader("Content-type", "application/json;");
			httpServletResponse.setStatus(500);
			httpServletResponse.setCharacterEncoding("UTF-8");
			ServiceResult serviceResult = ServiceResult.createFailServiceResult(exception.getMessage());
			String jsonServiceResult = JsonUtil.toJson(serviceResult);
			httpServletResponse.getWriter().write(jsonServiceResult);
		} catch (Exception e) {
			LogUtil.error("将统一异常写入到HttpServletResponse出现错误。",e);
		}
	}

	/**
	 * 支持跨域
	 */
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
				.allowedOrigins("*")
				.allowCredentials(true)
				.allowedMethods("*")
				.maxAge(3600);
	}
}