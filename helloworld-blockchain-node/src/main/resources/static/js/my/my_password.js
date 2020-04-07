//获取用户账号信息
function getUserInfo() {	
    $.ajax({
        type: "post",
        url: "/Api/User/QueryLoginUserInfo",
        contentType: "application/json",
        data: `{}`,
        dataType: "json",
        async: false,
        success: function (data) {
			$(".modify_info input[name=id]").val(data.result.userDto.userId);
			$(".modify_info input[name=name]").val(data.result.userDto.userName);
        },
        error: function (e) {
        }
    });
}
getUserInfo();
//修改密码
function modifyInfo() {
    var user = {};
	user.id = $(".modify_info input[name=id]").val();
	user.name = $(".modify_info input[name=name]").val();
	user.password = $(".modify_info input[name=password]").val();
    $.ajax({
        type: "post",
        url: "/Api/AdminConsole/UpdateAdminUserRequest",
        contentType: "application/json",
        data: `{
				"userDto":{
					"userId":${user.id},
					"userName":"${user.name}",
					"password":"${user.password}"
				}
		}`,
        dataType: "json",
        async: false,
        success: function (data) {
			if(data.serviceCode = "SUCCESS"){
				alert("修改成功");
			}else{
				alert("修改失败");
			}
			console.log(data);
        },
        error: function (e) {
        }
    });
}