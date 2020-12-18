package com.prayerlaputa.rpcfx.provider.config;

import com.prayerlaputa.rpcfx.api.OrderService;
import com.prayerlaputa.rpcfx.api.RpcfxResolver;
import com.prayerlaputa.rpcfx.api.UserService;
import com.prayerlaputa.rpcfx.provider.resolver.SimpleResolver;
import com.prayerlaputa.rpcfx.provider.service.OrderServiceImpl;
import com.prayerlaputa.rpcfx.provider.service.UserServiceImpl;
import com.prayerlaputa.rpcfx.server.RpcfxInvoker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author chenglong.yu
 * created on 2020/12/14
 */
@Configuration
public class BeanConfig {

    @Bean
    public RpcfxInvoker createInvoker(@Autowired RpcfxResolver resolver){
        return new RpcfxInvoker(resolver);
    }

    @Bean
    public RpcfxResolver createResolver(){
        return new SimpleResolver();
    }

    // 能否去掉name
    //
    @Bean
    public UserService createUserService(){
        return new UserServiceImpl();
    }

    @Bean
    public OrderService createOrderService(){
        return new OrderServiceImpl();
    }
}
