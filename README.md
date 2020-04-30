### 欢迎来到HelloworldBlockchain  
这是一个Helloworld级别的区块链公链系统。  

### 项目意义  
探索与实践区块链技术。   

### 特色 
多链系统：Helloworld区块链系统由多条链组成。每条链完全独立自主，例如可以拥有自己的共识策略、激励策略等。多链系统非常方便探索实践各种技术。   
无限大的每秒交易数量：部署足够多的链，就能够有承载每秒无限大交易数量的能力。   
新的共识实践：多级共识   
实时交易   

### 部署展示  
http://139.9.125.122:8444/index.html  

### 模块。  
### helloworld-blockchain-node  
helloworld-blockchain-node： 它集成了四个角色的功能。启动区块链节点后(项目打包、部署在文档下方)，在浏览器输入 http://localhost:8444 进入区块链系统的前台，即可从揽全局，体验区块链浏览器、区块链节点、节点管理员、开发调试等功能。  
* 一是作为区块链浏览器的角色，对外提供了查询交易、查询区块、查询区块链网络节点、查询未花费输出等功能。  
* 二是作为区块链节点的角色，主要负责与其它节点沟通，并自动的在整个区块链网络中寻找/发布：节点、区块、交易。  
* 三是作为节点管理员的角色，为用户提供管理本地节点的功能，例如增删节点、激活矿工（挖矿）、停用矿工、激活同步器(同步其它节点的区块数据)。  
* 四是开发调试角色，为开发人员提供了十分便利的调试功能。  

### helloworld-blockchain-core  
该模块是整个区块链系统的核心，它代表着一条区块链，且对外提供了大量的接口。为了精简，设计之初，它就被设计为不含有网络模块。除了网络模块，它含有一个区块链系统应有的功能，并在底层维护着一条区块链的区块数据，对外提供的功能包含1.秘钥生成 2.挖矿 3.交易验证 4.区块验证 4.分叉处理 5.区块回滚 6.新增区块 7.区块查询 8.交易查询 等等。

### helloworld-blockchain-crypto  
封装加密相关的类。1.生成私钥、公钥、地址 2.签名与签名校验 3.消息摘要 4.base58编码 等等。

### helloworld-blockchain-node-transport-dto  
该包用于存放【节点之间数据传输使用的】dto类，不存在任何业务逻辑。该包中的dto类以字段精简【节约节点之间数据的传输流量】、类型简单【方便多种编程语言转换】为设计目标。 

### 打包与发布  
使用maven打包有两个步骤：一是进入目录，二是运行打包命令。详细命令如下:  
```  
cd helloworld-blockchain-node  
mvn -P package-profile -Dmaven.test.skip=true clean package install spring-boot:repackage assembly:single  
```  
发布项目有两个步骤：一是进入打包结果目录，二是解压，三是进入解压文件目录，四是运行启动脚本  
```  
cd target  
tar -zxvf helloworld-blockchain-node-*.tar.gz  
cd HelloworldBlockchainNode  
./start.sh restart  
```  
