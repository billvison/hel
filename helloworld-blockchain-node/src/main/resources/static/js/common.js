
//信息提示框、确认框
//type:0  信息提示框:点击确定后关闭弹出框，没有下一步事务
//type:1  确认框:点击确定后，执行下一步事务,需要手动添加下一步事务
//不兼容IE8以下
var popBox = {
    create_box: function (msg,type) {
        var body = document.querySelector("body");
        var oDiv = document.createElement("div");
        oDiv.id = "n_popbox";
        var cancel,confirm;
        if (type == 0){
            cancel = "";
            confirm = "popBox.clear_box()";
        }else if (type == 1){
            cancel = "<button class=\"n_popbox_btn\" onclick=\"popBox.clear_box()\">取消</button>";
            confirm = "popBox.next_affair()";
        }
        oDiv.innerHTML =
            "<div class=\"n_popbox_cont\">" +
            "<div  class=\"n_popbox_msg\">"+msg+"</div>"+
            cancel+"<button class=\"n_popbox_btn btn2\" onclick="+confirm+">确定</button>"+
            "</div>"+
            "<div  class=\"n_popbox_bg\"></div>";
        body.appendChild(oDiv);
        this.fade_in("n_popbox");
    },
    clear_box: function () {
        var body = document.querySelector("body");
        body.removeChild(body.lastChild);
    },
    fade_in: function (para) {
        var o = document.getElementById(para);
        var n = 0;
        var k = window.setInterval(function () {
            n = n+1;
            m = (n/100) * (n/10);
            o.style.display = "block";
            o.style.opacity = m;
            if (m > 1){
                clearInterval(k);
            }
        }, 3)
    },
    next_affair: function () {
        popBox.clear_box();
        //关闭弹出框后，下一步想要做的事放在这
    }
}

//调用弹出框
// var btn1 = document.getElementById("btn1");
// var btn2 = document.getElementById("btn2");
// btn1.addEventListener('click',function () {
//     popBox.create_box("删除成功",0);
// });
// btn2.addEventListener('click',function () {
//     popBox.create_box("你确定删除吗",1);
// });



