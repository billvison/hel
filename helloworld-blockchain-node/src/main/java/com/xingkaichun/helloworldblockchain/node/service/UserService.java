package com.xingkaichun.helloworldblockchain.node.service;

import com.xingkaichun.helloworldblockchain.node.dto.user.UserDto;

import java.util.List;

public interface UserService {


    List<UserDto> queryAllUser();

    UserDto queryUserByUserId(int userId);
    UserDto queryUserByUserName(String userName);

    void newAdminUser(UserDto userDto);
    void updateAdminUser(UserDto userDto);
}
