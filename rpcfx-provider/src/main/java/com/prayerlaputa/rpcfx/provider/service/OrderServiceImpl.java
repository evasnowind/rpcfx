package com.prayerlaputa.rpcfx.provider.service;

import com.prayerlaputa.rpcfx.api.Order;
import com.prayerlaputa.rpcfx.api.OrderService;

public class OrderServiceImpl implements OrderService {

    @Override
    public Order findOrderById(int id) {
        return new Order(id, "Cuijing" + System.currentTimeMillis(), 9.9f);
    }
}
