### 欢迎来到HelloworldBlockchain
HelloworldBlockchain项目架构清晰，文档齐全，中文注释，可读性高，为区块链初学者学习研究而生。  
HelloworldBlockchain是一个Helloworld级别的区块链公链系统。  
HelloworldBlockchain是一个Helloworld级别的数字货币项目。  
HelloworldBlockchain开发调试简单，下载源码，导入idea(eclipse)，无需任何配置，找到启动类文件com.xingkaichun.helloworldblockchain.explorer.HelloWorldBlockchainExplorerApplication，右键运行，即可启动项目。  



### 联系方式
作者微信号xing_kai_chun



### HelloworldBlockchain项目意义  
初学者探索与实践区块链技术。



### 演示
http://119.3.57.171/



### 帮助手册
[1.如何创建数字货币账户？](https://zhuanlan.zhihu.com/p/352458209)  
[2.有了账户，如何查看该账户余额？](https://zhuanlan.zhihu.com/p/352458209)  
[3.免费领取数字货币，以供体验测试数字货币。](https://zhuanlan.zhihu.com/p/352458209)  
[4.用领取的数字货币，进行实际的交易，体验使用数字货币。](https://zhuanlan.zhihu.com/p/352458209)  
[5.怎么挖矿](https://zhuanlan.zhihu.com/p/352458209)



### 技术文档
[区块大小、交易大小](https://zhuanlan.zhihu.com/p/336827577)  
[交易序列化](https://zhuanlan.zhihu.com/p/353323689)


### 集成开发环境搭建
#### eclipse
HelloworldBlockchain项目没有任何复杂依赖，请搜索eclipse如何导入已存在的maven项目。
成功将项目导入eclipse后，找到类文件com.xingkaichun.helloworldblockchain.explorer.HelloWorldBlockchainExplorerApplication，右键运行，正常情况下则会启动成功。接下来，快乐的调试代码吧！项目的前端地址是：http://localhost/ 。
#### intellij idea
HelloworldBlockchain项目没有任何复杂依赖，请搜索idea如何导入已存在的maven项目。
成功将项目导入idea后，找到类文件com.xingkaichun.helloworldblockchain.explorer.HelloWorldBlockchainExplorerApplication，右键运行，正常情况下则会启动成功。接下来，快乐的调试代码吧！项目的前端地址是：http://localhost/ 。



### 模块架构
#### helloworld-blockchain-crypto
该模块封装了加密相关的工具。1.数字货币账户工具：①账户(私钥、公钥、地址)生成工具。②私钥、公钥、地址的相互转换工具。2.数字签名与签名验证。3.消息摘要：SHA-256、RipeMD160。4.字节编码方案：base58、hex。5.数据结构：默克尔树工具。
#### helloworld-blockchain-util
该模块存放封装的开发工具类
#### helloworld-blockchain-setting
该模块存放全局配置
#### helloworld-blockchain-core
该模块是整个区块链系统的核心，它代表着一个单机版区块链系统，它在底层维护着一条区块链的完整数据。设计之初，为了精简，它就被设计为不含有网络模块。除了网络模块，它含有一个区块链系统应有的功能，包含1.区块链账户生成 2.挖矿 3.新增区块到区块链 4.新增交易，交易将会被矿工挖矿使用 5.数据校验：区块验证、交易验证 6.分叉处理 7.链上区块回滚 8.链上区块查询、交易查询、账户资金查询...... 等等。
#### helloworld-blockchain-netcore
网络版区块链系统实现。底层依赖helloworld-blockchain-core模块。在单机版区块链系统的基础上，新增了网络功能：自动的在整个区块链网络中寻找/发布：节点、区块、交易。
#### helloworld-blockchain-node-dto
该模块用于存放【节点之间数据传输使用的】dto类，不存在任何业务逻辑。该包中的dto类以字段精简【节约节点之间数据的传输流量】、类型简单【方便多种编程语言转换】为设计目标。 
#### helloworld-blockchain-explorer
启动后，在浏览器输入入口地址 http://localhost/ 进入区块链系统的前台。他对外提供两种功能。
* 一是作为区块链浏览器的角色，对外提供了查询交易、查询区块、查询区块链网络节点、查询地址未花费输出等功能。
* 二是作为管理员的角色，为用户提供管理本地节点的功能，例如增/删/改网络节点、激活/停用矿工、激活/停用同步器。



### 打包与发布
#### 项目打包
```  
进入项目目录(请根据本地的实际项目目录运行命令)  
cd C:\Users\xingkaichun\IdeaProjects\HelloworldBlockchain   
运行项目打包命令   
mvn -DskipTests=true clean package install  
```
#### 发布项目
```  
进入打包结果目录(请根据本地的实际项目目录运行命令)  
cd C:\Users\xingkaichun\IdeaProjects\HelloworldBlockchain\helloworld-blockchain-explorer\target  
解压  
unzip helloworld-blockchain-explorer-*.zip  
进入解压文件目录  
cd helloworld-blockchain-explorer    
运行启动脚本  
./start.sh restart  
```