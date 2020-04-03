package com.xingkaichun.helloworldblockchain.node.service;

import com.xingkaichun.helloworldblockchain.node.dto.user.UserDto;

import java.util.List;

public interface UserService {


    long queryUserSize();
    UserDto queryUserByUserName(String userName);
    void addUser(UserDto userDto);
    void updateUser(UserDto userDto);
}
