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
    var block_dto = {};
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
            block_dto.result = data.result.blockDTO;
        },
        error: function (e) {
        }
    });
	if(block_dto.result==null){
		alert("没有该高度");
		$("#block_height").val(" ");
	}
    var append_result = document.getElementById("search_result");
    var oDiv = document.createElement("div");
    oDiv.className = "search_result";
    oDiv.innerHTML = "<dl>" +
    				 "<dt>搜索结果</dt>" +
    				 "<dd><b>区块高度:</b>" + block_dto.result.height + "</dd>" +
    				 "<dd><b>时间戳:</b>" + block_dto.result.timestamp + "</dd>" +
    				 "<dd><b>前哈希:</b>" + block_dto.result.previousHash + "</dd>" +
					 "<dd><b>交易:</b>" + getTransData() + "</dd>" +
					 "<dd><b>merkleRoot:</b>" + block_dto.result.merkleRoot + "</dd>" +
					 "<dd><b>nonce:</b>" + block_dto.result.nonce + "</dd>" +
					 "<dd><b>后哈希:</b>" + block_dto.result.hash + "</dd>" +
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
	console.log(trans_uuid,typeof trans_uuid);
    var trans_dto = {};
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
            trans_dto.result = data.result.transactionDTO;
        },
        error: function (e) {
        }
    });
	if(trans_dto.result==null){
		alert("没有该UUID");
		$("#trans_id").val(" ");
	}
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
    var address = {};
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
            address.result = data.result;
			console.log(wallet_address,typeof wallet_address);
			console.log(data.message);
			console.log(data.result);
        },
        error: function (e) {
        }
    });
	if(address.utxos==null){
		alert("没有该地址");
		$("#address").val(" ");
	}
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
	var trans={};
    $.ajax({
        type: "post",
        url: url + "/Api/BlockChain/QueryMiningTransactionList",
        contentType: "application/json",
        data:`{}`,
        dataType: "json",
        async: false,
        success: function (data) {
			trans.List = data.result.transactionDtoList;  
        },
        error: function (e) {
        }
    });
    var mining_result = document.getElementById("mining_result");
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
	var trans={};
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
			trans.List = data.result.transactionDTO;
			// console.log(trans.List);
        },
        error: function (e) {
        }
    });
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