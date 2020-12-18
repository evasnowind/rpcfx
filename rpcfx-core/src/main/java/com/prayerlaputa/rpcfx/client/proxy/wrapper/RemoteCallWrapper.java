package com.prayerlaputa.rpcfx.client.proxy.wrapper;

import com.prayerlaputa.rpcfx.api.RpcfxRequest;
import com.prayerlaputa.rpcfx.api.RpcfxResponse;
import com.prayerlaputa.rpcfx.client.transport.AbstractTransport;
import com.prayerlaputa.rpcfx.client.transport.TransportFactory;
import com.prayerlaputa.rpcfx.common.RpcfxException;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;

import java.lang.reflect.Method;

/**
 * @author chenglong.yu
 * created on 2020/12/13
 */
@Slf4j
public class RemoteCallWrapper {

    private final Class<?> serviceClass;
    private final String url;
    private AbstractTransport transport;

    public RemoteCallWrapper(final Class<?> serviceClass, final String url) {
        this.serviceClass = serviceClass;
        this.url = url;

//        this.transport = TransportFactory.createTransport(TransportFactory.ASYNC_HTTP_CLIENT_TRANSPORT);
        this.transport = TransportFactory.createTransport(TransportFactory.NETTY_CLIENT_TRANSPORT);
    }

    @RuntimeType
    public Object byteBuddyInvoke(@This Object proxy, @Origin Method method,
                                  @AllArguments @RuntimeType Object[] args) throws RpcfxException {

        RpcfxRequest request = new RpcfxRequest();
        request.setServiceClass(this.serviceClass);
        request.setMethod(method.getName());
        request.setParams(args);

        /*
        TODO 进一步扩展：若失败，则重试，到达重试次数之后再抛出异常
        此处留待后续扩展。
         */
        RpcfxResponse response = null;
        try {
            response = transport.postCall(request, url);
        } catch (Exception e) {
            log.error("[byteBuddyInvoke post] error:", e);
            throw new RpcfxException(e);
        }

        // 这里判断response.status，处理异常
        // 考虑封装一个全局的RpcfxException

        return response.getResult();
    }


}
