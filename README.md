这是一个Helloworld级别的区块链系统，即一个简单的数字货币系统。

它包含了四个模块。  
helloworld-blockchain-node：  
区块链的节点模块。启动区块链节点后，在浏览器输入 http://localhost:8444 进入区块链系统的控制面板，在控制面板即可从揽全局，并控制这个区块链系统。  
node节点主要有两个功能，  
一是管理员身份控制本地区块链系统，例如增删节点、激活矿工（挖矿）、停用矿工、激活同步器(同步其它节点的区块数据)。  
二是集成了区块链浏览器的功能，提交交易至区块链网络、查询未花费交易输出、查询区块等。  
区块链节点，主要负责与其它节点沟通。启动区块链节点后，自动在整个区块链网络中寻找/发布：节点、区块、交易。  
helloworld-blockchain-core：该模块是整个区块链系统的核心：实现了秘钥体系、挖矿、区块校验、同步其它节点的区块的逻辑。  
helloworld-blockchain-dto：DTO包，该包中的类都是以字段最少为设计目标，用于在区块链网络中的不同节点传输区块数据。  
helloworld-blockchain-model：区块链系统内部使用的model类，字段有冗余，但是用起来十分方便。



helloworld-blockchain-node模块的打包与发布  
打包  
cd helloworld-blockchain-node  
mvn -P package-profile -Dmaven.test.skip=true clean package install spring-boot:repackage assembly:single  
启动  
cd target  
tar -zxvf helloworld-blockchain-node-*.tar.gz  
cd HelloworldBlockchainNode  
./start.sh restart

