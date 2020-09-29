package com.xingkaichun.helloworldblockchain.node.dto.user;

/**
 * 用户功能路由
 *
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public class UserApiRoute {
    //登录
    public static final String LOGIN = "/Api/User/Login";
    //退出
    public static final String EXIT = "/Api/User/Exit";
    //获取登录用户信息
    public static final String GET_LOGIN_USER = "/Api/User/GetLoginUser";
    //修改用户信息
    public static final String UPDATE_USER = "/Api/User/UpdateUser";
}
