package com.xingkaichun.helloworldblockchain.node.service;

import com.xingkaichun.helloworldblockchain.node.dto.user.UserDto;

import java.util.List;

public interface UserService {


    List<UserDto> queryAllUser();

    UserDto queryUserByUserName(String userName);

    void newUser(UserDto userDto);
}
