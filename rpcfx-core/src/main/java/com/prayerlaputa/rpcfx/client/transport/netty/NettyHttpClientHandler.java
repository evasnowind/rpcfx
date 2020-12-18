package com.prayerlaputa.rpcfx.client.transport.netty;

import com.alibaba.fastjson.JSON;
import com.prayerlaputa.rpcfx.api.RpcfxResponse;
import com.prayerlaputa.rpcfx.api.RpcfxResponseFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chenglong.yu
 * created on 2020/12/16
 */
@Slf4j
@ChannelHandler.Sharable
public class NettyHttpClientHandler extends SimpleChannelInboundHandler<FullHttpResponse> {

    private final InFlightRequests inFlightRequests;

    public NettyHttpClientHandler(InFlightRequests inFlightRequests) {
        this.inFlightRequests = inFlightRequests;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse httpObject) throws Exception {
        messageReceived(ctx, httpObject);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    public void messageReceived(ChannelHandlerContext ctx, FullHttpResponse httpObject) throws Exception {
        if (HttpResponseStatus.OK.equals(httpObject.status())) {
            String respStr = httpObject.content().toString(CharsetUtil.UTF_8);

            RpcfxResponse rpcfxResponse = JSON.parseObject(respStr, RpcfxResponse.class);

            RpcfxResponseFuture<RpcfxResponse> responseFuture = inFlightRequests.remove(rpcfxResponse.getRequestId());
            if (null != responseFuture) {
                responseFuture.getFuture().complete(rpcfxResponse);
            } else {
                log.error("找不到requestId={}的请求信息！", rpcfxResponse.getRequestId());
            }
        }
    }
}
