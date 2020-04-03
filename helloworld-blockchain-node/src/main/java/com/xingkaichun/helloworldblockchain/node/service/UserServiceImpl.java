package com.xingkaichun.helloworldblockchain.node.service;

import com.xingkaichun.helloworldblockchain.node.dao.UserDao;
import com.xingkaichun.helloworldblockchain.node.dto.user.UserDto;
import com.xingkaichun.helloworldblockchain.node.model.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public long queryUserSize() {
        return userDao.queryUserSize();
    }

    @Override
    public UserDto queryUserByUserName(String userName) {
        UserEntity userEntity = userDao.queryUserByUserName(userName);
        if(userEntity == null){
            return null;
        }
        UserDto userDto = classCast(userEntity);
        return userDto;
    }

    @Override
    public void addUser(UserDto userDto) {
        UserEntity userEntity = classCast(userDto);
        userDao.addUser(userEntity);
    }

    @Override
    public void updateUser(UserDto userDto) {
        UserEntity userEntity = classCast(userDto);
        userDao.updateUser(userEntity);
    }

    private UserEntity classCast(UserDto userDto) {
        UserEntity userEntity = new UserEntity();
        userEntity.setPassword(userDto.getPassword());
        userEntity.setUserName(userDto.getUserName());
        userEntity.setUserId(userDto.getUserId());
        return userEntity;
    }

    private UserDto classCast(UserEntity userEntity) {
        UserDto userDto = new UserDto();
        userDto.setUserId(userEntity.getUserId());
        userDto.setPassword(userEntity.getPassword());
        userDto.setUserName(userEntity.getUserName());
        return userDto;
    }
}
