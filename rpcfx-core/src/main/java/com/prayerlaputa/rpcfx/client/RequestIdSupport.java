package com.prayerlaputa.rpcfx.client;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author chenglong.yu
 * created on 2020/12/16
 */
public class RequestIdSupport {

    private final static AtomicLong nextRequestId = new AtomicLong(1);

    public static long next() {
        return nextRequestId.getAndIncrement();
    }
}
