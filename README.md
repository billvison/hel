### 欢迎来到HelloworldBlockchain  
这是一个Helloworld级别的区块链公链系统。  

### 项目意义  
探索与实践区块链技术。   

### 特色 
多链系统：Helloworld区块链系统由多条链组成。每条链完全独立自主，例如可以拥有自己的共识策略、激励策略等。由于每条链完全独立，因此非常适合探索实践各种区块链技术。   
无限大的每秒交易数量：部署足够多的链，就能够有承载每秒无限大交易数量的能力。   
支持跨链交易  
实时交易(已有想法，暂未代码实现)

### 币分配与关于创始人的奖励 
Helloworld币总共发行100 0000 0000枚。由于HelloworldBlockchain是一个多链系统，因此多条链的币加在一起是100 0000 0000枚。矿工与社区共同获得80%，用于挖矿激励、完善与推广系统。创始人从构思并编码实现花费了近两年，因此创始人依据创始人的劳苦功高，将总量的20%分配给了创始人。依据创始人的消耗价值估值约为100万RMB(高估了)。依此，100万RMB=20 0000 0000枚币，项目初始价值约为1RMB=2000枚币。我认为激励应当分配给能够促进技术进步、项目发展的人与机构。因此未来挖矿只会获得极少一部分激励。更多的分配方案在于对完善代码的技术人员奖励与项目推广的消耗。 

### 一些共识，如果您打算加入HelloworldBlockchain社区,意味着您已经同意了以下共识。      
HelloworldBlockchain是一个多链系统。每条链完全独立，可以拥有自己的激励政策、游戏规则等。  
为什么要设计成多链系统？  
单链意味着 每秒交易笔数受到普通节点的机器的限制。  

为什么要设计成每条链完全独立？  
完全独立意味着 链与链互不干扰，一条链不用去关心另一条链的状态。  
完全独立意味着 您可以新开一条链去探索实践您的想法。  

多链如何有机的统一成一个区块链系统？链与链如何交互？各个链的币的总量加在一起如何保证是100 0000 0000枚？如何跨链交易？  
多链的统一直接交于社区来实现。例如跨链，A链转账给B链，社区可以共识出一个时间点，硬编码B链，将A链的转账交于B链的账户。  

### 部署展示  
http://139.9.125.122:8444/index.html  
![HellowordlBlockchain首页](https://vi1.xiu123.cn/live/2020/05/01/08/1003v1588292968004573842.jpg)  

### 模块组织  
#### helloworld-blockchain-node  
helloworld-blockchain-node： 它集成了四个角色的功能。启动区块链节点后(项目打包、部署在文档下方)，在浏览器输入 http://localhost:8444 进入区块链系统的前台，即可从揽全局，体验区块链浏览器、区块链节点、节点管理员、开发调试等功能。  
* 一是作为区块链浏览器的角色，对外提供了查询交易、查询区块、查询区块链网络节点、查询未花费输出等功能。  
* 二是作为区块链节点的角色，主要负责与其它节点沟通，并自动的在整个区块链网络中寻找/发布：节点、区块、交易。  
* 三是作为节点管理员的角色，为用户提供管理本地节点的功能，例如增删节点、激活矿工（挖矿）、停用矿工、激活同步器(同步其它节点的区块数据)。  
* 四是开发调试角色，为开发人员提供了十分便利的调试功能。  

#### helloworld-blockchain-core  
该模块是整个区块链系统的核心，它代表着一条区块链，且对外提供了大量的接口。为了精简，设计之初，它就被设计为不含有网络模块。除了网络模块，它含有一个区块链系统应有的功能，并在底层维护着一条区块链的区块数据，对外提供的功能包含1.秘钥生成 2.挖矿 3.交易验证 4.区块验证 4.分叉处理 5.区块回滚 6.新增区块 7.区块查询 8.交易查询 等等。

#### helloworld-blockchain-crypto  
封装加密相关的类。1.数字货币账户工具：生成(椭圆曲线加密)私钥、公钥、地址。 2.数字签名与签名校验。 3.消息摘要：SHA-256、RipeMD160。 4.字节编码方案：base64、base58。 等等。

#### helloworld-blockchain-node-transport-dto  
该包用于存放【节点之间数据传输使用的】dto类，不存在任何业务逻辑。该包中的dto类以字段精简【节约节点之间数据的传输流量】、类型简单【方便多种编程语言转换】为设计目标。 

### 集成开发环境搭建 
#### eclipse  
HelloworldBlockchain项目没有任何复杂依赖，请搜索eclipse如何导入已存在的maven项目。 
成功将项目导入eclipse后，找到类文件com.xingkaichun.helloworldblockchain.node.HelloWorldBlockChainNodeApplication，右键运行，正常情况下则会启动成功。接下来，快乐的调试代码吧！项目的前端地址是：http://localhost:8444 。 

#### intellij idea   
HelloworldBlockchain项目没有任何复杂依赖，请搜索idea如何导入已存在的maven项目。 
成功将项目导入idea后，找到类文件com.xingkaichun.helloworldblockchain.node.HelloWorldBlockChainNodeApplication，右键运行，正常情况下则会启动成功。接下来，快乐的调试代码吧！项目的前端地址是：http://localhost:8444 。 

### 打包与发布  
使用maven打包有两个步骤：一是进入目录，二是运行打包命令。详细命令如下:  
```  
cd helloworld-blockchain-node  
mvn -P package-profile -Dmaven.test.skip=true clean package install spring-boot:repackage assembly:single  
```  
发布项目有四个步骤：一是进入打包结果目录，二是解压，三是进入解压文件目录，四是运行启动脚本  
```  
cd target  
tar -zxvf helloworld-blockchain-node-*.tar.gz  
cd HelloworldBlockchainNode  
./start.sh restart  
```  
