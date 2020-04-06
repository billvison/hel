//节点管理
var url = "";
//节点列表
function queryNodeList() {
    var node_list = {};
    $.ajax({
        type: "post",
        url: "/Api/AdminConsole/QueryNodeList",
        contentType: "application/json",
        data: `{}`,
        dataType: "json",
        async: false,
        success: function (data) {
			node_list.nodeDTOList = data.result.nodeList;
			var ary = [];
			for (var i=0; i<node_list.nodeDTOList.length; i++) {
				node_list.nodeDTOList[i].isNodeAvailable = node_list.nodeDTOList[i].isNodeAvailable.toString();
				node_list.nodeDTOList[i].fork = node_list.nodeDTOList[i].fork.toString();
			}
			// console.log(data.result.nodeList);		
        },
        error: function (e) {
        }
    });
	var node_list_html = template("node_list_template",node_list);
	$("#node_list").html(node_list_html);
}
queryNodeList();
//删除节点
function deleteNode() {
    var node_attr = {};
	var cur_btn = event.srcElement ? event.srcElement : event.target;
	var get_ele = cur_btn.parentNode.previousElementSibling;
	node_attr.id = get_ele.firstElementChild.textContent.substring(3);
	node_attr.port = get_ele.firstElementChild.nextElementSibling.textContent.substring(3);
	console.log(node_attr);
    $.ajax({
        type: "post",
        url: "/Api/AdminConsole/DeleteNode",
        contentType: "application/json",
        data: `{
			"node":{
					    "ip": "${node_attr.id}",
			            "port": ${node_attr.port}
				   }
		}`,
        dataType: "json",
        async: false,
        success: function (data) {
			alert(data.message);
			queryNodeList();
        },
        error: function (e) {
        }
    });
}
//[新增节点]
	//点击新增:生成自定义内容作为参数,调用公用弹窗函数,传入参数,生成弹出框
	//等待用户输入内容
	//点击取消:直接销毁弹出框
	//点击确定:获取用户输入内容传入data,新增成功弹出提示,销毁弹出框
//生成弹出框
function addNode(){
	//点击确定后处理的业务(打包第三个参数)
	var nextStaff = function(){
		addNodepara();
	}
	var getContent = 
			'<dl><dt><h2>新增节点</h2></dt>' +
			'<dd><font>ip: </font><input name="id" type="text" class="c_txt"></dd>' +
			'<dd><font>端口: </font><input name="port" type="text" class="c_txt"></dd>' +
			'<dd><font>区块链高度: </font><input name="blockHeight" type="text" class="c_txt"></dd>' +
			'<dd><font>连接失败次数: </font><input name="errorConnection" type="text" class="c_txt"></dd>' +
			'<dd><font>是否可用: </font>' +
				'<select name="available"><option selected="selected" value="true">true</option><option value="false">false</option></select>' +
			'</dd>' +
			'<dd><font>分叉: </font>' +
				'<select name="fork"><option value="true">true</option><option selected="selected" value="false">false</option></select>' +
			'</dd></dl>';
	popBox.createBox(getContent,1,nextStaff);
}
//获取用户输入内容
function getInput(){
	var all = {};
	all.id = $(".n_popbox_msg input[name=id]").val();
	all.port = $(".n_popbox_msg input[name=port]").val();
	all.blockHeight = $(".n_popbox_msg input[name=blockHeight]").val();
	all.errorConnection = $(".n_popbox_msg input[name=errorConnection]").val();
	all.available = $(".n_popbox_msg select[name=available] option:selected").val();
	all.fork = $(".n_popbox_msg  select[name=fork] option:selected").val();
	console.log(all);
	return all;	
}
//传入参数
function addNodepara() {
    var node_list = {};
	var inputVal = getInput();
    $.ajax({
        type: "post",
        url: "/Api/AdminConsole/AddNode",
        contentType: "application/json",
        data: `{
			"node":{
			            "ip": "${inputVal.id}",
			            "port": ${inputVal.port},
			            "blockChainHeight": ${inputVal.blockHeight},
			            "isNodeAvailable": ${inputVal.available},
			            "errorConnectionTimes": ${inputVal.errorConnection},
			            "fork": ${inputVal.fork}
				}
		}`,
        dataType: "json",
        async: false,
        success: function (data) {
			if(data.serviceCode = "SUCCESS"){
				alert("新增成功");
				queryNodeList();
				console.log(data);
			}else{
				alert("新增失败");
				console.log(data);
			}
			
        },
        error: function (e) {
        }
    });
}

//[修改节点]
	//点击修改:获取当前点击列表中的内容作为参数,调用公用弹窗函数,传入参数,生成弹出框
	//等待用户修改内容
	//点击取消:直接销毁弹出框
	//点击确定:获取用户修改内容传入data,修改成功弹出提示,销毁弹出框
//获取点击列表的内容
function getClickInput(){
	var cur_btn = event.srcElement ? event.srcElement : event.target;
	var get_ele = cur_btn.parentNode.previousElementSibling.childNodes;
	var all = {};
	all.id = get_ele[1].textContent.substring(3);
	all.port = get_ele[3].textContent.substring(3);
	all.blockHeight = get_ele[5].textContent.substring(6);
	all.errorConnection = get_ele[7].textContent.substring(7);
	all.available = get_ele[9].textContent.substring(5);
	all.available_un = reverseBoolean(all.available);
	all.fork = get_ele[11].textContent.substring(3);
	all.fork_un = reverseBoolean(all.fork);
	// console.log(all);
	return all;	
}
function modifyNode(){
	//点击确定后处理的业务(打包第三个参数)
	var nextStaff = function(){
		modifyNodePara();
	}
	var clicked_c = getClickInput();
	var getContent = 
			'<dl><dt><h2>修改节点</h2></dt>' +
			'<dd><font>ip: </font><input name="id" readonly="readonly" value="'+clicked_c.id+'" type="text" class="c_txt undo"></dd>' +
			'<dd><font>端口: </font><input name="port" readonly="readonly" value="'+clicked_c.port+'" type="text" class="c_txt undo"></dd>' +
			'<dd><font>区块链高度: </font><input name="blockHeight" value="'+clicked_c.blockHeight+'" type="text" class="c_txt"></dd>' +
			'<dd><font>连接失败次数: </font><input name="errorConnection" value="'+clicked_c.errorConnection+'" type="text" class="c_txt"></dd>' +
			'<dd><font>是否可用: </font>' +
				'<select name="available"><option selected="selected" value="'+clicked_c.available+'">'+clicked_c.available+'</option>' +
				'<option value="'+clicked_c.available_un+'">'+clicked_c.available_un+'</option></select>' +
			'</dd>' +
			'<dd><font>分叉: </font>' +
				'<select name="fork"><option selected="selected" value="'+clicked_c.fork+'">'+clicked_c.fork+'</option>' +
				'<option value="'+clicked_c.fork_un+'">'+clicked_c.fork_un+'</option></select>' +
			'</dd></dl>';
	popBox.createBox(getContent,1,nextStaff);
}
function modifyNodePara() {
    var node_list = {};
	var inputVal = getInput();
	console.log(inputVal);
    $.ajax({
        type: "post",
        url: "/Api/AdminConsole/UpdateNode",
        contentType: "application/json",
        data: `{
			"node":{
			            "ip": "${inputVal.id}",
			            "port": ${inputVal.port},
			            "blockChainHeight": ${inputVal.blockHeight},
			            "isNodeAvailable": ${inputVal.available},
			            "errorConnectionTimes": ${inputVal.errorConnection},
			            "fork": ${inputVal.fork}
				}
		}`,
        dataType: "json",
        async: false,
        success: function (data) {
			if(data.serviceCode=="FAIL") {
				alert("修改失败");
				// console.log(data);
			}else{
				alert("修改成功");
				queryNodeList();
			}	
        },
        error: function (e) {
        }
    });
}