package com.xingkaichun.helloworldblockchain.netcore.dto.user.response;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class QueryLoginUserInfoResponse {

    private LoginUserDto userDto;


    public static class LoginUserDto {
        private int userId;
        private String userName;




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

        //endregion
    }




    //region get set

    public LoginUserDto getUserDto() {
        return userDto;
    }

    public void setUserDto(LoginUserDto userDto) {
        this.userDto = userDto;
    }

    //endregion
}
