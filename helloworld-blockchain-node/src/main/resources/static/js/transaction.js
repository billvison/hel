//
//新增输入框
function addText(para){
	var cur_btn = event.srcElement ? event.srcElement : event.target;
	var par = cur_btn.parentNode.parentNode;
	if(para == 1){
		var odiv = document.createElement("li");
		odiv.innerHTML = "<input type=\"text\" class=\"c_txt\" name=\"uuid\" placeholder=\"请输入交易输出的UUID\"><span onclick=\"removeLi(1)\" class=\"add_btn\">删除</span>";
		par.appendChild(odiv);
	}else if(para ==2){
		var odiv = document.createElement("dl");
		odiv.className = "output";
		odiv.innerHTML = "<dd><input type=\"text\" class=\"c_txt\" placeholder=\"输出地址\"></dd><dd><input type=\"text\" class=\"c_txt\" placeholder=\"输出金额\"><span onclick=\"removeLi(2)\" class=\"add_btn\">删除</span></dd>";
		par.parentNode.appendChild(odiv);
	}	
}
//删除新增
function removeLi(para){
	var cur_btn = event.srcElement ? event.srcElement : event.target;
	if(para ==1){
		var par = cur_btn.parentNode;
	}else if(para ==2){
		var par = cur_btn.parentNode.parentNode;
	}	
	par.parentNode.removeChild(par);
}
//获取输入
function getInputVal(){
	var all = {};
	all.privatekey = $("#input_val input[name=privatekey]").val();//私钥
	all.uuid = new Array();//input uuid
	var uuid_ul = document.getElementById("uuid");
	var uuid_li = uuid_ul.querySelectorAll("li");
	for(var i=0; i<uuid_li.length;i++){
		all.uuid.push(uuid_li[i].firstChild.value); 
	}
	all.output = new Array();//output
	var output_div = document.getElementById("output");
	var output_dl = output_div.querySelectorAll("dl");
	all.char = "";
	for (var i=0; i<output_dl.length; i++) {
		var output_dd = output_dl[i].querySelectorAll("dd");
		all.char += '{\"address\"' + ':\"' + output_dd[0].firstChild.value + '\",' +
			   '\"value\"' + ':\"' + output_dd[1].firstChild.value + '\"},';
		
	}
	all.char = all.char.substring(0, all.char.length - 1);
	// console.log(all.char);
	return all;
}

//提交到区块链网络
var url = "";
function submitTrans() {
    $.ajax({
        type: "post",
        url: url + "/Api/BlockChain/SubmitTransaction",
        contentType: "application/json",
        data:`{
                "normalTransactionDto":{
                		"privateKey":"${getInputVal().privatekey}",
                		"inputs":["${getInputVal().uuid}"],
                		"outputs":[${getInputVal().char}]		
                	}
        }`,
        dataType: "json",
        async: false,
        success: function (data) {
            if(data.serviceCode != 'SUCCESS'){
                alert(data.message);
                return;
            }
            var transaction={};
            transaction.trans = data.result.transactionDTO.transactionUUID;
            alert("提交成功,交易UUID为:" + transaction.trans);
        },
        error: function (e) {
        }
    });
}