//生成钱包
function generateWallet(){
	var wallet = {};
	$.ajax({
	    type: "post",
	    url: "/Api/BlockChain/GenerateWalletDTO",
	    contentType: "application/json",
	    data: `{}`,
	    dataType: "json",
	    async: false,
	    success: function (data) {
	        wallet = data.result.walletDTO;
			// console.log(wallet);
	    },
	    error: function (e) {
	    }
	});
	var getContent = '<h2 class="red">提示: 钱包一旦生成网站不会保存, 请用户自己复制并保存!</h2>' +
					 '<div><span>私钥:</span>'+wallet.privateKey+'</div>' +
					 '<div><span>公钥:</span>'+wallet.publicKey+'</div>' +
					 '<div><span>地址:</span>'+wallet.address+'</div>';
	function blank(){
		// console.log("a");
	}
	var nextStaff = function(){
			blank();
		}
	popBox.createBox(getContent,1,nextStaff);
}


