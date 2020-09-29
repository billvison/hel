package com.xingkaichun.helloworldblockchain.node.service;

import com.xingkaichun.helloworldblockchain.node.dao.UserDao;
import com.xingkaichun.helloworldblockchain.node.dto.user.UserDto;
import com.xingkaichun.helloworldblockchain.node.model.UserEntity;
import com.xingkaichun.helloworldblockchain.node.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public void updateUser(UserDto userDto) {
        UserEntity userEntity = convertUserDto2UserEntity(userDto);
        userDao.updateUser(userEntity);
    }

    @Override
    public UserDto login(HttpServletRequest httpServletRequest, UserDto userDtoReq) {
        UserEntity userEntity = userDao.queryUserByUserName(userDtoReq.getUserName());
        if(userEntity == null){
            return null;
        }
        if(!userEntity.getPassword().equals(userDtoReq.getPassword())){
            return null;
        }
        UserDto userDto = convertUserEntity2UserDto(userEntity);
        userDto.setPassword("******");

        SessionUtil.saveLoginUser(httpServletRequest,userDto);
        return userDto;
    }

    @Override
    public UserDto getLoginUser(HttpServletRequest httpServletRequest) {
        UserDto userDto = SessionUtil.getLoginUser(httpServletRequest);
        if(userDto == null){
            return null;
        }
        userDto.setPassword("*******");
        return userDto;
    }

    @Override
    public void initUser(String userName, String password) {
        long userSize = userDao.queryUserSize();
        if(userSize > 0){
            return;
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setUserName(userName);
        userEntity.setPassword(password);
        userDao.addUser(userEntity);
    }

    private UserEntity convertUserDto2UserEntity(UserDto userDto) {
        UserEntity userEntity = new UserEntity();
        userEntity.setPassword(userDto.getPassword());
        userEntity.setUserName(userDto.getUserName());
        userEntity.setUserId(userDto.getUserId());
        return userEntity;
    }

    private UserDto convertUserEntity2UserDto(UserEntity userEntity) {
        UserDto userDto = new UserDto();
        userDto.setUserId(userEntity.getUserId());
        userDto.setPassword(userEntity.getPassword());
        userDto.setUserName(userEntity.getUserName());
        return userDto;
    }
}
