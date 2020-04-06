var delay_load_index;
var address_index = 1;
var click_times = 0;
var search_result = document.getElementById("search_result"); //获取输出到的父容器
//选择搜索类型
$("#search_select").change(function() {
	var value = $("#search_select option:selected").val();
	switch (value) {
		case "block_byheight":
			$("#search_input").css("display","block");
			$("#search_input").attr("placeholder","请输入高度");
			break;
		case "block_byhash":
			$("#search_input").css("display","block");
			$("#search_input").attr("placeholder","请输入哈希值");
			break;
		case "txos_byaddress":
			$("#search_input").css("display","block");
			$("#search_input").attr("placeholder","请输入地址");
			break;
		case "utxos_byaddress":
			$("#search_input").css("display","block");
			$("#search_input").attr("placeholder","请输入地址");
			break;
		case "trans_byuuid":
			$("#search_input").css("display","block");
			$("#search_input").attr("placeholder","请输入UUID");
			break;
		case "minning_byuuid":
			$("#search_input").css("display","block");
			$("#search_input").attr("placeholder","请输入UUID");
			break;
		case "minning_byall":
			$("#search_input").css("display","none");
			break;
	}
});
//公共函数:搜索
function searchUnit() {
	var url = "/Api/BlockChain",
		address = "",
		data = "",
		result = {};
	var type = $("#search_select option:selected").val(); //获取搜索类型
	var input_val = $("#search_input").val(); //获取搜索输入内容
	var search_title = document.getElementById("search_title");
	search_title.style.display = "block";
	switch (type) {
		case "block_byheight": //根据高度搜索区块
			address = "/QueryBlockDtoByBlockHeight";
			data = '"blockHeight":' + input_val;
			break;
		case "block_byhash": //根据哈希值搜索区块
			address = "/QueryBlockDtoByBlockHash";
			data = '"blockHash":"' + input_val + '"';
			break;
		case "txos_byaddress": //根据地址搜索交易输出
			address = "/QueryTxosByAddress";
			data = '"address":"' + input_val + '",' + '"pageCondition":{"from":'+address_index+',"size":5}';
			break;
		case "utxos_byaddress": //根据地址搜索未花费交易输出
			address = "/QueryUtxosByAddress";
			data = '"address":"' + input_val + '",' + '"pageCondition":{"from":'+address_index+',"size":5}';
			break;
		case "trans_byuuid": //根据交易UUID搜索交易
			address = "/QueryTransactionByTransactionUUID";
			data = '"transactionUUID":"' + input_val + '"';
			break;
		case "minning_byuuid": //根据交易UUID搜索挖矿中交易
			address = "/QueryMiningTransactionByTransactionUUID";
			data = '"transactionUUID":"' + input_val + '"';
			break;
		case "minning_byall": //查询挖矿中的交易
			address = "/QueryMiningTransactionList";
			data = '"pageCondition":{"from":'+address_index+',"size":3}';
			break;
	}	
	$.ajax({
		type: "post",
		url: url + address,
		contentType: "application/json",
		data: `{
			${data}
		}`,
		dataType: "json",
		async: false,
		success: function(data) {	
			if (data.serviceCode=="SUCCESS") {
				if (data.result.utxos && data.result.utxos.length == 0) {
					noMore();
				} else if(data.result.transactionDtoList && data.result.transactionDtoList.length == 0){
					noMore();
				} else{
					console.log(data);
					result = data.result;
					var odiv = document.createElement("div");
					odiv.className = "result_list";
					odiv.id = "result_list";
					odiv.innerHTML = resultTemp(result,type); //调用模板函数,传入返回数据和类型,生成搜索结果页面
					if (click_times == 0) {
						search_result.appendChild(odiv); //结果输出到页面
					} else {
						console.log("第二次点击");
						search_result.removeChild(search_result.firstChild.nextElementSibling);
						search_result.appendChild(odiv); //结果输出到页面
					}
					click_times = 1;
				}
				
			} else{
				search_result.textContent = "暂无内容";
			}
		},
		error: function(e) {}
	});

}
//模板函数,result为ajax返回的搜索,type为搜索类型
function resultTemp(result,type){
	var show_result;
	switch (type) {
		case "block_byheight":
			show_result = blockByHeight(result);
			break;
		case "block_byhash":
			show_result = blockByHeight(result);
			break;
		case "txos_byaddress":
			show_result = txosByAddress(result);
			break;
		case "utxos_byaddress":
			show_result = txosByAddress(result);
			break;
		case "trans_byuuid":
			show_result = transByUuid(result);
			break;
		case "minning_byuuid":
			show_result = transByUuid(result);
			break;
		case "minning_byall":
			show_result = transByAll(result);
			break;
	}
	return show_result;
}
//展示搜索结果(根据根据高度搜索区块)
function blockByHeight(result){
	var transList;
	var pageCondition;
	if (result.block.transactionQuantity > 0) {
		delay_load_index = result.block.startTransactionSequenceNumberInBlockChain;
		transList = showTransList();
	} else{
		transList = null;
	}
	var temp =
			'<dl><dd>时间戳: ' + result.block.timestamp + '</dd>' +
			'<dd>前哈希: ' + result.block.previousHash + '</dd>' +
			'<dd>区块高度: ' + result.block.height + '</dd>' +		
			'<dd>merkleRoot: ' + result.block.merkleRoot + '</dd>' +
			'<dd>nonce: ' + result.block.nonce + '</dd>' +
			'<dd>哈希: ' + result.block.hash + '</dd>' +
			'<dd>consensusTarget: ' + result.block.consensusTarget.value + '</dd>' +
			'<dd>transactionQuantity: ' + result.block.transactionQuantity + '</dd>' +
			'<dd>startTransactionSequenceNumberInBlockChain: ' + result.block.startTransactionSequenceNumberInBlockChain + '</dd>' +
			'<dd>endTransactionSequenceNumberInBlockChain: ' + result.block.endTransactionSequenceNumberInBlockChain + '</dd>' +
			'<dd id="trans_list">交易: ' + transList + '</dd></dl>';
	return temp;
}
//展示搜索结果(根据地址搜索未花费交易输出)
function txosByAddress(result){
	var temp = "";
	for (var i=0; i<result.utxos.length; i++) {
		temp += '<dl><dd>transactionOutputUUID: ' + result.utxos[i].transactionOutputUUID + '</dd>' +
				'<dd>stringAddress: ' + result.utxos[i].stringAddress.value + '</dd>' +
				'<dd>value: ' + result.utxos[i].value + '</dd>' +
				'<dd>blockHeight: ' + result.utxos[i].blockHeight + '</dd>' +
				'<dd>transactionSequenceNumberInBlock: ' + result.utxos[i].transactionSequenceNumberInBlock + '</dd>' +
				'<dd>transactionOutputSequence: ' + result.utxos[i].transactionOutputSequence + '</dd></dl>';
	}
	return temp;
}
//展示搜索结果(根据交易UUID搜索交易,根据交易UUID搜索挖矿中交易)
function transByUuid(result){
	var temp =
			'<dl><dd>timestamp: ' + result.transactionDTO.timestamp + '</dd>' +
			'<dd>transactionUUID: ' + result.transactionDTO.transactionUUID + '</dd>' +
			'<dd>transactionType: ' + result.transactionDTO.transactionType + '</dd>' +
			'<dd><b>inputs:</b> ' + inputs() + '</dd>' +
			'<dd><b>outputs:</b> ' + outputs() + '</dd>' +
			'<dd>signature: ' + result.transactionDTO.signature + '</dd></dl>';
	function inputs(){
		var char = "";
		for (var i=0; i<result.transactionDTO.inputs.length; i++) {
			char += '<dl class="child"><dd>unspendTransactionOutputUUID: ' + result.transactionDTO.inputs[i].unspendTransactionOutputUUID + '</dd>' +
					'<dd>publicKey: ' + result.transactionDTO.inputs[i].publicKey + '</dd></dl>';
		}
		return char;
	}
	function outputs(){
		var char = "";
		for (var i=0; i<result.transactionDTO.outputs.length; i++) {
			char += '<dl class="child"><dd>transactionOutputUUID: ' + result.transactionDTO.outputs[i].transactionOutputUUID + '</dd>' +
					'<dd>address: ' + result.transactionDTO.outputs[i].value + '</dd>' +
					'<dd>value: ' + result.transactionDTO.outputs[i].value + '</dd></dl>';
		}
		return char;
	}
	return temp;
}
//展示搜索结果(查询挖矿中的交易)
function transByAll(result){
	var temp =  '';
	for (var j=0; j<result.transactionDtoList.length; j++) {				
		temp += '<dl><dd>timestamp: ' + result.transactionDtoList[j].timestamp + '</dd>' +
				'<dd>transactionUUID: ' + result.transactionDtoList[j].transactionUUID + '</dd>' +
				'<dd>transactionType: ' + result.transactionDtoList[j].transactionType + '</dd>' +
				'<dd><b>inputs:</b> ' + inputs() + '</dd>' +
				'<dd><b>outputs:</b> ' + outputs() + '</dd>' +
				'<dd>signature: ' + result.transactionDtoList[j].signature + '</dd></dl>';
		function inputs(){
			var char = "";
			for (var i=0; i<result.transactionDtoList[j].inputs.length; i++) {
				char += '<dl class="child"><dd>unspendTransactionOutputUUID: ' + result.transactionDtoList[j].inputs[i].unspendTransactionOutputUUID + '</dd>' +
						'<dd>publicKey: ' + result.transactionDtoList[j].inputs[i].publicKey + '</dd></dl>';
			}
			return char;
		}
		function outputs(){
			var char = "";
			for (var i=0; i<result.transactionDtoList[j].outputs.length; i++) {
				char += '<dl class="child"><dd>transactionOutputUUID: ' + result.transactionDtoList[j].outputs[i].transactionOutputUUID + '</dd>' +
						'<dd>address: ' + result.transactionDtoList[j].outputs[i].value + '</dd>' +
						'<dd>value: ' + result.transactionDtoList[j].outputs[i].value + '</dd></dl>';
			}
			return char;
		}
	}
	return temp;
}
//调用QueryTransactionByTransactionHeight接口,搜索高度/哈希所用
function showTransList(){
	var result = {};
	var char = "";
	console.log(delay_load_index);
	$.ajax({
	    type: "post",
	    url: "/Api/BlockChain/QueryTransactionByTransactionHeight",
	    contentType: "application/json",
	    data: `{
			"pageCondition":{
			  "from":${delay_load_index},
			  "size":1
			 }
		}`,
	    dataType: "json",
	    async: false,
	    success: function (data) {
			if (data.result.transactionList.length == 0) {
				var trans_list = document.getElementById("trans_list");
				var frag = document.createElement("div");
				frag.id = "nomore";
				frag.innerHTML = "没有更多了";
				trans_list.appendChild(frag);
			} else{
				result = data.result;
				char = tempTransList(result);
				delay_load_index++;
			}
	    },
	    error: function (e) {
	    }
	});
	return char;
}
function tempTransList(result){
	var temp = "";
	for (var j=0; j<result.transactionList.length; j++) {				
		temp += '<dl><dd>timestamp: ' + result.transactionList[j].timestamp + '</dd>' +
				'<dd>transactionUUID: ' + result.transactionList[j].transactionUUID + '</dd>' +
				'<dd>transactionType: ' + result.transactionList[j].transactionType + '</dd>' +
				'<dd><b>inputs:</b> ' + inputs() + '</dd>' +
				'<dd><b>outputs:</b> ' + outputs() + '</dd>' +
				'<dd>signature: ' + result.transactionList[j].signature + '</dd>' +
				'<dd>transactionSequenceNumberInBlock: ' + result.transactionList[j].transactionSequenceNumberInBlock + '</dd>' +
				'<dd>transactionSequenceNumberInBlockChain: ' + result.transactionList[j].transactionSequenceNumberInBlockChain + '</dd>' +
				'<dd>blockHeight: ' + result.transactionList[j].blockHeight + '</dd></dl>';
		function inputs(){
			var char = "";
			if (result.transactionList[j].inputs !== null) {
				for (var i=0; i<result.transactionList[j].inputs.length; i++) {
					char += '<dl class="child"><dd>unspendTransactionOutputUUID: ' + result.transactionList[j].inputs[i].unspendTransactionOutput.unspendTransactionOutputUUID + '</dd>' +
							'<dd>stringAddress: ' + result.transactionList[j].inputs[i].unspendTransactionOutput.stringAddress.value + '</dd>' +
							'<dd>value: ' + result.transactionList[j].inputs[i].unspendTransactionOutput.value + '</dd>' +
							'<dd>blockHeight: ' + result.transactionList[j].inputs[i].unspendTransactionOutput.blockHeight + '</dd>' +
							'<dd>transactionSequenceNumberInBlock: ' + result.transactionList[j].inputs[i].unspendTransactionOutput.transactionSequenceNumberInBlock + '</dd>' +
							'<dd>transactionOutputSequence: ' + result.transactionList[j].inputs[i].unspendTransactionOutput.transactionOutputSequence + '</dd>' +
							'<dd>publicKey: ' + result.transactionList[j].inputs[i].stringPublicKey.publicKey + '</dd></dl>';
				}
			} else{
				char = null;
			}			
			return char;
		}
		function outputs(){
			var char = "";
			if (result.transactionList[j].outputs !== null) {
				for (var i=0; i<result.transactionList[j].outputs.length; i++) {
					char += '<dl class="child"><dd>transactionOutputUUID: ' + result.transactionList[j].outputs[i].transactionOutputUUID + '</dd>' +
							'<dd>stringAddress: ' + result.transactionList[j].outputs[i].stringAddress.value + '</dd>' +
							'<dd>value: ' + result.transactionList[j].outputs[i].value + '</dd>' +
							'<dd>blockHeight: ' + result.transactionList[j].outputs[i].blockHeight + '</dd>' +
							'<dd>transactionSequenceNumberInBlock: ' + result.transactionList[j].outputs[i].transactionSequenceNumberInBlock + '</dd>' +
							'<dd>transactionOutputSequence: ' + result.transactionList[j].outputs[i].transactionOutputSequence + '</dd></dl>';
				}
			} else{
				char = null;
			}
			return char;
		}
	}
	return temp;
}

//延迟加载
window.onscroll = function(){
	var scrollTop = document.documentElement.scrollTop || document.body.scrollTop;
	var clientHeight = document.documentElement.clientHeight || document.body.clientHeight;
	var scrollHeight = document.documentElement.scrollHeight || document.body.scrollHeight;
	var type = $("#search_select option:selected").val(); //获取搜索类型
	if (scrollTop + clientHeight == scrollHeight && document.getElementById("nomore") == null) {		
		switch (type) {
			case "block_byheight": //根据高度搜索区块
				var trans_list = document.getElementById("trans_list");
				var frag = document.createElement("div");
				frag.innerHTML = showTransList();
				trans_list.appendChild(frag);
				break;
			case "block_byhash": //根据哈希值搜索区块
				var trans_list = document.getElementById("trans_list");
				var frag = document.createElement("div");
				frag.innerHTML = showTransList();
				trans_list.appendChild(frag);
				break;
			case "txos_byaddress": //根据地址搜索交易输出
				address_index = address_index + 5;
				searchUnit();
				break;
			case "utxos_byaddress": //根据地址搜索未花费交易输出
				address_index = address_index + 5;
				searchUnit();
				break;
			case "minning_byall": //查询挖矿中的交易
				address_index = address_index + 3;
				searchUnit();
				break;
		}
	}
}
//显示没有更多了
function noMore(){
	var frag = document.createElement("div");
	frag.id = "nomore";
	frag.innerHTML = "没有更多了";
	search_result.appendChild(frag);
}



