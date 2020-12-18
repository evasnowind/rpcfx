package com.prayerlaputa.rpcfx.api;

import java.util.concurrent.CompletableFuture;

/**
 * @author chenglong.yu
 * created on 2020/12/16
 */
public class RpcfxResponseFuture <T>{
    private final long requestId;
    private final CompletableFuture<T> future;
    private final long timestamp;

    public RpcfxResponseFuture(long requestId, CompletableFuture<T> future) {
        this.requestId = requestId;
        this.future = future;
        this.timestamp = System.nanoTime();
    }


    public long getRequestId() {
        return requestId;
    }

    public CompletableFuture<T> getFuture() {
        return future;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
