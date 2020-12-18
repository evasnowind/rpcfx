package com.prayerlaputa.rpcfx.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.prayerlaputa.rpcfx.api.RpcfxRequest;
import com.prayerlaputa.rpcfx.api.RpcfxResolver;
import com.prayerlaputa.rpcfx.api.RpcfxResponse;
import com.prayerlaputa.rpcfx.common.RpcfxException;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * RPC服务端：
 * 0. 生成proxy
 *  0.1 读取配置（dubbo config）
 *  0.2 生成proxy(dubbo proxy)
 *  0.3 服务注册（dubbo registry）
 * 1. 接收用户请求
 *  1.1 底层请求的处理，反序列化（dubbo protocol, exchange, transport, serialize）
 *  1.2 监控(dubbo monitor)
 * 2. 业务逻辑
 *  2.1 dubbo service 用户自己实现
 * 2. 发现对应服务
 *  2.1 服务注册与发现（dubbo registry）
 *  2.2 集群容错、负载均衡（dubbo cluster）
 * 3. 发起请求、重试
 *  3.1 底层请求的处理，序列化（dubbo protocol, exchange, transport, serialize）
 */
@Slf4j
public class RpcfxInvoker {

    private RpcfxResolver resolver;

    public RpcfxInvoker(RpcfxResolver resolver){
        this.resolver = resolver;
    }

    public RpcfxResponse invoke(RpcfxRequest request) {
        
        Long requestId = request.getRequestId();
        
        RpcfxResponse response = new RpcfxResponse();
        response.setRequestId(requestId);
        Class serviceClass = request.getServiceClass();

        Object service = resolver.resolve(serviceClass);
        try {
            Method method = resolveMethodFromClass(service.getClass(), request.getMethod());
            Object result = method.invoke(service, request.getParams()); // dubbo, fastjson,

            response.setResult(result);
            response.setStatus(true);
            System.out.println("response json:" + JSON.toJSONString(response));
            return response;
        } catch ( IllegalAccessException | InvocationTargetException e) {
            // 3.Xstream

            // 2.封装一个统一的RpcfxException
            // 客户端也需要判断异常
            log.error("[invoke] error:", e);
            response.setException(new RpcfxException(e));
            response.setStatus(false);
            return response;
        }
    }

    private Method resolveMethodFromClass(Class<?> klass, String methodName) {
        return Arrays.stream(klass.getMethods())
                .filter(m -> methodName.equals(m.getName()))
                .findFirst()
                .get();
    }

}
