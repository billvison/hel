package com.xingkaichun.helloworldblockchain.core.impl;

import com.xingkaichun.helloworldblockchain.core.Wallet;
import com.xingkaichun.helloworldblockchain.crypto.AccountUtil;
import com.xingkaichun.helloworldblockchain.crypto.ByteUtil;
import com.xingkaichun.helloworldblockchain.crypto.model.Account;
import com.xingkaichun.helloworldblockchain.util.FileUtil;
import com.xingkaichun.helloworldblockchain.util.JsonUtil;
import com.xingkaichun.helloworldblockchain.util.KvDBUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 邢开春 409060350@qq.com
 */
public class WalletImpl extends Wallet {

    private static final String WALLET_DATABASE_NAME = "WalletDatabase";
    private String walletDatabasePath = null;

    public WalletImpl(String blockchainDataPath) {
        this.walletDatabasePath = FileUtil.newPath(blockchainDataPath, WALLET_DATABASE_NAME);
    }

    @Override
    public List<Account> getAllAccount() {
        List<Account> accountList = new ArrayList<>();
        //获取所有
        List<byte[]> bytesAccountList = KvDBUtil.get(walletDatabasePath,1,100000000);
        if(bytesAccountList != null){
            for(byte[] bytesAccount:bytesAccountList){
                Account account = JsonUtil.fromJson(ByteUtil.decodeToUtf8String(bytesAccount),Account.class);
                accountList.add(account);
            }
        }
        return accountList;
    }

    @Override
    public Account createAccount() {
        Account account = AccountUtil.randomAccount();
        return account;
    }

    @Override
    public Account createAndAddAccount() {
        Account account = createAccount();
        addAccount(account);
        return account;
    }

    @Override
    public void addAccount(Account account) {
        KvDBUtil.put(walletDatabasePath,ByteUtil.encode(account.getAddress()),ByteUtil.encode(JsonUtil.toJson(account)));
    }

    @Override
    public void deleteAccountByAddress(String address) {
        KvDBUtil.delete(walletDatabasePath,ByteUtil.encode(address));
    }
}
