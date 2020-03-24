
var url = "";
//获取列表
function getNodeList() {
    var node_list = {};
    $.ajax({
        type: "post",
        url: url + "/Api/BlockChain/Ping",
        contentType: "application/json",
        data: `{}`,
        dataType: "json",
        async: false,
        success: function (data) {
			
            node_list.result = data.result;
			console.log(node_list.result);
        },
        error: function (e) {
        }
    });
    var node_list_html = template("node_list_template",node_list);
    $("#node_list").html(node_list_html);
    return node_list.result;
}
getNodeList();