package com.xingkaichun.helloworldblockchain.node.service;

import com.xingkaichun.helloworldblockchain.node.dao.UserDao;
import com.xingkaichun.helloworldblockchain.node.dto.user.UserDto;
import com.xingkaichun.helloworldblockchain.node.model.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;


    @Override
    public List<UserDto> queryAllUser() {
        List<UserEntity> userEntityList = userDao.queryAllUser();
        List<UserDto> userDtoList = classCast(userEntityList);
        return userDtoList;
    }

    @Override
    public UserDto queryUserByUserId(int userId) {
        UserEntity userEntity = userDao.queryUserByUserId(userId);
        if(userEntity == null){
            return null;
        }
        UserDto userDto = classCast(userEntity);
        return userDto;
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

    @Transactional
    @Override
    public void newAdminUser(UserDto userDto) {
        userDao.deleteAllUser();
        UserEntity userEntity = classCast(userDto);
        userDao.addUser(userEntity);
    }

    @Override
    public void updateAdminUser(UserDto userDto) {
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

    private List<UserDto> classCast(List<UserEntity> userEntityList) {
        if(userEntityList == null){
            return null;
        }
        List<UserDto> userDtoList = new ArrayList<>();
        for(UserEntity userEntity:userEntityList){
            userDtoList.add(classCast(userEntity));
        }
        return userDtoList;
    }

    private UserDto classCast(UserEntity userEntity) {
        UserDto userDto = new UserDto();
        userDto.setUserId(userEntity.getUserId());
        userDto.setPassword(userEntity.getPassword());
        userDto.setUserName(userEntity.getUserName());
        return userDto;
    }
}
