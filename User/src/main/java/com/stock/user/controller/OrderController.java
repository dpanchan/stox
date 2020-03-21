package com.stock.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stock.user.models.web.ExecutedOrder;
import com.stock.user.models.web.OrderDetails;
import com.stock.user.service.OrderService;

@RestController
public class OrderController {
	
	@Autowired
	private OrderService orderService;

	@RequestMapping("/user/order")
	public ExecutedOrder processOrder(@RequestBody OrderDetails orderDetails) {
		return orderService.execute(orderDetails);
	}
}
