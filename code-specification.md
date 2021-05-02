### 代码规范
#### 1.开发尽量基于贫血模型进行开发，若对象有行为，则将该行为写在该对象伴随的工具类中，伴随的工具类的类名要求是该类的名字+Tool后缀。
案例：区块对象  
类Block见https://github.com/xingkaichun/HelloworldBlockchain/blob/master/helloworld-blockchain-core/src/main/java/com/xingkaichun/helloworldblockchain/core/model/Block.java  
区块有一个获取该区块手续费的行为，则该行为写在类BlockTool中,见https://github.com/xingkaichun/HelloworldBlockchain/blob/master/helloworld-blockchain-core/src/main/java/com/xingkaichun/helloworldblockchain/core/tools/BlockTool.java

#### 2.工具类取名规范
与业务无关的工具类，类名以Util后缀结尾  
与业务相关的工具类，类名以Tool后缀结尾

#### 3.不要自己造轮子
案例：Base58工具类  
项目需要一个base58编码与解码的工具，若是自己写，可能错误百出。

#### 4.不要拷贝开源项目中的复杂的代码片段到自己项目中，建议直接引入该项目进行使用。
案例：Base58工具类  
若是发现了一个优秀开源的项目中有Base58工具类工具类，直接引入使用就行了，不建议将代码片段拷贝到自己项目，若拷贝代码片段到自己项目中，不能保证自己的开发人员不动它，而且引入的开源项目后续可能还有对该算法的升级优化，直接引入以后直接跟着升级下版本号即可，使用起来十分便捷。

#### 5.隔离
HelloworldBlockchain项目由8个子项目组成。原则上，只有子项目helloworld-blockchain-crypto、子项目helloworld-blockchain-util可以引入外部项目。其它子项目有引入外部项目的需求时，通过这两个项目间接使用。这两个项目和SLF4J作用类似，对内部项目屏蔽实现细节，可以随时切换外部项目的实现。

#### 6.DTO VO MODEL ENTITY命名
entity命名规范：类名+Entity后缀结尾  
DTO命名规范：类名+DTO后缀结尾  
MODEL命名规范：类名后不需要特别后缀标识  
VO命名规范：类名+VO后缀结尾  
注意：只有两台节点间通信才会用到DTO，为了清晰，创建了子项目helloworld-blockchain-netcore-dto专门存放DTO。