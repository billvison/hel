//生成钱包
function generateWallet(){
	var stringAccount = {};
	$.ajax({
	    type: "post",
	    url: "/Api/BlockChain/GenerateAccount",
	    contentType: "application/json",
	    data: `{}`,
	    dataType: "json",
	    async: false,
	    success: function (data) {
	        stringAccount = data.result.stringAccount;
			// console.log(wallet);
	    },
	    error: function (e) {
	    }
	});
	var getContent = '<h2 class="red">提示: 请在您信任的站点生成钱包！请您保存好自己的钱包!网站不会保存您的钱包信息，且不对您的钱包丢失负任何责任。</h2>' +
					 '<div><span>私钥:</span>'+stringAccount.stringPrivateKey.value+'</div>' +
					 '<div><span>地址:</span>'+stringAccount.stringAddress.value+'</div>';
	function blank(){
		// console.log("a");
	}
	var nextStaff = function(){
			blank();
		}
	popBox.createBox(getContent,1,nextStaff);
}


