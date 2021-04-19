package com.xingkaichun.helloworldblockchain.explorer.vo.account;

public class QueryAccountDetailByAddressResponse {

    private String address;
    private String balance;
    private String receipt;
    private String spent;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getReceipt() {
        return receipt;
    }

    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }

    public String getSpent() {
        return spent;
    }

    public void setSpent(String spent) {
        this.spent = spent;
    }
}
