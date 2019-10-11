package com.xingkaichun.blockchain.core.impl;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class LightweightBlockChain {
    private List<String> addUtxoList = new ArrayList<>();
    private List<String> deleteUtxoList = new ArrayList<>();
}