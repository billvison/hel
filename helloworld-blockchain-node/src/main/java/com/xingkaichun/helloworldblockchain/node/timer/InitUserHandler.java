package com.xingkaichun.helloworldblockchain.node.timer;

import com.google.gson.Gson;
import com.xingkaichun.helloworldblockchain.node.dto.user.UserDto;
import com.xingkaichun.helloworldblockchain.node.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * 初始化用户
 */
public class InitUserHandler {

    private static final Logger logger = LoggerFactory.getLogger(InitUserHandler.class);

    @Autowired
    private UserService userService;

    @Autowired
    private Gson gson;


    @PostConstruct
    private void startThread() throws IOException {
        List<UserDto> queryAllUser = userService.queryAllUser();
        if(queryAllUser == null || queryAllUser.size()==0){
            String userName = UUID.randomUUID().toString();
            String password = UUID.randomUUID().toString();
            UserDto userDto = new UserDto();
            userDto.setUserName(userName);
            userDto.setPassword(password);
            userService.newAdminUser(userDto);

            FileWriter fileWriter = null;
            try {
                fileWriter = new FileWriter(new File("InitUser.txt"));
                fileWriter.write(gson.toJson(userDto));
                fileWriter.close();
            } catch (IOException e) {
                logger.error("创建用户出错",e);
                if(fileWriter != null){
                    fileWriter.close();
                }
            }
        }

    }

}
