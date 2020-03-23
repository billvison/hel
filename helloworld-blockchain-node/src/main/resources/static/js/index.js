
var url = "";
var course_container = document.getElementById("course_container");
var click_course_id = {};

//获取教程列表
function getCourseList() {
    var course_list = {};
    $.ajax({
        type: "post",
        url: url + "/Book/QueryBookList",
        contentType: "application/json",
        data: `{}`,
        dataType: "json",
        async: false,
        success: function (data) {
            course_list.BookDTOList = data.Result.BookDTOList;
        },
        error: function (e) {
        }
    });
    var course_list_html = template("course_list_template",course_list);
    $("#course_list").html(course_list_html);
    return course_list.BookDTOList;
}
getCourseList();

//生成弹出框
function createPopBox(e) {
    var course_title;
    var user = {
        BookName: "",
        BookDescription: "",
        SeoUrl: "",
        SeoTitle: "",
        SeoKeywords: "",
        SeoDescription: ""
    };
    if (e == 0){
        user = getCurrentCourse();  //(modify_step:1)
        course_title = "修改教程";
    }else {
        course_title = "新增教程";
    }
    var oDiv = document.createElement("div");
    oDiv.className = "create_main";
    oDiv.innerHTML =
        "<div class=\"create_cont\">" +
            "<div class=\"t\">"+course_title+"<span onclick=\"closePopBox()\">X</span></div>" +
            "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" id=\"add_course\" class=\"course_add_cont course_add_cont2\">" +
                "<tr>" +
                    "<td width=\"100\">教程名称</td>" +
                    "<td><input type=\"text\" value=\""+user.BookName+"\" name=\"name\" class=\"txt\"></td>" +
                "</tr>" +
                "<tr>" +
                    "<td>教程描述</td>" +
                    "<td><input type=\"text\" value=\""+user.BookDescription+"\" name=\"description\" class=\"txt\"></td>" +
                "</tr>" +
                "<tr>" +
                    "<td>SEO网址</td>" +
                    "<td><input type=\"text\" value=\""+user.SeoUrl+"\" name=\"seo_url\" class=\"txt\"></td>" +
                "</tr>" +
                "<tr>" +
                    "<td>SEO标题</td>" +
                    "<td><input type=\"text\" value=\""+user.SeoTitle+"\" name=\"seo_title\" class=\"txt\"></td>" +
                "</tr>" +
                "<tr>" +
                    "<td>SEO关键字</td>" +
                    "<td><input type=\"text\" value=\""+user.SeoKeywords+"\" name=\"seo_keywords\" class=\"txt\"></td>" +
                "</tr>" +
                "<tr>" +
                    "<td>SEO描述</td>" +
                    "<td><input type=\"text\" value=\""+user.SeoDescription+"\" name=\"seo_description\" class=\"txt\"></td>" +
                "</tr>" +
                "<tr>" +
                    "<td></td>" +
                    "<td><input type=\"button\" class=\"c_btn c_btn_imp\" value=\"提交\" onclick=\"submitAddData("+e+")\"></td>" +
                "</tr>" +
            "</table>" +
        "</div>";
    course_container.appendChild(oDiv);  //(add_step:1)(modify_step:2)
}

//获取用户输入信息
function userInputInfo() {  //(add_step:2)(modify_step:3)
    var user = {};
    user.name = $("#add_course input[name=name]").val();
    user.description = $("#add_course input[name=description]").val();
    user.seo_url = $("#add_course input[name=seo_url]").val();
    user.seo_title = $("#add_course input[name=seo_title]").val();
    user.seo_keywords = $("#add_course input[name=seo_keywords]").val();
    user.seo_description = $("#add_course input[name=seo_description]").val();
    return user;
}

//通过URL获取教程ID
function getCourseIdByUrl() {
    var cur_btn = event.srcElement ? event.srcElement : event.target;
    var url = cur_btn.parentNode.parentNode.children[0].getAttribute("href");
    var args = {};
    var query = url.substring(2);
    var pairs = query.split("?");
    for (var i = 0; i < pairs.length; i++) {
        var pos = pairs[i].indexOf("=");
        if (pos == -1) continue;
        var name = pairs[i].substring(0, pos);
        var value = pairs[i].substring(pos + 1);
        value = decodeURIComponent(value);
        args[name] = value;
        click_course_id.id = value;
    }
    return args;
}

//通过教程ID获取所点击教程的单条信息
function getCurrentCourse() {
    var cur_id = getCourseIdByUrl().BookId;
    var course_arr = getCourseList();
    var arr;
    for (var i=0; i<course_arr.length; i++){
        if (course_arr[i].BookId == cur_id){
            arr = course_arr[i];
        }
    }
    return arr;
}

//提交数据
function submitAddData(para) {
    if (para == 0){  //提交修改数据
        $.ajax({  //(modify_step:4)
            type: "post",
            url: url+"/Book/UpdateBook",
            contentType:"application/json",
            data:`{
                "BookId":"${click_course_id.id}",
                "BookName":"${userInputInfo().name}",
                "BookDescription":"${userInputInfo().description}",
                "SeoUrl":"${userInputInfo().seo_url}",
                "SeoTitle":"${userInputInfo().seo_title}",
                "SeoKeywords":"${userInputInfo().seo_keywords}",
                "SeoDescription":"${userInputInfo().seo_description}",
                "IsSoftDelete":false
        }`,
            dataType: "json",
            async:false,
            success: function(data){
                alert("修改成功！");  //(modify_step:5)
                closePopBox();  //(modify_step:6)
                getCourseList();  //(modify_step:7)
            },
            error:function(e){
            }
        });
    }else {  //提交新增数据
        $.ajax({  //(add_step:3)
            type: "post",
            url: url+"/Book/Addbook",
            contentType:"application/json",
            data:`{
                "BookName":"${userInputInfo().name}",
                "BookDescription":"${userInputInfo().description}",
                "SeoUrl":"${userInputInfo().seo_url}",
                "SeoTitle":"${userInputInfo().seo_title}",
                "SeoKeywords":"${userInputInfo().seo_keywords}",
                "SeoDescription":"${userInputInfo().seo_description}"
        }`,
            dataType: "json",
            async:false,
            success: function(data){
                alert("新增成功！");  //(add_step:4)
                closePopBox();  //(add_step:5)
                getCourseList();  //(add_step:6)
            },
            error:function(e){
            }
        });
    }
}

//销毁弹出框
function closePopBox() {
    course_container.removeChild(course_container.children[0]);
}

//新增教程
function addCourse() {
    createPopBox(1);
}

//修改教程
function modifyCourse() {
    createPopBox(0);
}




