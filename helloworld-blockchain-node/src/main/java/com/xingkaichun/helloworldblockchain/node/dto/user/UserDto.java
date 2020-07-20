package com.xingkaichun.helloworldblockchain.node.dto.user;

import java.io.Serializable;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class UserDto implements Serializable {

    private int userId;
    private String userName;
    private String password;




    //region get set

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
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
