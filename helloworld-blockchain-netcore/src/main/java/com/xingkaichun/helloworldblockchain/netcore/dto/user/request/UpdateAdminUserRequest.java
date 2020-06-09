package com.xingkaichun.helloworldblockchain.netcore.dto.user.request;

import com.xingkaichun.helloworldblockchain.netcore.dto.user.UserDto;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class UpdateAdminUserRequest {

    private UserDto userDto;




    //region get set

    public UserDto getUserDto() {
        return userDto;
    }

    public void setUserDto(UserDto userDto) {
        this.userDto = userDto;
    }

    //endregion
}
