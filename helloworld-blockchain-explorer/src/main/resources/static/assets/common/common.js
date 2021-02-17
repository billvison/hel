async function $ajax(option){
    const obj = {
        type: "post",
        contentType: "application/json",
        dataType: "json",
        data: `{}`,
        async: false,
    }
    Object.assign(obj , option)
    return new Promise ((resolve, rejact) => {
        $.ajax({
            ...obj,
            success(data){
                resolve(data)
            },
            error(err){
                rejact(err)
            }
        })
    })
};

function transactionHtml(item){
    let left = '';
    item.transactionInputViewList.forEach(item1 => {
        left += `<div>付：<span><a title="地址，点击查看地址详情。" target="_blank" href="./address.html?address=${item1.address}">${item1.address}</a></span>&nbsp;<a title="交易输出，点击查看交易输出详情。" target="_blank" href="./transactionoutput.html?transactionHash=${item1.transactionHash}&transactionOutputIndex=${item1.transactionOutputIndex}"><i class="glyphicon-euro"></i></a>&nbsp;<span>${item1.value}</span></div>`
    })
    let right = ''
    item.transactionOutputViewList.forEach(item1 => {
        right += `<div style="display:flex">收：<span><a title="地址，点击查看地址详情。" target="_blank" href="./address.html?address=${item1.address}">${item1.address}</a></span>&nbsp;<a title="交易输出，点击查看交易输出详情。" target="_blank" href="./transactionoutput.html?transactionHash=${item1.transactionHash}&transactionOutputIndex=${item1.transactionOutputIndex}"><i class="glyphicon-euro"></i></a>&nbsp;<span>${item1.value}</span></div>`
    });
    let transactionHtml = `
        <div class="transaction">
            <!-- 开头 -->
            <div class="head">
                <div class="min-width-40"><a title="交易哈希，点击查看交易详情。" target="_blank" href="./transaction.html?transactionHash=${item.transactionHash}">${item.transactionHash}</a></div>
                <div class="min-width-20 text-align-center">${item.transactionType}</div>
                <div class="min-width-20 text-align-center">${item.transactionFee}</div>
                <div class="min-width-20 text-align-center">${item.blockTime}</div>
            </div>
            <div class="body">
                <div style="width:auto; min-width:50%;">${left}</div>
                <div style="width:auto; min-width:40%;">${right}</div>
            </div>
        </div>
    `;
    return transactionHtml;
};
