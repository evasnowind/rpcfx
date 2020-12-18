package com.prayerlaputa.rpcfx.client.transport;

import com.prayerlaputa.rpcfx.client.transport.httpclient.AsyncHttpClientTransport;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.reactor.IOReactorConfig;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 简单工厂模式
 *
 * @author chenglong.yu
 * created on 2020/12/16
 */
public class TransportFactory {

    public static final int ASYNC_HTTP_CLIENT_TRANSPORT = 0;
    public static final int NETTY_CLIENT_TRANSPORT = 1;

    private static final int DEFAULT_HTTP_CLIENT_THREAD_NUM = 10;

    private static CloseableHttpAsyncClient ASYNC_HTTP_CLIENT;

    public static ConcurrentHashMap<Integer, AbstractTransport> TRANSPORT_POOL = new ConcurrentHashMap<>();

    static {
        initAsyncHttpClient(DEFAULT_HTTP_CLIENT_THREAD_NUM);
    }

    private static void initAsyncHttpClient(int ioThreadCnt) {
        IOReactorConfig ioConfig = IOReactorConfig.custom()
                .setConnectTimeout(1000)
                .setSoTimeout(1000)
                .setIoThreadCount(ioThreadCnt)
                .setRcvBufSize(32 * 1024)
                .build();

        /*
        使用httpclient异步发送
         */
        ASYNC_HTTP_CLIENT = HttpAsyncClients.custom().setMaxConnTotal(40)
                .setMaxConnPerRoute(8)
                .setDefaultIOReactorConfig(ioConfig)
                .setKeepAliveStrategy((response, context) -> 6000)
                .build();
        ASYNC_HTTP_CLIENT.start();
    }

    public static AbstractTransport createTransport(int transportType) {
        TRANSPORT_POOL.putIfAbsent(transportType, buildTransport(transportType));
        return TRANSPORT_POOL.get(transportType);
    }

    private static AbstractTransport buildTransport(int transportType) {
        AbstractTransport transport = null;
        switch (transportType) {
            case ASYNC_HTTP_CLIENT_TRANSPORT:
                transport = new AsyncHttpClientTransport(ASYNC_HTTP_CLIENT);
                break;
            case NETTY_CLIENT_TRANSPORT:
            default:
                transport = new NettyClientTransport();
                break;
        }

        return transport;
    }
}
