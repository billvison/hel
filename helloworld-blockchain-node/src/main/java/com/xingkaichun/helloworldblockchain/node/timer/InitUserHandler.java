package com.xingkaichun.helloworldblockchain.node.timer;

import com.google.gson.Gson;
import com.xingkaichun.helloworldblockchain.node.transport.dto.user.UserDto;
import com.xingkaichun.helloworldblockchain.node.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

/**
 * 初始化用户，每次启动系统时，会校验数据库中是否存在用户。
 * 如果不存在用户，自动生成一个用户，并把用户写入外部文件供系统使用者查看生成的用户名与密码。
 */
public class InitUserHandler {

    private static final Logger logger = LoggerFactory.getLogger(InitUserHandler.class);

    @Autowired
    private UserService userService;

    @Autowired
    private Gson gson;

    @Value("${server.port}")
    private int serverPort;

    @PostConstruct
    private void startThread() throws IOException {
        long userSize = userService.queryUserSize();
        if(userSize>0){
            return;
        }


        String userName = String.valueOf(UUID.randomUUID());
        String password = String.valueOf(UUID.randomUUID());
        UserDto userDto = new UserDto();
        userDto.setUserName(userName);
        userDto.setPassword(password);
        userService.addUser(userDto);

        FileWriter fileWriter = null;
        try {
            String userInfo = String.format("由于您是第一次启动系统，系统自动为您分配了账户。\n" +
                        "用户名是[%s]，密码是[%s]。\n" +
                        "登录网址是[localhost:%s]，请在浏览器中登录系统并修改您的密码。\n" +
                        "为保安全，请另在其它地方妥善保存您的账户，并删除此文件。"
                        ,userName,password,serverPort);
            fileWriter = new FileWriter(new File("InitUser.txt"));
            fileWriter.write(userInfo);
            fileWriter.close();
        } catch (IOException e) {
            logger.error("创建用户出错",e);
            if(fileWriter != null){
                fileWriter.close();
            }
        }
    }

}
