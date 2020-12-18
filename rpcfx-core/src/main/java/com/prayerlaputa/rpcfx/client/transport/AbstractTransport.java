package com.prayerlaputa.rpcfx.client.transport;

import com.prayerlaputa.rpcfx.api.RpcfxRequest;
import com.prayerlaputa.rpcfx.api.RpcfxResponse;

/**
 * @author chenglong.yu
 * created on 2020/12/16
 */
public abstract class AbstractTransport {

    public abstract RpcfxResponse postCall(RpcfxRequest req, String url) throws Exception;
}
