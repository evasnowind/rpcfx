package com.prayerlaputa.rpcfx.api;

public interface RpcfxResolver {

    <T> T resolve(Class<T> serviceClass);

}
