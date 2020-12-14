package com.xingkaichun.helloworldblockchain.core.model.script;


/**
 * 操作码枚举
 * 每一个操作码，请准确描述操作处理过程。
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public enum OperationCodeEnum {

    /**
     * 操作前，栈元素为(A B C D E)(栈顶<---栈底)
     * 复制(栈顶元素A)，(复制出来的元素是A2)
     * 将A2放入栈。
     * 操作后，栈元素为(A2 A B C D E)(栈顶<---栈底)
     * OPERATION_CODE_DUPLICATE
     */
    OP_DUP(new byte[]{(byte)0x00}, "OP_DUP",0),
    /**
     * 操作前，栈元素为(A B C D E)(栈顶<---栈底)
     * 删除栈顶元素A
     * 将元素A当做公钥求地址A2(对A先做HASH256哈希，在做HASH160哈希操作，得A2)
     * 将A2放入栈。
     * 操作后，栈元素为(A2 B C D E)(栈顶<---栈底)
     * OPERATION_CODE_HASH160
     */
    OP_HASH160(new byte[]{(byte)0x01}, "OP_HASH160",0),
    /**
     * 操作前，栈元素为(A B C D E)(栈顶<---栈底)
     * 删除栈顶元素A B
     * 比较元素A B是否相等，不等抛出异常，中断脚本执行。
     * 操作后，栈元素为(C D E)(栈顶<---栈底)
     * OPERATION_CODE_EQUAL_VERIFY
     */
    OP_EQUALVERIFY(new byte[]{(byte)0x02}, "OP_EQUALVERIFY",0),
    /**
     * 操作前，栈元素为(A B C D E)(栈顶<---栈底)
     * 删除栈顶元素A B
     * 元素A是公钥，元素B是交易签名，通过公钥校验交易签名是否正确。
     * 如果校验失败，抛出异常；如果校验成功，然后将true放入栈。
     * 操作后，栈元素为(true C D E)(栈顶<---栈底)
     * OPERATION_CODE_CHECK_SIGNATURE
     */
    OP_CHECKSIG(new byte[]{(byte)0x03},"OP_CHECKSIG",0),
    /**
     * 操作前，栈元素为(A B C D E)(栈顶<---栈底)
     * 将脚本中的下一个数据[最大1024字符]A2放入栈，如果下一个数据的长度大于1024字符，抛出异常。
     * 操作后，栈元素为(A2 A B C D E)(栈顶<---栈底)
     * OPERATION_CODE_PUSH_DATA
     */
    OP_PUSHDATA1024(new byte[]{(byte)0x04}, "OP_PUSHDATA1024",1024);




    OperationCodeEnum(byte[] code, String name,long size) {
        this.code = code;
        this.name = name;
        this.size = size;
    }

    //操作码
    private byte[] code;
    //操作的名字
    private String name;
    //如果操作码后跟着操作数，size代表操作数的位数限制
    private long size;

    public byte[] getCode() {
        return code;
    }
    public String getName() {
        return name;
    }
    public long getSize() {
        return size;
    }
}
