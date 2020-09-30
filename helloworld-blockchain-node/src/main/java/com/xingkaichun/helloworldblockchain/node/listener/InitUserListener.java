package com.xingkaichun.helloworldblockchain.node.listener;

import com.xingkaichun.helloworldblockchain.node.dao.UserDao;
import com.xingkaichun.helloworldblockchain.node.model.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * 初始化用户，每次启动系统时，会校验数据库中是否存在用户。
 * 如果不存在用户，自动生成一个用户，用户名默认是admin，密码默认是123456。
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
@Component
public class InitUserListener implements ApplicationListener<ApplicationReadyEvent> {

    private final static Logger logger = LoggerFactory.getLogger(InitUserListener.class);

    @Autowired
    private UserDao userDao;

    String DEFAULT_USER_NAME = "admin";
    String DEFAULT_PASSWORD = "123456";


     public void onApplicationEvent(ApplicationReadyEvent event) {
         logger.info("初始化用户中...");
         long userSize = userDao.queryUserSize();
         if(userSize > 0){
             System.out.println("由于已经存在用户了，不在创建默认用户。");
             return;
         }
         UserEntity userEntity = new UserEntity();
         userEntity.setUserName(DEFAULT_USER_NAME);
         userEntity.setPassword(DEFAULT_PASSWORD);
         userDao.addUser(userEntity);
         logger.info(String.format("创建默认用户[用户名：%s，密码%s]。",DEFAULT_USER_NAME,DEFAULT_PASSWORD));
     }

}