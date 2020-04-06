//判断用户是否登录
var url = "";
var login_state = {};
function isLogin() {
    var user = {};
	var user_login = document.getElementById("user_login");
    $.ajax({
        type: "post",
        url: url + "/Api/User/QueryLoginUserInfo",
        contentType: "application/json",
        data: `{}`,
        dataType: "json",
        async: false,
        success: function (data) {
			login_state.state = data.serviceCode;
			if(data.serviceCode == "SUCCESS"){
				user_login.innerHTML = data.result.userDto.userName;
			}else{	
				user_login.innerHTML = '<a href="my.html">登录</a>';
			}
        },
        error: function (e) {
        }
    });
}
isLogin();
