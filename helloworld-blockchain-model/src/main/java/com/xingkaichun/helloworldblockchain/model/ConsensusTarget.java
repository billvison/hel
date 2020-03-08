package com.xingkaichun.helloworldblockchain.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 挖矿共识目标
 */
@Data
public class ConsensusTarget implements Serializable {

    private String value;
}
