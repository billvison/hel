package com.xingkaichun.helloworldblockchain.core;

import com.xingkaichun.helloworldblockchain.crypto.model.Account;

import java.util.List;

/**
 * 钱包
 * 管理拥有的账户（增加账户、删除账户、查询账户、获取账户等）
 * @author 邢开春 409060350@qq.com
 */
public abstract class Wallet {

    public abstract List<Account> getAllAccount();

    public abstract Account createAccount();

    public abstract Account createAndAddAccount();

    public abstract void addAccount(Account account);

    public abstract void deleteAccountByAddress(String address);
}
