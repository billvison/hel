package com.xingkaichun.helloworldblockchain.application.vo.account;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class QueryAllAccountsResponse {

    private long balance;
    private List<AccountVo> accounts;


    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public List<AccountVo> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<AccountVo> accounts) {
        this.accounts = accounts;
    }

    public static class AccountVo implements Serializable {

        private String privateKey;
        private String address;
        private long value;

        public AccountVo(String privateKey, String address, long value) {
            this.privateKey = privateKey;
            this.address = address;
            this.value = value;
        }

        public AccountVo() {
        }

        public String getPrivateKey() {
            return privateKey;
        }

        public void setPrivateKey(String privateKey) {
            this.privateKey = privateKey;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public long getValue() {
            return value;
        }

        public void setValue(long value) {
            this.value = value;
        }
    }

}
