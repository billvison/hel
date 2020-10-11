package com.xingkaichun.helloworldblockchain.node.util;

import com.xingkaichun.helloworldblockchain.node.dto.user.UserDto;

import javax.servlet.http.HttpServletRequest;

/**
 * session工具类
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class SessionUtil {

    private static final String LOGIN_USER = "LOGIN_USER";

    public static UserDto getLoginUser(HttpServletRequest httpServletRequest){
        UserDto userDto = (UserDto) httpServletRequest.getSession().getAttribute(LOGIN_USER);
        return userDto;
    }

    public static void saveLoginUser(HttpServletRequest httpServletRequest, UserDto userDto) {
        httpServletRequest.getSession().setAttribute(LOGIN_USER,userDto);
    }

    public static void clearLoginUser(HttpServletRequest httpServletRequest) {
        httpServletRequest.getSession().removeAttribute(LOGIN_USER);
        httpServletRequest.getSession().invalidate();
    }
}
