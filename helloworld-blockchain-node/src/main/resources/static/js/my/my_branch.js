//分叉管理
//获取分叉列表
var url = "";
function queryBranch() {
    var branch_list = {};
    $.ajax({
        type: "post",
        url: "/Api/BlockChain/QueryBlockchainBranch",
        contentType: "application/json",
        data: `{}`,
        dataType: "json",
        async: false,
        success: function (data) {
			branch_list.blockList = data.result.blockList;
			// console.log(data.result.blockList);
        },
        error: function (e) {
        }
    });
	var branch_list_html = template("branch_list_template",branch_list);
	$("#branch_list").html(branch_list_html);
}
queryBranch();
//删除分叉
function deleteBranch(){
	var cur_btn = event.srcElement ? event.srcElement : event.target;
	var oDD = cur_btn.parentNode;
	oDD.parentNode.removeChild(oDD);
}
//新增分叉
function addBranch(){
	var getContent =
			'<dl><dt><h2>添加分叉</h2></dt>' +
			'<dd><font>区块高度: </font><input name="blockHeight" type="text" class="c_txt"></dd>' +
			'<dd><font>区块哈希: </font><input name="blockHash" type="text" class="c_txt"></dd>';
	var nextStaff = function(){
		createBranchDd();
	}
	function getInput(){
		var all = {};
		all.blockHeight = $(".n_popbox_msg input[name=blockHeight]").val();
		all.blockHash = $(".n_popbox_msg input[name=blockHash]").val();
		return all;	
	}
	function createBranchDd(){
		var branch_list = document.getElementById("branch_list");
		var odd = document.createElement("dd");
		var datas = getInput();
		odd.innerHTML = 
			'<span>区块高度：'+ datas.blockHeight +'</span>'+
			'<span>区块哈希：'+ datas.blockHash +'</span>'+
			'<font class="delete" onclick="deleteBranch()">X</font>';
		branch_list.appendChild(odd);
	}
	popBox.createBox(getContent,1,nextStaff);	
}
//获取分叉列表
function getBranchList(){
		var all = {};
		all.char = "";
		var odl = document.getElementById("branch_list");
		var odd = odl.querySelectorAll("dd");
		for (var i=0; i<odd.length; i++) {
			all.char += '{\"blockHeight\"' + ':' + odd[i].firstElementChild.textContent.substring(5) + ',' +
				   '\"blockHash\"' + ':\"' + odd[i].firstElementChild.nextElementSibling.textContent.substring(5) + '\"},';
		}
		all.char = all.char.substring(0, all.char.length - 1);
		return all;	
}
//保存更改
function saveBranch(){
	$.ajax({
	    type: "post",
	    url: "/Api/AdminConsole/UpdateBranchchainBranch",
	    contentType: "application/json",
	    data: `{
			"blockList":[${getBranchList().char}]
		}`,
	    dataType: "json",
	    async: false,
	    success: function (data) {
			if(data.serviceCode = "SUCCESS"){
				alert(data.message);
				queryBranch();
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