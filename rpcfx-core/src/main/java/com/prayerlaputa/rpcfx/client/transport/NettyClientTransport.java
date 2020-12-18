package com.prayerlaputa.rpcfx.client.transport;

import com.alibaba.fastjson.JSON;
import com.prayerlaputa.rpcfx.api.RpcfxRequest;
import com.prayerlaputa.rpcfx.api.RpcfxResponse;
import com.prayerlaputa.rpcfx.api.RpcfxResponseFuture;
import com.prayerlaputa.rpcfx.client.RequestIdSupport;
import com.prayerlaputa.rpcfx.client.transport.netty.InFlightRequests;
import com.prayerlaputa.rpcfx.client.transport.netty.NettyHttpClientInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestEncoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.util.AttributeKey;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

/**
 * @author chenglong.yu
 * created on 2020/12/16
 */
public class NettyClientTransport extends AbstractTransport {


    private static Bootstrap BOOTSTRAP;

    private static Channel channel;

    static {
        initNettyClient();
    }

    private static InFlightRequests inFlightRequests;

    public NettyClientTransport() {
    }

    public static final int MAX_RETRY = 5;
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 8080;

    public static void initNettyClient() {

        inFlightRequests = new InFlightRequests();

        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        BOOTSTRAP = new Bootstrap();
        BOOTSTRAP.group(workerGroup)
                .channel(NioSocketChannel.class)
                .attr(AttributeKey.newInstance("clientName"), "nettyClient")
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new NettyHttpClientInitializer(inFlightRequests));

        try {
            channel = BOOTSTRAP.connect(HOST, PORT).sync().channel();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public RpcfxResponse postCall(RpcfxRequest req, String url) throws Exception {

        final long reqId = RequestIdSupport.next();

        req.setRequestId(reqId);
        String reqJson = JSON.toJSONString(req);
        System.out.println("req json: " + reqJson);

        CompletableFuture<RpcfxResponse> completableFuture = new CompletableFuture<>();
        inFlightRequests.put(new RpcfxResponseFuture(reqId, completableFuture));

        URI uri = new URI("/");
        DefaultFullHttpRequest request = new DefaultFullHttpRequest( HttpVersion.HTTP_1_1, HttpMethod.POST, uri.toASCIIString(),
                Unpooled.wrappedBuffer(reqJson.getBytes(StandardCharsets.UTF_8)));

        request.headers().set(HttpHeaders.Names.HOST, HOST);
        request.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        request.headers().set(HttpHeaders.Names.CONTENT_LENGTH,
                request.content().readableBytes());
        request.headers().set(HttpHeaders.Names.CONTENT_TYPE, "application/json");

        channel.writeAndFlush(request).addListener((ChannelFutureListener) channelFuture -> {
            // 处理发送失败的情况
            if (!channelFuture.isSuccess()) {
                completableFuture.completeExceptionally(channelFuture.cause());
                channel.close();
            }
        });

        return completableFuture.get();
    }




}
