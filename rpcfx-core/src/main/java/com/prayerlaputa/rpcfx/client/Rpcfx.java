package com.prayerlaputa.rpcfx.client;


import com.alibaba.fastjson.parser.ParserConfig;
import com.prayerlaputa.rpcfx.client.proxy.FactoryProducer;
import com.prayerlaputa.rpcfx.client.proxy.ProxyFactoryType;
import com.prayerlaputa.rpcfx.client.proxy.factory.AbstractProxyFactory;
import com.prayerlaputa.rpcfx.client.proxy.factory.ByteBuddyAbstractProxyFactory;
import com.prayerlaputa.rpcfx.common.RpcfxException;
import lombok.extern.slf4j.Slf4j;

/**
 * RPC客户端：
 * 生成代理对象
 * 常见方式：
 * 1. 动态代理
 * 2. AOP(字节码生成)
 *
 */
@Slf4j
public final class Rpcfx {

    /**
     * 使用抽象工厂模式，创建代理的工厂没必要每次都创建。
     * 并且，比如本次使用ByteBuddy写的ByteBuddyAbstractProxyFactory，
     * 采用了异步httpclient发送请求，用同一个工厂可以复用该工程内的
     * httpclient线程池。
     */
//    static AbstractProxyFactory proxyFactory = FactoryProducer.getFactory(ProxyFactoryType.JDK_DYNAMIC_PROXY);
    static AbstractProxyFactory proxyFactory = FactoryProducer.getFactory(ProxyFactoryType.BYTE_BUDDY);

    static {
        ParserConfig.getGlobalInstance().addAccept("com.prayerlaputa");
    }

    public static <T> T create(final Class<T> serviceClass, final String url) {
        // 0. 替换动态代理 -> AOP
        T proxy = null;

        try {
            //使用ByteBuddy生成动态代理
            proxy = proxyFactory.createProxy(serviceClass, url);
        } catch (RpcfxException e) {
            log.error("[create] ", e);
            throw new RpcfxException(e);
        }

        return proxy;
    }
}
