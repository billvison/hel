package com.xingkaichun.helloworldblockchain.node.service;

import com.xingkaichun.helloworldblockchain.core.BlockChainCore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
@Service
public class AdminConsoleServiceImpl implements AdminConsoleService {

    @Autowired
    private BlockChainCore blockChainCore;

    @Autowired
    private NodeService nodeService;


    @Override
    public boolean isMinerActive() {
        return blockChainCore.getMiner().isActive();
    }

    @Override
    public void activeMiner() throws Exception {
        blockChainCore.getMiner().active();
    }

    @Override
    public void deactiveMiner() throws Exception {
        blockChainCore.getMiner().deactive();
    }

    public boolean isSynchronizerActive() {
        return blockChainCore.getSynchronizer().isActive();
    }

    public boolean deactiveSynchronizer() {
        blockChainCore.getSynchronizer().deactive();
        return true;
    }

    public boolean activeSynchronizer() {
        blockChainCore.getSynchronizer().active();
        return true;
    }
}
