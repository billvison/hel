var search_menu = document.getElementById('search_menu');
$('.byheight_t').click(function(){
	$('.search_menu').children().removeClass("on");
	$(this).addClass("on");
	$('.byheight').css('display','block');
	$('.byuuid').css('display','none');
	$('.byaddress').css('display','none');
	$('.bymining').css('display','none');
	$('.trans_byuuid').css('display','none');
});
$('.byuuid_t').click(function(){
	$('.search_menu').children().removeClass("on");
	$(this).addClass("on");
	$('.byheight').css('display','none');
	$('.byuuid').css('display','block');
	$('.byaddress').css('display','none');
	$('.bymining').css('display','none');
	$('.trans_byuuid').css('display','none');
});
$('.byaddress_t').click(function(){
	$('.search_menu').children().removeClass("on");
	$(this).addClass("on");
	$('.byheight').css('display','none');
	$('.byuuid').css('display','none');
	$('.byaddress').css('display','block');
	$('.bymining').css('display','none');
	$('.trans_byuuid').css('display','none');
});
$('.bymining_t').click(function(){
	$('.search_menu').children().removeClass("on");
	$(this).addClass("on");
	$('.byheight').css('display','none');
	$('.byuuid').css('display','none');
	$('.byaddress').css('display','none');
	$('.bymining').css('display','block');
	$('.trans_byuuid').css('display','none');
});
$('.trans_byuuid_t').click(function(){
	$('.search_menu').children().removeClass("on");
	$(this).addClass("on");
	$('.byheight').css('display','none');
	$('.byuuid').css('display','none');
	$('.byaddress').css('display','none');
	$('.bymining').css('display','none');
	$('.trans_byuuid').css('display','block');
});

var url = "";
//根据区块高度搜索
function searchByBlockHeight() {	
	var block_height = $("#block_height").val();
	if(block_height == null || block_height == ''){
	    alert("请输入区块高度");
	    return;
	}
	var ajaxResult = {};
    $.ajax({
        type: "post",
        url: url + "/Api/BlockChain/QueryBlockDtoByBlockHeight",
        contentType: "application/json",
        data: `{
			"blockHeight":"${block_height}"
		}`,
        dataType: "json",
        async: false,
        success: function (data) {
            ajaxResult = data;
        },
        error: function (e) {
        }
    });

    if(ajaxResult.serviceCode != 'SUCCESS'){
        alert(ajaxResult.message);
        return;
    }

    var block_dto = {};
    block_dto.result = ajaxResult.result.blockDTO;
    var append_result = document.getElementById("search_result");
    var oDiv = document.createElement("div");
    oDiv.className = "search_result";
    oDiv.innerHTML = "<dl>" +
    				 "<dt>搜索结果</dt>" +
    				 "<dd><b>区块高度:</b>" + block_dto.result.height + "</dd>" +
    				 "<dd><b>区块产生的时间戳:</b>" + block_dto.result.timestamp + "</dd>" +
    				 "<dd><b>上一个区块的哈希值:</b>" + block_dto.result.previousHash + "</dd>" +
					 "<dd><b>交易列表:</b>" + getTransData() + "</dd>" +
					 "<dd><b>默克尔树根:</b>" + block_dto.result.merkleRoot + "</dd>" +
					 "<dd><b>nonce:</b>" + block_dto.result.nonce + "</dd>" +
					 "<dd><b>当前区块哈希值:</b>" + block_dto.result.hash + "</dd>" +
    				 "</dl>";
	
	function getTransData(){
		var oDl = {};
		oDl.char = "";
		for (var i=0; i<block_dto.result.transactions.length; i++) {
			oDl.char += "<dd><b>时间戳: </b>" + block_dto.result.transactions[i].timestamp + "</dd>" +
						"<dd><b>交易类型: </b>" + block_dto.result.transactions[i].transactionType + "</dd>" +
						"<dd><b>交易UUID: </b>" + block_dto.result.transactions[i].transactionUUID + "</dd>" +
						"<dd><b class=\"s\">输入: </b>" +  getInputsData() + "</dd>" +
						"<dd><b class=\"s\">输出: </b>" + getOutputsData() + "</dd>" +
						"<dd><b>签名: </b>" + block_dto.result.transactions[i].signature + "</dd>";
			function getInputsData(){
				var odiv = {};
				odiv.char = "";
				for (var j=0; j<block_dto.result.transactions[i].inputs.length; j++) {
					odiv.char += "<p>未花费交易输出UUID: " + block_dto.result.transactions[i].inputs[j].unspendTransactionOutputUUID + "</p>" +
									  "<p>公钥: " + block_dto.result.transactions[i].inputs[j].publicKey + "</p>";
				}
				return odiv.char;	
			}
			function getOutputsData(){
				var odiv = {};
				odiv.char = "";
				for (var j=0; j<block_dto.result.transactions[i].outputs.length; j++) {
					odiv.char += "<p>交易输出UUID: " + block_dto.result.transactions[i].outputs[j].transactionOutputUUID + "</p>" +
									  "<p>地址: " + block_dto.result.transactions[i].outputs[j].address + "</p>" +
									  "<p>金额: " + block_dto.result.transactions[i].outputs[j].value + "</p>";
				}
				return odiv.char;	
			}
		}
		return oDl.char;
	}
	append_result.removeChild(append_result.childNodes[0]);
	append_result.appendChild(oDiv);
}

//根据UUID搜索
function searchByUuid() {	
	var trans_uuid = $("#trans_id").val();
    if(trans_uuid == null || trans_uuid == ''){
        alert("请输入交易UUID");
        return;
    }
	console.log(trans_uuid,typeof trans_uuid);
    var ajaxResult = {};
    $.ajax({
        type: "post",
        url: url + "/Api/BlockChain/QueryTransactionByTransactionUUID",
        contentType: "application/json",
        data: `{
			"transactionUUID":"${trans_uuid}"
		}`,
        dataType: "json",
        async: false,
        success: function (data) {
            ajaxResult = data;
        },
        error: function (e) {
        }
    });
    if(ajaxResult.serviceCode != 'SUCCESS'){
        alert(ajaxResult.message);
        return;
    }

    var trans_dto = {};
    trans_dto.result = ajaxResult.result.transactionDTO;
    var append_result = document.getElementById("trans_result");
    var oDiv = document.createElement("div");
    oDiv.className = "search_result";
    oDiv.innerHTML = "<dl>" +
    				 "<dt>搜索结果</dt>" +
    				 "<dd><b>时间戳:</b>" + trans_dto.result.timestamp + "</dd>" +
    				 "<dd><b>交易UUID:</b>" + trans_dto.result.transactionUUID + "</dd>" +
    				 "<dd><b>交易类型:</b>" + trans_dto.result.transactionType + "</dd>" +
    				 "<dd><b>签名:</b>" + trans_dto.result.signature + "</dd>" +
    				 "<dd><b>输入:</b>" + trans_dto.result.inputs + "</dd>" +
    				 "<dd><b>输出UUID:</b>" + trans_dto.result.outputs[0].transactionOutputUUID + "</dd>" +
					 "<dd><b>输出地址:</b>" + trans_dto.result.outputs[0].address + "</dd>" +
					 "<dd><b>输出金额:</b>" + trans_dto.result.outputs[0].value + "</dd>" +
    				 "</dl>";
	append_result.removeChild(append_result.childNodes[0]);
	append_result.appendChild(oDiv);
}
//根据地址搜索
function searchByAddress() {	
	var wallet_address = $("#v_address").val();
    if(wallet_address == null || wallet_address == ''){
        alert("请输入钱包地址");
        return;
    }
	var ajaxResult = {};
    $.ajax({
        type: "post",
        url: url + "/Api/BlockChain/QueryTxosByAddress",
        contentType: "application/json",
        data: `{
			"address":"${wallet_address}"
		}`,
        dataType: "json",
        async: false,
        success: function (data) {
            ajaxResult = data;
			console.log(wallet_address,typeof wallet_address);
			console.log(data.message);
			console.log(data.result);
        },
        error: function (e) {
        }
    });
    if(ajaxResult.serviceCode != 'SUCCESS'){
    	alert(ajaxResult.message);
    	return;
    }
    var address = {};
    address.result = ajaxResult.result;
    var address_result = document.getElementById("address_result");
    var oDiv = document.createElement("div");
	oDiv.className = "search_result";
	console.log(address.result.utxos.length);
	for(var i=0; i<address.result.utxos.length; i++){
		oDiv.innerHTML += "<dl>" +
						 "<dt>搜索结果</dt>" +
						 "<dd><b>交易ID:</b>" + address.result.utxos[i].transactionOutputUUID + "</dd>" +
						 "<dd><b>地址:</b>" + address.result.utxos[i].stringAddress.value + "</dd>" +
						 "<dd><b>金额:</b>" + address.result.utxos[i].value + "</dd>" +
						 "</dl>";
	}
    
	address_result.removeChild(address_result.childNodes[0]);
	address_result.appendChild(oDiv);
}
//查询挖矿中的交易
function queryMiningTrans() {
	var ajaxResult = {};
    $.ajax({
        type: "post",
        url: url + "/Api/BlockChain/QueryMiningTransactionList",
        contentType: "application/json",
        data:`{}`,
        dataType: "json",
        async: false,
        success: function (data) {
            ajaxResult = data;
        },
        error: function (e) {
        }
    });
    if(ajaxResult.serviceCode != 'SUCCESS'){
    	alert(ajaxResult.message);
    	return;
    }
    var trans={};
    trans.List = ajaxResult.result.transactionDtoList;
    var mining_result = document.getElementById("mining_result");
    if(trans.List == null || trans.List.length ==0){
        alert("没有查询到正在被挖矿的交易。");
        return;
    }
	for (var i=0; i<trans.List.length; i++) {
		var oDl = document.createElement("dl");
		oDl.className = "trans_list";
		oDl.innerHTML += "<dd><b>时间戳: </b>" + trans.List[i].timestamp + "</dd>" + 
						 "<dd><b>交易类型: </b>" + trans.List[i].transactionType + "</dd>" + 
						 "<dd><b>交易UUID: </b>" + trans.List[i].transactionUUID + "</dd>" +
						 "<dd><b class=\"s\">输入: </b>" +  getInputsData() + "</dd>" +
						 "<dd><b class=\"s\">输出: </b>" + getOutputsData() + "</dd>" +
						 "<dd><b>签名: </b>" + trans.List[i].signature + "</dd>";
		mining_result.appendChild(oDl);
		function getInputsData(){
			var odiv = {};
			odiv.char = "";
			for (var j=0; j<trans.List[i].inputs.length; j++) {
				odiv.char += "<p>未花费交易输出UUID: " + trans.List[i].inputs[j].unspendTransactionOutputUUID + "</p>" +
								  "<p>公钥: " + trans.List[i].inputs[j].publicKey + "</p>";
			}
			return odiv.char;
		}
		function getOutputsData(){
			var odiv = {};
			odiv.char = "";
			for (var j=0; j<trans.List[i].outputs.length; j++) {
				odiv.char += "<p>交易输出UUID: " + trans.List[i].outputs[j].transactionOutputUUID + "</p>" +
								  "<p>地址: " + trans.List[i].outputs[j].address + "</p>" +
								  "<p>金额: " + trans.List[i].outputs[j].value + "</p>";
			}
			return odiv.char;
		}
	}
}
//根据UUID查询挖矿中的交易
function queryMiningTransByUUID() {
	var querybyuuid = $("#querybyuuid").val();
    if(querybyuuid == null || querybyuuid == ''){
        alert("请输入交易UUID");
        return;
    }
	var ajaxResult = {};
    $.ajax({
        type: "post",
        url: url + "/Api/BlockChain/QueryMiningTransactionByTransactionUUID",
        contentType: "application/json",
        data:`{
			"transactionUUID":"${querybyuuid}"
		}`,
        dataType: "json",
        async: false,
        success: function (data) {
            ajaxResult = data;
			console.log(data);
        },
        error: function (e) {
        }
    });
    if(ajaxResult.serviceCode != 'SUCCESS'){
    	alert(ajaxResult.message);
    	return;
    }

    var trans={};
    trans.List = ajaxResult.result.transactionDTO;
    var trans_byuuid_result = document.getElementById("trans_byuuid_result");
	var oDl = document.createElement("dl");
	oDl.className = "trans_list";
	oDl.innerHTML += "<dd><b>时间戳: </b>" + trans.List.timestamp + "</dd>" + 
						 "<dd><b>交易类型: </b>" + trans.List.transactionType + "</dd>" + 
						 "<dd><b>交易UUID: </b>" + trans.List.transactionUUID + "</dd>" +
						 "<dd><b class=\"s\">输入: </b>" +  getInputsData() + "</dd>" +
						 "<dd><b class=\"s\">输出: </b>" + getOutputsData() + "</dd>" +
						 "<dd><b>签名: </b>" + trans.List.signature + "</dd>";
	trans_byuuid_result.appendChild(oDl);
	function getInputsData(){
			var odiv = {};
			odiv.char = "";
			for (var j=0; j<trans.List.inputs.length; j++) {
				odiv.char += "<p>未花费交易输出UUID: " + trans.List.inputs[j].unspendTransactionOutputUUID + "</p>" +
								  "<p>公钥: " + trans.List.inputs[j].publicKey + "</p>";
			}
			return odiv.char;
	}
	function getOutputsData(){
			var odiv = {};
			odiv.char = "";
			for (var j=0; j<trans.List.outputs.length; j++) {
				odiv.char += "<p>交易输出UUID: " + trans.List.outputs[j].transactionOutputUUID + "</p>" +
								  "<p>地址: " + trans.List.outputs[j].address + "</p>" +
								  "<p>金额: " + trans.List.outputs[j].value + "</p>";
			}
			return odiv.char;
	}
	
}