package com.xingkaichun.helloworldblockchain.core.model.transaction;

import org.junit.Assert;
import org.junit.Test;

public class TransactionTypeTest {

    @Test
    public void transactionTypeCheckTest()
    {
        Assert.assertEquals(2,TransactionType.values().length);
    }
}
