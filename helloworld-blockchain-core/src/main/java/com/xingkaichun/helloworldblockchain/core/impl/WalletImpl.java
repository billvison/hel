package com.xingkaichun.helloworldblockchain.core.impl;

import com.xingkaichun.helloworldblockchain.core.CoreConfiguration;
import com.xingkaichun.helloworldblockchain.core.Wallet;
import com.xingkaichun.helloworldblockchain.crypto.AccountUtil;
import com.xingkaichun.helloworldblockchain.crypto.ByteUtil;
import com.xingkaichun.helloworldblockchain.crypto.model.Account;
import com.xingkaichun.helloworldblockchain.util.FileUtil;
import com.xingkaichun.helloworldblockchain.util.JsonUtil;
import com.xingkaichun.helloworldblockchain.util.KvDbUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 默认实现
 *
 * @author 邢开春 409060350@qq.com
 */
public class WalletImpl extends Wallet {

    private static final String WALLET_DATABASE_NAME = "WalletDatabase";
    private final String walletDatabasePath;

    public WalletImpl(CoreConfiguration coreConfiguration) {
        this.walletDatabasePath = FileUtil.newPath(coreConfiguration.getCorePath(), WALLET_DATABASE_NAME);
    }

    @Override
    public List<Account> getAllAccounts() {
        List<Account> accountList = new ArrayList<>();
        //获取所有
        List<byte[]> bytesAccountList = KvDbUtil.get(walletDatabasePath,1,100000000);
        if(bytesAccountList != null){
            for(byte[] bytesAccount:bytesAccountList){
                Account account = JsonUtil.fromJson(ByteUtil.utf8BytesToString(bytesAccount),Account.class);
                accountList.add(account);
            }
        }
        return accountList;
    }

    @Override
    public Account createAccount() {
        return AccountUtil.randomAccount();
    }

    @Override
    public Account createAndSaveAccount() {
        Account account = createAccount();
        saveAccount(account);
        return account;
    }

    @Override
    public void saveAccount(Account account) {
        KvDbUtil.put(walletDatabasePath,ByteUtil.stringToUtf8Bytes(account.getAddress()),ByteUtil.stringToUtf8Bytes(JsonUtil.toJson(account)));
    }

    @Override
    public void deleteAccountByAddress(String address) {
        KvDbUtil.delete(walletDatabasePath,ByteUtil.stringToUtf8Bytes(address));
    }
}
