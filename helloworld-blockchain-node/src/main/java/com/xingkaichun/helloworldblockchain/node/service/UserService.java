package com.xingkaichun.helloworldblockchain.node.service;

import com.xingkaichun.helloworldblockchain.node.dto.user.UserDto;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户service
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public interface UserService {

    void updateUser(UserDto userDto);
    UserDto login(HttpServletRequest httpServletRequest, UserDto userDto);
    UserDto getLoginUser(HttpServletRequest httpServletRequest);
}
