package com.prayerlaputa.rpcfx.proxy;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Method;

/**
 * @author chenglong.yu
 * created on 2020/12/14
 */
public class TestByteBuddyDemo {

    public static void main(String[] args) throws InstantiationException, IllegalAccessException {

        HelloService service = newProxy(HelloService.class, new Object() {
            @RuntimeType
            public Object byteBuddyInvoke(@This Object proxy, @Origin Method method, @AllArguments @RuntimeType Object[] args) throws Throwable {
                System.out.println("execute proxy!");
                return "test";
            }
        });

        String res = service.sayHello("hello!");
        System.out.println(res);
    }

    public static <T> T newProxy(Class<T> interfaceType, Object handler) throws IllegalAccessException, InstantiationException {
        Class<? extends T> cls = new ByteBuddy()
                .subclass(interfaceType)
                .method(ElementMatchers.isDeclaredBy(interfaceType))
                .intercept(MethodDelegation.to(handler, "handler"))
                .make()
                .load(interfaceType.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                .getLoaded();
        return cls.newInstance();
    }
}
