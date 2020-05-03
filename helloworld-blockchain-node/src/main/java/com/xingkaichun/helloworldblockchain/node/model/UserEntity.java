package com.xingkaichun.helloworldblockchain.node.model;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class UserEntity {

    private Integer userId;
    private String userName;
    private String password;




    //region get set

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    //endregion
}
