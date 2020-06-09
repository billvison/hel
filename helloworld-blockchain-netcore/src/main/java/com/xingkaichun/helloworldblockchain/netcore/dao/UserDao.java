package com.xingkaichun.helloworldblockchain.netcore.dao;

import com.xingkaichun.helloworldblockchain.netcore.model.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * 用户dao
 *
 * @author 邢开春 xingkaichun@qq.com
 */
@Mapper
@Component
public interface UserDao {

    void addUser(UserEntity userEntity);

    UserEntity queryUserByUserName(@Param("userName") String userName);

    void updateUser(UserEntity userEntity);

    long queryUserSize();
}
