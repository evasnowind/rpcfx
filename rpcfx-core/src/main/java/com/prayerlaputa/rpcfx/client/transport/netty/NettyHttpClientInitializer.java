package com.prayerlaputa.rpcfx.client.transport.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;

/**
 * @author chenglong.yu
 * created on 2020/12/16
 */
public class NettyHttpClientInitializer extends ChannelInitializer<SocketChannel> {

    private final InFlightRequests inFlightRequests;

    public NettyHttpClientInitializer(InFlightRequests inFlightRequests) {
        this.inFlightRequests = inFlightRequests;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();

        p.addLast(new HttpRequestEncoder());
        p.addLast(new HttpResponseDecoder());
        // Remove the following line if you don't want automatic content decompression.
        p.addLast("inflater", new HttpContentDecompressor());
//
//        //HttpObjectAggregator会把多个消息转换为 一个单一的FullHttpRequest或是FullHttpResponse
        p.addLast("aggregator", new HttpObjectAggregator(1024 * 1024));
        p.addLast("handler", new NettyHttpClientHandler(inFlightRequests));
    }
}
