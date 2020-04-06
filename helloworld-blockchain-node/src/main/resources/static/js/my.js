//获取矿工状态,显示矿工状态 显示按钮状态
//如果挖矿中,按钮显示停止,点击按钮停止挖矿,更新矿工状态和按钮状态
//如果休息中,按钮显示挖矿,点击按钮开始挖矿,更新矿工状态和按钮状态
//同步器同理

var url = "";
var miner = {};//矿工信息
var syn = {};//同步信息
var node = {};//节点信息
var miner_status = document.getElementById('miner_status');//获取矿工状态
var miner_handle = document.getElementById('miner_handle');//获取激活按钮
var syn_status = document.getElementById('syn_status');//获取同步状态
var syn_handle = document.getElementById('syn_handle');//获取同步按钮
var miner_address = document.getElementById('miner_address');//获取矿工地址
//获取矿工地址
function getMinerAddress() {   
    $.ajax({
        type: "post",
        url: url + "/Api/AdminConsole/QueryMinerAddress",
        contentType: "application/json",
        data: `{}`,
        dataType: "json",
        async: false,
        success: function (data) {
            miner_address.textContent = data.result.minerAddress;
			// console.log(data.result.minerAddress);
        },
        error: function (e) {
        }
    });
}
getMinerAddress();
//修改矿工地址
function modifyMinerAddress() {
	var getContent = '<dl><dt><h2>修改地址地址</h2></dt>' +
				     '<dd><font>请输入新地址:</font><input name="address" type="text" class="c_txt"></dd></dl>';
	var nextStaff = function(){
		modifyAddress();
	}
	popBox.createBox(getContent,1,nextStaff);
	function modifyAddress(){
		var address = $(".n_popbox_msg input[name=address]").val();
		$.ajax({
		    type: "post",
		    url: url + "/Api/AdminConsole/SetMinerAddress",
		    contentType: "application/json",
		    data: `{
				"minerAddress":"${address}"
			}`,
		    dataType: "json",
		    async: false,
		    success: function (data) {
				if(data.serviceCode = "SUCCESS"){
					alert(data.message);
					getMinerAddress();
					// miner_address.innerHTML = data.result.minerAddress;
				}   
		    },
		    error: function (e) {
		    }
		});
	}
}
//获取矿工状态
function getMinerStatus() {   
    $.ajax({
        type: "post",
        url: url + "/Api/AdminConsole/IsMinerActive",
        contentType: "application/json",
        data: `{}`,
        dataType: "json",
        async: false,
        success: function (data) {
            miner.staus = data.result.minerInActiveState;
        },
        error: function (e) {
        }
    });
	if (miner.staus){
		miner_status.innerHTML = "挖矿中";
		miner_handle.innerHTML = "停止挖矿";
	}else{
		miner_status.innerHTML = " 休息中";
		miner_handle.innerHTML = "挖矿";
	}	
    return miner.staus;
}
getMinerStatus();
//获取同步状态
function getSynStatus() {   
    $.ajax({
        type: "post",
        url: url + "/Api/AdminConsole/IsSynchronizerActive",
        contentType: "application/json",
        data: `{}`,
        dataType: "json",
        async: false,
        success: function (data) {
            syn.staus = data.result.synchronizerInActiveState;
        },
        error: function (e) {
        }
    });
	if (syn.staus){
		syn_status.innerHTML = "已同步";
		syn_handle.innerHTML = "停止同步";
	}else{
		syn_status.innerHTML = "未同步";
		syn_handle.innerHTML = "同步";
	}	
    return syn.staus;
}
getSynStatus();
//获取自动搜索节点状态
function searchNodeStatus() {   
    $.ajax({
        type: "post",
        url: url + "/Api/AdminConsole/IsAutoSearchNode",
        contentType: "application/json",
        data: `{}`,
        dataType: "json",
        async: false,
        success: function (data) {
            node.staus = data.result.autoSearchNewNode;
			// console.log(data);
        },
        error: function (e) {
        }
    });
	if (node.staus){
		node_status.innerHTML = "允许";
		node_handle.innerHTML = "禁止";
	}else{
		node_status.innerHTML = "禁止";
		node_handle.innerHTML = "允许";
	}	
    return node.staus;
}
searchNodeStatus();
//挖矿或激活同步或允许自动搜索节点
function activeMiner(para) {
	var address = null;
	var data_para = "";
	if(para == 'miner'){
		address = "/Api/AdminConsole/ActiveMiner";
	}else if(para == 'syn'){
		address = "/Api/AdminConsole/ActiveSynchronizer";
	}else if(para == 'node'){
		address = "/Api/AdminConsole/SetAutoSearchNode";
		data_para = '"autoSearchNode"'+":"+"true";
	}
    $.ajax({
        type: "post",
        url: url + address,
        contentType: "application/json",
        data: `{${data_para}}`,
        dataType: "json",
        async: false,
        success: function (data) {
            if(para == 'miner'){
            	getMinerStatus();
            }else if(para == 'syn'){
            	getSynStatus();
            }else if(para == 'node'){
            	searchNodeStatus();
            }
        },
        error: function (e) {
        }
    });
}
//停止挖矿或停止同步或禁止自动搜索节点
function deactiveMiner(para) {
	var address = null;
	var data_para = "";
	if(para == 'miner'){
		address = "/Api/AdminConsole/DeactiveMiner";
	}else if(para == 'syn'){
		address = "/Api/AdminConsole/DeactiveSynchronizer";
	}else if(para == 'node'){
		address = "/Api/AdminConsole/SetAutoSearchNode";
		data_para = '"autoSearchNode"'+":"+"false";
	}
    $.ajax({
        type: "post",
        url: url + address,
        contentType: "application/json",
        data: `{${data_para}}`,
        dataType: "json",
        async: false,
        success: function (data) {
            if(para == 'miner'){
            	getMinerStatus();
            }else if(para == 'syn'){
            	getSynStatus();
            }else if(para == 'node'){
            	searchNodeStatus();
            }
        },
        error: function (e) {
        }
    });
}

//双向操控
function toggleUnits(para){
	if(para == 'miner'){
		var i = getMinerStatus();
	}else if(para == 'syn'){
		var i = getSynStatus();
	}else if(para == 'node'){
		var i = searchNodeStatus();
	}
	if(i){
		deactiveMiner(para);
	}else{
		activeMiner(para);
	}
}



