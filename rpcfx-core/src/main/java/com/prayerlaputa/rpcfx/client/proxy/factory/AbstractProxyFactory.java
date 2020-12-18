package com.prayerlaputa.rpcfx.client.proxy.factory;

import com.prayerlaputa.rpcfx.client.proxy.ProxyFactoryType;
import com.prayerlaputa.rpcfx.common.RpcfxException;

/**
 *
 * @author chenglong.yu
 * created on 2020/12/13
 */
public abstract class AbstractProxyFactory {

    public abstract ProxyFactoryType getFactoryType();

    public abstract <T> T createProxy(final Class<T> serviceClass, final String url) throws RpcfxException;
}
