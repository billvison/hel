package com.xingkaichun.helloworldblockchain.node.service;



public interface ConfigurationService {


    String getMinerAddress();

    void writeMinerAddress(String address);

    boolean autoSearchNode();

    void writeAutoSearchNode(boolean autoSearchNewNode);
}
