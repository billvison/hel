
var url = "";
//获取列表
function getNodeList() {
    var node_list = {};
    $.ajax({
        type: "post",
        url: url + "/Api/Blockchain/Ping",
        contentType: "application/json",
        data: `{}`,
        dataType: "json",
        async: false,
        success: function (data) {			
			node_list.result = data.result;
			var ary = [];
			for (var i=0; i<node_list.result.nodeList.length; i++) {
				node_list.result.nodeList[i].isNodeAvailable = node_list.result.nodeList[i].isNodeAvailable.toString();
				node_list.result.nodeList[i].fork = node_list.result.nodeList[i].fork.toString();
			}
			// console.log(node_list);	
        },
        error: function (e) {
        }
    });
    var node_list_html = template("node_list_template",node_list);
    $("#node_list").html(node_list_html);
    return node_list.result;
}
getNodeList();