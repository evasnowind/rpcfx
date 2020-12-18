package com.prayerlaputa.rpcfx.client.proxy;

import com.prayerlaputa.rpcfx.client.proxy.factory.AbstractProxyFactory;
import com.prayerlaputa.rpcfx.client.proxy.factory.ByteBuddyAbstractProxyFactory;
import com.prayerlaputa.rpcfx.client.proxy.factory.JdkDynamicAbstractProxyFactory;

/**
 * @author chenglong.yu
 * created on 2020/12/14
 */
public class FactoryProducer {

    public static AbstractProxyFactory getFactory(ProxyFactoryType type) {
        AbstractProxyFactory factory = null;
        switch (type) {
            case JDK_DYNAMIC_PROXY:
                factory = new JdkDynamicAbstractProxyFactory();
                break;
            case BYTE_BUDDY:
                factory = new ByteBuddyAbstractProxyFactory();
                break;
            default:
                break;
        }
        return factory;
    }
}
