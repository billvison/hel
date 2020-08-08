var delay_load_index;
var address_index = 1;
var click_times = 0;
var search_result = document.getElementById("search_result"); //获取输出到的父容器
//选择搜索类型
$("#search_select").change(function() {
	//重置状态
	$("#search_result").empty();
    delay_load_index =0;
    address_index = 1;
	click_times = 0;

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
			$("#search_input").attr("placeholder","请输入Hash");
			break;
		case "minning_byuuid":
			$("#search_input").css("display","block");
			$("#search_input").attr("placeholder","请输入Hash");
			break;
		case "minning_byall":
			$("#search_input").css("display","none");
			break;
	}
});
//公共函数:搜索
function searchUnit() {
    //重置状态
    delay_load_index =0;
    address_index = 1;
	click_times = 0;

    $("#search_result").empty();
    innerSearchUnit();
}
function innerSearchUnit() {
	var url = "/Api/BlockChain",
		address = "",
		data = "",
		result = {};
	var type = $("#search_select option:selected").val(); //获取搜索类型
	var input_val = $("#search_input").val(); //获取搜索输入内容
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
		case "trans_byuuid": //根据交易Hash搜索交易
			address = "/QueryTransactionByTransactionHash";
			data = '"transactionHash":"' + input_val + '"';
			break;
		case "minning_byuuid": //根据交易Hash搜索挖矿中交易
			address = "/QueryMiningTransactionByTransactionHash";
			data = '"transactionHash":"' + input_val + '"';
			break;
		case "minning_byall": //查询挖矿中的交易
			address = "/QueryMiningTransactionList";
			data = '"pageCondition":{"from":'+address_index+',"size":50}';
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
			    if(click_times == 0){
                    if (data.result.txos && data.result.txos.length == 0) {
                        click_times += 1;
                        showSearchEmptyResult();
                        return;
                    } else if (data.result.utxos && data.result.utxos.length == 0) {
                    	click_times += 1;
                        showSearchEmptyResult();
                        return;
                    } else if(data.result.transactionDtoList && data.result.transactionDtoList.length == 0){
                        click_times += 1;
                        showSearchEmptyResult();
                        return;
                    }
			    }else{
                    if (data.result.txos && data.result.txos.length == 0) {
                        click_times += 1;
                        noMore();
                        return;
                    } else if (data.result.utxos && data.result.utxos.length == 0) {
                    click_times += 1;
                       noMore();
                       return;
                   } else if(data.result.transactionDtoList && data.result.transactionDtoList.length == 0){
                        click_times += 1;
                        noMore();
                        return;
                    }
			    }
                console.log(data);
                result = data.result;
                var odiv = document.createElement("div");
                odiv.className = "result_list";
                odiv.id = "result_list";
                odiv.innerHTML = resultTemp(result,type); //调用模板函数,传入返回数据和类型,生成搜索结果页面
                search_result.appendChild(odiv); //结果输出到页面
                click_times += 1;
			} else{
				showSearchEmptyResult();
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
			show_result = utxosByAddress(result);
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
			'<dd>前哈希: ' + result.block.previousBlockHash + '</dd>' +
			'<dd>区块高度: ' + result.block.height + '</dd>' +		
			'<dd>merkleRoot: ' + result.block.merkleRoot + '</dd>' +
			'<dd>consensusValue: ' + result.block.consensusValue + '</dd>' +
			'<dd>哈希: ' + result.block.hash + '</dd>' +
			'<dd>explain: ' + result.block.consensusVariableHolder.explain + '</dd>' +
			'<dd>transactionQuantity: ' + result.block.transactionQuantity + '</dd>' +
			'<dd>startTransactionSequenceNumberInBlockChain: ' + result.block.startTransactionSequenceNumberInBlockChain + '</dd>' +
			'<dd>endTransactionSequenceNumberInBlockChain: ' + result.block.endTransactionSequenceNumberInBlockChain + '</dd>' +
			'<dd id="trans_list">交易: ' + transList + '</dd></dl>';
	return temp;
}
//展示搜索结果(根据地址搜索未花费交易输出)
function txosByAddress(result){
	var temp = "";
	if(!result.txos || result.txos.length==0){
	} else {
        for (var i=0; i<result.txos.length; i++) {
            temp += '<dl><dd>transactionOutputHash: ' + result.txos[i].transactionOutputHash + '</dd>' +
                    '<dd>stringAddress: ' + result.txos[i].address + '</dd>' +
                    '<dd>value: ' + result.txos[i].value + '</dd>' +
                    '<dd>scriptLock: ' + JSON.stringify(result.txos[i].scriptLock)+ '</dd>' +
                    '<dd>blockHeight: ' + result.txos[i].blockHeight + '</dd>' +
                    '<dd>transactionSequenceNumberInBlock: ' + result.txos[i].transactionSequenceNumberInBlock + '</dd>' +
                    '<dd>transactionOutputSequence: ' + result.txos[i].transactionOutputSequence + '</dd></dl>';
        }
	}
	return temp;
}
function utxosByAddress(result){
	var temp = "";
	if(!result.utxos || result.utxos.length==0){
	} else {
        for (var i=0; i<result.utxos.length; i++) {
            temp += '<dl><dd>transactionOutputHash: ' + result.utxos[i].transactionOutputHash + '</dd>' +
                    '<dd>stringAddress: ' + result.utxos[i].address + '</dd>' +
                    '<dd>value: ' + result.utxos[i].value + '</dd>' +
                    '<dd>scriptLock: ' + JSON.stringify(result.utxos[i].scriptLock)+ '</dd>' +
                    '<dd>blockHeight: ' + result.utxos[i].blockHeight + '</dd>' +
                    '<dd>transactionSequenceNumberInBlock: ' + result.utxos[i].transactionSequenceNumberInBlock + '</dd>' +
                    '<dd>transactionOutputSequence: ' + result.utxos[i].transactionOutputSequence + '</dd></dl>';
        }
	}
	return temp;
}
//展示搜索结果(根据交易Hash搜索交易,根据交易Hash搜索挖矿中交易)
function transByUuid(result){
	var temp =
			'<dl><dd>timestamp: ' + result.transactionDTO.timestamp + '</dd>' +
			'<dd>transactionHash: ' + result.transactionDTO.transactionHash + '</dd>' +
			'<dd>transactionType: ' + result.transactionDTO.transactionType + '</dd>' +
			'<dd><b>inputs:</b> ' + inputs() + '</dd>' +
			'<dd><b>outputs:</b> ' + outputs() + '</dd></dl>';
	function inputs(){
		var char = "";
		for (var i=0; i<result.transactionDTO.inputs.length; i++) {
			char += '<dl class="child"><dd>unspendTransactionOutputHash: ' + result.transactionDTO.inputs[i].unspendTransactionOutputHash + '</dd>' +
                    '<dd>scriptKey: ' + JSON.stringify(result.transactionDTO.inputs[i].scriptKey)+ '</dd></dl>';
		}
		return char;
	}
	function outputs(){
		var char = "";
		for (var i=0; i<result.transactionDTO.outputs.length; i++) {
			char += '<dl class="child"><dd>transactionOutputHash: ' + result.transactionDTO.outputs[i].transactionOutputHash + '</dd>' +
					'<dd>address: ' + result.transactionDTO.outputs[i].value + '</dd>' +
					'<dd>value: ' + result.transactionDTO.outputs[i].value + '</dd>' +
					'<dd>value: ' + result.transactionDTO.outputs[i].scriptLock + '</dd></dl>';
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
				'<dd>transactionHash: ' + result.transactionDtoList[j].transactionHash + '</dd>' +
				'<dd>transactionType: ' + result.transactionDtoList[j].transactionType + '</dd>' +
				'<dd><b>inputs:</b> ' + inputs() + '</dd>' +
				'<dd><b>outputs:</b> ' + outputs() + '</dd></dl>';
		function inputs(){
			var char = "";
			for (var i=0; i<result.transactionDtoList[j].inputs.length; i++) {
				char += '<dl class="child"><dd>unspendTransactionOutputHash: ' + result.transactionDtoList[j].inputs[i].unspendTransactionOutputHash + '</dd>' +
						'<dd>publicKey: ' + result.transactionDtoList[j].inputs[i].publicKey + '</dd></dl>';
			}
			return char;
		}
		function outputs(){
			var char = "";
			for (var i=0; i<result.transactionDtoList[j].outputs.length; i++) {
				char += '<dl class="child"><dd>transactionOutputHash: ' + result.transactionDtoList[j].outputs[i].transactionOutputHash + '</dd>' +
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
				delay_load_index +=1;
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
				'<dd>transactionHash: ' + result.transactionList[j].transactionHash + '</dd>' +
				'<dd>transactionType: ' + result.transactionList[j].transactionType + '</dd>' +
				'<dd><b>inputs:</b> ' + inputs() + '</dd>' +
				'<dd><b>outputs:</b> ' + outputs() + '</dd>' +
				'<dd>transactionSequenceNumberInBlock: ' + result.transactionList[j].transactionSequenceNumberInBlock + '</dd>' +
				'<dd>transactionSequenceNumberInBlockChain: ' + result.transactionList[j].transactionSequenceNumberInBlockChain + '</dd>' +
				'<dd>blockHeight: ' + result.transactionList[j].blockHeight + '</dd></dl>';
		function inputs(){
			var char = "";
			if (result.transactionList[j].inputs !== null) {
				for (var i=0; i<result.transactionList[j].inputs.length; i++) {
					char += '<dl class="child"><dd>unspendTransactionOutputHash: ' + result.transactionList[j].inputs[i].unspendTransactionOutput.transactionOutputHash + '</dd>' +
							'<dd>stringAddress: ' + result.transactionList[j].inputs[i].unspendTransactionOutput.address + '</dd>' +
							'<dd>value: ' + result.transactionList[j].inputs[i].unspendTransactionOutput.value + '</dd>' +
							'<dd>blockHeight: ' + result.transactionList[j].inputs[i].unspendTransactionOutput.blockHeight + '</dd>' +
							'<dd>transactionSequenceNumberInBlock: ' + result.transactionList[j].inputs[i].unspendTransactionOutput.transactionSequenceNumberInBlock + '</dd>' +
							'<dd>transactionOutputSequence: ' + result.transactionList[j].inputs[i].unspendTransactionOutput.transactionOutputSequence + '</dd>' +
							'<dd>scriptLock: ' + JSON.stringify(result.transactionList[j].inputs[i].unspendTransactionOutput.scriptLock)+ '</dd>' +
							'<dd>scriptKey: ' + JSON.stringify(result.transactionList[j].inputs[i].scriptKey)+ '</dd></dl>';
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
					char += '<dl class="child"><dd>transactionOutputHash: ' + result.transactionList[j].outputs[i].transactionOutputHash + '</dd>' +
							'<dd>stringAddress: ' + result.transactionList[j].outputs[i].address + '</dd>' +
							'<dd>value: ' + result.transactionList[j].outputs[i].value + '</dd>' +
							'<dd>scriptLock: ' + JSON.stringify(result.transactionList[j].outputs[i].scriptLock) + '</dd>' +
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
				innerSearchUnit();
				break;
			case "utxos_byaddress": //根据地址搜索未花费交易输出
				address_index = address_index + 5;
				innerSearchUnit();
				break;
			case "minning_byall": //查询挖矿中的交易
				address_index = address_index + 3;
				innerSearchUnit();
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
//显示没有搜索到内容
function showSearchEmptyResult(){
	var frag = document.createElement("div");
	frag.id = "nomore";
	frag.innerHTML = "没有搜索到内容";
	search_result.appendChild(frag);
}



