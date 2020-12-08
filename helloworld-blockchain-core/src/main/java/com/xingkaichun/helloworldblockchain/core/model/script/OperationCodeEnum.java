package com.xingkaichun.helloworldblockchain.core.model.script;


/**
 * 操作码枚举
 * 每一个操作码，请准确描述操作处理过程。
 * @author 邢开春 微信HelloworldBlockchain 邮箱xingkaichun@qq.com
 */
public enum OperationCodeEnum {

    /**
     * 将栈顶的元素复制一份，并放入栈。
     * OPERATION_CODE_DUPLICATE
     */
    OP_DUP(new byte[]{(byte)0x00}, "OP_DUP",0),
    /**
     * 把栈顶元素当做公钥，先做HASH256哈希，在做HASH160哈希，然后删除栈顶元素，将结果放入栈。
     * OPERATION_CODE_HASH160
     */
    OP_HASH160(new byte[]{(byte)0x01}, "OP_HASH160",0),
    /**
     * 比较栈顶的前两个元素是否相等，不等抛出异常。
     * 无论是否相等，最后从栈顶移除这两个元素。
     * OPERATION_CODE_EQUAL_VERIFY
     */
    OP_EQUALVERIFY(new byte[]{(byte)0x02}, "OP_EQUALVERIFY",0),
    /**
     * 栈顶第一个元素是公钥
     * 栈顶第二个元素是交易签名
     * 通过公钥校验交易签名是否正确。
     * 如果校验成功，先从栈中移除这两个元素，然后将true放入栈。
     * 如果校验失败，抛出异常。
     * 无论如何，栈中一定要移除这两个元素。
     * OPERATION_CODE_CHECK_SIGNATURE
     */
    OP_CHECKSIG(new byte[]{(byte)0x03},"OP_CHECKSIG",0),
    /**
     * 将下一个数据[最大1024位比特]放入栈
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
