package com.xingkaichun.helloworldblockchain.node.dto.adminconsole.response;

import com.xingkaichun.helloworldblockchain.node.dto.nodeserver.Node;
import lombok.Data;

import java.util.List;

/**
 *
 * @author 邢开春 xingkaichun@qq.com
 */
@Data
public class QueryNodeListResponse {

    private List<Node> nodeList;
}
