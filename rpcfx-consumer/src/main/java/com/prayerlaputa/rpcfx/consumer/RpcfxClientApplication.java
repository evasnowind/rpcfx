package com.prayerlaputa.rpcfx.consumer;

import com.prayerlaputa.rpcfx.api.Order;
import com.prayerlaputa.rpcfx.api.OrderService;
import com.prayerlaputa.rpcfx.api.User;
import com.prayerlaputa.rpcfx.api.UserService;
import com.prayerlaputa.rpcfx.client.Rpcfx;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RpcfxClientApplication {

	public static void main(String[] args) {
		UserService userService = Rpcfx.create(UserService.class, "http://localhost:8080/");
		User user = userService.findById(1);
		System.out.println("find user id=1 from server: " + user.getName());

		OrderService orderService = Rpcfx.create(OrderService.class, "http://localhost:8080/");
		Order order = orderService.findOrderById(1992129);
		System.out.println(String.format("find order name=%s, amount=%f",order.getName(),order.getAmount()));
	}

}
