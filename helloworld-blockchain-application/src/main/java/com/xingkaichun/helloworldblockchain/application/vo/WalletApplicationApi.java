package com.xingkaichun.helloworldblockchain.application.vo;

/**
 * 钱包应用接口
 *
 * @author 邢开春 409060350@qq.com
 */
public class WalletApplicationApi {



    //生成账户(公钥、私钥、地址)
    public static final String GENERATE_ACCOUNT = "/Api/WalletApplication/GenerateAccount";
    //生成账户(公钥、私钥、地址)并保存
    public static final String GENERATE_AND_SAVE_ACCOUNT = "/Api/WalletApplication/GenerateAndSaveAccount";
    //新增账户
    public static final String ADD_ACCOUNT = "/Api/WalletApplication/AddAccount";
    //删除账户
    public static final String DELETE_ACCOUNT = "/Api/WalletApplication/DeleteAccount";
    //查询所有的账户
    public static final String QUERY_ALL_ACCOUNTS = "/Api/WalletApplication/QueryAllAccounts";



    //构建交易
    public static final String BUILD_TRANSACTION = "/Api/WalletApplication/BuildTransaction";
    //提交交易到区块链网络
    public static final String SUBMIT_TRANSACTION_TO_BLOCKCHIAIN_NEWWORK = "/Api/WalletApplication/SubmitTransactionToBlockchainNetwork";

}
