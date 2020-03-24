//获取矿工状态,显示矿工状态 显示按钮状态
//如果挖矿中,按钮显示停止,点击按钮停止挖矿,更新矿工状态和按钮状态
//如果休息中,按钮显示挖矿,点击按钮开始挖矿,更新矿工状态和按钮状态
//同步器同理

var url = "";
var miner = {};//矿工信息
var syn = {};//同步信息
var miner_status = document.getElementById('miner_status');//获取矿工状态
var miner_handle = document.getElementById('miner_handle');//获取激活按钮
var syn_status = document.getElementById('syn_status');//获取同步状态
var syn_handle = document.getElementById('syn_handle');//获取同步按钮
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
//挖矿
function activeMiner(para) {
	var address = null;
	if(para == 'miner'){
		address = "/Api/AdminConsole/ActiveMiner";
	}else if(para == 'syn'){
		address = "/Api/AdminConsole/ActiveSynchronizer";
	}
    $.ajax({
        type: "post",
        url: url + address,
        contentType: "application/json",
        data: `{}`,
        dataType: "json",
        async: false,
        success: function (data) {
            miner.staus = data.result.activeMinerSuccess;
        },
        error: function (e) {
        }
    });
	getMinerStatus();
	getSynStatus();
    return miner.staus;
}
//停止挖矿
function deactiveMiner(para) {
	var address = null;
	if(para == 'miner'){
		address = "/Api/AdminConsole/DeactiveMiner";
	}else if(para == 'syn'){
		address = "/Api/AdminConsole/DeactiveSynchronizer";
	}
    $.ajax({
        type: "post",
        url: url + address,
        contentType: "application/json",
        data: `{}`,
        dataType: "json",
        async: false,
        success: function (data) {
            miner.staus = data.result.deactiveMinerSuccess;
        },
        error: function (e) {
        }
    });
	getMinerStatus();
	getSynStatus();
    return miner.staus;
}
//操纵矿工
function toggleUnits(para){
	if(para == 'miner'){
		var i = getMinerStatus();
	}else if(para == 'syn'){
		var i = getSynStatus();
	}
	if(i){
		deactiveMiner(para);
	}else{
		activeMiner(para);
	}
}
//生成钱包
function generateWallet(){
	var wallet = {};
	$.ajax({
	    type: "post",
	    url: url + "/Api/BlockChain/GenerateWalletDTO",
	    contentType: "application/json",
	    data: `{}`,
	    dataType: "json",
	    async: false,
	    success: function (data) {
	        wallet.walletDTO = data.result.walletDTO;
			// wallet.privateKey = data.result.walletDTO.privateKey;
	    },
	    error: function (e) {
	    }
	});
	return wallet.walletDTO;
}
var generate_wallet = document.getElementById("generate_wallet");

generate_wallet.addEventListener('click',function () {
	// console.log(generateWallet().privateKey);
	var wallet = generateWallet();
	var a = '<h1>注意: 以下内容为自动生成,请妥善保管,丢失一律不负责</h1>'+
			'<div><b>私钥</b>: '+ wallet.privateKey + '</div>' +
			'<div><b>公钥</b>: '+	wallet.publicKey + '</div>' +
			'<div><b>地址</b>: '+	wallet.address + '</div>' ;
    popBox.create_box(a,0);
});

//添加节点
function addNodes(){
	var getContent = '<h2 class="t_title">添加节点</h2>' +
					 '<p class="t_cont">ip: ' + '<input type="text" class="c_txt c_ip"></p>' +
					 '<p class="t_cont">端口: ' + '<input type="text" class="c_txt c_port"></p>';
	popBox.create_box(getContent,1);
}


