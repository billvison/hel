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
		var odiv = document.createElement("ul");
		odiv.className = "output";
		odiv.innerHTML = "<li><input type=\"text\" class=\"c_txt\" placeholder=\"输出地址\"></li><li><input type=\"text\" class=\"c_txt\" placeholder=\"输出金额\"><span onclick=\"removeLi(2)\" class=\"add_btn\">删除</span></li>";
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
	// all.output = new Array();//output
	// var output_ul = document.getElementsByClassName("output");
	// for (var i=0; i<output_ul.length; i++) {
	// 	// console.log(output_ul[i]);
	// 	var output_li = output_ul[i].querySelectorAll("li");
	// 	var output_para = {};
	// 	output_para.address = output_li[0].firstChild.value;
	// 	output_para.value = output_li[1].firstChild.value;
	// 	// console.log(output_para);
	// 	all.output.push(output_para);
	// }
	// console.log(all);
	return all;
}
//获取地址和金额
function getOutput(){
	var all = {};
	all.output = new Array();//output
	var output_ul = document.getElementsByClassName("output");
	for (var i=0; i<output_ul.length; i++) {
		var output_li = output_ul[i].querySelectorAll("li");
		var output_para = {};
		output_para.address = output_li[0].firstChild.value;
		output_para.value = output_li[1].firstChild.value;
		all.output.push(output_para);
	}
	return all;
}
//提交到区块链网络
var url = "";
function submitTrans() {
	var transaction={};
    $.ajax({
        type: "post",
        url: url + "Api/BlockChain/SubmitTransaction",
        contentType: "application/json",
        data:`{
                "normalTransactionDto":{
                		"privateKey":"${getInputVal().privatekey}",
                		"inputs":["${getInputVal().uuid}"],
                		"outputs":[
                			{
                				"address":"${getOutput().output[0].address}",
                				"value":"${getOutput().output[0].value}"
                			}
                		]		
                	}
        }`,
        dataType: "json",
        async: false,
        success: function (data) {
            transaction.trans = data.result.transactionDTO.transactionUUID;
			console.log(transaction.trans);
        },
        error: function (e) {
        }
    });
    // return transaction.transactionUUID;
}