//分叉管理
//获取分叉列表
var url = "";
function queryFork() {
    var fork_list = {};
    $.ajax({
        type: "post",
        url: "/Api/BlockChain/QueryBlockchainFork",
        contentType: "application/json",
        data: `{}`,
        dataType: "json",
        async: false,
        success: function (data) {
			fork_list.blockList = data.result.blockList;
			// console.log(data.result.blockList);
        },
        error: function (e) {
        }
    });
	var fork_list_html = template("fork_list_template",fork_list);
	$("#fork_list").html(fork_list_html);
}
queryFork();
//删除分叉
function deleteFork(){
	var cur_btn = event.srcElement ? event.srcElement : event.target;
	var oDD = cur_btn.parentNode;
	oDD.parentNode.removeChild(oDD);
}
//新增分叉
function addFork(){
	var getContent =
			'<dl><dt><h2>添加分叉</h2></dt>' +
			'<dd><font>区块高度: </font><input name="blockHeight" type="text" class="c_txt"></dd>' +
			'<dd><font>区块哈希: </font><input name="blockHash" type="text" class="c_txt"></dd>';
	var nextStaff = function(){
		createForkDd();
	}
	function getInput(){
		var all = {};
		all.blockHeight = $(".n_popbox_msg input[name=blockHeight]").val();
		all.blockHash = $(".n_popbox_msg input[name=blockHash]").val();
		return all;	
	}
	function createForkDd(){
		var fork_list = document.getElementById("fork_list");
		var odd = document.createElement("dd");
		var datas = getInput();
		odd.innerHTML = 
			'<span>区块高度：'+ datas.blockHeight +'</span>'+
			'<span>区块哈希：'+ datas.blockHash +'</span>'+
			'<font class="delete" onclick="deleteFork()">X</font>';
		fork_list.appendChild(odd);
	}
	popBox.createBox(getContent,1,nextStaff);	
}
//获取分叉列表
function getForkList(){
		var all = {};
		all.char = "";
		var odl = document.getElementById("fork_list");
		var odd = odl.querySelectorAll("dd");
		for (var i=0; i<odd.length; i++) {
			all.char += '{\"blockHeight\"' + ':' + odd[i].firstElementChild.textContent.substring(5) + ',' +
				   '\"blockHash\"' + ':\"' + odd[i].firstElementChild.nextElementSibling.textContent.substring(5) + '\"},';
		}
		all.char = all.char.substring(0, all.char.length - 1);
		return all;	
}
//保存更改
function saveFork(){
	$.ajax({
	    type: "post",
	    url: "/Api/AdminConsole/UpdateBlockchainFork",
	    contentType: "application/json",
	    data: `{
			"blockList":[${getForkList().char}]
		}`,
	    dataType: "json",
	    async: false,
	    success: function (data) {
			if(data.serviceCode = "SUCCESS"){
				alert(data.message);
				queryFork();
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