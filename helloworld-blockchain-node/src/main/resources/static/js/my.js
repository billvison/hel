//获取矿工状态,显示矿工状态 显示按钮状态
//如果挖矿中,按钮显示停止,点击按钮停止挖矿,更新矿工状态和按钮状态
//如果休息中,按钮显示挖矿,点击按钮开始挖矿,更新矿工状态和按钮状态
//同步器同理

var url = "";
var miner = {};//矿工信息
var syn = {};//同步信息
var node = {};//节点信息
var block_heght = document.getElementById('block_heght');//获取区块高度
var miner_status = document.getElementById('miner_status');//获取矿工状态
var miner_handle = document.getElementById('miner_handle');//获取激活按钮
var syn_status = document.getElementById('syn_status');//获取同步状态
var syn_handle = document.getElementById('syn_handle');//获取同步按钮
var miner_address = document.getElementById('miner_address');//获取矿工地址
var fork_block_size = document.getElementById('fork_block_size');//硬分叉区块个数
var node_error_delete = document.getElementById('node_error_delete');//删除节点连接错误阈值
var node_search_interval = document.getElementById('node_search_interval');//主动寻找节点的时间间隔
var search_new_block = document.getElementById('search_new_block');//主动寻找新的区块的时间间隔
var check_local_block = document.getElementById('check_local_block');//主动检测自身是否是区块链网络区块链长度最大的时间间隔
//获取区块高度
function queryBlockHeight() {   
    $.ajax({
        type: "post",
        url: url + "/Api/BlockChain/Ping",
        contentType: "application/json",
        data: `{}`,
        dataType: "json",
        async: false,
        success: function (data) {
            block_heght.textContent = data.result.blockChainHeight;			
        },
        error: function (e) {
        }
    });
}
queryBlockHeight();
//删除区块
function removeBlock() {
	var getContent = '<dl><dt><h2>删除区块</h2></dt>' +
				     '<dd><font>输入区块高度:</font><input name="block_height" type="text" class="c_txt"></dd></dl>';
	var nextStaff = function(){
		remveBlockAjax();
	}
	popBox.createBox(getContent,1,nextStaff);
	function remveBlockAjax(){
		var height = $(".n_popbox_msg input[name=block_height]").val();
		$.ajax({
		    type: "post",
		    url: url + "/Api/AdminConsole/RemoveBlock",
		    contentType: "application/json",
		    data: `{
				"blockHeight":"${height}"
			}`,
		    dataType: "json",
		    async: false,
		    success: function (data) {
				if(data.serviceCode = "SUCCESS"){
					alert(data.message);
					queryBlockHeight();
				}   
		    },
		    error: function (e) {
		    }
		});
	}
}
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
            console.log(data);
            if(data.result.minerAddress != null && data.result.minerAddress!=''){
                miner_address.textContent = "矿工地址："+data.result.minerAddress;
            }else{
                miner_address.textContent = "默认矿工地址："+data.result.defaultMinerAccount.address
                + "默认矿工私钥："+data.result.defaultMinerAccount.privateKey;
            }
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
		modifyAddressAjax();
	}
	popBox.createBox(getContent,1,nextStaff);
	function modifyAddressAjax(){
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
		miner_status.innerHTML = "休息中";
		miner_handle.innerHTML = "开启挖矿";
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
		syn_status.innerHTML = "同步中";
		syn_handle.innerHTML = "停止同步";
	}else{
		syn_status.innerHTML = "休息中";
		syn_handle.innerHTML = "开启同步";
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

//开关按钮双向操控
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
//获取配置信息
function getConfig(){
	var ary = [
		"FORK_BLOCK_SIZE",
		"NODE_ERROR_CONNECTION_TIMES_REMOVE_THRESHOLD",
		"NODE_SEARCH_NEW_NODE_TIME_INTERVAL",
		"SEARCH_NEW_BLOCKS_TIME_INTERVAL",
		"CHECK_LOCAL_BLOCKCHAIN_HEIGHT_IS_HIGH_TIME_INTERVAL"
	];
	for (var i=0; i<ary.length; i++) {
		var ospan = document.getElementById(ary[i]);
		$.ajax({
		    type: "post",
		    url: url + "/Api/AdminConsole/GetConfigurationByConfigurationKey",
		    contentType: "application/json",
		    data: `{
				"confKey":"${ary[i]}"
			}`,
		    dataType: "json",
		    async: false,
		    success: function (data) {
				ospan.textContent = data.result.configurationDto.confValue;
		    },
		    error: function (e) {
		    }
		});
	}
}
//配置
getConfig();
function setConfig(){
	var cur_btn = event.srcElement ? event.srcElement : event.target;
	var id = cur_btn.previousElementSibling.id;
	console.log(id);
	var getContent = '<dl><dt><h2>设置</h2></dt>' +
				     '<dd><font>输入数字:</font><input name="value" type="text" class="c_txt"></dd></dl>';
	var nextStaff = function(){
		setConfigAjax();
	}
	popBox.createBox(getContent,1,nextStaff);
	function setConfigAjax(){
		var value = $(".n_popbox_msg input[name=value]").val();
		$.ajax({
		    type: "post",
		    url: url + "/Api/AdminConsole/SetConfiguration",
		    contentType: "application/json",
		    data: `{
				"configurationDto":{
						"confKey":"${id}",
						"confValue":"${value}"
					}
			}`,
		    dataType: "json",
		    async: false,
		    success: function (data) {
				if(data.serviceCode = "SUCCESS"){
					alert(data.message);
					cur_btn.previousElementSibling.textContent = value;
				}  
		    },
		    error: function (e) {
		    }
		});
	}	
}

