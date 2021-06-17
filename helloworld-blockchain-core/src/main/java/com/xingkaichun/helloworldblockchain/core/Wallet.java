package com.xingkaichun.helloworldblockchain.core;

import com.xingkaichun.helloworldblockchain.core.model.wallet.BuildTransactionRequest;
import com.xingkaichun.helloworldblockchain.core.model.wallet.BuildTransactionResponse;
import com.xingkaichun.helloworldblockchain.crypto.model.Account;

import java.util.List;

/**
 * 钱包
 * 管理拥有的账户（增加账户、删除账户、查询账户、获取账户等）
 *
 * @author 邢开春 409060350@qq.com
 */
public abstract class Wallet {

    //钱包所在的区块链
    protected BlockchainDatabase blockchainDatabase;

    public abstract List<Account> getAllAccounts();

    public abstract Account createAccount();

    public abstract Account createAndSaveAccount();

    public abstract void saveAccount(Account account);

    public abstract void deleteAccountByAddress(String address);

    /**
     * 获取地址余额
     */
    public abstract long getBalanceByAddress(String address);

    /**
     * 构建交易。
     */
    public abstract BuildTransactionResponse buildTransaction(BuildTransactionRequest request) ;
}
