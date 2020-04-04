package com.xingkaichun.helloworldblockchain.node.util;

import com.xingkaichun.helloworldblockchain.node.dto.user.UserDto;

import javax.servlet.http.HttpServletRequest;

public class SessionUtils {

    private final static String ADMIN_USER_KEY = "ADMIN_USER";

    public static UserDto getAdminUser(HttpServletRequest httpServletRequest){
        UserDto userDto = (UserDto) httpServletRequest.getSession().getAttribute(ADMIN_USER_KEY);
        return userDto;
    }

    public static void saveAdminUser(HttpServletRequest httpServletRequest, UserDto userDto) {
        httpServletRequest.getSession().setAttribute(ADMIN_USER_KEY,userDto);
    }

    public static void clearAdminUser(HttpServletRequest httpServletRequest) {
        httpServletRequest.getSession().removeAttribute(ADMIN_USER_KEY);
        httpServletRequest.getSession().invalidate();
    }
}
