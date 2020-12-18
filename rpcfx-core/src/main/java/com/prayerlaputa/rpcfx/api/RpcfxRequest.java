package com.prayerlaputa.rpcfx.api;

import lombok.Data;

@Data
public class RpcfxRequest<T> {

    private Class<T> serviceClass;

    private String method;

    private Object[] params;

    private Long requestId;
}
