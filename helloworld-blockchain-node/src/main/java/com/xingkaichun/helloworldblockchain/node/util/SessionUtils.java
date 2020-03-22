package com.xingkaichun.helloworldblockchain.node.util;

import com.xingkaichun.helloworldblockchain.node.dto.user.UserDto;

import javax.servlet.http.HttpServletRequest;

public class SessionUtils {

    private final static String USER_KEY = "USER";

    public static UserDto getUser(HttpServletRequest httpServletRequest){
        UserDto userDto = (UserDto) httpServletRequest.getSession().getAttribute(USER_KEY);
        return userDto;
    }

    public static void saveUser(HttpServletRequest httpServletRequest, UserDto userDto) {
        httpServletRequest.getSession().setAttribute(USER_KEY,userDto);
    }
}
