# 手写一个简单的RPC

目前已完成的扩展内容：

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
 