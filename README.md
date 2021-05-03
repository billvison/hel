### 欢迎来到HelloworldBlockchain
HelloworldBlockchain项目架构清晰，文档齐全，中文注释，可读性高，精简易学，为初学者学习研究区块链技术而生。  
HelloworldBlockchain是一个Helloworld级别的区块链公链系统。  
HelloworldBlockchain是一个Helloworld级别的数字货币项目。  
HelloworldBlockchain开发调试简单，下载源码，导入idea(eclipse)，无需任何配置，找到启动类文件com.xingkaichun.helloworldblockchain.explorer.HelloWorldBlockchainExplorerApplication，右键运行，即可启动项目，然后打开浏览器，访问项目的前端地址 http://localhost/ ，快乐的调试玩耍吧。  



### 关于我(邢开春)
作者微信号xing_kai_chun



### 项目意义  
初学者实践与探索区块链技术。



### 演示地址
http://119.3.57.171/



### 数字货币初体验(在演示网站操作，无需下载、安装)
[1.如何拥有一个数字货币账户？](https://zhuanlan.zhihu.com/p/352458209)  
[2.有了账户，如何查看该账户的余额？](https://zhuanlan.zhihu.com/p/352458209)  
[3.有了账户，怎么获取数字货币？](https://zhuanlan.zhihu.com/p/352458209)  
[4.有了账户，账户上也有钱了，怎么进行交易？](https://zhuanlan.zhihu.com/p/352458209)  
[5.怎么挖矿赚数字货币？](https://zhuanlan.zhihu.com/p/352458209)



### 数字货币中体验(下载安装包，在自己机器上操作)(单机版本玩法：独乐乐)
[单机版玩法](https://blog.csdn.net/xingkaichun/article/details/116377603)



### 数字货币中体验(下载安装包，在自己机器上操作)(网络版本玩法：众乐乐。加入Helloworld网络，与大家一起玩)
[网络版玩法](https://blog.csdn.net/xingkaichun/article/details/116377734)



### 数字货币高体验(下载源码，导入集成开发工具，我的地盘我做主)
如果真的想要学习区块链，怎么能不下载源码玩耍呢？



### 数字货币超神体验(在github上提交issue、pull)
在github上提交issue、pull，与社区一起学习区块链技术。



### 技术文档
[必读--白皮书](https://www.zhihu.com/question/51047975/answer/1778438713)  
[必读--数据结构](https://zhuanlan.zhihu.com/p/332265582)  
[钱包:公钥、私钥、地址](https://zhuanlan.zhihu.com/p/38196092)  
[哈希运算](https://zhuanlan.zhihu.com/p/354442546)  
[双哈希运算](https://zhuanlan.zhihu.com/p/353575311)  
[区块哈希](https://zhuanlan.zhihu.com/p/353570191)  
[交易哈希](https://zhuanlan.zhihu.com/p/353574892)  
[默克尔树](https://zhuanlan.zhihu.com/p/40142647)  
[区块大小、交易大小](https://zhuanlan.zhihu.com/p/336827577)  
[脚本](https://zhuanlan.zhihu.com/p/353582574)  
[UTXO未花费交易输出](https://www.zhihu.com/question/59913301/answer/1779203932)  
[杂谈--双花攻击与51%攻击](https://zhuanlan.zhihu.com/p/258952892)



### 代码规范
[代码规范](https://github.com/xingkaichun/HelloworldBlockchain/blob/master/code-specification.md)



### 产品设计
[产品设计](https://github.com/xingkaichun/HelloworldBlockchain/blob/master/helloworldcoin-design.md)



### 集成开发环境搭建
#### eclipse
HelloworldBlockchain项目没有任何复杂依赖，将项目导入eclipse，找到类文件com.xingkaichun.helloworldblockchain.explorer.HelloWorldBlockchainExplorerApplication，右键运行，正常情况下则会启动成功。接下来，打开浏览器，访问项目的前端地址 http://localhost/ ，快乐的调试玩耍吧。
#### intellij idea
HelloworldBlockchain项目没有任何复杂依赖，将项目导入idea，找到类文件com.xingkaichun.helloworldblockchain.explorer.HelloWorldBlockchainExplorerApplication，右键运行，正常情况下则会启动成功。接下来，打开浏览器，访问项目的前端地址 http://localhost/ ，快乐的调试玩耍吧。



### 模块架构
#### helloworld-blockchain-explorer
启动后，在浏览器输入地址 http://localhost/ 进入区块链系统的前台。他对外提供三种角色的功能。
* 一是作为区块链浏览器的角色，对外提供了查询交易、查询区块、查询区块链网络节点、查询地址未花费输出等功能。
* 二是作为区块链数字货币钱包的角色，对用户提供查询资产，转账交易等功能。
* 三是作为管理员的角色，为用户提供管理本地节点的功能，例如增/删/改/查网络节点、激活/停用矿工、激活/停用同步器。
#### helloworld-blockchain-crypto
该模块封装了加密相关的工具。1.数字货币账户工具：①账户(私钥、公钥、公钥哈希、地址)生成工具。②私钥、公钥、公钥哈希、地址的相互转换工具。2.数字签名与签名验证。3.消息摘要：SHA-256、RipeMD160。4.字节编码方案：base58、hex。5.数据结构：默克尔树工具等。
#### helloworld-blockchain-util
该模块存放封装的开发工具类
#### helloworld-blockchain-setting
该模块存放全局配置
#### helloworld-blockchain-core
该模块是整个区块链系统的核心，它代表着一个单机版区块链系统，它在底层维护着一条区块链的完整数据。设计之初，为了精简，它被设计为不含有网络模块。除了不含有网络模块外，它包含了一个区块链系统应有的所有功能，包含1.区块链账户生成 2.转账 3.提交交易至区块链 4.挖矿 5.新增区块到区块链 6.数据校验：区块验证、交易验证  7.链上区块回滚 8.链上区块查询、交易查询、账户资金查询...... 等等。
#### helloworld-blockchain-netcore
网络版区块链系统实现。底层依赖helloworld-blockchain-core模块。在单机版区块链系统的基础上，1.新增了网络功能：自动的在整个区块链网络中寻找/发布：节点、区块、交易。2.分叉处理。
#### helloworld-blockchain-netcore-client
节点交互网络客户端，不同节点仅通过该客户端即可完成节点间的数据交互操作。设计上，两个节点间的数据交互的所有操作都定义在了该客户端之中。原则上，少定义两个节点间的数据交互操作行为，不定义非必要操作行为。
#### helloworld-blockchain-node-dto
该模块用于存放【节点之间数据传输使用的】dto类，不存在任何业务逻辑。该包中的dto类以字段精简【节约节点之间数据的传输流量】、类型简单【方便多种编程语言转换】为设计目标。 




### 打包与发布
#### 项目打包
```  
进入项目目录(请根据本地的实际项目目录运行命令)  
cd C:\Users\xingkaichun\IdeaProjects\HelloworldBlockchain   
运行项目打包命令   
mvn -DskipTests=true clean package
```
#### 发布项目
```  
进入打包结果目录(请根据本地的实际项目目录运行命令)  
cd C:\Users\xingkaichun\IdeaProjects\HelloworldBlockchain\helloworld-blockchain-explorer\target  
运行启动命令  
java -jar helloworld-blockchain-explorer-1.0-SNAPSHOT.jar  
```