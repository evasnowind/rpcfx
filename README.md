# 手写一个简单的RPC

本项目是在 [rpcfx](https://github.com/JavaCourse00/JavaCourseCodes/tree/main/07rpc/rpc01) 提供的RPC框架（最基础版本，基本除了通信啥都没有，由[kimmking](https://github.com/kimmking) 提供）基础上，不断扩展，通过一步步亲自手写RPC框架的各种必要组件、完成各种功能的方式，来学习RPC的各种关键技术。

## 目标

升级 [rpcfx](https://github.com/JavaCourse00/JavaCourseCodes/tree/main/07rpc/rpc01) ，做如下内容：

- [x] 尝试将服务端写死查找接口实现类变成泛型和反射
- [x] 尝试将客户端动态代理改成字节码增强的方式，添加异常处理
- [x] 尝试使用Netty+HTTP作为client端传输方式
-  [ ] :star: :star:  尝试使用压测并分析优化RPC性能
-  [ ]  :star: :star: 尝试使用Netty+TCP作为两端传输方式
-  [ ]  :star: :star: 尝试自定义二进制序列化
-  [ ]  :star: :star: 尝试压测改进后的RPC并分析优化
-  [ ]  :star: :star: 尝试将fastjson改成xstream
-  [ ]  :star: :star: 尝试使用字节码生成方式代替服务端反射
-  [ ] rpcfx 1.0: 给自定义RPC实现简单的分组(group)和版本(version)
-  [ ] rpcfx 2.0: 给自定义RPC实现：
  -  [ ] 基于zookeeper的注册中心，消费者和生产者可以根据注册中心查找可用服务进行调用(直接选择列表里的最后一个)。
  -  [ ] 当有生产者启动或者下线时，通过zookeeper通知并更新各个消费者，使得各个消费者可以调用新生产者或者不调用下线生产者。 
-  [ ] rpcfx 3.0  :star: : 在2.0的基础上继续增强rpcfx实现： 
  -  [ ] 3.0 : 实现基于zookeeper的配置中心，消费者和生产者可以根据配置中心配置参数（分组，版本，线程池大小等）
  -  [ ] 3.1 : 实现基于zookeeper的元数据中心，将服务描述元数据保存到元数据中心。
  -  [ ] 3.2 : 实现基于etcd/nacos/apollo等基座的配置/注册/元数据中心。
- [ ] rpcfx 4.0  :star: :star:  在3.2的基础上继续增强rpcfx实现：
  -  [ ] 4.0：实现基于tag的简单路由；
  - 4.1：实现基于Weight/ConsistentHash的负载均衡
  -  [ ] 4.2：实现基于IP黑名单的简单流控
  -  [ ] 4.3：完善RPC框架里的超时处理，增加重试参数
-  [ ] rpcfx 5.0 :star: :star: :star: 在4.3的基础上继续增强rpcfx实现： 
  -  [ ] 5.0 : 实现利用HTTP头跨进程传递Context参数（隐式传参）
  -  [ ] 5.1：实现消费端mock一个指定对象的功能（Mock功能）
  -  [ ] 5.2：实现消费端可以通过一个泛化接口调用不同服务（泛化调用）
  -  [ ] 5.3：实现基于Weight/ConsistentHash的负载均衡
  -  [ ] 5.4：实现基于单位时间调用次数的流控，可以基于令牌桶等算法
-  [ ] rpcfx 6.0 :star: :star: :star::star: 压测，并分析调优5.4版本



### 其他学习RPC的备选课题

- 实现简单的Protocol Buffer/Thrift/gRPC(选任一个)远程调用demo
- 实现简单的WebService-Axis2/CXF远程调用demo
- 按dubbo-samples项目的各个demo学习具体功能使用。
-  :star: :star: 基于上面的自定义序列化，实现Dubbo的序列化扩展
-  :star: :star: 基于上面的自定义RPC，实现Dubbo的RPC扩展
-  :star: :star: 在Dubbo的filter机制上，实现REST权限控制，可参考dubbox
-  :star: :star: 实现一个自定义Dubbo的Cluster/Loadbalance扩展，如果一分钟内调用某个服务/提供者超过10次，则拒绝提供服务直到下一分钟
-  :star: :star: 整合Dubbo+Sentinel，实现限流功能
-  :star: :star: 整合Dubbo与Skywalking，实现全链路性能监控



## 目前已完成的扩展内容

先简单整理下。

- [X] 生成代理对象时采用字节码增强的方式，替代java动态代理
    - 实践了一下抽象工厂设计模式
- [X] 考虑封装一个全局的RpcfxException
- [X] server端返回请求结果时，只需序列化一次
- [X] 使用Netty+HTTP作为client端传输方式
    - 另外也实现了httpclient作为客户端传输方式，因为没看到老师作业题目更新了，不过多做点也挺好
    - 实践了下简单工厂模式
- [X] 添加请求超时判断逻辑
    - 参考了李玥老师的《消息队列高手课》 https://github.com/liyue2008/simple-rpc-framework  里的 NettyTransport
    

后续可以进一步扩展：
- [ ] 通过SPI的方式，将不同的动态代理实现、网络请求实现插件化，可以动态配置
    - 目前实现了：ByteBuddy动态代理、JDK动态代理两种方式； 网络请求有异步HTTPClient, netty两种方式。
- [ ] 自定义序列化、反序列化
    - 这个如果使用netty就会很方便，可以使用MessageToMessageCodec ，具体可以参考之前我写过的netty-im（netty入门与实战 小册子的内容） https://github.com/evasnowind/netty-im
- [ ] 模仿dubbo，支持使用注解简化RPC调用
- [ ] 负载均衡
- [ ] 其他扩展



## 遇到的问题
- fastjson序列化一次
    - 可以配置HttpMessageConverters，简化序列化。
    - 参考 https://blog.csdn.net/lmb55/article/details/90676823
- netty发送HTTP请求
    - 这个感觉有点绕远，本来netty可以支持更底层的序列化。
    - 主要参考： 
        - https://blog.csdn.net/xbt312/article/details/99829118/  
        - https://blog.csdn.net/qq_28822933/article/details/83591252
- netty总是输出debug级别日志
    - 常见的spring boot配置不生效，最后还是找了一个logback.xml的模板，参考https://blog.csdn.net/qq_37469055/article/details/92849354
- 解决netty异步请求与动态代理同步返回结果之间的矛盾
    - RPC调用通过动态代理发送出请求后，需要阻塞，直到返回结果。而netty的话最大的优点就是NIO，返回结果需要通过回调拿到。为了解决这两者的冲突，使用了CompletableFuture，动态代理这一层在拿到返回结果前通过CompletableFuture#get()方法一直阻塞。netty的handler中拿到返回结果后，调用CompletableFuture.complete()方法将结果返回，动态代理层CompletableFuture#get也就会停止阻塞、返回结果。
    - 为了在客户端识别出是哪个请求得出了返回结果，在请求、响应中都加入了requestId，并在客户端维护一个map，该map同时也会解决请求超时问题，参见InFlightRequests。更进一步，可以在这个InFlightRequests里做限流、熔断。
