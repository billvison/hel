package com.xingkaichun.helloworldblockchain.node.init;

import com.xingkaichun.helloworldblockchain.node.dto.user.UserDto;
import com.xingkaichun.helloworldblockchain.node.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * 初始化用户，每次启动系统时，会校验数据库中是否存在用户。
 * 如果不存在用户，自动生成一个用户，并把用户写入外部文件供系统使用者查看生成的用户名与密码。
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class InitUserHandler {

    @Autowired
    private UserService userService;

    String DEFAULT_USER_NAME = "admin";
    String DEFAULT_PASSWORD = "123456";

    @PostConstruct
    private void init(){
        long userSize = userService.queryUserSize();
        if(userSize > 0){
            return;
        }

        UserDto userDto = new UserDto();
        userDto.setUserName(DEFAULT_USER_NAME);
        userDto.setPassword(DEFAULT_PASSWORD);
        userService.addUser(userDto);
    }
}
