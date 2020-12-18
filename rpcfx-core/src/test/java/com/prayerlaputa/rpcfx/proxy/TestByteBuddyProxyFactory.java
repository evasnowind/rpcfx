package com.prayerlaputa.rpcfx.proxy;

import com.prayerlaputa.rpcfx.client.proxy.factory.ByteBuddyAbstractProxyFactory;
import com.prayerlaputa.rpcfx.common.RpcfxException;

/**
 * @author chenglong.yu
 * created on 2020/12/14
 */
public class TestByteBuddyProxyFactory {


    public static void main(String[] args) {
        ByteBuddyAbstractProxyFactory factory = new ByteBuddyAbstractProxyFactory();
        try {
            HelloService service = factory.createProxy(HelloService.class, "http://www.baidu.com");
            String res = service.sayHello("hello world!");
            System.out.println(res);
        } catch (RpcfxException e) {
            e.printStackTrace();
        }
    }
}
