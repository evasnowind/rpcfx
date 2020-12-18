package com.prayerlaputa.rpcfx.client.proxy.factory;

import com.prayerlaputa.rpcfx.client.proxy.ProxyFactoryType;
import com.prayerlaputa.rpcfx.client.proxy.wrapper.RemoteCallWrapper;
import com.prayerlaputa.rpcfx.common.RpcfxException;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.reactor.IOReactorConfig;

/**
 *
 * @author chenglong.yu
 * created on 2020/12/13
 */
@Slf4j
public class ByteBuddyAbstractProxyFactory extends AbstractProxyFactory {

    private CloseableHttpAsyncClient httpclient;
    private int ioThreadCnt = 10;

    public ByteBuddyAbstractProxyFactory() {
        init(ioThreadCnt);
    }

    public ByteBuddyAbstractProxyFactory(int ioThreadCnt) {
        init(ioThreadCnt);
    }

    private void init(int ioThreadCnt) {

        this.ioThreadCnt = ioThreadCnt;

        IOReactorConfig ioConfig = IOReactorConfig.custom()
                .setConnectTimeout(1000)
                .setSoTimeout(1000)
                .setIoThreadCount(this.ioThreadCnt)
                .setRcvBufSize(32 * 1024)
                .build();

        /*
        使用httpclient异步发送
         */
        httpclient = HttpAsyncClients.custom().setMaxConnTotal(40)
                .setMaxConnPerRoute(8)
                .setDefaultIOReactorConfig(ioConfig)
                .setKeepAliveStrategy((response, context) -> 6000)
                .build();
        httpclient.start();
    }

    @Override
    public ProxyFactoryType getFactoryType() {
        return ProxyFactoryType.BYTE_BUDDY;
    }

    @Override
    public <T> T createProxy(Class<T> serviceClass, String url) throws RpcfxException {
        T proxy = null;

        try {
            RemoteCallWrapper handler = new RemoteCallWrapper(serviceClass, url);

            Class<? extends T> cls = new ByteBuddy()
                    .subclass(serviceClass)
                    .method(ElementMatchers.isDeclaredBy(serviceClass))
                    .intercept(MethodDelegation.to(handler, "handler"))
                    .make()
                    .load(serviceClass.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                    .getLoaded();

            proxy = cls.newInstance();

        } catch (Exception e) {
            e.printStackTrace();
            log.error("[createProxy] error:", e);
            throw new RpcfxException(e);
        }

        return proxy;
    }

}
