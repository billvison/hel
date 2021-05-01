package com.xingkaichun.helloworldblockchain.core.model.script;


/**
 * 操作码枚举
 * 每一个操作码，请准确描述操作处理过程。
 * @author 邢开春 409060350@qq.com
 */
public enum OperationCodeEnum {
    /**
     * 操作前，假设栈元素为(A B C D E)(栈顶<---栈底)
     * 若栈内少于0个元素，抛出异常。
     * 将脚本中的下一个数据A2放入栈。
     * 操作后，栈元素为(A2 A B C D E)(栈顶<---栈底)
     * 操作码全称是OPERATION_CODE_PUSH_DATA
     */
    OP_PUSHDATA(new byte[]{(byte)0x00}, "OP_PUSHDATA"),
    /**
     * 操作前，假设栈元素为(A B C D E)(栈顶<---栈底)
     * 若栈内少于1个元素，抛出异常。
     * 复制(栈顶元素A)，(复制出来的元素是A2)
     * 将A2放入栈。
     * 操作后，栈元素为(A2 A B C D E)(栈顶<---栈底)
     * 操作码全称是OPERATION_CODE_DUPLICATE
     */
    OP_DUP(new byte[]{(byte)0x01}, "OP_DUP"),
    /**
     * 操作前，假设栈元素为(A B C D E)(栈顶<---栈底)
     * 若栈内少于1个元素，抛出异常。
     * 删除栈顶元素A
     * 将元素A当做公钥求地址A2(对A先做HASH256哈希，在做HASH160哈希操作，得A2)
     * 将A2放入栈。
     * 操作后，栈元素为(A2 B C D E)(栈顶<---栈底)
     * 操作码全称是OPERATION_CODE_HASH160
     */
    OP_HASH160(new byte[]{(byte)0x02}, "OP_HASH160"),
    /**
     * 操作前，假设栈元素为(A B C D E)(栈顶<---栈底)
     * 若栈内少于2个元素，抛出异常。
     * 删除栈顶元素A B
     * 比较元素A B是否相等，不等抛出异常，中断脚本执行。
     * 操作后，栈元素为(C D E)(栈顶<---栈底)
     * 操作码全称是OPERATION_CODE_EQUAL_VERIFY
     */
    OP_EQUALVERIFY(new byte[]{(byte)0x03}, "OP_EQUALVERIFY"),
    /**
     * 操作前，假设栈元素为(A B C D E)(栈顶<---栈底)
     * 若栈内少于2个元素，抛出异常。
     * 删除栈顶元素A B
     * 元素A是公钥，元素B是交易签名，通过公钥校验交易签名是否正确。如果校验失败，抛出异常；如果校验成功，然后将true放入栈。
     * 操作后，栈元素为(true C D E)(栈顶<---栈底)
     * 操作码全称是OPERATION_CODE_CHECK_SIGNATURE
     */
    OP_CHECKSIG(new byte[]{(byte)0x04},"OP_CHECKSIG");





    OperationCodeEnum(byte[] code, String name) {
        this.code = code;
        this.name = name;
    }

    //操作码
    private byte[] code;
    //操作的名字
    private String name;

    public byte[] getCode() {
        return code;
    }
    public String getName() {
        return name;
    }
}
